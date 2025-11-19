package application.controller;

import application.model.data_objects.LanguageOption;

import application.model.data_objects.LoginResult;
import application.model.data_objects.RegistrationResult;
import application.model.entity.Post;
import application.model.entity.User;
import application.model.service.UserService;
import application.utils.Paths;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LogInController {
    UserService userService;

    @FXML
    private VBox loginMenu;
    @FXML
    private AnchorPane createAccountMenu;
    @FXML
    private TextArea postContent;
    @FXML
    private ListView<Post> feedPagePostList;
    @FXML
    private ComboBox<LanguageOption> languageOptions;

    // Login
    @FXML
    private TextField loginUsername;
    @FXML
    private TextField loginPassword;
    @FXML
    private Label loginResultLabel;

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

    //this method is used for safely initializing fxml components after they have been loaded.
    @FXML
    private void initialize() {
        setLanguageOptions();

    }

    // Default constructor for JavaFX/FXML
    public LogInController() {
        this.userService = new UserService(); // Keep this for normal operation
    }

    // Constructor for testing - allow injection of mock service
    public LogInController(UserService userService) {
        this.userService = userService;
    }

    public void login(ActionEvent actionEvent) throws IOException {

        resultText.setText("");
        String usernameText = loginUsername.getText();
        String passwordText = loginPassword.getText();


        // check that all fields are filled
        if (loginUsername.getText().isEmpty() || loginPassword.getText().isEmpty()) {
            loginResultLabel.setText(SceneManager.getSceneManager().getResBundle().getString("login.fieldsmissingprompt"));
            return;
        }

        // attempt login
        try {
            User user = new User(usernameText, passwordText);
            if (userService == null) {
                userService = new UserService();
            }
            LoginResult result = userService.loginUser(user);

            if (result.isSuccess()) {
                SessionManager.getInstance().setUser(result.getUser());
                SessionManager.getInstance().setToken(result.getToken());

                loginResultLabel.setText(result.getMessage());

                PauseTransition pause = new PauseTransition(Duration.seconds(1)); // Cannot use sleep, because it blocks UI
                pause.setOnFinished(e -> {
                    SceneManager.getSceneManager().switchScene(Paths.APP);
                });
                pause.play();

            } else {
                loginResultLabel.setText(result.getMessage());
            }
        } catch (Exception e) {
            loginResultLabel.setText(SceneManager.getSceneManager().getResBundle().getString("login.systemerrorprompt"));
        }
    }

    public void goToRegisterView(ActionEvent actionEvent){
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

            resultText.setText(SceneManager.getSceneManager().getResBundle().getString("register.fillfields"));
            return;
        }

        // Lazy email validation
        String email = newEmail.getText();
        if (!email.contains("@") || !email.contains(".")) {
            resultText.setText(SceneManager.getSceneManager().getResBundle().getString("register.promptvalidemail"));
            return;
        }

        // check password matching
        if (!password.equals(passwordConfirm)) {
            resultText.setText(SceneManager.getSceneManager().getResBundle().getString("register.passwordsnotmatching"));
            return;
        }
        // get other inputs
        String firstName = newFirstname.getText();
        String lastName = newLastname.getText();
        String username = newUsername.getText();
        String birthdate = newBirthdate.getValue().toString();

        // create new user entity
        try {
            User user = new User(firstName, lastName, email, username, birthdate, password);
            if (userService == null) {
                userService = new UserService();
            }
            RegistrationResult result = userService.registerUser(user);

            if (result.isSuccess()) {
                resultText.setText(result.getMessage());
                PauseTransition pause = new PauseTransition(Duration.seconds(2)); // Cannot use sleep, because it blocks UI
                pause.setOnFinished(e -> {
                    createAccountMenu.setVisible(false);
                    loginMenu.setVisible(true); // Return to log in menu after 2 sec
                });
                pause.play();
            } else {
                resultText.setText(result.getMessage());
            }
        } catch (Exception e) {
            resultText.setText("Registration failed due to system error. Please try again.");
        }
    }

    public void goToLoginView(ActionEvent actionEvent) throws IOException {
        //empty all fields
        newFirstname.clear();
        newLastname.clear();
        newUsername.clear();
        newEmail.clear();
        newBirthdate.setValue(null);
        loginUsername.clear();
        loginPassword.clear();
        newPassword.clear();
        newPasswordConfirm.clear();
        resultText.setText("");

        //change back to log in
        createAccountMenu.setVisible(false);
        loginMenu.setVisible(true);
    }

    public void setLanguageOptions() {
        languageOptions.getItems().clear();


        Image ukIcon = new Image(getClass().getResourceAsStream("/images/flags/uk-icon.png"));
        LanguageOption uk = new LanguageOption("English", "en", "UK", ukIcon);

        Image jpIcon = new Image(getClass().getResourceAsStream("/images/flags/japan-icon.png"));
        LanguageOption jp = new LanguageOption("Japanese", "ja", "JP", jpIcon);

        Image iranIcon = new Image(getClass().getResourceAsStream("/images/flags/iran-icon.png"));
        LanguageOption iran = new LanguageOption("Persian", "fa", "IR", iranIcon);

        Image finlandIcon = new Image(getClass().getResourceAsStream("/images/flags/finland-icon.png"));
        LanguageOption finland = new LanguageOption("Finland", "fi", "FI", finlandIcon);


        languageOptions.getItems().addAll(finland, jp, iran, uk);


        languageOptions.setCellFactory(param -> new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(LanguageOption item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(item.getIcon());
                    imageView.setFitHeight(40);
                    imageView.setFitWidth(40);
                    setGraphic(imageView);
                }
            }
        });

        languageOptions.setButtonCell(languageOptions.getCellFactory().call(null));
        //languageOptions.getSelectionModel().select(uk);
    }

    public void changeLanguage(ActionEvent actionEvent) {
        Locale locale = new Locale(languageOptions.getSelectionModel().getSelectedItem().getLanguageCode(), languageOptions.getSelectionModel().getSelectedItem().getCountryCode());
        ResourceBundle rb = ResourceBundle.getBundle("LanguageBundle", locale);
        SceneManager.getSceneManager().setResBundle(rb);
        SceneManager.getSceneManager().switchScene(Paths.LOGIN);
    }

}
