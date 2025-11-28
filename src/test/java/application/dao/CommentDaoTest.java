package application.dao;

import application.model.entity.Comment;
import dao.CommentDao;
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
class CommentDaoTest {

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private EntityTransaction mockTransaction;

    @Mock
    private TypedQuery<Comment> mockQuery;

    private CommentDao commentDao;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        commentDao = new CommentDao();
        testComment = new Comment(1, 10, "This is a test comment");
        testComment.setId(1);
    }

    // comment persisting test

    @Test
    void testPersist_ValidComment_Success() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);

            // act
            commentDao.persist(testComment);

            // assert
            verify(mockEntityManager, atLeastOnce()).getTransaction();
            verify(mockTransaction).begin();
            verify(mockEntityManager).persist(testComment);
            verify(mockTransaction).commit();
        }
    }

    @Test
    void testPersist_NullComment_ThrowsException() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);
            doThrow(new IllegalArgumentException()).when(mockEntityManager).persist(null);

            // act & assert
            assertThrows(IllegalArgumentException.class, () -> commentDao.persist(null));
        }
    }

    @Test
    void testPersist_CommentWithEmptyContent_Success() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            Comment emptyComment = new Comment(1, 10, "");
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);

            // act & assert
            assertDoesNotThrow(() -> commentDao.persist(emptyComment));
            verify(mockEntityManager).persist(emptyComment);
        }
    }

    @Test
    void testPersist_CommentWithLongContent_Success() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            String longContent = "This is a very long comment ".repeat(50);
            Comment longComment = new Comment(1, 10, longContent);

            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.getTransaction()).thenReturn(mockTransaction);

            // act & assert
            assertDoesNotThrow(() -> commentDao.persist(longComment));
            verify(mockEntityManager).persist(longComment);
        }
    }

    // find comments by postid

    @Test
    void testFindCommentsByPostId_WithExistingComments_ReturnsComments() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            List<Comment> expectedComments = Arrays.asList(
                    new Comment(1, 10, "First comment"),
                    new Comment(2, 10, "Second comment"),
                    new Comment(3, 10, "Third comment")
            );

            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString(), eq(Comment.class))).thenReturn(mockQuery);
            when(mockQuery.setParameter(eq("postId"), eq(10))).thenReturn(mockQuery);
            when(mockQuery.getResultList()).thenReturn(expectedComments);

            // act
            List<Comment> result = commentDao.findCommentsByPostId(10);

            // assert
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals(expectedComments, result);

            // verify all comments belong to the correct post
            assertTrue(result.stream().allMatch(comment -> comment.getPostId() == 10));
        }
    }

    @Test
    void testFindCommentsByPostId_WithNoComments_ReturnsEmptyList() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString(), eq(Comment.class))).thenReturn(mockQuery);
            when(mockQuery.setParameter(eq("postId"), eq(99))).thenReturn(mockQuery);
            when(mockQuery.getResultList()).thenReturn(Arrays.asList());

            // act
            List<Comment> result = commentDao.findCommentsByPostId(99);

            // assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testFindCommentsByPostId_WithInvalidPostId_ReturnsEmptyList() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString(), eq(Comment.class))).thenReturn(mockQuery);
            when(mockQuery.setParameter(eq("postId"), eq(-1))).thenReturn(mockQuery);
            when(mockQuery.getResultList()).thenReturn(Arrays.asList());

            // act
            List<Comment> result = commentDao.findCommentsByPostId(-1);

            // assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testFindCommentsByPostId_WhenDatabaseException_ReturnsEmptyList() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString(), eq(Comment.class))).thenThrow(new RuntimeException("DB Error"));

            // act
            List<Comment> result = commentDao.findCommentsByPostId(10);

            // assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testFindCommentsByPostId_WithSingleComment_ReturnsSingleComment() {
        try (MockedStatic<MariaDbJpaConnection> mockedStatic = mockStatic(MariaDbJpaConnection.class)) {
            // arrange
            Comment singleComment = new Comment(1, 5, "Only comment");
            List<Comment> singleCommentList = Arrays.asList(singleComment);

            mockedStatic.when(MariaDbJpaConnection::getInstance).thenReturn(mockEntityManager);
            when(mockEntityManager.createQuery(anyString(), eq(Comment.class))).thenReturn(mockQuery);
            when(mockQuery.setParameter(eq("postId"), eq(5))).thenReturn(mockQuery);
            when(mockQuery.getResultList()).thenReturn(singleCommentList);

            // act
            List<Comment> result = commentDao.findCommentsByPostId(5);

            // assert
            assertEquals(1, result.size());
            assertEquals(singleComment, result.get(0));
        }
    }
}