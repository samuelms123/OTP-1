package application.dao;

import application.model.entity.User;
import dao.UserDao;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoIntegrationTest {

    private UserDao userDao;
    private User testUser;

    @BeforeAll
    void setup() {
        userDao = new UserDao();
        // Ensure clean state
        userDao.deleteAll();
    }

    @BeforeEach
    void init() {
        testUser = new User("John", "Doe", "john@test.com",
                "johndoe", "1990-01-01", "password123");
    }

    @AfterEach
    void cleanup() {
        userDao.deleteAll();
    }

    @Test
    void persist_ShouldSaveUserToDatabase() {
        userDao.persist(testUser);

        User found = userDao.findUser("johndoe");
        assertNotNull(found);
        assertEquals("john@test.com", found.getEmail());
    }

    @Test
    void findUser_WithExistingUsername_ShouldReturnUser() {
        userDao.persist(testUser);

        User found = userDao.findUser("johndoe");

        assertNotNull(found);
        assertEquals("johndoe", found.getUsername());
    }

    @Test
    void findUser_WithNonExistingUsername_ShouldReturnNull() {
        User found = userDao.findUser("nonexistent");

        assertNull(found);
    }

    @Test
    void isUserNameUnique_WithNewUsername_ShouldReturnTrue() {
        assertTrue(userDao.isUserNameUnique("uniqueuser"));
    }

    @Test
    void isUserNameUnique_WithExistingUsername_ShouldReturnFalse() {
        userDao.persist(testUser);

        assertFalse(userDao.isUserNameUnique("johndoe"));
    }

    @Test
    void merge_ShouldUpdateExistingUser() {
        userDao.persist(testUser);
        User userToUpdate = userDao.findUser("johndoe");
        userToUpdate.setFirstName("Jane");

        boolean result = userDao.merge(userToUpdate);

        assertTrue(result);
        User updated = userDao.findUser("johndoe");
        assertEquals("Jane", updated.getFirstName());
    }

    @Test
    void findUsersByQuery_ShouldReturnMatchingUsers() {
        User user1 = new User("Alice", "Smith", "alice@test.com",
                "alice", "1990-01-01", "password");
        User user2 = new User("Alex", "Jones", "alex@test.com",
                "alexj", "1990-01-01", "password");
        userDao.persist(user1);
        userDao.persist(user2);

        List<String> results = userDao.findUsersByQuery("al", "exclude");

        assertNotNull(results);
        assertTrue(results.contains("alice"));
        assertTrue(results.contains("alexj"));
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        userDao.persist(testUser);
        User user2 = new User("Jane", "Doe", "jane@test.com",
                "janedoe", "1990-01-01", "password");
        userDao.persist(user2);

        List<User> users = userDao.findAll();

        assertNotNull(users);
        assertEquals(2, users.size());
    }
}