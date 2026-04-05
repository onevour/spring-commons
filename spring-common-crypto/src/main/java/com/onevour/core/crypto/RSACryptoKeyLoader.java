package com.onevour.core.crypto;

import java.security.Key;
import java.security.KeyPair;
import java.util.Base64;

public abstract class RSACryptoKeyLoader extends CryptoKeyAdapter {

    protected KeyPair keyPair;

    @Override
    public Key encryptKey() {
        return keyPair.getPublic();
    }

    @Override
    public Key decryptKey() {
        return keyPair.getPrivate();
    }

    @Override
    public String encryptKeyBase64() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    @Override
    public String decryptKeyBase64() {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    protected byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }


}
