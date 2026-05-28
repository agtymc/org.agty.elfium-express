package org.agty.elfiumexpress.storage.thumbs;

import com.luciad.imageio.webp.WebPReadParam;
import javax.imageio.*;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class Thumbs {
    private File imageFile;
    private double width = 200;
    private double height = 200;
    private String destination = "./";
    private float quality = .75f;
    private boolean isWebp = false;
    private String mimeType = "image/jpeg";

    public Thumbs() {}

    public Thumbs(String image) {
        this.imageFile = new File(image);
    }

    public static void createThumb(String src, String dst, double width, double height, float quality) throws IOException {
        Thumbs thumb = new Thumbs(src);
        thumb.setWidth(width);
        thumb.setHeight(height);
        thumb.setDestination(dst);
        thumb.setQuality(quality);
        thumb.save();
    }

    public void setSource(String source) {
        this.imageFile = new File(source);
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        if (mimeType != null && !mimeType.isEmpty()) {
            this.mimeType = mimeType;
        }
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public BufferedImage getImage() throws IOException {
        return resize();
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public float getQuality() {
        return quality;
    }

    public void setQuality(float quality) {
        this.quality = quality;
    }

    public void save() throws IOException {
        BufferedImage image = resize();

        if (image == null) return;

        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer = iter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(getQuality() > 0 ? getQuality() : 0.75f);

        File file = new File(getDestination());
        Path parent = file.toPath().getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        FileImageOutputStream output = new FileImageOutputStream(file);
        writer.setOutput(output);

        IIOImage ioimage = new IIOImage(image, null, null);
        writer.write(null, ioimage, iwp);
        writer.dispose();
    }

    public BufferedImage resize() throws IOException {

        BufferedImage imgSrc = readImageFile();

        if (imgSrc == null) return null;

        int originalWidth = imgSrc.getWidth();
        int originalHeight = imgSrc.getHeight();

        if (originalWidth == 0 || originalHeight == 0) return null;

        if (originalWidth < width && originalHeight < height) {
            return imgSrc;
        }

        double originalRatio = (double) originalWidth / originalHeight;
        double sizeRatio = width / height;

        if (sizeRatio < originalRatio) {
            height = width / originalRatio;
        } else {
            width = height * originalRatio;
        }

        BufferedImage resizedImage = new BufferedImage((int) width, (int) height,
                BufferedImage.TYPE_INT_RGB);// getCompatibleImage((int) width, (int) height);
        Graphics2D g2d = resizedImage.createGraphics();

        double xScale = width / imgSrc.getWidth();
        double yScale = height / imgSrc.getHeight();

        AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
        g2d.drawRenderedImage(imgSrc, at);
        g2d.dispose();

        return resizedImage;
    }

    private BufferedImage readImageFile() throws IOException {
        if (getMimeType().equals("image/webp")) {
            return readImageWebp();
        }
        return readImage();
    }

    private BufferedImage readImageWebp() throws IOException {
        ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();
        WebPReadParam readParam = new WebPReadParam();
        readParam.setBypassFiltering(true);
        reader.setInput(new FileImageInputStream(imageFile));
        return reader.read(0, readParam);
    }

    private BufferedImage readImage() throws IOException {
        return ImageIO.read(new FileImageInputStream(imageFile));
    }
}
