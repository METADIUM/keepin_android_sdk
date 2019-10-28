# Keepin android SDK
Keepin android SDK 설명.

### Prepare SDK
프로젝트 `build.gradle` 파일에 repository 를 추가
```
repositories {
    maven { url "https://jitpack.io" }
}
```


앱 `build.gradle` 파일에 dependency 를 추가
```
dependencies {
    implementation 'com.github.METADIUM:keepin_android_sdk:demo-appec-SNAPSHOT'
}
```

### Java doc
[Java Reference](https://metadium.github.io/keepin_android_sdk/index.html) 참조

### Initializing
`AndroidManifest.xml` 에 meta-data 로 service-id 설정<br>
service-id 는 [Metadium registry](https://github.com/METADIUM/meta-SP-Registry/blob/master/service_registry.md) 발급합니다.  

```
<application>
        <meta-data
                android:name="KEEPIN_SERVICE_ID"
                android:value="{service_id}"
                />
<application>
```
SDK 초기화
```
try {
    KeepinSDK keepinSdk = new KeepinSDK(getContext());
} catch (NotInstalledKeepinException e) {
    // google play store
    startActivity(e.getIntent());
}
```

### Usage
#### 키 등록 요청
Keepin 앱에 서비스의 키 등록을 요청합니다.
키는 keepin 앱에서 생성하고 관리하며 서명 요청 시 해당 키로 sign 하여 반환합니다.
```
keepinSdk.registerKey("nonce", new Callback<RegisterKeyData>() {
   @Override
   public void onResult(ServiceResult<RegisterKeyData> result) {
       if (result.isSuccess()) {
           String metaId = result.getResult().getMetaId(); // user Meta ID
           Stromg did = result.getResult().getDid(); // did. 'did:meta:testnet:00...113'
           String signature = result.getResult().getSignature(); // signed message(nonce)
           String transactionId = result.getResult().getTransactionId(); // 키를 등록한 transaction hash

           // 서버에 did, signature 를 전송하고 검증 후 사용자 계정과 맵핑
       } else {
           // error to register

           if (result.getResult().getError().getCode() == ServiceResult.Error.CODE_NOT_CREATE_META_ID) {
               // Meta ID 가 생성하지 않음
           }
           else if (result.getResult().getError().getCode() == ServiceResult.Error.ERROR_CODE_UN_LINKED_SERVICE) {
               // Service 등록을 하지 않음
           }

       }
   }
});
```

#### 서명 요청
해당 서비스로 등록되어 있는 키로 서명을 요청합니다.
```
keepinSDK.sign(getNonce(),
    true, /** 서비스가 키가 등록되어 있지 않으면 자동으로 키 생성하여 서비스 등록 */
    metaId, /** metaId 가 같은지 확인 시 필요. null 이면 확인 안함 */
    new Callback<SignData>() {
    @Override
    public void onResult(ServiceResult<SignData> result) {
        if (result.isSuccess()) {
            String metaId = result.getResult().getMetaId(); // user Meta ID
            String did = result.getResult().getDid(); // did
            String signature = result.getResult().getSignature(); // signed message(nonce)
            String transactionId = result.getResult.getTransactionId();

            if (transactionId != null) {
                //  서비스 키가 새로 등록되었음
            }

            // 서버에 did, signature 를 전송하여 검증
        }
        else {
            // error to sign
            if (result.getResult().getError().getCode() == ServiceResult.Error.CODE_NOT_CREATE_META_ID) {
                // Meta ID 가 생성하지 않음
            }
            else if (result.getResult().getError().getCode() == ServiceResult.Error.CODE_NOT_MATCHED_META_ID) {
                // 요청하는 Meta ID 와 Keepin 에 생성되어 있는 Meta ID 가 같지 않음
            }
            else if (result.getResult().getError().getCode() == ServiceResult.Error.ERROR_CODE_UN_LINKED_SERVICE) {
                // Service 등록을 하지 않음
            }
        }
    }
});
```
#### 키 삭제 요청
등록되어 있는 키를 삭제 요청을 합니다.
```
keepinSDK.removeKey(metaId, new Callback<RemoveKeyData>() {
    @Override
    public void onResult(ServiceResult<RemoveKeyData> result) {
        if (result.isSuccess()) {
            String metaId = result.getResult().getMetaId(); // user Meta ID
            String transactionId = result.getResult().getTransactionId(); // 키를 등록한 transaction hash

            // TODO 해당 서비스 서버에 metaId 를 전송하여 사용자 계정에서 삭제
        }
        else {
            showErrorToast(result.getError());
        }
    }
});
```

#### Meta ID 확인
현재 Keepin 앱에 발급된 Meta ID 를 확인합니다.
```
keepinSDK.getMetaId(new ReturnCallback<String>() {
    @Override
    public void onReturn(String result) {
        // result is meta id
    }
});
```

#### 서비스 키 등록 여부 확인
현재 서비스의 키가 Keepin 앱에 등록되어 있는지 확인합니다.
```
sdk.hasKey(new ReturnCallback<Boolean>() {
    @Override
    public void onReturn(Boolean result) {
        showToast("hasKey="+result);
    }
});
```


# Server side 에서의 검증 처리
#### Java
서버에서 did 와 signature 로 검증을 하기 위해서는 did resolver 에서 key 정보를 가져와야 합니다.
did resolver 와 통신하는 라이브러리를 제공하고 있으며 [did-resolver-java-client](https://github.com/METADIUM/did-resolver-java-client)에서 확인하시기 바랍니다.

```
String sinature = "...";    // Cilent 에서 전달 받은 서명
String did = "...";         // Client 에서 전달 받은 did
String serviceId = "...";   // 발급받은 service id
String nonce = "...";       // 서명에 사용된 메세지

// did-resolver server 에서 public key 정보를 얻는다.
DidDocument didDocument = DIDResolverAPI.getInstance().getDocument(did, true);
if (didDocument != null) {
    try {
        // 서명을 ec-recover 한 후 did document 에 해당 키가 존재하는지 확인한다.
        if (didDocument.hasRecoverAddressFromSignature(nonce.getBytes("utf-8"), signature)) {
            // 검증 성공
        }
        else {
            // Not found public key
        }
    }
    catch (SignatureException e) {
        // 검증 실패
    }
}
else {
    // 존재하지 않는 DID
}
```
