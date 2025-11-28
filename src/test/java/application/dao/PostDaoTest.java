package application.dao;

import application.model.entity.Post;
import application.model.entity.User;
import dao.PostDao;
import datasource.MariaDbJpaConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostDaoTest {

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private EntityTransaction mockTransaction;

    @Mock
    private TypedQuery<Post> mockQuery;

    private PostDao postDao;
    private Post testPost;
    private User testUser;

    @BeforeEach
    void setUp() {
        postDao = new PostDao();
        testUser = createTestUser(1, "testuser");

        testPost = new Post(
                testUser.getId(),
                "Test post content",
                "http://example.com/image.jpg",
                Timestamp.from(Instant.now()),
                "en_US"
        );
        testPost.setId(1);
    }

    // post presistence tests

    @Test
    void testPersist_ValidPost_Success() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);

            // act
            postDao.persist(testPost);

            // assert
            verify(mockEntityManager, atLeastOnce()).getTransaction();
            verify(mockTransaction).begin();
            verify(mockEntityManager).persist(testPost);
            verify(mockTransaction).commit();
        }
    }

    @Test
    void testPersist_NullPost_ThrowsException() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);
            doThrow(new IllegalArgumentException()).when(mockEntityManager).persist(null);

            // act & assert
            assertThrows(IllegalArgumentException.class, () -> postDao.persist(null));
        }
    }

    @Test
    void testPersist_TransactionRollback_OnException() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);
            doThrow(new RuntimeException("Database error")).when(mockEntityManager).persist(any(Post.class));

            // act & assert
            assertThrows(RuntimeException.class, () -> postDao.persist(testPost));
        }
    }

    // find posts by users tests

    @Test
    void testFindPostsByUsers_WithValidUsers_ReturnsPosts() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            Set<User> users = new HashSet<>();
            users.add(testUser);

            User user2 = createTestUser(2, "user2");
            users.add(user2);

            List<Post> expectedPosts = Arrays.asList(
                    createTestPost(1, testUser.getId()),
                    createTestPost(2, user2.getId())
            );

            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString(), eq(Post.class))).thenReturn(mockQuery);
            when(mockQuery.setParameter(anyString(), anyList())).thenReturn(mockQuery);
            when(mockQuery.getResultList()).thenReturn(expectedPosts);

            // act
            List<Post> result = postDao.findPostsByUsers(users);

            // assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(expectedPosts, result);

            //verify(mockQuery).setParameter("userIds", Arrays.asList(2, 1));
        }
    }

    @Test
    void testFindPostsByUsers_WithEmptyUsers_ReturnsEmptyList() {
        // act
        List<Post> result = postDao.findPostsByUsers(new HashSet<>());

        // assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindPostsByUsers_WithNullUsers_ReturnsEmptyList() {
        // act
        List<Post> result = postDao.findPostsByUsers(null);

        // assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindPostsByUsers_WhenDatabaseException_ReturnsEmptyList() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            Set<User> users = new HashSet<>();
            users.add(testUser);

            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString(), eq(Post.class))).thenThrow(new RuntimeException("DB Error"));

            // act
            List<Post> result = postDao.findPostsByUsers(users);

            // assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testFindPostsByUsers_WithSingleUser_ReturnsUserPosts() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            Set<User> users = Collections.singleton(testUser);
            List<Post> userPosts = Arrays.asList(
                    createTestPost(1, testUser.getId()),
                    createTestPost(2, testUser.getId()),
                    createTestPost(3, testUser.getId())
            );

            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString(), eq(Post.class))).thenReturn(mockQuery);
            when(mockQuery.setParameter(anyString(), anyList())).thenReturn(mockQuery);
            when(mockQuery.getResultList()).thenReturn(userPosts);

            // act
            List<Post> result = postDao.findPostsByUsers(users);

            // assert
            assertEquals(3, result.size());
            assertTrue(result.stream().allMatch(post -> post.getUserId() == testUser.getId()));
        }
    }

    // delete post tests

    @Test
    void testDeletePostById_ExistingPost_ReturnsTrue() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            Post existingPost = createTestPost(1, testUser.getId());

            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);
            when(mockEntityManager.find(Post.class, 1)).thenReturn(existingPost);

            // act
            boolean result = postDao.deletePostById(1);

            // assert
            assertTrue(result);
            verify(mockEntityManager).find(Post.class, 1);
            verify(mockEntityManager).remove(existingPost);
            verify(mockTransaction).commit();
        }
    }

    @Test
    void testDeletePostById_NonExistingPost_ReturnsFalse() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);
            when(mockEntityManager.find(Post.class, 999)).thenReturn(null);

            // act
            boolean result = postDao.deletePostById(999);

            // assert
            assertFalse(result);
            verify(mockTransaction).rollback();
            verify(mockEntityManager, never()).remove(any());
        }
    }

    @Test
    void testDeletePostById_WhenExceptionOccurs_ReturnsFalse() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);
            when(mockEntityManager.find(Post.class, 1)).thenThrow(new RuntimeException("DB Error"));

            // act
            boolean result = postDao.deletePostById(1);

            // assert
            assertFalse(result);
        }
    }

    // find all posts tests

    @Test
    void testFindAll_WithExistingPosts_ReturnsAllPosts() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            List<Post> allPosts = Arrays.asList(
                    createTestPost(1, 1),
                    createTestPost(2, 2),
                    createTestPost(3, 1)
            );

            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString())).thenReturn(mockQuery);
            when(mockQuery.getResultList()).thenReturn(allPosts);

            // act
            List<Post> result = postDao.findAll();

            // assert
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals(allPosts, result);
        }
    }

    @Test
    void testFindAll_WhenDatabaseException_ReturnsEmptyList() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString())).thenThrow(new RuntimeException("DB Error"));

            // act
            List<Post> result = postDao.findAll();

            // assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testFindAll_WithNoPosts_ReturnsEmptyList() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString())).thenReturn(mockQuery);
            when(mockQuery.getResultList()).thenReturn(new ArrayList<>());

            // act
            List<Post> result = postDao.findAll();

            // assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // helper methods

    private User createTestUser(int id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(username + "@test.com");
        return user;
    }

    private Post createTestPost(int postId, int userId) {
        Post post = new Post(
                userId,
                "Test content for post " + postId,
                "image" + postId + ".jpg",
                Timestamp.from(Instant.now()),
                "en_US"
        );
        post.setId(postId);
        return post;
    }
}