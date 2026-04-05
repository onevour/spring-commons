package com.onevour.core;

import com.onevour.core.crypto.key.AESKeyBase64Loader;
import com.onevour.core.crypto.key.AESKeyMinioLoader;
import com.onevour.core.crypto.aes.AESCrypto;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AESCryptoTest {

    @Test
    void test_aes_encrypt_decrypt_v3_cbc() throws Exception {
        String key = "";
        String iv = "";
        String encryptedString = "2UyxoFVKVvO8l7o6qqKj9O1gKh4Kba7VOJYYR+cxqHebVLxOdFBjt8UbOgv5n/sBYB8xKLCO0X4tlThgCn9k48MkPGN7k3QY3GT3GAI7skU+p4mzrcFLnR1c2DvRdmwZikLxHM11nty9yYPrGYKexQBx8ZLp+as3N9DdFD38x8Ow6vc8eeSw5irujpxnaM17rUczLQngELJEDZBiZI+GZxCIDnd0spJA43FDFFMh4mKXoFGsHGUqwvChwIuHBZ2/myuoZYW9MjqL7PVp/3SDx5gVe6fzMMvBrHgLZmqpB53RtIvst2QoPAT2F8fNL3Zi0a/QI/7q7CZXZDo5TMHtoml8hZWU0vJS1QhlVgDVDFMVQGxTEa9+9jc9DaU3yXEuB38OaVTxr8e6KkGwA+tCdu+pavDJdwCWdd3KhdG4Ebjklxz5mCA/9lhCw76DGT+F07uy0Lb0+a8YHT85tua7ctIcrezqfspFhFAnD6kRNFgffTiypJCH65GMOHDmptpZJzAFSenzAmMP350UOLH0LhyPTGC9M/XI3Spxo32W8AsIgD2AbDBKpJAjFNqh9dt1WVdUGKWftFXdHGSLSs39HMfOuj5VGufaSs2jtXWIrS+BtK7Acg5ljBQqFR+g5yPr/HY98eL1X9y0V6stZR0LCvt1bU0t/QkoH8m7sHSBWYk801PGK4bWYo+gii/NM5WZZZNm0tERpLv6zHeZV6ViIhNmqmgByjNKtDTWqifd+QzCyl+qcNDu+Ik+XP8U0m7CD9FAZMFi5HX0ApWtBbajxzm6eeR1mSlCywbJjZgqHl89xReFNrwEUju5sfOlkAvv43MBx5FG1P8dHPk1Rxn6ciEjCnO2X8B2E6ltX/u902e7fUSsOPmnT3lVvH/7Kq76+MSvl7OOjGQMikxHr1tQ9OLO9iQjr7sYZLFCIWJQGRBB8kfogd0leN3qEf/IuNFEzBSa4KScpycIBP/M2BxCjkhyEmJYyRvztTLjoEBeBZxpOJU1CAz9+5WUJwoiZ+0GwelX9uFnCGkFWI0TOrCX0z9TfVJ+KsF38CEhftOw0o8IoJLIOseBJ53HdHkgg+BuNQArMe+TiSpgIhEwK6cwaGYEhWN20PbQxCOJ2xnZQIJ38wGvJi4IHaIy4FPVWQ+0r4xRY3R6dtCCRY8+agvQ/84TVg0FB2/k1qGSmFDh8tNv6V6XZpOGp2NjHtwlEvAs";
        AESCrypto aesCrypto = new AESCrypto("AES/CBC/PKCS5Padding", new AESKeyBase64Loader(key, iv));
        // decrypt
        String decrypt = aesCrypto.decrypt(encryptedString);
        log.info("\n{}", decrypt);
        // encrypt
        String plainText = "Test123#$#@4";
        byte[] encrypted = aesCrypto.encrypt(plainText);
        encryptedString = Base64.getEncoder().encodeToString(encrypted);
        log.info("\n{}", encryptedString);
        // decrypt
        decrypt = aesCrypto.decrypt(encryptedString);
        log.info("\n{}", decrypt);
        Assertions.assertEquals(plainText, decrypt);
    }

    @Test
    void test_aes_encrypt_decrypt_v3_gcm() throws Exception {
        String key = "";
        AESCrypto aesCrypto = new AESCrypto("AES/GCM/NoPadding", new AESKeyBase64Loader(key));
        // encrypt
        String plainText = "Test123#$#@4";
        byte[] encrypted = aesCrypto.encrypt(plainText);
        String encryptedString = Base64.getEncoder().encodeToString(encrypted);
        log.info("\n{}", encryptedString);
        // decrypt
        String decrypt = aesCrypto.decrypt(encryptedString);
        log.info("\n{}", decrypt);
        Assertions.assertEquals(plainText, decrypt);
    }

    @Test
    void test_aes_load_from_minio() throws Exception {
        String minioServerUrl = "https://minio:9000";
        String username = "";
        String password = "";

        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioServerUrl)
                .credentials(username, password)
                .build();

        AESKeyMinioLoader adapter = new AESKeyMinioLoader(minioClient, "bucket", "key/aes/", "dev");
        AESCrypto rsaCrypto = new AESCrypto("AES/GCM/NoPadding", adapter);
        String plainText = "Test123#$#@4";
        String encryptedString = rsaCrypto.encryptToBase64(plainText);
        log.info("\n{}", encryptedString);
        String decrypt = rsaCrypto.decrypt(encryptedString);
        log.info("\n{}", decrypt);
        Assertions.assertEquals(plainText, decrypt);
    }

}
