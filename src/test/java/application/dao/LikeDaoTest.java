package application.dao;

import application.model.entity.Like;
import application.model.entity.Post;
import application.model.entity.User;
import dao.LikeDao;
import datasource.MariaDbJpaConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeDaoTest {

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private EntityTransaction mockTransaction;

    private LikeDao likeDao;
    private User testUser;
    private Post testPost;
    private Like testLike;

    @BeforeEach
    void setUp() {
        likeDao = new LikeDao();
        testUser = createTestUser(1, "testuser");
        testPost = createTestPost(10, testUser.getId());
        testLike = new Like(testUser, testPost);
    }

    // like persisting tests

    @Test
    void testPersist_ValidLike_Success() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);

            // act
            likeDao.persist(testLike);

            // assert
            verify(mockEntityManager, atLeastOnce()).getTransaction();
            verify(mockTransaction).begin();
            verify(mockEntityManager).persist(testLike);
            verify(mockTransaction).commit();
        }
    }

    @Test
    void testPersist_NullLike_ThrowsException() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);
            doThrow(new IllegalArgumentException()).when(mockEntityManager).persist(null);

            // act & assert
            assertThrows(IllegalArgumentException.class, () -> likeDao.persist(null));
        }
    }

    // find likes by post id tests

    @Test
    void testFindLikesByPostId_WithExistingLikes_ReturnsLikes() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            User user2 = createTestUser(2, "user2");
            User user3 = createTestUser(3, "user3");

            List<Like> expectedLikes = Arrays.asList(
                    new Like(testUser, testPost),
                    new Like(user2, testPost),
                    new Like(user3, testPost)
            );

            TypedQuery<Like> mockLikeQuery = mock(TypedQuery.class);

            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString(), eq(Like.class))).thenReturn(mockLikeQuery);
            when(mockLikeQuery.setParameter(eq("postId"), eq(10))).thenReturn(mockLikeQuery);
            when(mockLikeQuery.getResultList()).thenReturn(expectedLikes);

            // act
            List<Like> result = likeDao.findLikesByPostId(10);

            // assert
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals(expectedLikes, result);

            // verify all likes are for the correct post
            assertTrue(result.stream().allMatch(like -> like.getPost().getId() == 10));
        }
    }

    @Test
    void testFindLikesByPostId_WithNoLikes_ReturnsEmptyList() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            TypedQuery<Like> mockLikeQuery = mock(TypedQuery.class);

            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString(), eq(Like.class))).thenReturn(mockLikeQuery);
            when(mockLikeQuery.setParameter(eq("postId"), eq(99))).thenReturn(mockLikeQuery);
            when(mockLikeQuery.getResultList()).thenReturn(Arrays.asList());

            // act
            List<Like> result = likeDao.findLikesByPostId(99);

            // assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testFindLikesByPostId_WhenDatabaseException_ReturnsEmptyList() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString(), eq(Like.class))).thenThrow(new RuntimeException("DB Error"));

            // act
            List<Like> result = likeDao.findLikesByPostId(10);

            // assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // check if user liked post tests

    @Test
    void testCheckIfUserLikedPost_UserHasLiked_ReturnsTrue() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.find(eq(Like.class), any(Like.LikeId.class))).thenReturn(testLike);

            // act
            boolean result = likeDao.checkIfUserLikedPost(1, 10);

            // assert
            assertTrue(result);

            // verify the correct LikeId was used
            verify(mockEntityManager).find(eq(Like.class), argThat(likeId ->
                    likeId instanceof Like.LikeId &&
                            ((Like.LikeId) likeId).getUserId() == 1 &&
                            ((Like.LikeId) likeId).getPostId() == 10
            ));
        }
    }

    @Test
    void testCheckIfUserLikedPost_UserHasNotLiked_ReturnsFalse() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.find(eq(Like.class), any(Like.LikeId.class))).thenReturn(null);

            // act
            boolean result = likeDao.checkIfUserLikedPost(2, 10);

            // assert
            assertFalse(result);
        }
    }

    @Test
    void testCheckIfUserLikedPost_WhenExceptionOccurs_ReturnsFalse() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.find(eq(Like.class), any(Like.LikeId.class))).thenThrow(new RuntimeException("DB Error"));

            // act
            boolean result = likeDao.checkIfUserLikedPost(1, 10);

            // assert
            assertFalse(result);
        }
    }

    // find like tests

    @Test
    void testFindLike_ExistingLike_ReturnsLike() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.find(eq(Like.class), any(Like.LikeId.class))).thenReturn(testLike);

            // act
            Like result = likeDao.findLike(1, 10);

            // assert
            assertNotNull(result);
            assertEquals(testLike, result);
        }
    }

    @Test
    void testFindLike_NonExistingLike_ReturnsNull() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.find(eq(Like.class), any(Like.LikeId.class))).thenReturn(null);

            // act
            Like result = likeDao.findLike(999, 999);

            // assert
            assertNull(result);
        }
    }

    @Test
    void testFindLike_WhenExceptionOccurs_ReturnsNull() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.find(eq(Like.class), any(Like.LikeId.class))).thenThrow(new RuntimeException("DB Error"));

            // act
            Like result = likeDao.findLike(1, 10);

            // assert
            assertNull(result);
        }
    }

    // delete like test

    @Test
    void testDeleteLike_ValidLike_Success() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);
            when(mockEntityManager.contains(testLike)).thenReturn(true);

            // act
            likeDao.deleteLike(testLike);

            // assert
            verify(mockEntityManager, atLeastOnce()).getTransaction();
            verify(mockTransaction).begin();
            verify(mockEntityManager).remove(testLike);
            verify(mockTransaction).commit();
        }
    }

    @Test
    void testDeleteLike_DetachedLike_MergesFirst() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);
            when(mockEntityManager.contains(testLike)).thenReturn(false);
            when(mockEntityManager.merge(testLike)).thenReturn(testLike);

            // act
            likeDao.deleteLike(testLike);

            // assert
            verify(mockEntityManager).merge(testLike);
            verify(mockEntityManager).remove(testLike);
        }
    }

    // helper method for create user

    private User createTestUser(int id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(username + "@test.com");
        return user;
    }

    private Post createTestPost(int id, int userId) {
        Post post = new Post();
        post.setId(id);
        post.setUserId(userId);
        return post;
    }
}