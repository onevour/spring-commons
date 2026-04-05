package com.onevour.core;

import com.onevour.core.crypto.RSACryptoKeyLoader;
import com.onevour.core.crypto.key.RSAKeyBase64Loader;
import com.onevour.core.crypto.key.RSAKeyMinioLoader;
import com.onevour.core.crypto.rsa.RSACrypto;
import com.onevour.core.crypto.utils.CryptoUtils;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RSACryptoTest {

    private String fileToBase64(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
//            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return Base64.getEncoder().encodeToString(is.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void test_rsa_encrypt_decrypt_v3_ecb_load_public() throws Exception {
        String privateKey = null;
        String publicKey = fileToBase64("/keypair/public.key");
        RSACryptoKeyLoader adapter = new RSAKeyBase64Loader(privateKey, publicKey);
        RSACrypto rsaCrypto = new RSACrypto("RSA/ECB/PKCS1Padding", adapter);
        String plainText = "Test123#$#@4";
        String encryptedString = rsaCrypto.encryptToBase64(plainText);
        log.info("\n{}", encryptedString);
        Assertions.assertNotNull(encryptedString, "not null result");
    }

    @Test
    void test_rsa_encrypt_decrypt_v3_ecb_block_chiper() throws Exception {
        String privateKey = null;
//        String publicKey = fileToBase64("/keypair/public.key");
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDjsNVcqK9y5UedRJySABHf5LlLWeFaFnTUcHNM+5IV404hpkqAPwKU2yCbYwy6yIPTZEv7cCh4EDPBzhzMhlDpWLMZhI5PMoqWVy9/I/EysiiwUehZwkExCf/KHuMuM89PuLL4APD/1IeyFgyG4MQg5LuxCo3GjebgAhbhIPir3wIDAQAB";
        RSACryptoKeyLoader adapter = new RSAKeyBase64Loader(privateKey, publicKey);
        RSACrypto rsaCrypto = new RSACrypto("RSA/ECB/PKCS1Padding", adapter);
        String plainText = "{\"pub_key\":\"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCo/wwm3YehVJSY8w6Hg80VHHjYCZ+JOzW4BY2hwFqbLIje3wURSK9f+95wxcREHDdK//Y3Wk6/RUyodvN8uVKuRjYi+pybjclSSauo/4hVxXw7q+pEun/tbBDF0VpEQBvqkS/ouX+nUthuJJOguGe1EGXPXuztHbLQBK5OaN7NHwIDAQAB\",\"license\":\"ewogICAgInZlcnNpb24iOiAiMi4xIiwKICAgICJjb250cmFjdCI6IHsKICAgICAgICAiY3VzdG9tZXIiOiAiUFRBU0xJUkFOQ0FOR0FOSU5ET05FU0lBX3Y4LjE1IGFuZCBhYm92ZSIsCiAgICAgICAgImV4cGlyYXRpb24iOiB7CiAgICAgICAgICAgICJkYXkiOiAxLAogICAgICAgICAgICAibW9udGgiOiAxMSwKICAgICAgICAgICAgInllYXIiOiAyMDI2CiAgICAgICAgfSwKICAgICAgICAiaHdpZHMiOiBbCiAgICAgICAgICAgICJBSGZNeURVNnhLYUd6MElEIgogICAgICAgIF0sCiAgICAgICAgInByb2R1Y3RzIjogWwogICAgICAgICAgICAiaWZhY2UiCiAgICAgICAgXSwKICAgICAgICAiaWRraXQiOiB7CiAgICAgICAgICAgICJkYXRhYmFzZV9zaXplIjogMCwKICAgICAgICAgICAgIm1heF9jbGllbnRfY29ubmVjdGlvbnMiOiAwCiAgICAgICAgfSwKICAgICAgICAiZG90IjogewogICAgICAgICAgICAibW9iaWxlIjogewogICAgICAgICAgICAgICAgImZhY2UiOiB7CiAgICAgICAgICAgICAgICAgICAgImVuYWJsZWQiOiB0cnVlCiAgICAgICAgICAgICAgICB9LAogICAgICAgICAgICAgICAgImRvY3VtZW50IjogewogICAgICAgICAgICAgICAgICAgICJlbmFibGVkIjogdHJ1ZQogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgICJuZmMiOiB7CiAgICAgICAgICAgICAgICAgICAgImVuYWJsZWQiOiB0cnVlCiAgICAgICAgICAgICAgICB9LAogICAgICAgICAgICAgICAgImZhY2VMaXRlIjogewogICAgICAgICAgICAgICAgICAgICJlbmFibGVkIjogdHJ1ZQogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgICJmZWF0dXJlcyI6IHsKICAgICAgICAgICAgICAgICAgICAicmVhbFRpbWVUcmFuc2FjdGlvblJlcG9ydGluZ0VuYWJsZWQiOiB0cnVlLAogICAgICAgICAgICAgICAgICAgICJyZWFsVGltZUFuYWx5dGljc1JlcG9ydGluZ0VuYWJsZWQiOiB0cnVlCiAgICAgICAgICAgICAgICB9LAogICAgICAgICAgICAgICAgInBhbG0iOiB7CiAgICAgICAgICAgICAgICAgICAgImVuYWJsZWQiOiB0cnVlCiAgICAgICAgICAgICAgICB9CiAgICAgICAgICAgIH0KICAgICAgICB9CiAgICB9LAogICAgImNvbnRyYWN0X3NpZ25hdHVyZSI6ICJqNmtmM0xBNGVCNENPS0RmeGVVUlJYMUpwRVpBczZEbVFOaFVuWnNBTElXOC9RcmFQZFlVWUZqa1NDeUxRd3BQMGZmUzlpWWxXWUtlYkNiV2ExekZERll3Q3JnM3VFZ2R1UlRMUXorSVJkQ0dSdVJKam1BcW5MTmhxeUVaTHFGdzlGd3JXbFBFdWZEUlQrWi9rd1NnZ3FyMXFvazFPVTlTTjJXQzhyaG12aU09Igp9\"}";
        String encryptedString = encryptLargeMessage(adapter.encryptKey(), plainText);
        log.info("\n{}", encryptedString);
        Assertions.assertNotNull(encryptedString, "not null result");
    }

    @Test
    void test_rsa_encrypt_decrypt_v3_ecb() throws Exception {
        String privateKey = fileToBase64("/keypair/private.key");
        String publicKey = fileToBase64("/keypair/public.key");
        RSACryptoKeyLoader adapter = new RSAKeyBase64Loader(privateKey, publicKey);
        RSACrypto rsaCrypto = new RSACrypto("RSA/ECB/PKCS1Padding", adapter);
        String plainText = "Test123#$#@4";
        String encryptedString = rsaCrypto.encryptToBase64(plainText);
        log.info("\n{}", encryptedString);
        // decrypt
        String decrypt = rsaCrypto.decrypt(encryptedString);
        log.info("\n{}", decrypt);
        Assertions.assertEquals(plainText, decrypt);
        encryptedString = rsaCrypto.encryptBlockCipherToString(plainText);
        log.info("\nstring: {}", encryptedString);
        // decrypt
        decrypt = rsaCrypto.decryptBlockCipherFromString(encryptedString);
        log.info("\n{}", decrypt);
        Assertions.assertEquals(plainText, decrypt);

        encryptedString = rsaCrypto.encryptBlockCipherToBase64(plainText);
        log.info("\nbase64: {}", encryptedString);
        // decrypt
        decrypt = rsaCrypto.decryptBlockCipherFromBase64(encryptedString);
        log.info("\n{}", decrypt);
        Assertions.assertEquals(plainText, decrypt);
    }

    @Test
    void test_rsa_load_from_minio() throws Exception {
        String minioServerUrl = "https://dev-asli-minio.mypoc.id:9000";
        String username = "minioadmin";
        String password = "EwQER9n4ykWTf6B";

        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioServerUrl)
                .credentials(username, password)
                .build();

        RSAKeyMinioLoader adapter = new RSAKeyMinioLoader(minioClient, "face-sdk", "key/rsa/", "dev");
        RSACrypto rsaCrypto = new RSACrypto("RSA/ECB/PKCS1Padding", adapter);
        String plainText = "Test123#$#@4";
        String encryptedString = rsaCrypto.encryptToBase64(plainText);
        log.info("\n{}", encryptedString);
        String decrypt = rsaCrypto.decrypt(encryptedString);
        log.info("\n{}", decrypt);
        Assertions.assertEquals(plainText, decrypt);
    }

    public String encryptLargeMessage(Key publicKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = CryptoUtils.blockCipher(cipher, bytes, Cipher.ENCRYPT_MODE);
        char[] encryptedTransferable = Hex.encodeHex(encrypted);
        return new String(encryptedTransferable);
    }

}
