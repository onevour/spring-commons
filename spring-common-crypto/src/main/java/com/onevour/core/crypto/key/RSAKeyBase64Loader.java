package com.onevour.core.crypto.key;

import com.onevour.core.crypto.RSACryptoKeyLoader;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

public class RSAKeyBase64Loader extends RSACryptoKeyLoader {

    private final String base64PublicKey;
    private final String base64PrivateKey;

    public RSAKeyBase64Loader(String base64PublicKey, String base64PrivateKey) {
        this.base64PrivateKey = base64PrivateKey;
        this.base64PublicKey = base64PublicKey;
    }

    @Override
    public void loadKey() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = null;
            PrivateKey privateKey = null;
            // public
            if (Objects.nonNull(base64PublicKey)) {
                X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(base64PublicKey));
                publicKey = keyFactory.generatePublic(keySpecPublic);
            }
            // private
            if (Objects.nonNull(base64PrivateKey)) {
                PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(base64PrivateKey));
                privateKey = keyFactory.generatePrivate(keySpecPrivate);
            }
            // keypair
            keyPair = new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}
