# 05. Crypto

Modul `spring-common-crypto` membungkus `javax.crypto` untuk AES dan RSA. Tidak bergantung
Spring, jadi bisa dipakai di aplikasi apa pun.

```groovy
implementation 'com.github.onevour.spring-commons:spring-common-crypto:<tag>'
```

Dua konsep:

- **Engine**: `AESCrypto` atau `RSACrypto`, dibuat dengan nama transformasi JCA.
- **Key loader**: dari mana kunci diambil. Kunci dimuat saat engine dibuat.

Semua engine mengimplementasikan `Crypto`:

```java
byte[] encrypt(String plainText);
String encryptToBase64(String plainText);
String decrypt(String base64);
String decrypt(byte[] data);
```

## AES

| Loader | Konstruktor | Catatan |
|---|---|---|
| `AESKeyBase64Loader` | `(base64Key)` atau `(base64Key, base64Iv)` | kunci 16/24/32 byte dalam Base64 |
| `AESKeyStringLoader` | `(key)` atau `(key, base64Iv)` | kunci dipakai byte UTF-8 apa adanya, panjangnya harus 16/24/32 |
| `AESKeyMinioLoader` | `(minioClient, bucket, path, keyId)` | membaca objek `path + keyId + "/secret.key"` dan `"/vector.key"` (IV, opsional) |

Transformasi yang didukung: mengandung `CBC` atau `GCM`.

### AES/CBC (butuh IV)

```java
AESCrypto aes = new AESCrypto("AES/CBC/PKCS5Padding", new AESKeyBase64Loader(base64Key, base64Iv));

String cipherText = aes.encryptToBase64("Test123#$#@4");
String plainText  = aes.decrypt(cipherText);
```

Selalu beri IV untuk CBC. Tanpa IV, enkripsi tetap "berhasil" dengan IV acak dari JCE yang tidak
tersimpan di mana pun, sehingga hasilnya tidak pernah bisa didekripsi.

### AES/GCM (tanpa IV di loader)

```java
AESCrypto aes = new AESCrypto("AES/GCM/NoPadding", new AESKeyBase64Loader(base64Key));
String cipherText = aes.encryptToBase64("Test123#$#@4");
String plainText  = aes.decrypt(cipherText);
```

### Kunci dari MinIO

```java
MinioClient minio = MinioClient.builder()
        .endpoint(minioUrl)
        .credentials(accessKey, secretKey)
        .build();

AESCryptoKeyLoader loader = new AESKeyMinioLoader(minio, "my-bucket", "key/aes/", "prod");
AESCrypto aes = new AESCrypto("AES/GCM/NoPadding", loader);
```

Struktur objek di bucket: `key/aes/prod/secret.key` (wajib) dan `key/aes/prod/vector.key` (IV, untuk CBC).

## RSA

| Loader | Konstruktor | Catatan |
|---|---|---|
| `RSAKeyBase64Loader` | `(base64PublicKey, base64PrivateKey)` | public X.509, private PKCS#8, keduanya Base64 dari byte DER. Salah satu boleh `null`. |
| `RSAKeyMinioLoader` | `(minioClient, bucket, path, keyId)` | membaca `public.key` dan `private.key` di `path + keyId + "/"` |

Urutan argumen `RSAKeyBase64Loader` adalah **public dulu, lalu private**.

```java
RSACryptoKeyLoader loader = new RSAKeyBase64Loader(base64PublicKey, base64PrivateKey);
RSACrypto rsa = new RSACrypto("RSA/ECB/PKCS1Padding", loader);

String cipherText = rsa.encryptToBase64("Test123#$#@4");   // pakai public key
String plainText  = rsa.decrypt(cipherText);               // pakai private key
```

Hanya memegang public key (misalnya di sisi klien) cukup dengan `new RSAKeyBase64Loader(publicKey, null)`.
Memanggil `decrypt` tanpa private key akan gagal.

### Pesan panjang (block cipher)

RSA hanya bisa mengenkripsi data sepanjang ukuran kunci. Untuk pesan yang lebih panjang
pakai varian block cipher yang memotong pesan per blok. Ukuran blok dekripsi mengikuti ukuran
kunci (1024, 2048, 4096 bit), dan data yang dienkripsi versi lama dengan kunci 1024 bit tetap bisa dibaca:

```java
String hex    = rsa.encryptBlockCipherToString(jsonPanjang);   // keluaran hex
String base64 = rsa.encryptBlockCipherToBase64(jsonPanjang);   // keluaran Base64

String plain  = rsa.decryptBlockCipherFromString(hex);
String plain2 = rsa.decryptBlockCipherFromBase64(base64);
```

## Mengelola kunci

- Jangan menyimpan kunci di source code atau properti yang ikut ter-commit. Ambil dari environment variable, secret manager, atau MinIO.
- Untuk membaca kunci dari resource/file sendiri, konversi dulu ke Base64 lalu pakai `AESKeyBase64Loader` / `RSAKeyBase64Loader`:

```java
String base64 = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("/etc/app/public.key")));
```
