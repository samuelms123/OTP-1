package application.controller;

import application.utils.Paths;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SceneManager {
    private Stage stage;
    private ResourceBundle rb;

    public SceneManager(Stage stage, ResourceBundle rb) {
        this.stage = stage;
        this.rb = rb;
    }

    public void switchScene(String fxmlPath) {
        try {
            URL fxml = application.view.GUI.class.getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxml, rb);
            Scene scene = new Scene(loader.load());
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

    public void setResBundle(ResourceBundle rb) {
        this.rb = rb;
    }
}
