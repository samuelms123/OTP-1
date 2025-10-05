package application.utils;

import javafx.embed.swing.JFXPanel;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class ImageUtilsTest {

    private static byte[] testImageBytes;

    @BeforeAll
    static void init() throws Exception {
        // Init JavaFX runtime
        new JFXPanel();

        try (InputStream iS = ImageUtilsTest.class.getResourceAsStream("/images/mock/empty-avatar.jpg")) {
            assertNotNull(iS, "mock image not found");
            testImageBytes = iS.readAllBytes();
        }
    }

    @Test
    void testBlobToImageWithValidBytes() {
        Image image = ImageUtils.blobToImage(testImageBytes);
        assertNotNull(image);
        assertTrue(image.getWidth() > 0);
        assertTrue(image.getHeight() > 0);
    }

    @Test
    void testBlobToImageWithNullBytes() {
        Image image = ImageUtils.blobToImage(null);
        assertNull(image);
    }

    @Test
    void testImageToBlobWithValidImage() {
        Image image = ImageUtils.blobToImage(testImageBytes);
        byte[] bytes = ImageUtils.imageToBlob(image);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    void testImageToBlobWithNullImage() {
        byte[] bytes = ImageUtils.imageToBlob(null);
        assertNull(bytes);
    }

    @Test
    void testBlobToImageAndBack() {
        Image image = ImageUtils.blobToImage(testImageBytes);
        byte[] bytes = ImageUtils.imageToBlob(image);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }
}