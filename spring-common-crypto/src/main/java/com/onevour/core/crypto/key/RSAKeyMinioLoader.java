package com.onevour.core.crypto.key;

import com.onevour.core.crypto.RSACryptoKeyLoader;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

@Slf4j
public class RSAKeyMinioLoader extends RSACryptoKeyLoader {

    private final MinioClient minioClient;
    private final String bucket;
    private final String path;
    private final String keyId;

    public RSAKeyMinioLoader(MinioClient minioClient, String bucket, String path, String keyId) {
        this.minioClient = minioClient;
        this.bucket = bucket;
        this.path = path;
        this.keyId = keyId;
    }

    @Override
    public void loadKey() {
        try {
            String base64PublicKey = loadFileAsBase64("public.key");
            String base64PrivateKey = loadFileAsBase64("private.key");
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = null;
            PrivateKey privateKey = null;
            // public
            if (Objects.nonNull(base64PublicKey)) {
                X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(base64PublicKey));
                publicKey = keyFactory.generatePublic(keySpecPublic);
            }
            // private
            if (Objects.nonNull(base64PrivateKey)) {
                PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(base64PrivateKey));
                privateKey = keyFactory.generatePrivate(keySpecPrivate);
            }
            // keypair
            keyPair = new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String loadFileAsBase64(String fileName) {
        // load from minio
        try (InputStream is = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(path + keyId + "/" + fileName)
                        .build())) {

            return Base64.getEncoder().encodeToString(is.readAllBytes());
        } catch (Exception e) {
            log.warn("rsa key not found: {}/{}", keyId, fileName);
            return null;
        }
    }

}
