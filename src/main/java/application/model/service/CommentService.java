package application.model.service;

import application.controller.SessionManager;
import application.model.data_objects.PostResult;
import application.model.entity.Comment;
import dao.CommentDao;

public class CommentService {
    private CommentDao commentDao;
    AuthService authService;

    public CommentService() {
        this.commentDao = new CommentDao();
        this.authService = new AuthService();
    }

    public PostResult leaveComment(Comment comment) {
        // implementation for leaving a comment
        if (authService.authMe(SessionManager.getInstance().getToken()) != null) {
            commentDao.persist(comment);
            return new PostResult(true, "Comment left successfully");
        }
        return new PostResult(false, "Access denied");
    }
}
