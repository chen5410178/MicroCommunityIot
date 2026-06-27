package com.java110.core.factory;

import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class SliderCodeFactory {

    private static final Random RANDOM = new Random();
    private static final Base64 BASE64 = new Base64();

    public static SliderCodeData generateSliderCode(int bgWidth, int bgHeight, int sliderWidth, int sliderHeight) throws IOException {
        BufferedImage bgImage = new BufferedImage(bgWidth, bgHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bgImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 随机背景色
        Color bgColor = getRandomLightColor();
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, bgWidth, bgHeight);

        // 绘制随机干扰图形
        drawRandomShapes(g2d, bgWidth, bgHeight);

        // 随机抠图位置，边界留10px
        int x = RANDOM.nextInt(bgWidth - sliderWidth - 20) + 10;
        int y = RANDOM.nextInt(bgHeight - sliderHeight - 20) + 10;

        // 创建滑块图（从背景中扣出）
        BufferedImage sliderImage = new BufferedImage(sliderWidth, sliderHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D sliderG2d = sliderImage.createGraphics();
        sliderG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        sliderG2d.drawImage(bgImage.getSubimage(x, y, sliderWidth, sliderHeight), 0, 0, null);

        // 滑块边框+阴影
        sliderG2d.setColor(new Color(255, 255, 255, 180));
        sliderG2d.setStroke(new BasicStroke(2f));
        sliderG2d.drawRoundRect(0, 0, sliderWidth - 1, sliderHeight - 1, 4, 4);
        sliderG2d.dispose();

        // 背景图挖空区域做半透明遮罩 + 虚线边框
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(x, y, sliderWidth, sliderHeight, 4, 4);
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{5f}, 0f));
        g2d.drawRoundRect(x, y, sliderWidth, sliderHeight, 4, 4);
        g2d.dispose();

        String bgBase64 = toBase64(bgImage);
        String sliderBase64 = toBase64(sliderImage);

        return new SliderCodeData(bgBase64, sliderBase64, x, y, sliderWidth, sliderHeight);
    }

    private static void drawRandomShapes(Graphics2D g2d, int w, int h) {
        // 随机矩形
        for (int i = 0; i < 8; i++) {
            g2d.setColor(getRandomColor(100, 200));
            int rx = RANDOM.nextInt(w);
            int ry = RANDOM.nextInt(h);
            int rw = 20 + RANDOM.nextInt(60);
            int rh = 15 + RANDOM.nextInt(40);
            g2d.fillRoundRect(rx, ry, rw, rh, 8, 8);
        }
        // 随机圆
        for (int i = 0; i < 6; i++) {
            g2d.setColor(getRandomColor(80, 180));
            int cx = RANDOM.nextInt(w);
            int cy = RANDOM.nextInt(h);
            int cr = 8 + RANDOM.nextInt(30);
            g2d.fillOval(cx, cy, cr, cr);
        }
        // 随机线条
        g2d.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < 5; i++) {
            g2d.setColor(getRandomColor(120, 200));
            int x1 = RANDOM.nextInt(w);
            int y1 = RANDOM.nextInt(h);
            int x2 = x1 + RANDOM.nextInt(80) - 40;
            int y2 = y1 + RANDOM.nextInt(60) - 30;
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    private static Color getRandomLightColor() {
        int r = 200 + RANDOM.nextInt(56);
        int g = 200 + RANDOM.nextInt(56);
        int b = 200 + RANDOM.nextInt(56);
        return new Color(r, g, b);
    }

    private static Color getRandomColor(int min, int max) {
        int range = max - min;
        int r = min + RANDOM.nextInt(range);
        int g = min + RANDOM.nextInt(range);
        int b = min + RANDOM.nextInt(range);
        return new Color(r, g, b);
    }

    private static String toBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] bytes = baos.toByteArray();
        return "data:image/png;base64," + BASE64.encodeToString(bytes).trim();
    }

    public static class SliderCodeData {
        private String bgImage;
        private String sliderImage;
        private int x;
        private int y;
        private int sliderWidth;
        private int sliderHeight;

        public SliderCodeData(String bgImage, String sliderImage, int x, int y, int sliderWidth, int sliderHeight) {
            this.bgImage = bgImage;
            this.sliderImage = sliderImage;
            this.x = x;
            this.y = y;
            this.sliderWidth = sliderWidth;
            this.sliderHeight = sliderHeight;
        }

        public String getBgImage() { return bgImage; }
        public void setBgImage(String bgImage) { this.bgImage = bgImage; }
        public String getSliderImage() { return sliderImage; }
        public void setSliderImage(String sliderImage) { this.sliderImage = sliderImage; }
        public int getX() { return x; }
        public void setX(int x) { this.x = x; }
        public int getY() { return y; }
        public void setY(int y) { this.y = y; }
        public int getSliderWidth() { return sliderWidth; }
        public void setSliderWidth(int sliderWidth) { this.sliderWidth = sliderWidth; }
        public int getSliderHeight() { return sliderHeight; }
        public void setSliderHeight(int sliderHeight) { this.sliderHeight = sliderHeight; }
    }
}
