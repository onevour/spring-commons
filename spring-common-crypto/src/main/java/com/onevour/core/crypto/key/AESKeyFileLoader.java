package com.onevour.core.crypto.key;

import com.onevour.core.crypto.AESCryptoKeyLoader;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Objects;

public class AESKeyFileLoader extends AESCryptoKeyLoader {

    private final String path;

    private final String fileName;

    private AlgorithmParameterSpec algorithmParameterSpec;

    public AESKeyFileLoader(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    public AESKeyFileLoader(String path, String fileName, String iv) {
        this.path = path;
        this.fileName = fileName;
        if (Objects.nonNull(iv)) {
            this.algorithmParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
        }
    }

    @Override
    public AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return algorithmParameterSpec;
    }

    @Override
    public void loadKey() throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(path, fileName));
        boolean isValidAES128 = keyBytes.length == 16;
        boolean isValidAES192 = keyBytes.length == 24;
        boolean isValidAES256 = keyBytes.length == 32;
        if (isValidAES128 || isValidAES192 || isValidAES256) {
            secretKey = new SecretKeySpec(keyBytes, "AES");
        }
        throw new IllegalArgumentException("Invalid AES key length: " + keyBytes.length);
    }


}
