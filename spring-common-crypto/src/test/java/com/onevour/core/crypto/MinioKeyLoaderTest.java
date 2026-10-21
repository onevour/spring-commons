package com.onevour.core.crypto;

import com.onevour.core.crypto.aes.AESCrypto;
import com.onevour.core.crypto.key.AESKeyMinioLoader;
import com.onevour.core.crypto.key.RSAKeyMinioLoader;
import com.onevour.core.crypto.rsa.RSACrypto;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import okhttp3.Headers;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import java.io.ByteArrayInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Loader MinIO dengan MinioClient yang di-mock: memastikan path objek dan format yang dibaca. */
class MinioKeyLoaderTest {

    /** bucket palsu: object name -> isi */
    private MinioClient minioWith(Map<String, byte[]> objects) throws Exception {
        MinioClient client = mock(MinioClient.class);
        when(client.getObject(any(GetObjectArgs.class))).thenAnswer(inv -> {
            GetObjectArgs args = inv.getArgument(0);
            byte[] data = objects.get(args.object());
            if (data == null) throw new IllegalStateException("object not found: " + args.object());
            return new GetObjectResponse(Headers.of(), args.bucket(), null, args.object(), new ByteArrayInputStream(data));
        });
        return client;
    }

    @Test
    void aesLoaderReadsSecretAndVectorFromKeyFolder() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);
        byte[] key = generator.generateKey().getEncoded();
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        Map<String, byte[]> objects = new HashMap<>();
        objects.put("key/aes/prod/secret.key", key);
        objects.put("key/aes/prod/vector.key", iv);

        AESKeyMinioLoader loader = new AESKeyMinioLoader(minioWith(objects), "bucket", "key/aes/", "prod");
        AESCrypto aes = new AESCrypto("AES/CBC/PKCS5Padding", loader);
        assertEquals("hello", aes.decrypt(aes.encryptToBase64("hello")));
        assertNotNull(loader.getAlgorithmParameterSpec());
    }

    @Test
    void aesLoaderWithoutVectorStillWorksForGcm() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        Map<String, byte[]> objects = new HashMap<>();
        objects.put("key/aes/dev/secret.key", generator.generateKey().getEncoded());

        AESKeyMinioLoader loader = new AESKeyMinioLoader(minioWith(objects), "bucket", "key/aes/", "dev");
        AESCrypto aes = new AESCrypto("AES/GCM/NoPadding", loader);
        assertNull(loader.getAlgorithmParameterSpec());
        assertEquals("hello", aes.decrypt(aes.encryptToBase64("hello")));
    }

    @Test
    void rsaLoaderReadsPublicAndPrivateDer() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        Map<String, byte[]> objects = new HashMap<>();
        objects.put("key/rsa/prod/public.key", pair.getPublic().getEncoded());
        objects.put("key/rsa/prod/private.key", pair.getPrivate().getEncoded());

        RSACrypto rsa = new RSACrypto("RSA/ECB/PKCS1Padding", new RSAKeyMinioLoader(minioWith(objects), "bucket", "key/rsa/", "prod"));
        assertEquals("hello", rsa.decrypt(rsa.encryptToBase64("hello")));
    }

    @Test
    void rsaLoaderWithPublicOnlyCanEncrypt() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        Map<String, byte[]> objects = new HashMap<>();
        objects.put("key/rsa/client/public.key", generator.generateKeyPair().getPublic().getEncoded());

        RSACrypto rsa = new RSACrypto("RSA/ECB/PKCS1Padding", new RSAKeyMinioLoader(minioWith(objects), "bucket", "key/rsa/", "client"));
        assertNotNull(rsa.encryptToBase64("hello"));
        assertThrows(Exception.class, () -> rsa.decrypt(rsa.encryptToBase64("hello")));
    }
}
