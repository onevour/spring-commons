package com.onevour.core.applications.commons;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class Base64Commons {

    public static String encode(File fileName) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(fileName);
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public static byte[] decode(String image) {
        return Base64.getDecoder().decode(image.getBytes(StandardCharsets.UTF_8));
    }

    public static String encode(String fileName) throws IOException {
        byte[] encoded = Base64.getEncoder().encode(FileUtils.readFileToByteArray(new File(fileName)));
        return new String(encoded, StandardCharsets.US_ASCII);
    }

    public static String cleanMime(byte[] base64) {
        if (Objects.isNull(base64)) return null;
        return cleanMime(new String(base64, StandardCharsets.UTF_8));
    }

    public static String cleanMime(String base64) {
        if (Objects.isNull(base64)) return null;
        return StringUtils.replace(base64, "data:application/octet-stream;base64,", "");
    }

    public static byte[] cleanMimeToByteArray(byte[] base64) {
        String base64Clean = cleanMime(base64);
        if (Objects.isNull(base64Clean)) return null;
        return Base64.getDecoder().decode(base64Clean.getBytes(StandardCharsets.UTF_8));
    }
}
