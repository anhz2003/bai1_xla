package org.example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class Main {

    public static void main(String[] args) throws Exception {
        String outputDir = "E:\\xulyanh\\ketqua\\";
        Files.createDirectories(Paths.get(outputDir)); // Tạo thư mục nếu chưa tồn tại

        String imageFilePath = "E:\\xulyanh\\hinhanh\\pikvn002694-tranh-phong-canh-tuyen-tap-dep-file-psd.jpg";
        BufferedImage sourceImage = ImageIO.read(new File(imageFilePath));

        // Thực hiện các thao tác xử lý và lưu kết quả
        saveProcessedImage(invertColors(sourceImage), outputDir + "invert_colors.jpg");
        saveProcessedImage(enhanceContrastWithGamma(sourceImage, 1.5), outputDir + "contrast_gamma_enhanced.jpg");
        saveProcessedImage(applyGammaTransform(sourceImage, 0.5), outputDir + "gamma_transformed.jpg");
        saveProcessedImage(adaptiveHistogramEqualization(sourceImage), outputDir + "adaptive_histogram_equalized.jpg");
    }

    // Hàm lưu ảnh
    private static void saveProcessedImage(BufferedImage image, String outputPath) throws Exception {
        ImageIO.write(image, "jpg", new File(outputPath));
    }

    // Đảo ngược màu (Invert Colors)
    public static BufferedImage invertColors(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage invertedImage = new BufferedImage(width, height, img.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelColor = img.getRGB(x, y);
                int red = 255 - ((pixelColor >> 16) & 0xff); // Đảo màu đỏ
                int green = 255 - ((pixelColor >> 8) & 0xff);  // Đảo màu xanh lá
                int blue = 255 - (pixelColor & 0xff);         // Đảo màu xanh dương
                invertedImage.setRGB(x, y, (blue << 16) | (green << 8) | red); // Đảo vị trí màu
            }
        }
        return invertedImage;
    }

    // Tăng cường độ tương phản bằng Gamma Correction
    public static BufferedImage enhanceContrastWithGamma(BufferedImage img, double gammaValue) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage gammaEnhancedImage = new BufferedImage(width, height, img.getType());
        double inverseGamma = 1.0 / gammaValue;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelColor = img.getRGB(x, y);
                int red = limitRange((int) (255 * Math.pow(((pixelColor >> 16) & 0xff) / 255.0, inverseGamma)));
                int green = limitRange((int) (255 * Math.pow(((pixelColor >> 8) & 0xff) / 255.0, inverseGamma)));
                int blue = limitRange((int) (255 * Math.pow((pixelColor & 0xff) / 255.0, inverseGamma)));
                gammaEnhancedImage.setRGB(x, y, (red << 16) | (green << 8) | blue);
            }
        }
        return gammaEnhancedImage;
    }
    // Áp dụng biến đổi Gamma (Gamma Transform)
    public static BufferedImage applyGammaTransform(BufferedImage img, double gamma) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage gammaTransformedImage = new BufferedImage(width, height, img.getType());
        double constant = 255.0 / Math.pow(255, gamma);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelColor = img.getRGB(x, y);
                int red = limitRange((int) (constant * Math.pow(((pixelColor >> 16) & 0xff), gamma)));
                int green = limitRange((int) (constant * Math.pow(((pixelColor >> 8) & 0xff), gamma)));
                int blue = limitRange((int) (constant * Math.pow((pixelColor & 0xff), gamma)));
                gammaTransformedImage.setRGB(x, y, (red << 16) | (green << 8) | blue);
            }
        }
        return gammaTransformedImage;
    }

    // Cân bằng Histogram sử dụng CLAHE (Contrast Limited Adaptive Histogram Equalization)
    public static BufferedImage adaptiveHistogramEqualization(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage equalizedImage = new BufferedImage(width, height, img.getType());

        int[] histogram = new int[256];
        int clipLimit = 40;  // Giới hạn cắt để tránh quá sáng/tối
        int[] clippedHistogram = new int[256];

        // Tạo histogram
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelColor = img.getRGB(x, y);
                int grayscale = (int) (0.299 * ((pixelColor >> 16) & 0xff) +
                        0.587 * ((pixelColor >> 8) & 0xff) +
                        0.114 * (pixelColor & 0xff));
                histogram[grayscale]++;
            }
        }

        // Cắt giá trị của histogram theo giới hạn clip
        for (int i = 0; i < 256; i++) {
            clippedHistogram[i] = Math.min(histogram[i], clipLimit);
        }

        // Áp dụng cân bằng histogram
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelColor = img.getRGB(x, y);
                int grayscale = (int) (0.299 * ((pixelColor >> 16) & 0xff) +
                        0.587 * ((pixelColor >> 8) & 0xff) +
                        0.114 * (pixelColor & 0xff));
                int newGrayValue = clippedHistogram[grayscale] * 255 / (width * height);
                equalizedImage.setRGB(x, y, (newGrayValue << 16) | (newGrayValue << 8) | newGrayValue);
            }
        }
        return equalizedImage;
    }

    // Giới hạn giá trị pixel trong khoảng [0, 255]
    private static int limitRange(int value) {
        return Math.max(0, Math.min(255, value));
    }
}