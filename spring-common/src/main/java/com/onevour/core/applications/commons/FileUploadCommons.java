package com.onevour.core.applications.commons;



import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Base64;

public class FileUploadCommons {

    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        is.close();
        os.close();
    }

    public static byte[] base64ToByte(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    public static String byteToBase64(byte[] bytes ) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String pathToBase64(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        byte[] fileContent = IOUtils.toByteArray(url.openStream());
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public static byte[] pathToByte(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        return  IOUtils.toByteArray(url.openStream());
    }

}
