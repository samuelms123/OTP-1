package application.model.service;

import application.model.data_objects.LoginResult;
import application.model.data_objects.PostResult;
import application.model.data_objects.RegistrationResult;
import application.model.entity.Post;
import application.model.entity.User;
import dao.PostDao;
import dao.UserDao;

public class PostService {
    PostDao postDao;

    public PostService() {
        this.postDao = new PostDao();
    }

    public PostResult makePost(Post post) {
        postDao.persist(post);
        return new PostResult(true, "Post created successfully");
    }
}
