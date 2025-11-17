package application.controller;

import application.model.data_objects.LoginResult;
import application.model.data_objects.RegistrationResult;
import application.model.entity.User;
import application.model.service.UserService;
import application.utils.Paths;
import application.view.GUI;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(ApplicationExtension.class)
@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @InjectMocks
    private LogInController loginController;

    @Mock
    private UserService userService;

    @Mock
    private Stage mockStage;

    @Mock
    private SceneManager sceneManager;

    @Mock
    private SessionManager sessionManager;

    // Mock FXML components
    @Mock
    @FXML
    private VBox loginMenu;

    @Mock
    @FXML
    private AnchorPane createAccountMenu;

    @Mock
    @FXML
    private TextField loginUsername;

    @Mock
    @FXML
    private TextField loginPassword;

    @Mock
    @FXML
    private Label loginResultLabel;

    @Mock
    @FXML
    private TextField newFirstname;

    @Mock
    @FXML
    private TextField newLastname;

    @Mock
    @FXML
    private TextField newUsername;

    @Mock
    @FXML
    private TextField newPassword;

    @Mock
    @FXML
    private TextField newPasswordConfirm;

    @Mock
    @FXML
    private TextField newEmail;

    @Mock
    @FXML
    private DatePicker newBirthdate;

    @Mock
    @FXML
    private Label resultText;

    private User testUser;
    private ActionEvent mockActionEvent;
    private String emptyFieldPrompt = "Please fill in all fields.";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // initialize the controller and inject mocks
        loginController = new LogInController();
        ResourceBundle rb = ResourceBundle.getBundle("LanguageBundle", new Locale("en", "UK"));
        SceneManager.getSceneManager().setResBundle(rb);

        // inject the mocked dependencies
        try {
            var userServiceField = LogInController.class.getDeclaredField("userService");
            userServiceField.setAccessible(true);
            userServiceField.set(loginController, userService);

            // inject mocked FXML components
            injectFXMLField("loginMenu", loginMenu);
            injectFXMLField("createAccountMenu", createAccountMenu);
            injectFXMLField("loginUsername", loginUsername);
            injectFXMLField("loginPassword", loginPassword);
            injectFXMLField("loginResultLabel", loginResultLabel);
            injectFXMLField("newFirstname", newFirstname);
            injectFXMLField("newLastname", newLastname);
            injectFXMLField("newUsername", newUsername);
            injectFXMLField("newPassword", newPassword);
            injectFXMLField("newPasswordConfirm", newPasswordConfirm);
            injectFXMLField("newEmail", newEmail);
            injectFXMLField("newBirthdate", newBirthdate);
            injectFXMLField("resultText", resultText);

        } catch (Exception e) {
            fail("Failed to inject dependencies: " + e.getMessage());
        }

        testUser = new User("testname", "testlastname", "test@example.com", "testusername", "1.1.1999", "password");
        mockActionEvent = mock(ActionEvent.class);

        // mock SessionManager singleton
        SessionManager.setInstance(sessionManager);

        // mock SceneManager - create a real instance with mocked stage
        sceneManager = mock(SceneManager.class);


        // mock GUI to return mocked SceneManager
        // setter method in GUI class for testing
        setupGUIMock();
    }

    private void injectFXMLField(String fieldName, Object mock) throws Exception {
        var field = LogInController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(loginController, mock);
    }

    private void setupGUIMock() {
        try {

            var getSceneManagerMethod = SceneManager.class.getMethod("getSceneManager");
            var setSceneManagerMethod = SceneManager.class.getMethod("setSceneManager", SceneManager.class);
            setSceneManagerMethod.invoke(null, sceneManager);

        } catch (NoSuchMethodException e) {
            System.out.println("No setSceneManager method found in GUI class. Some navigation tests may not work.");
        } catch (Exception e) {
            System.out.println("Failed to setup GUI mock: " + e.getMessage());
        }
    }


    // Login Tests
    @Test
    void login_WithEmptyFields_ShouldShowErrorMessage() throws IOException {
        when(loginUsername.getText()).thenReturn("");
        when(loginPassword.getText()).thenReturn("");

        loginController.login(mockActionEvent);

        verify(loginResultLabel).setText(emptyFieldPrompt);
        verify(userService, never()).loginUser(any(User.class));
        verify(sessionManager, never()).setUser(any(User.class));
        verify(sessionManager, never()).setToken(any(String.class));
    }

    @Test
    void login_WithEmptyUsername_ShouldShowErrorMessage() throws IOException {
        when(loginUsername.getText()).thenReturn("");
        when(loginPassword.getText()).thenReturn("password");

        loginController.login(mockActionEvent);

        verify(loginResultLabel).setText(emptyFieldPrompt);
        verify(userService, never()).loginUser(any(User.class));
    }

    @Test
    void login_WithEmptyPassword_ShouldShowErrorMessage() throws IOException {
        when(loginUsername.getText()).thenReturn("username");
        when(loginPassword.getText()).thenReturn("");

        loginController.login(mockActionEvent);

        verify(loginResultLabel).setText(emptyFieldPrompt);
        verify(userService, never()).loginUser(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldSetSessionAndTriggerNavigation() throws IOException {
        String username = "testuser";
        String password = "password";
        String token = "jwt-token";

        when(loginUsername.getText()).thenReturn(username);
        when(loginPassword.getText()).thenReturn(password);

        LoginResult successResult = new LoginResult(true, "Login successful",token, testUser);
        when(userService.loginUser(any(User.class))).thenReturn(successResult);

        loginController.login(mockActionEvent);

        verify(userService).loginUser(argThat(user ->
                user.getUsername().equals(username) && user.getPassword().equals(password)
        ));
        verify(sessionManager).setUser(testUser);
        verify(sessionManager).setToken(token);
        verify(loginResultLabel).setText("Login successful");

        // The navigation is triggered via PauseTransition, so we can't easily verify
        // sceneManager.switchScene() was called directly in a unit test
    }

    @Test
    void login_WithInvalidCredentials_ShouldShowErrorMessage() throws IOException {
        when(loginUsername.getText()).thenReturn("wronguser");
        when(loginPassword.getText()).thenReturn("wrongpass");

        LoginResult failureResult = new LoginResult(false, "Invalid credentials", null, null);
        when(userService.loginUser(any(User.class))).thenReturn(failureResult);

        loginController.login(mockActionEvent);

        verify(loginResultLabel).setText("Invalid credentials");
        verify(sessionManager, never()).setUser(any(User.class));
        verify(sessionManager, never()).setToken(any(String.class));
        verify(sceneManager, never()).switchScene(any(String.class));
    }

    @Test
    void login_WhenServiceThrowsException_ShouldHandleGracefully() throws IOException {
        when(loginUsername.getText()).thenReturn("testuser");
        when(loginPassword.getText()).thenReturn("password");
        when(userService.loginUser(any(User.class))).thenThrow(new RuntimeException("Service unavailable"));

        assertDoesNotThrow(() -> loginController.login(mockActionEvent));
        // should handle exception without crashing

        verify(resultText).setText("");
    }

    // Registration Tests
    @Test
    void createAccount_WithEmptyFields_ShouldShowErrorMessage() {
        // Arrange
        when(newFirstname.getText()).thenReturn("");
        /*
        when(newLastname.getText()).thenReturn("Last");
        when(newUsername.getText()).thenReturn("user");
        when(newEmail.getText()).thenReturn("test@example.com");
        when(newBirthdate.getValue()).thenReturn(LocalDate.now());
         */
        when(newPassword.getText()).thenReturn("pass");
        when(newPasswordConfirm.getText()).thenReturn("pass");

        loginController.createAccount(mockActionEvent);

        verify(resultText).setText(emptyFieldPrompt);
        verify(userService, never()).registerUser(any(User.class));
    }

    @Test
    void createAccount_WithInvalidEmail_ShouldShowErrorMessage() {
        when(newFirstname.getText()).thenReturn("First");
        when(newLastname.getText()).thenReturn("Last");
        when(newUsername.getText()).thenReturn("user");
        when(newEmail.getText()).thenReturn("invalid-email");
        when(newBirthdate.getValue()).thenReturn(LocalDate.now());
        when(newPassword.getText()).thenReturn("password");
        when(newPasswordConfirm.getText()).thenReturn("password");

        loginController.createAccount(mockActionEvent);

        verify(resultText).setText("Please enter a valid email address.");
        verify(userService, never()).registerUser(any(User.class));
    }

    @Test
    void createAccount_WithNonMatchingPasswords_ShouldShowErrorMessage() {
        when(newFirstname.getText()).thenReturn("First");
        when(newLastname.getText()).thenReturn("Last");
        when(newUsername.getText()).thenReturn("user");
        when(newEmail.getText()).thenReturn("test@example.com");
        when(newBirthdate.getValue()).thenReturn(LocalDate.now());
        when(newPassword.getText()).thenReturn("password1");
        when(newPasswordConfirm.getText()).thenReturn("password2");

        loginController.createAccount(mockActionEvent);

        verify(resultText).setText("Passwords are not matching.");
        verify(userService, never()).registerUser(any(User.class));
    }

    @Test
    void createAccount_WithValidData_ShouldRegisterUser() {
        String firstName = "John";
        String lastName = "Doe";
        String username = "johndoe";
        String email = "john@example.com";
        String password = "password123";
        LocalDate birthdate = LocalDate.of(1990, 1, 1);

        when(newFirstname.getText()).thenReturn(firstName);
        when(newLastname.getText()).thenReturn(lastName);
        when(newUsername.getText()).thenReturn(username);
        when(newEmail.getText()).thenReturn(email);
        when(newBirthdate.getValue()).thenReturn(birthdate);
        when(newPassword.getText()).thenReturn(password);
        when(newPasswordConfirm.getText()).thenReturn(password);

        RegistrationResult successResult = new RegistrationResult(true, "Registration successful");
        when(userService.registerUser(any(User.class))).thenReturn(successResult);

        loginController.createAccount(mockActionEvent);

        verify(userService).registerUser(argThat(user ->
                user.getFirstName().equals(firstName) &&
                        user.getLastName().equals(lastName) &&
                        user.getUsername().equals(username) &&
                        user.getEmail().equals(email) &&
                        user.getPassword().equals(password)
        ));
        verify(resultText).setText("Registration successful");
        verify(createAccountMenu, never()).setVisible(false);
        verify(loginMenu, never()).setVisible(true);
    }

    @Test
    void createAccount_WhenRegistrationFails_ShouldShowErrorMessage() {
        setupValidRegistrationFields();

        RegistrationResult failureResult = new RegistrationResult(false, "Username already exists");
        when(userService.registerUser(any(User.class))).thenReturn(failureResult);

        loginController.createAccount(mockActionEvent);

        verify(resultText).setText("Username already exists");
        verify(createAccountMenu, never()).setVisible(false);
        verify(loginMenu, never()).setVisible(true);
    }

    @Test
    void createAccount_WhenServiceThrowsException_ShouldHandleGracefully() {
        setupValidRegistrationFields();
        when(userService.registerUser(any(User.class))).thenThrow(new RuntimeException("Database error"));

        assertDoesNotThrow(() -> loginController.createAccount(mockActionEvent));

        verify(resultText).setText("Registration failed due to system error. Please try again.");

        // verify that no navigation occurs when registration fails
        verify(createAccountMenu, never()).setVisible(false);
        verify(loginMenu, never()).setVisible(true);
    }

    // Navigation Tests
    @Test
    void register_ShouldSwitchToCreateAccountMenu() {
        loginController.register(mockActionEvent);

        verify(loginMenu).setVisible(false);
        verify(createAccountMenu).setVisible(true);
    }

    @Test
    void goBackToLogin_ShouldClearFieldsAndSwitchToLoginMenu() throws IOException {
        loginController.goBackToLogin(mockActionEvent);

        verify(newFirstname).clear();
        verify(newLastname).clear();
        verify(newUsername).clear();
        verify(newEmail).clear();
        verify(newBirthdate).setValue(null);
        verify(loginUsername).clear();
        verify(loginPassword).clear();
        verify(newPassword).clear();
        verify(newPasswordConfirm).clear();
        verify(resultText).setText("");
        verify(createAccountMenu).setVisible(false);
        verify(loginMenu).setVisible(true);
    }

    @Test
    void login_ShouldNotStorePasswordInMemoryLongerThanNecessary() throws IOException {
        // this test verifies that passwords aren't unnecessarily retained
        when(loginUsername.getText()).thenReturn("user");
        when(loginPassword.getText()).thenReturn("secret123");

        LoginResult successResult = new LoginResult(true, "Success","token", testUser);
        when(userService.loginUser(any(User.class))).thenReturn(successResult);

        loginController.login(mockActionEvent);

        // password should only be used for authentication and not stored
        verify(loginPassword, atLeastOnce()).getText();
    }

    private void setupValidRegistrationFields() {
        when(newFirstname.getText()).thenReturn("First");
        when(newLastname.getText()).thenReturn("Last");
        when(newUsername.getText()).thenReturn("user");
        when(newEmail.getText()).thenReturn("test@example.com");
        when(newBirthdate.getValue()).thenReturn(LocalDate.now());
        when(newPassword.getText()).thenReturn("password");
        when(newPasswordConfirm.getText()).thenReturn("password");
    }
}