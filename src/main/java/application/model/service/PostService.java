package application.model.service;

import application.controller.SessionManager;
import application.model.data_objects.LoginResult;
import application.model.data_objects.PostResult;
import application.model.data_objects.RegistrationResult;
import application.model.entity.Post;
import application.model.entity.User;
import dao.PostDao;
import dao.UserDao;

import java.util.List;

public class PostService {
    PostDao postDao;
    AuthService authService;

    public PostService() {
        postDao = new PostDao();
        authService = new AuthService();
    }

    public PostResult makePost(Post post) {
        if (authService.authMe(SessionManager.getInstance().getToken()) != null) {
            postDao.persist(post);
            return new PostResult(true, "Post created successfully");
        }
        return new PostResult(false, "Access denied");
    }

    public List<Post> getAllPosts() {
        return postDao.findAll();
    }
}
