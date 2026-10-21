package com.onevour.core.crypto;

import com.onevour.core.crypto.key.RSAKeyBase64Loader;
import com.onevour.core.crypto.rsa.RSACrypto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/** RSA offline: keypair dibuat saat test; keypair DER di resources dipakai untuk uji loader. */
class RSACryptoTest {

    static final String PLAIN = "Test123#$#@4";
    static String PUBLIC_B64;
    static String PRIVATE_B64;

    @BeforeAll
    static void generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        PUBLIC_B64 = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());   // X.509
        PRIVATE_B64 = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()); // PKCS#8
    }

    private static String resourceToBase64(String path) throws Exception {
        try (InputStream is = RSACryptoTest.class.getResourceAsStream(path)) {
            assertNotNull(is, path);
            return Base64.getEncoder().encodeToString(is.readAllBytes());
        }
    }

    @Test
    void roundTripWithGeneratedKeyPair() throws Exception {
        RSACrypto rsa = new RSACrypto("RSA/ECB/PKCS1Padding", new RSAKeyBase64Loader(PUBLIC_B64, PRIVATE_B64));
        String cipherText = rsa.encryptToBase64(PLAIN);
        assertEquals(PLAIN, rsa.decrypt(cipherText));
        assertEquals(PLAIN, rsa.decrypt(rsa.encrypt(PLAIN)));
        assertEquals(PUBLIC_B64, rsa.keyAdapter().encryptKeyBase64());
        assertEquals(PRIVATE_B64, rsa.keyAdapter().decryptKeyBase64());
    }

    @Test
    void loaderArgumentOrderIsPublicThenPrivate() {
        // urutan terbalik harus gagal saat loadKey, bukan diam-diam menghasilkan kunci salah
        assertThrows(IllegalArgumentException.class,
                () -> new RSACrypto("RSA/ECB/PKCS1Padding", new RSAKeyBase64Loader(PRIVATE_B64, PUBLIC_B64)));
    }

    @Test
    void publicKeyOnlyCanEncryptButNotDecrypt() throws Exception {
        RSACrypto rsa = new RSACrypto("RSA/ECB/PKCS1Padding", new RSAKeyBase64Loader(PUBLIC_B64, null));
        String cipherText = rsa.encryptToBase64(PLAIN);
        assertNotNull(cipherText);
        assertThrows(Exception.class, () -> rsa.decrypt(cipherText));
    }

    @Test
    void blockCipherHandlesMessagesLongerThanKeySize() throws Exception {
        RSACrypto rsa = new RSACrypto("RSA/ECB/PKCS1Padding", new RSAKeyBase64Loader(PUBLIC_B64, PRIVATE_B64));
        String longMessage = String.join("", Collections.nCopies(40, "{\"k\":\"0123456789\"}")); // > 245 byte
        assertThrows(Exception.class, () -> rsa.encrypt(longMessage), "RSA biasa harus gagal untuk pesan panjang");

        String hex = rsa.encryptBlockCipherToString(longMessage);
        assertEquals(longMessage, rsa.decryptBlockCipherFromString(hex));

        String base64 = rsa.encryptBlockCipherToBase64(longMessage);
        assertEquals(longMessage, rsa.decryptBlockCipherFromBase64(base64));

        byte[] raw = rsa.encryptBlockCipher(longMessage);
        assertEquals(longMessage, rsa.decryptBlockCipher(raw));
    }

    @Test
    void loadsDerKeyPairFromResources() throws Exception {
        String publicKey = resourceToBase64("/keypair/public.key");
        String privateKey = resourceToBase64("/keypair/private.key");
        RSACrypto rsa = new RSACrypto("RSA/ECB/PKCS1Padding", new RSAKeyBase64Loader(publicKey, privateKey));
        assertEquals(PLAIN, rsa.decrypt(rsa.encryptToBase64(PLAIN)));
    }

    @Test
    void blockCipherStillWorksWith1024BitKeyAndLegacyUtility() throws Exception {
        // kunci 1024 bit di resources = perilaku lama (blok dekripsi 128 byte) harus tetap kompatibel
        RSACrypto rsa = new RSACrypto("RSA/ECB/PKCS1Padding",
                new RSAKeyBase64Loader(resourceToBase64("/keypair/public.key"), resourceToBase64("/keypair/private.key")));
        String longMessage = String.join("", Collections.nCopies(20, "{\"k\":\"0123456789\"}"));
        byte[] encrypted = rsa.encryptBlockCipher(longMessage);

        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, rsa.keyAdapter().decryptKey());
        @SuppressWarnings("deprecation")
        byte[] legacy = com.onevour.core.crypto.utils.CryptoUtils.blockCipher(cipher, encrypted, javax.crypto.Cipher.DECRYPT_MODE);
        assertEquals(longMessage, new String(legacy, java.nio.charset.StandardCharsets.UTF_8));
        assertEquals(longMessage, rsa.decryptBlockCipher(encrypted));
    }
}
