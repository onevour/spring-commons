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
        String publicKey = "";
        RSACryptoKeyLoader adapter = new RSAKeyBase64Loader(privateKey, publicKey);
        RSACrypto rsaCrypto = new RSACrypto("RSA/ECB/PKCS1Padding", adapter);
        String plainText = "{\"pub_key\":\"\"}";
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
        String minioServerUrl = "https://minio.id:9000";
        String username = "";
        String password = "";

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
