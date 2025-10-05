package application.model.service;

import application.controller.SessionManager;
import application.model.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private User testUser;
    private AuthService authService;
    private String token;

    @BeforeEach
    void setup() {
        authService = new AuthService();
        testUser = new User(
                "testname",
                "testlastname",
                "test@example.com",
                "testusername",
                "1.1.1999",
                "password"
        );
        token = authService.createToken(testUser);
    }

    @Test
    void testCreateToken() {
        assertNotNull(token);
    }

    @Test
    void testVerifyToken() {
        assertNotNull(authService.authMe(token));
    }
}