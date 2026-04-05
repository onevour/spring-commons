package com.onevour.core.crypto.aes;

import com.onevour.core.crypto.AESCryptoEngine;
import com.onevour.core.crypto.AESCryptoKeyLoader;
import com.onevour.core.crypto.Crypto;
import com.onevour.core.crypto.CryptoKeyAdapter;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

 class AESCryptoEngineCBC implements AESCryptoEngine, Crypto {

    private final String transformation;

    private final AESCryptoKeyLoader keyAdapter;

    public AESCryptoEngineCBC(String transformation, CryptoKeyAdapter keyAdapter) {
        this.transformation = transformation;
        this.keyAdapter = (AESCryptoKeyLoader) keyAdapter;
    }

     @Override
     public CryptoKeyAdapter keyAdapter() {
         return keyAdapter;
     }

     @Override
    public byte[] encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, keyAdapter.decryptKey(), keyAdapter.getAlgorithmParameterSpec());
        return cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    }

     @Override
     public String encryptToBase64(String plainText) throws Exception {
         return Base64.getEncoder().encodeToString(encrypt(plainText));
     }

     @Override
    public String decrypt(String encryptedData) throws Exception {
        byte[] cipherText = Base64.getDecoder().decode(encryptedData);
        return decrypt(cipherText);
    }

    @Override
    public String decrypt(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, keyAdapter.decryptKey(), keyAdapter.getAlgorithmParameterSpec());
        byte[] decryptedBytes = cipher.doFinal(encryptedData);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}
