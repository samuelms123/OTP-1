package application.controller;

import application.model.entity.User;
import application.model.service.AuthService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    private User testUser;
    private User testUser2;
    private String testToken;
    private AuthService authService;

    @BeforeEach
    void setup() {
        SessionManager.getInstance().reset();
        authService = new AuthService();
        testUser = new User("testname", "testlastname", "test@example.com", "testusername", "1.1.1999", "password");
        testUser2 = new User("testname2", "testlastname2", "test2@example.com", "testusername2", "1.1.1999", "password2");
        testToken = authService.createToken(testUser);
        SessionManager.getInstance().setUser(testUser);
        SessionManager.getInstance().setToken(testToken);
    }

    @Test
    void getInstance() {
        SessionManager instance = SessionManager.getInstance();
        assertNotNull(instance);
    }

    @Test
    void getUser() {
        User user = SessionManager.getInstance().getUser();
        assertNotNull(user);
        assertEquals(testUser.getUsername(), user.getUsername());
        assertEquals(testUser.getPassword(), user.getPassword());
        assertEquals(testUser.getEmail(), user.getEmail());
        assertEquals(testUser.getFirstName(), user.getFirstName());
        assertEquals(testUser.getLastName(), user.getLastName());
        assertEquals(testUser.getBirthdate(), user.getBirthdate());
    }

    @Test
    void setUser() {
        SessionManager.getInstance().setUser(testUser2);
        User user = SessionManager.getInstance().getUser();
        assertEquals(testUser2.getUsername(), user.getUsername());
        assertEquals(testUser2.getPassword(), user.getPassword());
        assertEquals(testUser2.getEmail(), user.getEmail());
        assertEquals(testUser2.getFirstName(), user.getFirstName());
        assertEquals(testUser2.getLastName(), user.getLastName());
        assertEquals(testUser2.getBirthdate(), user.getBirthdate());
    }

    @Test
    void getToken() {
        String token = SessionManager.getInstance().getToken();
        assertNotNull(token);
        assertEquals(testToken, token);
    }

    @Test
    void setToken() {
        String token2 = "dsadasfdfafa";
        SessionManager.getInstance().setToken(token2);
        String token = SessionManager.getInstance().getToken();
        assertNotNull(token);
        assertEquals(token2, token);
    }
}