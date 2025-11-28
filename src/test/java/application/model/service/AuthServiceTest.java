package application.model.service;

import application.model.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import config.Config;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private User testUser;
    private AuthService authService;
    private String token;

    // helper methods
    private User createTestUser() {
        return createTestUser(123, "testuser");
    }

    private User createTestUser(int id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        return user;
    }

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

    @Nested
    class TokenCreationTests {

        @Test
        void testCreateToken_WithValidUser_ReturnsValidJWT() {
            // arrange
            User user = createTestUser();
            AuthService authService = new AuthService();

            // act
            String token = authService.createToken(user);

            // assert
            assertNotNull(token, "Token should not be null");
            assertFalse(token.isEmpty(), "Token should not be empty");
            assertEquals(3, token.split("\\.").length, "Token should have 3 JWT parts");

            // verify token can be decoded
            DecodedJWT decoded = JWT.decode(token);
            assertEquals(user.getUsername(), decoded.getClaim("userName").asString());
            assertEquals(user.getId(), decoded.getClaim("userId").asInt());
            assertEquals(user.getFirstName(), decoded.getClaim("firstName").asString());
            assertEquals(user.getLastName(), decoded.getClaim("lastName").asString());
        }

        @Test
        void testCreateToken_WithNullUser_ReturnsNull() {
            // arrange
            AuthService authService = new AuthService();

            // act
            String token = authService.createToken(null);

            // assert
            assertNull(token, "Token should be null for null user");
        }

        @Test
        void testCreateToken_WithPartialUserData_ReturnsValidToken() {
            // arrange
            User user = new User();
            user.setId(123);
            user.setUsername("testuser");
            // intentionally leaving firstName, lastName as null
            AuthService authService = new AuthService();

            // act
            String token = authService.createToken(user);

            // assert
            assertNotNull(token, "Token should handle null names gracefully");

            DecodedJWT decoded = JWT.decode(token);
            assertEquals("testuser", decoded.getClaim("userName").asString());
            assertEquals(123, decoded.getClaim("userId").asInt());
            // should handle null names without crashing
            assertDoesNotThrow(() -> decoded.getClaim("firstName").asString());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        void testCreateToken_WithEmptyUserData_HandlesGracefully(String emptyValue) {
            // arrange
            User user = new User();
            user.setId(1);
            user.setUsername(emptyValue);
            user.setFirstName(emptyValue);
            user.setLastName(emptyValue);
            AuthService authService = new AuthService();

            // act & assert
            assertDoesNotThrow(() -> authService.createToken(user));
        }
    }

}









