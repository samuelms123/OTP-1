package application.controller;

import application.model.entity.Comment;
import application.model.entity.Post;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class PostCellController {
    @FXML private TextField commentField;
    @FXML private Label authorLabel;
    @FXML private Label contentLabel;
    @FXML private Button likeButton;
    @FXML private Button commentButton;
    @FXML private ListView<String> postComments;

    private Post post;
    private Comment comment;

    public void setPost(Post post) {
        this.post = post;
        // for now hardcode author until you resolve user lookup
        authorLabel.setText("User #" + post.getUserId());
        contentLabel.setText(post.getContent());
    }

    public void setComment(Comment comment) {
        this.comment = comment;
        // for now hardcode author until you resolve user lookup
        authorLabel.setText("User #" + comment.getUserId());
        contentLabel.setText(comment.getContent());
    }

    @FXML
    private void initialize() {
        // like service should be called here
        likeButton.setOnAction(e -> System.out.println("Liked post " + post.getUserId()));
        commentButton.setOnAction(e -> System.out.println("Commented on post " + post.getUserId()));
    }
}
