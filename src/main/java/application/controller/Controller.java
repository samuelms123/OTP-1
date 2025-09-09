package application.controller;

import application.model.data_objects.RegistrationResult;
import application.model.entity.User;
import application.model.service.UserService;
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
    UserService userService = new UserService();

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
        String password = newPassword.getText();
        String passwordConfirm = newPasswordConfirm.getText();

        if (!password.equals(passwordConfirm)) {
            System.out.println("Passwords do not match"); // Do UI message
            return;
        }

        String firstName = newFirstname.getText();
        String lastName = newLastname.getText();
        String username = newUsername.getText();
        String email = newEmail.getText();
        String birthdate = newBirthdate.getValue().toString();

        // check that every field is filled

        User user = new User(firstName, lastName, email, username, birthdate, password);
        RegistrationResult result = userService.registerUser(user);

        if (result.isSuccess()) {
            // resultMessage.setText(result.getMessage()) set message to UI! (not implemented yet)
            System.out.println(result.getMessage());
            loginMenu.setVisible(true);
            return;
        }

        // set result message to UI if fails
        System.out.println(result.getMessage());
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