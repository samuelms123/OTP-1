package application.controller;

import application.model.entity.Post;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PostCellController {
    @FXML private Label authorLabel;
    @FXML private Label contentLabel;
    @FXML private Button likeButton;

    private Post post;

    public void setPost(Post post) {
        this.post = post;
        // for now hardcode author until you resolve user lookup
        authorLabel.setText("User #" + post.getUserId());
        contentLabel.setText(post.getContent());
    }

    @FXML
    private void initialize() {
        likeButton.setOnAction(e -> System.out.println("Liked post " + post.getUserId()));
    }
}
