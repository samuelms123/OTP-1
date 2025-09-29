package application.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUtils {

    public static Image blobToImage(byte[] byteArray) {
        if (byteArray == null) {
            System.out.println("blob is null");
            return null;
        }
        InputStream iStream = new ByteArrayInputStream(byteArray);
        return new Image(iStream);
    }

    public static byte[] imageToBlob(Image image) {
        if (image == null) {
            System.out.println("image is null");
            return null;
        }
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);

        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "png", oStream);
            byte[] imageBytes = oStream.toByteArray();
            return imageBytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
