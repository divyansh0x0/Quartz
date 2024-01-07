package app.comparators;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class ImageComparator {
    private static final long FNV_OFFSET_BASIS = 0xcbf29ce484222325L;
    private static final long FNV_PRIME = 0x100000001b3L;
    public static int calculateMurmurHash(BufferedImage image) {
        // Convert BufferedImage to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageData;
        try {
            ImageIO.write(image, "png", baos);
            imageData = baos.toByteArray();
        }catch (Exception e){
            imageData = new byte[0];
        }
        return murmurHash(imageData);
    }

    public static int murmurHash(byte[] data){
        int len = data.length;
        int c1 = 0xcc9e2d51;
        int c2 = 0x1b873593;

        int offset = 0;
        int h1 = 0;//seed
        int roundedEnd = offset + (len & 0xfffffffc);  // round down to 4 byte block

        for (int i = offset; i < roundedEnd; i += 4) {
            // little endian load order
            int k1 = (data[i] & 0xff) | ((data[i + 1] & 0xff) << 8) | ((data[i + 2] & 0xff) << 16) | (data[i + 3] << 24);
            k1 *= c1;
            k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
            k1 *= c2;

            h1 ^= k1;
            h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
            h1 = h1 * 5 + 0xe6546b64;
        }

        // tail
        int k1 = 0;

        switch(len & 0x03) {
            case 3:
                k1 = (data[roundedEnd + 2] & 0xff) << 16;
                // fallthrough
            case 2:
                k1 |= (data[roundedEnd + 1] & 0xff) << 8;
                // fallthrough
            case 1:
                k1 |= data[roundedEnd] & 0xff;
                k1 *= c1;
                k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                k1 *= c2;
                h1 ^= k1;
            default:
        }

        // finalization
        h1 ^= len;

        // fmix(h1);
        h1 ^= h1 >>> 16;
        h1 *= 0x85ebca6b;
        h1 ^= h1 >>> 13;
        h1 *= 0xc2b2ae35;
        h1 ^= h1 >>> 16;

        return h1;
    }
    public static long calculateMurmurHash(byte[] imageData) {
        long hash = FNV_OFFSET_BASIS;

        for (byte b : imageData) {
            hash ^= b & 0xFF;
            hash *= FNV_PRIME;
        }

        return hash;
    }
    // Utility method to convert byte array to hexadecimal string
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    public static long getAHash(BufferedImage image) {
        int hashSize = 7;

        // Resize the image to a fixed size
        BufferedImage resizedImage = resizeImage(image, hashSize, hashSize);

        // Convert the image to grayscale
        BufferedImage grayscaleImage = convertToGrayscale(resizedImage);

        // Calculate the average pixel value
        int averagePixelValue = calculateAveragePixelValue(grayscaleImage);

        // Construct the hash value
        long hash = 0;
        for (int y = 0; y < hashSize; y++) {
            for (int x = 0; x < hashSize; x++) {
                int pixelValue = getPixelValue(grayscaleImage, x, y);
                if (pixelValue >= averagePixelValue) {
                    hash |= 1L << (y * hashSize + x);
                }
            }
        }

        return hash;
    }

    private static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(resizedImage, 0, 0, null);
        g2d.dispose();
        return bufferedImage;
    }

    private static BufferedImage convertToGrayscale(BufferedImage image) {
        BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = grayscaleImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return grayscaleImage;
    }

    private static int calculateAveragePixelValue(BufferedImage image) {
        int sum = 0;
        int width = image.getWidth();
        int height = image.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sum += getPixelValue(image, x, y);
            }
        }
        return sum / (width * height);
    }

    private static int getPixelValue(BufferedImage image, int x, int y) {
        return image.getRaster().getSample(x, y, 0);
    }

    public static boolean lookSame(BufferedImage image1,BufferedImage image2) {
        // Adjust the threshold based on your requirements
        int threshold = 5;
        int hammingDistance = Long.bitCount(getAHash(image1) ^ getAHash(image2));

        return hammingDistance <= threshold;
    }
}