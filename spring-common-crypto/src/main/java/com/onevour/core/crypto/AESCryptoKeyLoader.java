package com.onevour.core.crypto;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

public abstract class AESCryptoKeyLoader extends CryptoKeyAdapter {

    protected SecretKey secretKey;

    public abstract AlgorithmParameterSpec getAlgorithmParameterSpec();

    @Override
    public Key encryptKey() {
        return secretKey;
    }

    @Override
    public Key decryptKey() {
        return secretKey;
    }

    @Override
    public String encryptKeyBase64() {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    @Override
    public String decryptKeyBase64() {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

}
