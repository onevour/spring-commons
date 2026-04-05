package com.onevour.core.crypto;

import java.security.Key;

public abstract class CryptoKeyAdapter {

    public abstract void loadKey() throws Exception;

    public abstract Key encryptKey();

    public abstract String encryptKeyBase64();

    public abstract Key decryptKey();

    public abstract String decryptKeyBase64();

}
