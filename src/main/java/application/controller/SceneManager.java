package application.controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SceneManager {
    private static SceneManager instance;
    private Stage stage;
    private ResourceBundle rb;

    private SceneManager() {

    }

    public static SceneManager getSceneManager() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    // FOR TESTING PURPOSES ONLY!
    public void setSceneManager(SceneManager manager) {
        instance = manager;
    }

    public void switchScene(String fxmlPath) {
        try {
            URL fxml = application.view.GUI.class.getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxml, rb);
            Parent root = loader.load();
            String lang = rb.getLocale().getLanguage();
            if (lang.equals("fa")) {
                root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            } else {
                root.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage, ResourceBundle rb) {
        this.stage = stage;
        this.rb = rb;
    }

    public void setResBundle(ResourceBundle rb) {
        this.rb = rb;
    }

    public ResourceBundle getResBundle() {
        return rb;
    }
}
