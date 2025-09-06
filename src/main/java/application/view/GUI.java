package application.view;

import application.controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;


public class GUI extends Application {

    private Stage stage;
    private Controller sceneController;

    public void start(Stage stage) throws Exception {
        stage.setTitle("Shout!");
        stage.setResizable(false);
        Image icon = new Image(getClass().getResourceAsStream("/images/icons/logo.png"));
        stage.getIcons().add(icon);


        URL fxml = GUI.class.getResource("/fxml/login.fxml");
        FXMLLoader loader = new FXMLLoader(fxml);
        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        stage.show();

        this.stage = stage;
    }

    @Override
    public void init() {
        sceneController = new Controller();
    }

    public Stage getStage() {
        return stage;
    }
}
