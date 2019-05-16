# Metadium android SDK
Metadium android SDK 설명.

# Installation
프로젝트 `build.gradle` 파일에 repository를 추가
```
repositories {
    maven { url "https://jitpack.io" }
}
```

### Keepin 앱이 키를 생성하고 관리 시
앱 `build.gradle` 파일에 dependency 를 추가
```
dependencies {
    implementation 'com.github.YoungBaeJeon.metadium_android_sdk:metasdk:0.2'
}
```

### 키를 서비스에서 관리 시
앱 `build.gradle` 파일에 dependency 와 complileOption 추가
```
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```
```
dependencies {
    implementation 'com.github.YoungBaeJeon.metadium_android_sdk:metaextsdk:0.1'
}
```

# Usage
`AndroidManifest.xml` 에 meta-data 로 service-id 설정<br>
service-id 는 Metadium 발급합니다.
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
    KeepinSDK sdk = new KeepinSDK(getContext());
} catch (NotInstalledKeepinException e) {
    // google play store
    startActivity(e.getIntent());
}
```

### Keepin 앱이 키를 생성하고 관리 시
###### 키 등록
키 등록 요청 시 Meta ID 가 생성되어 있지 않으면 Meta ID 생성 화면으로 이동하며 이후 서비스 키 등록 화면 노출

```
keepinSDK.sdk.registerKey("nonce", new Callback<RegisterKeyData>() {
   @Override
   public void onResult(ServiceResult<RegisterKeyData> result) {
       if (result.isSuccess()) {
           String metaId = result.getResult().getMetaId(); // user Meta ID
           String signature = result.getResult().getSignature(); // signed message(nonce)
           String transactionId = result.getResult().getTransactionId(); // 키를 등록한 transaction hash

           // TODO 해당 서비스 서버에 metaId, signature 를 전송하여 사용자 계정과 연결
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
###### 서명 요청
Meta ID 가 생성되어 있지 않으면 Meta ID 생성 및 서비스 키 등록 화면 노출<br>
서비스 키가 등록되어 있지 않으면 서비스 키 등록화면 노출<br>
서비스 키가 등록되어 있으면 인증화면 노출

```
// 등록되어 있는 키가 존재하지 않으면 자동 생성
keepinSDK.sign(getNonce(),
    true, /** 서비스가 키가 등록되어 있지 않으면 자동으로 키 생성하여 서비스 등록 */
    metaId, /** metaId 가 같은지 확인 시 필요. null 이면 확인 안함 */
    new Callback<SignData>() {
    @Override
    public void onResult(ServiceResult<SignData> result) {
        if (result.isSuccess()) {
            String metaId = result.getResult().getMetaId(); // user Meta ID
            String signature = result.getResult().getSignature(); // signed message(nonce)
            String transactionId = result.getResult.getTransactionId();

            if (transactionId != null) {
                //  서비스 키가 새로 등록되었음
            }

            // TODO 해당 서비스 서버에 metaId, signature 를 전송하여 인증
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

// 기존에 등록되어 키로만 서명 요청
keepinSDK.sign(getNonce(), false, null, new Callback<SignData>() {
    @Override
    public void onResult(ServiceResult<SignData> result) {
        if (result.isSuccess()) {
            String metaId = result.getResult().getMetaId(); // user Meta ID
            String signature = result.getResult().getSignature(); // signed message(nonce)

            // TODO 해당 서비스 서버에 metaId, signature 를 전송하여 인증
        }
        else {
            // error to sign
            if (result.getResult().getError().getCode() == ServiceResult.Error.CODE_NOT_CREATE_META_ID) {
                // Meta ID 가 생성되어 있지 않음.
            }
            else if (result.getResult().getError().getCode() == ServiceResult.Error.CODE_NOT_MATCHED_META_ID) {
                // 요청하는 Meta ID 와 Keepin 에 생성되어 있는 Meta ID 가 같지 않음
            }
            else if (result.getResult().getError().getCode() == ServiceResult.Error.ERROR_CODE_UN_LINKED_SERVICE) {
                // Service 가 등록되어 있지 않음.
            }
        }
    }
});

```
###### 키 삭제
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

# Server side 에서의 인증 처리
###### Java

[Web3j](https://github.com/web3j/web3j#getting-started) 추가
[IdentityRegistry](./app/src/main/java/com/metadium/metadiumsdk/IdentityRegistry.java), [ServiceKeyResolver](./app/src/main/java/com/metadium/metadiumsdk/IdentityRegistry.java) 소스 복사하여 포함한다.

```
public static Sign.SignatureData stringToSignatureData(String signature) {
    byte[] bytes = Numeric.hexStringToByteArray(signature);
    return new Sign.SignatureData(bytes[64], Arrays.copyOfRange(bytes, 0, 32), Arrays.copyOfRange(bytes, 32, 64));
}


String sinature = "...";    // Cilent 에서 전달 받은 서명
String metaId = "...";      // Client 에서 전달 받은 Meta ID
String serviceId = "...";   // 발급받은 service id
// ec-recover
SignatureData signatureData = stringToSignatureData(signature);
BigInteger publicKey;
try {
    publicKey = Sign.signedMessageToKey(nonce.getBytes(), signatureData);
}
catch (SignatureException e) {
    // invalid signature
}
String key = Numeric.prependHexPrefix(Keys.getAddress(publicKey));

// to ein
BigInteger ein = Numeric.toBigInt(result.getResult().getMetaId());

// IdentityRegistry 에서 resolver address 획득
Web3j web3j = Web3j.build(new HttpService("https://api.metadium.com/dev"));
IdentityRegistry identityRegistry = IdentityRegistry.load(
        "0xBE2bB3d7085fF04BdE4B3F177a730a826f05cB70",
        web3j,
        new ReadonlyTransactionManager(web3j, null),
        new StaticGasProvider(BigInteger.ZERO, BigInteger.ZERO)
);
Tuple4<String, List<String>, List<String>, List<String>> identity = identityRegistry.getIdentity(ein).send();
if (identity.getValue4().size() > 0) {
    String resolverAddress = identity.getValue4().get(0);

    // 키가 등록되어 있는지 확인
    ServiceKeyResolver serviceKeyResolver = ServiceKeyResolver.load(
            resolverAddress,
            web3j,
            new ReadonlyTransactionManager(web3j, null),
            new StaticGasProvider(BigInteger.ZERO, BigInteger.ZERO)
    );
    boolean hasForKey = serviceKeyResolver.isKeyFor(key, ein).send();
    String symbol = serviceKeyResolver.getSymbol(key).send();

    if (hasForKey) {
        if (serviceId.equalsIgnoreCase(symbol)) {
            // 키 등록되어 있음
        }
        else {
            // 키는 등록되어 있으나 제공 서비스가 아님
        }
    }
    else {
        showToast("Not exists key in Resolver");
    }
}
else {
    // resolver address 가 없음. => 등록된 키가 없다고 간주함.
}
```
