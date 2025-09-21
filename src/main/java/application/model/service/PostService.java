package application.model.service;

import application.controller.SessionManager;
import application.model.data_objects.LoginResult;
import application.model.data_objects.PostResult;
import application.model.data_objects.RegistrationResult;
import application.model.entity.Comment;
import application.model.entity.Like;
import application.model.entity.Post;
import application.model.entity.User;
import dao.CommentDao;
import dao.LikeDao;
import dao.PostDao;
import dao.UserDao;

import java.util.List;

public class PostService {
    PostDao postDao;
    AuthService authService;
    CommentDao commentDao;
    LikeDao likeDao;

    public PostService() {
        postDao = new PostDao();
        authService = new AuthService();
        commentDao = new CommentDao();
        likeDao = new LikeDao();
    }

    public PostResult makePost(Post post) {
        if (authService.authMe(SessionManager.getInstance().getToken()) != null) {
            postDao.persist(post);
            return new PostResult(true, "Post created successfully");
        }
        return new PostResult(false, "Access denied");
    }

    public PostResult makeComment(Comment comment) {
        if (authService.authMe(SessionManager.getInstance().getToken()) != null) {
            commentDao.persist(comment);
            return new PostResult(true, "Comment created successfully");
        }
        return new PostResult(false, "Access denied");
    }

    public boolean likePost(User user, Post post) {
        if (authService.authMe(SessionManager.getInstance().getToken()) != null) {
            if (!likeDao.checkIfUserLikedPost(post.getId(), user.getId())) {
                likeDao.persist(new Like(user, post));
                return true;
            }
        }
        return false;
    }

    public List<Like> getLikesForPost(int postId) {
        return likeDao.findLikesByPostId(postId);
    }

    public List<Post> getAllPosts() {
        return postDao.findAll();
    }

    public List<Comment> getCommentsForPost(int postId) {
        return commentDao.findCommentsByPostId(postId);
    }

}
