package com.onevour.core.crypto.key;

import com.onevour.core.crypto.AESCryptoKeyLoader;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Objects;

@Slf4j
public class AESKeyBase64Loader extends AESCryptoKeyLoader {

    private final String base64Key;

    private AlgorithmParameterSpec algorithmParameterSpec;

    public AESKeyBase64Loader(String base64Key) {
        this.base64Key = base64Key;
    }

    public AESKeyBase64Loader(String base64Key, String iv) {
        this.base64Key = base64Key;
        if (Objects.nonNull(iv)) {
            this.algorithmParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
        }
    }

    @Override
    public AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return algorithmParameterSpec;
    }

    @Override
    public void loadKey() {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        secretKey = new SecretKeySpec(keyBytes, "AES");
    }


}
