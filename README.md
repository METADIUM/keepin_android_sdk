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
    implementation 'com.github.YoungBaeJeon.metadium_android_sdk:metasdk:0.1'
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
`AndroidManifest.xml` 에 meta-data 로 service-id 설정
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
        }
    }
});
```
###### 서명 요청
```
keepinSDK.sign(getNonce(), new Callback<SignData>() {
    @Override
    public void onResult(ServiceResult<SignData> result) {
        if (result.isSuccess()) {
            String metaId = result.getResult().getMetaId(); // user Meta ID
            String signature = result.getResult().getSignature(); // signed message(nonce)

            // TODO 해당 서비스 서버에 metaId, signature 를 전송하여 인증
        }
        else {
            // error to sign
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
[Web3j](https://github.com/web3j/web3j#getting-started) 추가<br>
Import [MetaIdentity](https://github.com/YoungBaeJeon/meta_android_sdk/blob/master/contract/MetaIdentity.java)
```
public static String signatureDataToString(Sign.SignatureData signatureData) {
    ByteBuffer buffer = ByteBuffer.allocate(65);
    buffer.put(signatureData.getR());
    buffer.put(signatureData.getS());
    buffer.put(signatureData.getV());
    return Numeric.toHexString(buffer.array());
}

public static Sign.SignatureData stringToSignatureData(String signature) {
    byte[] bytes = Numeric.hexStringToByteArray(signature);
    return new Sign.SignatureData(bytes[64], Arrays.copyOfRange(bytes, 0, 32), Arrays.copyOfRange(bytes, 32, 64));
}


// ec-recover
SignatureData signatureData = stringToSignatureData(signature);
BigInteger publicKey = Sign.signedMessageToKey(referrer.getChallege().getBytes("utf-8"), signatureData);
byte[] address = Bytes.expandPadded(Numeric.hexStringToByteArray(Keys.getAddress(publicKey)), 32);

// MetaID 주소의 contract 에 해당 address 가 존재하는지 확인
Web3j web3j = Web3j.build(new HttpService("https://api.metadium.com/dev"));
MetaIdentity contract = MetaIdentity.load(
    account.getIdentificationId(),
    web3j,
    new TransactionManager(web3j, null) {
        @Override
        public EthSendTransaction sendTransaction(BigInteger gasPrice, BigInteger gasLimit, String to, String data, BigInteger value) throws IOException {
            return null;
        }
    },
    new StaticGasProvider(BigInteger.ZERO, BigInteger.ZERO)
);
if (contract.keyHasPurpose(keyBytes, BigInteger.valueOf(5)).send()) {
    // 인증 성공
}
else {
    // 존재하지 않는 키
}
```
