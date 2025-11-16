package application.model.service;

import application.controller.SceneManager;
import application.controller.SessionManager;
import application.model.data_objects.PostResult;
import application.model.entity.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import dao.CommentDao;
import dao.LikeDao;
import dao.PostDao;
import dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostDao postDao;

    @Mock
    private AuthService authService;

    @Mock
    private CommentDao commentDao;

    @Mock
    private LikeDao likeDao;

    @Mock
    private UserDao userDao;

    @Mock
    private SessionManager sessionManager;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private Post testPost;
    private Comment testComment;
    private Like testLike;
    private String testToken;
    private DecodedJWT decodedJWT;

    @BeforeEach
    void setUp() {
        testToken = "test-token";
        testUser = new User("testname", "testlastname", "test@example.com", "testusername", "1.1.1999", "password");
        testPost = new Post(1, "Test Post", "Test Content", Timestamp.from(Instant.now()), SceneManager.getSceneManager().getResBundle().getLocale().toString());
        testComment = new Comment(1, 1, "Test Comment");
        testLike = new Like(testUser, testPost);

        // create a mock DecodedJWT for authentication
        decodedJWT = mock(DecodedJWT.class);

        // inject SessionManager singleton instance
        SessionManager.setInstance(sessionManager);
    }

    @Test
    void makePost_WhenUserAuthenticated_ShouldCreatePostSuccessfully() {
        when(sessionManager.getToken()).thenReturn(testToken);
        when(authService.authMe(testToken)).thenReturn(decodedJWT);

        PostResult result = postService.makePost(testPost);

        assertTrue(result.isSuccess());
        assertEquals("Post created successfully", result.getMessage());
        verify(postDao).persist(testPost);
    }

    @Test
    void makePost_WhenUserNotAuthenticated_ShouldReturnAccessDenied() {
        when(sessionManager.getToken()).thenReturn(testToken);
        when(authService.authMe(testToken)).thenReturn(null);

        PostResult result = postService.makePost(testPost);

        assertFalse(result.isSuccess());
        assertEquals("Access denied", result.getMessage());
        verify(postDao, never()).persist(any(Post.class));
    }

    @Test
    void makeComment_WhenUserAuthenticated_ShouldCreateCommentSuccessfully() {
        when(sessionManager.getToken()).thenReturn(testToken);
        when(authService.authMe(testToken)).thenReturn(decodedJWT);

        PostResult result = postService.makeComment(testComment);

        assertTrue(result.isSuccess());
        assertEquals("Comment created successfully", result.getMessage());
        verify(commentDao).persist(testComment);
    }

    @Test
    void makeComment_WhenUserNotAuthenticated_ShouldReturnAccessDenied() {
        when(sessionManager.getToken()).thenReturn(testToken);
        when(authService.authMe(testToken)).thenReturn(null);

        PostResult result = postService.makeComment(testComment);

        assertFalse(result.isSuccess());
        assertEquals("Access denied", result.getMessage());
        verify(commentDao, never()).persist(any(Comment.class));
    }

    @Test
    void likePost_WhenUserAuthenticatedAndNotLiked_ShouldAddLike() {
        when(sessionManager.getToken()).thenReturn(testToken);
        when(authService.authMe(testToken)).thenReturn(decodedJWT);
        when(likeDao.checkIfUserLikedPost(testUser.getId(), testPost.getId())).thenReturn(false);

        boolean result = postService.likePost(testUser, testPost);

        assertTrue(result);
        verify(likeDao).persist(any(Like.class));
    }

    @Test
    void likePost_WhenUserAuthenticatedAndAlreadyLiked_ShouldRemoveLike() {
        when(sessionManager.getToken()).thenReturn(testToken);
        when(authService.authMe(testToken)).thenReturn(decodedJWT);
        when(likeDao.checkIfUserLikedPost(testUser.getId(), testPost.getId())).thenReturn(true);
        when(likeDao.findLike(testUser.getId(), testPost.getId())).thenReturn(testLike);

        boolean result = postService.likePost(testUser, testPost);

        assertFalse(result);
        verify(likeDao).deleteLike(testLike);
        verify(likeDao, never()).persist(any(Like.class));
    }

    @Test
    void likePost_WhenUserNotAuthenticated_ShouldReturnFalse() {
        when(sessionManager.getToken()).thenReturn(testToken);
        when(authService.authMe(testToken)).thenReturn(null);

        boolean result = postService.likePost(testUser, testPost);

        assertFalse(result);
        verify(likeDao, never()).persist(any(Like.class));
        verify(likeDao, never()).deleteLike(any(Like.class));
    }

    @Test
    void getLikesForPost_ShouldReturnLikesList() {
        List<Like> expectedLikes = Arrays.asList(testLike);
        when(likeDao.findLikesByPostId(testPost.getId())).thenReturn(expectedLikes);

        List<Like> result = postService.getLikesForPost(testPost.getId());

        assertEquals(expectedLikes, result);
        verify(likeDao).findLikesByPostId(testPost.getId());
    }

    @Test
    void getAllPosts_ShouldReturnAllPosts() {
        List<Post> expectedPosts = Arrays.asList(testPost);
        when(postDao.findAll()).thenReturn(expectedPosts);

        List<Post> result = postService.getAllPosts();

        assertEquals(expectedPosts, result);
        verify(postDao).findAll();
    }

    @Test
    void getPostsByFollowers_ShouldReturnPostsFromFollowingAndCurrentUser() {
        // set up
        User follower1 = new User("follower1", "testlastname", "follower1@example.com", "testusername1", "1.1.1999", "password");;
        User follower2 = new User("follower2", "testlastname", "follower2@example.com", "testusername2", "1.1.1999", "password");;

        Set<User> following = new HashSet<>();
        following.add(follower1);
        following.add(follower2);

        testUser.setFollowing(following);

        Post post1 = new Post(follower1.getId(), "Follower1 Post", "Content", Timestamp.from(Instant.now()), SceneManager.getSceneManager().getResBundle().getLocale().toString());
        Post post2 = new Post(follower2.getId(), "Follower2 Post", "Content", Timestamp.from(Instant.now()), SceneManager.getSceneManager().getResBundle().getLocale().toString());
        Post currentUserPost = new Post(1, "My Post", "Content", Timestamp.from(Instant.now()), SceneManager.getSceneManager().getResBundle().getLocale().toString());

        List<Post> expectedPosts = Arrays.asList(post1, post2, currentUserPost);

        when(sessionManager.getUser()).thenReturn(testUser);
        when(postDao.findPostsByUsers(any(Set.class))).thenReturn(expectedPosts);

        // do the thing
        List<Post> result = postService.getPostsByFollowers(testUser);

        // asserts
        assertEquals(expectedPosts, result);
        verify(postDao).findPostsByUsers(any(Set.class));

        // verify the set includes both followers and current user
        verify(postDao).findPostsByUsers(argThat(users ->
                users.containsAll(following) && users.contains(testUser)
        ));
    }

    @Test
    void getCommentsForPost_ShouldReturnCommentsList() {
        List<Comment> expectedComments = Arrays.asList(testComment);
        when(commentDao.findCommentsByPostId(testPost.getId())).thenReturn(expectedComments);

        List<Comment> result = postService.getCommentsForPost(testPost.getId());

        assertEquals(expectedComments, result);
        verify(commentDao).findCommentsByPostId(testPost.getId());
    }

    @Test
    void likePost_WhenLikeDaoThrowsException_ShouldHandleGracefully() {
        when(sessionManager.getToken()).thenReturn(testToken);
        when(authService.authMe(testToken)).thenReturn(decodedJWT);
        when(likeDao.checkIfUserLikedPost(testUser.getId(), testPost.getId())).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> postService.likePost(testUser, testPost));
    }

    @Test
    void makePost_WhenPostDaoThrowsException_ShouldPropagateException() {
        when(sessionManager.getToken()).thenReturn(testToken);
        when(authService.authMe(testToken)).thenReturn(decodedJWT);
        doThrow(new RuntimeException("DB error")).when(postDao).persist(testPost);

        assertThrows(RuntimeException.class, () -> postService.makePost(testPost));
    }
}