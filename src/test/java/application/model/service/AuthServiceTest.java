package application.model.service;

import application.controller.SessionManager;
import application.model.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private static User testUser;
    private static AuthService authService;
    private static String token;

    @BeforeAll
    static void setup() {
        authService = new AuthService();
        testUser = new User("testname", "testlastname", "test@example.com", "testusername", "1.1.1999", "password");
    }


    @Test
    void testCreateToken() {
        String token = authService.createToken(testUser);
        assertNotNull(token);
        this.token = token;
    }

    @Test
    void testVerifyToken() {
        assertNotNull(authService.authMe(token));
        assertNull(authService.authMe("wrong token"));
    }


}