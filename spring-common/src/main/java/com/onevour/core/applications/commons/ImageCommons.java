package com.onevour.core.applications.commons;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
@Component
public class ImageCommons {

    @Value("${app.file.upload-dir:uploads}")
    String basePath;

    public static byte[] transferAlpha(String fileName) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        File file = new File(fileName);
        InputStream is;
        try {
            is = new FileInputStream(file);
            // If it is a MultipartFile type, then it also has a method to convert to a stream: is = file.getInputStream();
            BufferedImage bi = ImageIO.read(is);
            Image image = (Image) bi;
            ImageIcon imageIcon = new ImageIcon(image);
            BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
            g2D.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
            int alpha = 0;
            for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage.getHeight(); j1++) {
                for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage.getWidth(); j2++) {
                    int rgb = bufferedImage.getRGB(j2, j1);

                    int R = (rgb & 0xff0000) >> 16;
                    int G = (rgb & 0xff00) >> 8;
                    int B = (rgb & 0xff);
                    if (((255 - R) < 30) && ((255 - G) < 30) && ((255 - B) < 30)) {
                        rgb = ((alpha + 1) << 24) | (rgb & 0x00ffffff);
                    }

                    bufferedImage.setRGB(j2, j1, rgb);

                }
            }

            g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
            ImageIO.write(bufferedImage, "png", new File("D:\\08\\12.png")); // Direct output file
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return byteArrayOutputStream.toByteArray();
    }

    public String transferAlphaBase64(File file, String out) {
        AtomicReference<File> tmp = new AtomicReference<>();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream is;
        try {
            is = new FileInputStream(file);
            // If it is a MultipartFile type, then it also has a method to convert to a stream: is = file.getInputStream();
            BufferedImage bi = ImageIO.read(is);
            Image image = (Image) bi;
            ImageIcon imageIcon = new ImageIcon(image);
            BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
            g2D.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
            int alpha = 0;
            for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage.getHeight(); j1++) {
                for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage.getWidth(); j2++) {
                    int rgb = bufferedImage.getRGB(j2, j1);

                    int R = (rgb & 0xff0000) >> 16;
                    int G = (rgb & 0xff00) >> 8;
                    int B = (rgb & 0xff);
                    if (((255 - R) < 30) && ((255 - G) < 30) && ((255 - B) < 30)) {
                        rgb = ((alpha + 1) << 24) | (rgb & 0x00ffffff);
                    }

                    bufferedImage.setRGB(j2, j1, rgb);

                }
            }
            g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
            if (Objects.isNull(out) || out.isEmpty()) {
                out = basePath + File.separator + "tmp" + File.separator + "ektp_" + UUID.randomUUID().toString() + ".png";
            }
            File output = new File(out);
            ImageIO.write(bufferedImage, "png", output); // Direct output file
            tmp.set(output);
            return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(output));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Objects.nonNull(tmp.get())) tmp.get().delete();
        }
        return "";
    }

    public static String resizeImage(String original) {
        try {
            byte[] data = Base64.getDecoder().decode(original);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
            BufferedImage resized = cropCenter(image);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resized, "png", baos);
            byte[] encoded = Base64.getEncoder().encode(baos.toByteArray());
            return new String(encoded, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            log.error("error read image", e);
            // handle
            return null;
        }
    }

    public static String resizeImagePassPhoto(String original) {
        try {
            byte[] data = Base64.getDecoder().decode(original);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
            BufferedImage resized = cropPassPhoto(image);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resized, "png", baos);
            byte[] encoded = Base64.getEncoder().encode(baos.toByteArray());
            return new String(encoded, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            log.error("error read image", e);
            // handle
            return null;
        }
    }

    /*
     * find smaller w or h
     * */
    public static BufferedImage cropCenter(BufferedImage img) {
        int height = img.getHeight();
        int width = img.getWidth();
        log.debug("resize image from origin {} {}", height, width);
        if (height > width) {
            int xc = 0;
            int yc = (height - width) / 2;
            return img.getSubimage(xc, yc, width, width);
        } else {
            int xc = (width - height) / 2;
            int yc = 0;
            return img.getSubimage(xc, yc, height, height);
        }
    }

    public static BufferedImage cropPassPhoto(BufferedImage img) {
        int height = img.getHeight();
        int width = img.getWidth();
        log.debug("resize image from origin {} {}", height, width);
        if (height > width) {
            int xc = 0;
            int yc = (height - width) / 2;
            return img.getSubimage(xc, yc, width, width + (width / 4));
        } else {
            int xc = (width - height) / 2;
            int yc = 0;
            return img.getSubimage(xc, yc, height, height + (height / 4));
        }
    }

    private static BufferedImage cropCenter(BufferedImage originalImage, double amount) {
        int height = originalImage.getHeight();
        int width = originalImage.getWidth();

        int targetWidth = (int) (width * amount);
        int targetHeight = (int) (height * amount);
        // Coordinates of the image's middle
        int xc = (width - targetWidth) / 2;
        int yc = (height - targetHeight) / 2;

        // Crop
        BufferedImage croppedImage = originalImage.getSubimage(
                xc,
                yc,
                targetWidth, // widht
                targetHeight // height
        );
        return croppedImage;
    }

    private static BufferedImage resize(BufferedImage img, int width, int height) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    public static byte[] toPng(File file) throws IOException {
        BufferedImage bi = ImageIO.read(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

}
