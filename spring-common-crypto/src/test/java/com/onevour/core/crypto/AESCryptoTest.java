package com.onevour.core.crypto;

import com.onevour.core.crypto.aes.AESCrypto;
import com.onevour.core.crypto.key.AESKeyBase64Loader;
import com.onevour.core.crypto.key.AESKeyFileLoader;
import com.onevour.core.crypto.key.AESKeyStringLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.KeyGenerator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/** AES offline: kunci dibuat saat test, tidak butuh MinIO maupun kunci rahasia. */
class AESCryptoTest {

    static final String PLAIN = "Test123#$#@4 dengan huruf non-ASCII: é ü 日本";
    static String KEY_B64;   // 256 bit
    static String IV_B64;    // 16 byte

    @BeforeAll
    static void generateKey() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);
        KEY_B64 = Base64.getEncoder().encodeToString(generator.generateKey().getEncoded());
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IV_B64 = Base64.getEncoder().encodeToString(iv);
    }

    @Test
    void cbcRoundTripWithBase64Loader() throws Exception {
        AESCrypto aes = new AESCrypto("AES/CBC/PKCS5Padding", new AESKeyBase64Loader(KEY_B64, IV_B64));
        String cipherText = aes.encryptToBase64(PLAIN);
        assertNotEquals(PLAIN, cipherText);
        assertEquals(PLAIN, aes.decrypt(cipherText));
        assertEquals(PLAIN, aes.decrypt(aes.encrypt(PLAIN)));
        // CBC dengan IV tetap: deterministik
        assertEquals(cipherText, aes.encryptToBase64(PLAIN));
    }

    @Test
    void cbcWithoutIvEncryptsWithUnrecoverableRandomIv() throws Exception {
        // JCE membuat IV acak saat enkripsi tanpa IV, dan IV itu tidak tersimpan di mana pun:
        // enkripsi "berhasil" tapi hasilnya tidak bisa didekripsi. Selalu beri IV untuk CBC.
        AESCrypto aes = new AESCrypto("AES/CBC/PKCS5Padding", new AESKeyBase64Loader(KEY_B64));
        String cipherText = aes.encryptToBase64(PLAIN);
        assertThrows(Exception.class, () -> aes.decrypt(cipherText));
    }

    @Test
    void gcmRoundTripAndRandomNonce() throws Exception {
        AESCrypto aes = new AESCrypto("AES/GCM/NoPadding", new AESKeyBase64Loader(KEY_B64));
        String c1 = aes.encryptToBase64(PLAIN);
        String c2 = aes.encryptToBase64(PLAIN);
        assertNotEquals(c1, c2, "nonce acak: ciphertext harus berbeda tiap panggilan");
        assertEquals(PLAIN, aes.decrypt(c1));
        assertEquals(PLAIN, aes.decrypt(c2));
    }

    @Test
    void gcmDetectsTampering() throws Exception {
        AESCrypto aes = new AESCrypto("AES/GCM/NoPadding", new AESKeyBase64Loader(KEY_B64));
        byte[] cipher = aes.encrypt(PLAIN);
        cipher[cipher.length - 1] ^= 0x01;
        assertThrows(AEADBadTagException.class, () -> aes.decrypt(cipher));
    }

    @Test
    void wrongKeyCannotDecryptCbc() throws Exception {
        AESCrypto good = new AESCrypto("AES/CBC/PKCS5Padding", new AESKeyBase64Loader(KEY_B64, IV_B64));
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);
        String otherKey = Base64.getEncoder().encodeToString(generator.generateKey().getEncoded());
        AESCrypto bad = new AESCrypto("AES/CBC/PKCS5Padding", new AESKeyBase64Loader(otherKey, IV_B64));
        String cipherText = good.encryptToBase64(PLAIN);
        assertThrows(BadPaddingException.class, () -> bad.decrypt(cipherText));
    }

    @Test
    void stringLoaderUsesRawUtf8Bytes() throws Exception {
        String key16 = "0123456789abcdef"; // 16 byte = AES-128
        AESCrypto aes = new AESCrypto("AES/GCM/NoPadding", new AESKeyStringLoader(key16));
        assertEquals(PLAIN, aes.decrypt(aes.encryptToBase64(PLAIN)));
        assertEquals(Base64.getEncoder().encodeToString(key16.getBytes()), aes.keyAdapter().encryptKeyBase64());
        assertEquals(aes.keyAdapter().encryptKeyBase64(), aes.keyAdapter().decryptKeyBase64());
    }

    @Test
    void fileLoaderReadsRawKeyBytes(@TempDir Path dir) throws Exception {
        byte[] key = Base64.getDecoder().decode(KEY_B64);
        Files.write(dir.resolve("aes.key"), key);
        AESCrypto aes = new AESCrypto("AES/CBC/PKCS5Padding", new AESKeyFileLoader(dir.toString(), "aes.key", IV_B64));
        assertEquals(PLAIN, aes.decrypt(aes.encryptToBase64(PLAIN)));
    }

    @Test
    void fileLoaderRejectsWrongKeyLength(@TempDir Path dir) throws Exception {
        Files.write(dir.resolve("bad.key"), new byte[10]);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new AESCrypto("AES/GCM/NoPadding", new AESKeyFileLoader(dir.toString(), "bad.key")));
        assertTrue(ex.getMessage().contains("10"));
    }

    @Test
    void unsupportedTransformationRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AESCrypto("AES/ECB/PKCS5Padding", new AESKeyBase64Loader(KEY_B64)));
    }
}
