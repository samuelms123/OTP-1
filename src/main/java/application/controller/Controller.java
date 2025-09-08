package application.controller;

import entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Controller {

    @FXML
    private VBox loginMenu;
    @FXML
    private AnchorPane createAccountMenu;
    @FXML
    private AnchorPane profilePage;
    @FXML
    private VBox feedPage;

    // Register
    @FXML
    private TextField newFirstname;
    @FXML
    private TextField newLastname;
    @FXML
    private TextField newUsername;
    @FXML
    private TextField newPassword;
    @FXML
    private TextField newPasswordConfirm;
    @FXML
    private TextField newEmail;
    @FXML
    private DatePicker newBirthdate;



    public void login(ActionEvent actionEvent) throws IOException {
        URL fxml = application.view.GUI.class.getResource("/fxml/app.fxml");
        FXMLLoader loader = new FXMLLoader(fxml);
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        URL fxml = application.view.GUI.class.getResource("/fxml/login.fxml");
        FXMLLoader loader = new FXMLLoader(fxml);
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void register(ActionEvent actionEvent){
        loginMenu.setVisible(false);
        createAccountMenu.setVisible(true);
    }

    public void createAccount(ActionEvent actionEvent){
        createAccountMenu.setVisible(false);
        String username = newUsername.getText();
        String password = newPassword.getText();

        //User user = new User(username)
        loginMenu.setVisible(true);


    }

    public void openProfilePage(ActionEvent actionEvent){
        feedPage.setVisible(false);
        profilePage.setVisible(true);
    }

    public void openFeedPage(ActionEvent actionEvent){
        profilePage.setVisible(false);
        feedPage.setVisible(true);
    }
}