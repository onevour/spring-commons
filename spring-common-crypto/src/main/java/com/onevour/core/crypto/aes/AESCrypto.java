package com.onevour.core.crypto.aes;

import com.onevour.core.crypto.AESCryptoEngine;
import com.onevour.core.crypto.Crypto;
import com.onevour.core.crypto.AESCryptoKeyLoader;
import com.onevour.core.crypto.CryptoKeyAdapter;

import java.util.Objects;

public class AESCrypto implements Crypto {

    private AESCryptoEngine engine;

    public AESCrypto(String transformation, AESCryptoKeyLoader keyAdapter) throws Exception {
        keyAdapter.loadKey();
        if (transformation.contains("CBC")) {
            engine = new AESCryptoEngineCBC(transformation, keyAdapter);
        }
        if (transformation.contains("GCM")) {
            engine = new AESCryptoEngineGCM(transformation, keyAdapter);
        }
        if (Objects.isNull(engine)) {
            throw new IllegalArgumentException("Unsupported mode " + transformation);
        }
    }

    @Override
    public CryptoKeyAdapter keyAdapter() {
        return engine.keyAdapter();
    }

    @Override
    public byte[] encrypt(String plainText) throws Exception {
        return engine.encrypt(plainText);
    }

    @Override
    public String encryptToBase64(String plainText) throws Exception {
        return engine.encryptToBase64(plainText);
    }

    @Override
    public String decrypt(String encryptedData) throws Exception {
        return engine.decrypt(encryptedData);
    }

    @Override
    public String decrypt(byte[] encryptedData) throws Exception {
        return engine.decrypt(encryptedData);
    }

}
