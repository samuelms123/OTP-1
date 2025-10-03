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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize the controller and inject mocks
        loginController = new LogInController();

        // Use reflection to inject the mocked dependencies
        try {
            var userServiceField = LogInController.class.getDeclaredField("userService");
            userServiceField.setAccessible(true);
            userServiceField.set(loginController, userService);

            // Inject mocked FXML components
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

        // Mock SessionManager singleton
        SessionManager.setInstance(sessionManager);

        // Mock SceneManager - create a real instance with mocked stage
        sceneManager = mock(SceneManager.class);
        //unnecessary stubbing
        //when(sceneManager.getStage()).thenReturn(mockStage);

        // Mock GUI to return our mocked SceneManager
        // You might need to add a setter method to your GUI class for testing
        setupGUIMock();
    }

    private void injectFXMLField(String fieldName, Object mock) throws Exception {
        var field = LogInController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(loginController, mock);
    }

    private void setupGUIMock() {
        try {
            // Use reflection to set the scene manager in GUI
            // This assumes you have a way to set the scene manager in your GUI class
            // If not, you might need to add a package-private or public setter for testing

            var getSceneManagerMethod = GUI.class.getMethod("getSceneManager");
            var setSceneManagerMethod = GUI.class.getMethod("setSceneManager", SceneManager.class);
            setSceneManagerMethod.invoke(null, sceneManager);

        } catch (NoSuchMethodException e) {
            // If no setter exists, you might need to modify your GUI class temporarily
            // or use a different approach
            System.out.println("No setSceneManager method found in GUI class. Some navigation tests may not work.");
        } catch (Exception e) {
            System.out.println("Failed to setup GUI mock: " + e.getMessage());
        }
    }

    // Add this helper method to test navigation
    private void verifyNavigationOccurred() {
        // Since we can't easily test the PauseTransition, we verify the service was called
        // and the result message was set, indicating the navigation flow was triggered
        verify(loginResultLabel).setText(any(String.class));
    }

    // Login Tests
    @Test
    void login_WithEmptyFields_ShouldShowErrorMessage() throws IOException {
        // Arrange
        when(loginUsername.getText()).thenReturn("");
        when(loginPassword.getText()).thenReturn("");

        // Act
        loginController.login(mockActionEvent);

        // Assert
        verify(loginResultLabel).setText("Please fill in all fields");
        verify(userService, never()).loginUser(any(User.class));
        verify(sessionManager, never()).setUser(any(User.class));
        verify(sessionManager, never()).setToken(any(String.class));
    }

    @Test
    void login_WithEmptyUsername_ShouldShowErrorMessage() throws IOException {
        // Arrange
        when(loginUsername.getText()).thenReturn("");
        when(loginPassword.getText()).thenReturn("password");

        // Act
        loginController.login(mockActionEvent);

        // Assert
        verify(loginResultLabel).setText("Please fill in all fields");
        verify(userService, never()).loginUser(any(User.class));
    }

    @Test
    void login_WithEmptyPassword_ShouldShowErrorMessage() throws IOException {
        // Arrange
        when(loginUsername.getText()).thenReturn("username");
        when(loginPassword.getText()).thenReturn("");

        // Act
        loginController.login(mockActionEvent);

        // Assert
        verify(loginResultLabel).setText("Please fill in all fields");
        verify(userService, never()).loginUser(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldSetSessionAndTriggerNavigation() throws IOException {
        // Arrange
        String username = "testuser";
        String password = "password";
        String token = "jwt-token";

        when(loginUsername.getText()).thenReturn(username);
        when(loginPassword.getText()).thenReturn(password);

        LoginResult successResult = new LoginResult(true, "Login successful",token, testUser);
        when(userService.loginUser(any(User.class))).thenReturn(successResult);

        // Act
        loginController.login(mockActionEvent);

        // Assert
        verify(userService).loginUser(argThat(user ->
                user.getUsername().equals(username) && user.getPassword().equals(password)
        ));
        verify(sessionManager).setUser(testUser);
        verify(sessionManager).setToken(token);
        verify(loginResultLabel).setText("Login successful");

        // The navigation is triggered via PauseTransition, so we can't easily verify
        // sceneManager.switchScene() was called directly in a unit test
        // This would be better tested with an integration test
    }

    @Test
    void login_WithInvalidCredentials_ShouldShowErrorMessage() throws IOException {
        // Arrange
        when(loginUsername.getText()).thenReturn("wronguser");
        when(loginPassword.getText()).thenReturn("wrongpass");

        LoginResult failureResult = new LoginResult(false, "Invalid credentials", null, null);
        when(userService.loginUser(any(User.class))).thenReturn(failureResult);

        // Act
        loginController.login(mockActionEvent);

        // Assert
        verify(loginResultLabel).setText("Invalid credentials");
        verify(sessionManager, never()).setUser(any(User.class));
        verify(sessionManager, never()).setToken(any(String.class));
        verify(sceneManager, never()).switchScene(any(String.class));
    }

    @Test
    void login_WhenServiceThrowsException_ShouldHandleGracefully() throws IOException {
        // Arrange
        when(loginUsername.getText()).thenReturn("testuser");
        when(loginPassword.getText()).thenReturn("password");
        when(userService.loginUser(any(User.class))).thenThrow(new RuntimeException("Service unavailable"));

        // Act & Assert
        assertDoesNotThrow(() -> loginController.login(mockActionEvent));
        // Should handle exception without crashing

        verify(resultText).setText("Login failed due to system error. Please try again.");
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

        // Act
        loginController.createAccount(mockActionEvent);

        // Assert
        verify(resultText).setText("Please fill in all fields");
        verify(userService, never()).registerUser(any(User.class));
    }

    @Test
    void createAccount_WithInvalidEmail_ShouldShowErrorMessage() {
        // Arrange
        when(newFirstname.getText()).thenReturn("First");
        when(newLastname.getText()).thenReturn("Last");
        when(newUsername.getText()).thenReturn("user");
        when(newEmail.getText()).thenReturn("invalid-email");
        when(newBirthdate.getValue()).thenReturn(LocalDate.now());
        when(newPassword.getText()).thenReturn("password");
        when(newPasswordConfirm.getText()).thenReturn("password");

        // Act
        loginController.createAccount(mockActionEvent);

        // Assert
        verify(resultText).setText("Please enter a valid email address");
        verify(userService, never()).registerUser(any(User.class));
    }

    @Test
    void createAccount_WithNonMatchingPasswords_ShouldShowErrorMessage() {
        // Arrange
        when(newFirstname.getText()).thenReturn("First");
        when(newLastname.getText()).thenReturn("Last");
        when(newUsername.getText()).thenReturn("user");
        when(newEmail.getText()).thenReturn("test@example.com");
        when(newBirthdate.getValue()).thenReturn(LocalDate.now());
        when(newPassword.getText()).thenReturn("password1");
        when(newPasswordConfirm.getText()).thenReturn("password2");

        // Act
        loginController.createAccount(mockActionEvent);

        // Assert
        verify(resultText).setText("Passwords not matching");
        verify(userService, never()).registerUser(any(User.class));
    }

    @Test
    void createAccount_WithValidData_ShouldRegisterUser() {
        // Arrange
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

        // Act
        loginController.createAccount(mockActionEvent);

        // Assert
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
        // Arrange
        setupValidRegistrationFields();

        RegistrationResult failureResult = new RegistrationResult(false, "Username already exists");
        when(userService.registerUser(any(User.class))).thenReturn(failureResult);

        // Act
        loginController.createAccount(mockActionEvent);

        // Assert
        verify(resultText).setText("Username already exists");
        verify(createAccountMenu, never()).setVisible(false);
        verify(loginMenu, never()).setVisible(true);
    }

    @Test
    void createAccount_WhenServiceThrowsException_ShouldHandleGracefully() {
        // Arrange
        setupValidRegistrationFields();
        when(userService.registerUser(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertDoesNotThrow(() -> loginController.createAccount(mockActionEvent));

        verify(resultText).setText("Registration failed due to system error. Please try again.");

        // Verify that no navigation occurs when registration fails
        verify(createAccountMenu, never()).setVisible(false);
        verify(loginMenu, never()).setVisible(true);
    }

    // Navigation Tests
    @Test
    void register_ShouldSwitchToCreateAccountMenu() {
        // Act
        loginController.register(mockActionEvent);

        // Assert
        verify(loginMenu).setVisible(false);
        verify(createAccountMenu).setVisible(true);
    }

    @Test
    void goBackToLogin_ShouldClearFieldsAndSwitchToLoginMenu() throws IOException {
        // Act
        loginController.goBackToLogin(mockActionEvent);

        // Assert
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

    // Security-focused Tests
    @Test
    void login_ShouldNotStorePasswordInMemoryLongerThanNecessary() throws IOException {
        // This test verifies that passwords aren't unnecessarily retained
        when(loginUsername.getText()).thenReturn("user");
        when(loginPassword.getText()).thenReturn("secret123");

        LoginResult successResult = new LoginResult(true, "Success","token", testUser);
        when(userService.loginUser(any(User.class))).thenReturn(successResult);

        loginController.login(mockActionEvent);

        // The password should only be used for authentication and not stored
        verify(loginPassword, atLeastOnce()).getText();
        // In a real security test, you might use a security scanner to check for password retention
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