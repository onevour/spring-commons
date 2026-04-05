package com.onevour.core.crypto.rsa;

import com.onevour.core.crypto.Crypto;
import com.onevour.core.crypto.CryptoKeyAdapter;
import com.onevour.core.crypto.RSACryptoEngine;
import com.onevour.core.crypto.RSACryptoKeyLoader;
import com.onevour.core.crypto.utils.CryptoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class RSACrypto implements RSACryptoEngine, Crypto {

    private final String transformation;

    private final RSACryptoKeyLoader cryptoKeyAdapter;

    public RSACrypto(String transformation, RSACryptoKeyLoader cryptoKeyAdapter) throws Exception {
        this.transformation = transformation;
        this.cryptoKeyAdapter = cryptoKeyAdapter;
        cryptoKeyAdapter.loadKey();
    }

    @Override
    public CryptoKeyAdapter keyAdapter() {
        return cryptoKeyAdapter;
    }

    @Override
    public byte[] encrypt(String plainText) throws Exception {
        byte[] messageToBytes = plainText.getBytes();
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, cryptoKeyAdapter.encryptKey());
        return cipher.doFinal(messageToBytes);
    }

    @Override
    public String encryptToBase64(String plainText) throws Exception {
        return Base64.getEncoder().encodeToString(encrypt(plainText));
    }

    @Override
    public byte[] encryptBlockCipher(String message) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, cryptoKeyAdapter.encryptKey());
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        return CryptoUtils.blockCipher(cipher, bytes, Cipher.ENCRYPT_MODE);
    }


    @Override
    public String encryptBlockCipherToBase64(String message) throws Exception {
        return Base64.getEncoder().encodeToString(encryptBlockCipher(message));
    }

    @Override
    public String encryptBlockCipherToString(String message) throws Exception {
        byte[] encrypted = encryptBlockCipher(message);
        char[] encryptedTransferable = Hex.encodeHex(encrypted);
        return new String(encryptedTransferable);
    }

    @Override
    public String decrypt(String encryptedData) throws Exception {
        return decrypt(Base64.getDecoder().decode(encryptedData));
    }

    @Override
    public String decrypt(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, cryptoKeyAdapter.decryptKey());
        byte[] decryptedMessage = cipher.doFinal(encryptedData);
        return new String(decryptedMessage, StandardCharsets.UTF_8);
    }

    @Override
    public String decryptBlockCipher(byte[] bts) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, cryptoKeyAdapter.decryptKey());
        byte[] decrypted = CryptoUtils.blockCipher(cipher, bts, Cipher.DECRYPT_MODE);
        return new String(decrypted, StandardCharsets.UTF_8).trim();
    }

    @Override
    public String decryptBlockCipherFromString(String messageEncrypted) throws Exception {
        byte[] bts = Hex.decodeHex(messageEncrypted.toCharArray());
        return decryptBlockCipher(bts);
    }

    @Override
    public String decryptBlockCipherFromBase64(String messageEncrypted) throws Exception {
        byte[] bts = Base64.getDecoder().decode(messageEncrypted);
        return decryptBlockCipher(bts);
    }

}