package application.controller;

import application.model.data_objects.RegistrationResult;
import application.model.entity.User;
import application.model.service.UserService;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    @FXML
    private Label resultText;




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
        resultText.setText("");
        String password = newPassword.getText();
        String passwordConfirm = newPasswordConfirm.getText();

        // check that all of the fields are filled
        if (newFirstname.getText().isEmpty() ||
                newLastname.getText().isEmpty() ||
                newUsername.getText().isEmpty() ||
                newEmail.getText().isEmpty() ||
                newBirthdate.getValue() == null ||
                newPassword.getText().isEmpty() ||
                newPasswordConfirm.getText().isEmpty()) {

            resultText.setText("Please fill in all fields");
            return;
        }

        // Lazy email validation
        String email = newEmail.getText();
        if (!email.contains("@") || !email.contains(".")) {
            resultText.setText("Please enter a valid email address");
            return;
        }

        // check password matching
        if (!password.equals(passwordConfirm)) {
            resultText.setText("Passwords not matching");
            return;
        }
        // get other inputs
        String firstName = newFirstname.getText();
        String lastName = newLastname.getText();
        String username = newUsername.getText();
        String birthdate = newBirthdate.getValue().toString();

        // create new user entity
        User user = new User(firstName, lastName, email, username, birthdate, password);
        RegistrationResult result = userService.registerUser(user);

        if (result.isSuccess()) {
            resultText.setText(result.getMessage());
            PauseTransition pause = new PauseTransition(Duration.seconds(2)); // Cannot use sleep, because it blocks UI
            pause.setOnFinished(e -> {
                createAccountMenu.setVisible(false);
                loginMenu.setVisible(true); // Return to log in menu after 2 sec
            });
            pause.play();
        }
        else {
            resultText.setText(result.getMessage());
        }
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