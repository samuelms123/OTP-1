package application.view;

import application.controller.SceneManager;
import application.utils.Paths;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;



public class GUI extends Application {
    private static SceneManager sceneManager;

    public void start(Stage stage) throws Exception {
        stage.setTitle("Shout!");
        stage.setResizable(false);
        Image icon = new Image(getClass().getResourceAsStream("/images/icons/logo.png"));
        stage.getIcons().add(icon);

       sceneManager = new SceneManager(stage);
       sceneManager.switchScene(Paths.LOGIN);
    }

    public static SceneManager getSceneManager() {
        return sceneManager;
    }
}
