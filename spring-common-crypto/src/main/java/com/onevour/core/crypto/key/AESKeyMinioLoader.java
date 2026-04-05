package com.onevour.core.crypto.key;

import com.onevour.core.crypto.AESCryptoKeyLoader;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Objects;

@Slf4j
public class AESKeyMinioLoader extends AESCryptoKeyLoader {

    private final MinioClient minioClient;
    private final String bucket;
    private final String path;
    private final String keyId;

    private AlgorithmParameterSpec algorithmParameterSpec;

    public AESKeyMinioLoader(MinioClient minioClient, String bucket, String path, String keyId) {
        this.minioClient = minioClient;
        this.bucket = bucket;
        this.path = path;
        this.keyId = keyId;
    }

    @Override
    public AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return algorithmParameterSpec;
    }

    @Override
    public void loadKey() {
        // vector
        String base64IV = loadFileAsBase64("vector.key");
        if (Objects.nonNull(base64IV)) {
            this.algorithmParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(base64IV));
        }
        // secret key
        String base64Key = loadFileAsBase64("secret.key");
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        secretKey = new SecretKeySpec(keyBytes, "AES");
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
