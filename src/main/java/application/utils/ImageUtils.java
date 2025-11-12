package application.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    public static Image blobToImage(byte[] byteArray) {
        if (byteArray == null) {
            throw new NullPointerException("byteArray cannot be null");
        }
        InputStream iStream = new ByteArrayInputStream(byteArray);
        return new Image(iStream);
    }

    public static byte[] imageToBlob(Image image) {
        if (image == null) {
            throw new NullPointerException("image cannot be null");
        }
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);

        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "png", oStream);
            return oStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error converting blob to image", e);
        }
    }
}
