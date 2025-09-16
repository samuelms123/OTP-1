package application.model.service;

import application.model.data_objects.LoginResult;
import application.model.data_objects.RegistrationResult;
import application.model.entity.User;
import at.favre.lib.crypto.bcrypt.BCrypt;
import config.Config;
import dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // init mocks
        user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("password");

    }
    @Test
    void registerShouldFailWhenEmailIsTaken() {
        when(userDao.isUserEmailUnique(user.getEmail())).thenReturn(false); // mock behaviour

        RegistrationResult result = userService.registerUser(user);
        assertFalse(result.isSuccess()); // assert registration failure
        assertEquals("Email already taken", result.getMessage());
        verify(userDao, never()).persist(any(User.class)); // Basically verifies that persist method is not called during this test, because the email is already taken.
    }

    @Test
    void registerShouldFailWhenUsernameIsTaken() {
        when(userDao.isUserEmailUnique(user.getEmail())).thenReturn(true); // Need to mock the email to pass because of registerUser() execute order
        when(userDao.isUserNameUnique(user.getUsername())).thenReturn(false);

        RegistrationResult result = userService.registerUser(user);
        assertFalse(result.isSuccess());
        assertEquals("Username already taken", result.getMessage());
        verify(userDao, never()).persist(any(User.class));
    }

    @Test
    void shouldRegisterSuccessfully() {
        when(userDao.isUserEmailUnique(user.getEmail())).thenReturn(true);
        when(userDao.isUserNameUnique(user.getUsername())).thenReturn(true);

        RegistrationResult result = userService.registerUser(user);
        assertTrue(result.isSuccess());
        assertEquals("User registered successfully", result.getMessage());
        verify(userDao).persist(user); // Verify that the persist method is called!
    }

    @Test
    void loginShouldFail() {
        when(userDao.findUser(user.getUsername())).thenReturn(null);

        LoginResult result = userService.loginUser(user);
        assertFalse(result.isSuccess());
        assertEquals("User not found", result.getMessage());
    }

    @Test
    void loginShouldSuccess() {
        when(userDao.findUser(user.getUsername())).thenReturn(user);
        user.setPassword(BCrypt.withDefaults().hashToString(Integer.parseInt(Config.SALT_ROUNDS), user.getPassword().toCharArray()));

        User logInUser = new User("testuser", "password");
        LoginResult result = userService.loginUser(logInUser);
        assertTrue(result.isSuccess());
        assertEquals("User logged in successfully", result.getMessage());
    }
}