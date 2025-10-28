package application.view;

import application.controller.SceneManager;
import application.utils.Paths;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;


public class GUI extends Application {
    private static SceneManager sceneManager;

    public void start(Stage stage) throws Exception {
        stage.setTitle("Shout!");
        stage.setResizable(false);
        Image icon = new Image(getClass().getResourceAsStream("/images/icons/logo.png"));
        stage.getIcons().add(icon);
        Locale locale = new Locale("en", "UK");
        ResourceBundle rb = ResourceBundle.getBundle("LanguageBundle", locale);

       sceneManager = new SceneManager(stage, rb);
       sceneManager.switchScene(Paths.LOGIN);
    }

    // for testing purposes
    public static void setSceneManager(SceneManager manager) {
        sceneManager = manager;
    }

    public static SceneManager getSceneManager() {
        return sceneManager;
    }
}
