package com.onevour.core.crypto;

public interface Crypto {

    CryptoKeyAdapter keyAdapter();

    byte[] encrypt(String plainText) throws Exception;

    String encryptToBase64(String plainText) throws Exception;

    String decrypt(String encryptedData) throws Exception;

    String decrypt(byte[] encryptedData) throws Exception;

}