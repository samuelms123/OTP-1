package application.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class ImageUtils {
    private ImageUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static Image blobToImage(byte[] byteArray) {
        Objects.requireNonNull(byteArray, "byteArray cannot be null");
        InputStream iStream = new ByteArrayInputStream(byteArray);
        return new Image(iStream);
    }

    public static byte[] imageToBlob(Image image) {
        Objects.requireNonNull(image, "image cannot be null");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);

        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "png", oStream);
            return oStream.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Error converting blob to image", e);
        }
    }
}
