package com.onevour.core.crypto.key;

import com.onevour.core.crypto.AESCryptoKeyLoader;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Objects;

public class AESKeyStringLoader extends AESCryptoKeyLoader {

    private final String stringKey;

    private AlgorithmParameterSpec algorithmParameterSpec;

    public AESKeyStringLoader(String stringKey) {
        this.stringKey = stringKey;
    }

    public AESKeyStringLoader(String stringKey, String iv) {
        this.stringKey = stringKey;
        if (Objects.nonNull(iv)) {
            this.algorithmParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
        }

    }

    @Override
    public AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return algorithmParameterSpec;
    }

    @Override
    public void loadKey() {
        byte[] keyBytes = stringKey.getBytes(StandardCharsets.UTF_8);
        secretKey = new SecretKeySpec(keyBytes, "AES");
    }
}
