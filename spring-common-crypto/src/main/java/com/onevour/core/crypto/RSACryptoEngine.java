package com.onevour.core.crypto;


public interface RSACryptoEngine extends Crypto {

    String encryptBlockCipherToBase64(String message) throws Exception;

    String encryptBlockCipherToString(String message) throws Exception;

    byte[] encryptBlockCipher(String message) throws Exception;

    String decryptBlockCipher(byte[] bts) throws Exception;

    String decryptBlockCipherFromString(String messageEncrypted) throws Exception;

    String decryptBlockCipherFromBase64(String messageEncrypted) throws Exception;

}
