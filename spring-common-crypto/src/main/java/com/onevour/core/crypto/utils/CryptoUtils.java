package com.onevour.core.crypto.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class CryptoUtils {

    private CryptoUtils() {

    }

    /** blok plaintext per operasi enkripsi; cukup untuk semua ukuran kunci RSA >= 1024 bit dengan PKCS#1 */
    public static final int ENCRYPT_BLOCK_SIZE = 100;

    /** ukuran kunci default (byte) = RSA 1024 bit, perilaku lama */
    public static final int DEFAULT_KEY_SIZE_BYTES = 128;

    /**
     * @deprecated blok dekripsi dianggap 128 byte (RSA 1024 bit). Untuk kunci lain pakai
     * {@link #blockCipher(Cipher, byte[], int, int)} dengan ukuran kunci dalam byte.
     */
    @Deprecated
    public static byte[] blockCipher(Cipher cipher, byte[] bytes, int mode) throws IllegalBlockSizeException, BadPaddingException {
        return blockCipher(cipher, bytes, mode, DEFAULT_KEY_SIZE_BYTES);
    }

    /**
     * Enkripsi/dekripsi pesan yang lebih panjang dari ukuran kunci RSA, blok per blok.
     * Enkripsi memotong plaintext per {@link #ENCRYPT_BLOCK_SIZE} byte; dekripsi memotong
     * ciphertext per {@code keySizeBytes} (setiap blok hasil RSA selalu sebesar kuncinya).
     *
     * @param keySizeBytes ukuran kunci RSA dalam byte (modulus bitLength / 8), mis. 256 untuk 2048 bit
     */
    public static byte[] blockCipher(Cipher cipher, byte[] bytes, int mode, int keySizeBytes) throws IllegalBlockSizeException, BadPaddingException {
        // string initialize 2 buffers.
        // scrambled will hold intermediate results
        byte[] scrambled = new byte[0];

        // toReturn will hold the total result
        byte[] toReturn = new byte[0];
        // if we encrypt we use 100 byte long blocks. Decryption requires key-size long blocks (because of RSA)
        int length = (mode == Cipher.ENCRYPT_MODE) ? ENCRYPT_BLOCK_SIZE : keySizeBytes;

        // another buffer. this one will hold the bytes that have to be modified in this step
        byte[] buffer = new byte[length];
        for (int i = 0; i < bytes.length; i++) {

            // if we filled our buffer array we have our block ready for de- or encryption
            if ((i > 0) && (i % length == 0)) {
                //execute the operation
                scrambled = cipher.doFinal(buffer);
                if (mode == 2) {
//                    log.debug("scramble start mode {}", Cipher.ENCRYPT_MODE);
//                    log.debug("scramble {}", new String(scrambled));
//                    log.debug("scramble end");
                }
                // add the result to our total result.
                toReturn = append(toReturn, scrambled);
                // here we calculate the length of the next buffer required
                int newlength = length;

                // if newlength would be longer than remaining bytes in the bytes array we shorten it.
                if (i + length > bytes.length) {
                    newlength = bytes.length - i;
                }
                // clean the buffer array
                buffer = new byte[newlength];
            }
            // copy byte into our buffer.
            buffer[i % length] = bytes[i];
        }

        // this step is needed if we had a trailing buffer. should only happen when encrypting.
        // example: we encrypt 110 bytes. 100 bytes per run means we "forgot" the last 10 bytes. they are in the buffer array
        scrambled = cipher.doFinal(buffer);
        if (mode == 2) {
//            log.debug("scramble end mode {}", Cipher.ENCRYPT_MODE);
//            log.debug("scramble end {}", new String(scrambled));
//            log.debug("scramble end last");
        }
        // final step before we can return the modified data.
        toReturn = append(toReturn, scrambled);

        return toReturn;
    }

    private static byte[] append(byte[] prefix, byte[] suffix) {
        byte[] toReturn = new byte[prefix.length + suffix.length];
        for (int i = 0; i < prefix.length; i++) {
            toReturn[i] = prefix[i];
        }
        for (int i = 0; i < suffix.length; i++) {
            toReturn[i + prefix.length] = suffix[i];
        }
        return toReturn;
    }

}
