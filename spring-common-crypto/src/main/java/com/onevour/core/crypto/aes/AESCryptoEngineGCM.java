package com.onevour.core.crypto.aes;

import com.onevour.core.crypto.AESCryptoEngine;
import com.onevour.core.crypto.AESCryptoKeyLoader;
import com.onevour.core.crypto.CryptoKeyAdapter;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

class AESCryptoEngineGCM implements AESCryptoEngine {

    private final int GCM_NONCE_LENGTH = 12; // Nonce size in bytes

    private final int GCM_TAG_LENGTH = 16; // Tag size in bytes

    private final SecureRandom random = new SecureRandom();

    private final String transformation;

    private final AESCryptoKeyLoader keyAdapter;

    public AESCryptoEngineGCM(String transformation, CryptoKeyAdapter keyAdapter) {
        this.transformation = transformation;
        this.keyAdapter = (AESCryptoKeyLoader) keyAdapter;
    }

    @Override
    public CryptoKeyAdapter keyAdapter() {
        return keyAdapter;
    }

    @Override
    public byte[] encrypt(String plainText) throws Exception {
        // gcm
        byte[] nonce = new byte[GCM_NONCE_LENGTH];
        random.nextBytes(nonce);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, keyAdapter.decryptKey(), gcmSpec);
        byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        // combine nonce and encrypted data
        byte[] result = new byte[nonce.length + encryptedData.length];
        System.arraycopy(nonce, 0, result, 0, nonce.length);
        System.arraycopy(encryptedData, 0, result, nonce.length, encryptedData.length);
        return result;
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
        byte[] nonce = new byte[GCM_NONCE_LENGTH];
        System.arraycopy(encryptedData, 0, nonce, 0, nonce.length);
        // exclude nonce
        byte[] cipherText = new byte[encryptedData.length - GCM_NONCE_LENGTH];
        System.arraycopy(encryptedData, GCM_NONCE_LENGTH, cipherText, 0, cipherText.length);
        // Create the cipher instance and initialize it
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, keyAdapter.decryptKey(), gcmSpec);
        // Perform decryption
        byte[] decryptedData = cipher.doFinal(cipherText);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

}