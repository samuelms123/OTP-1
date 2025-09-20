package application.controller;

import application.model.entity.Comment;
import application.model.entity.Post;
import application.model.entity.User;
import application.model.service.PostService;
import application.model.service.UserService;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;

public class PostCellController {
    private static final double LIST_CELL_HEIGHT = 24.0;
    @FXML private TextField commentField;
    @FXML private Label authorLabel;
    @FXML private Label contentLabel;
    @FXML private Button likeButton;
    @FXML private Button commentButton;
    @FXML private ListView<String> postComments;

    private PostService postService;
    private UserService userService;

    private ObservableList<String> commentItems = FXCollections.observableArrayList();

    private Post post;

    public void setPost(Post post) {
        this.post = post;
        User author = userService.getUserById(post.getUserId());
        authorLabel.setText(author != null ? author.getUsername() : "Unknown User");
        contentLabel.setText(post.getContent());
    }

    public void setComments(List<Comment> comments) {
        commentItems.clear();
        // add to observable list after clear
        for (Comment comment : comments) {
            User commentAuthor = userService.getUserById(comment.getUserId());
            String username = commentAuthor != null ? commentAuthor.getUsername() : "Unknown User";
            commentItems.add(username + ": " + comment.getContent());
        }
    }

    public void addComment(ActionEvent actionEvent) {
        String commentText = commentField.getText();
        if (!commentText.isEmpty()) {
            commentItems.add(SessionManager.getInstance().getUser().getUsername() + ": " + commentText);
            commentField.clear();
        }
        // persist comment on this post
        postService.makeComment(new Comment(SessionManager.getInstance().getUser().getId(), post.getId(), commentText));
    }

    @FXML
    private void initialize() {
        postService = new PostService();
        userService = new UserService();

        // set items for observable list view
        postComments.setItems(commentItems);

        // bind height of list view to number of items
        postComments.prefHeightProperty().bind(Bindings.size(commentItems).multiply(LIST_CELL_HEIGHT).add(2));
        // some performance optimization
        postComments.setFixedCellSize(LIST_CELL_HEIGHT);

        // like service should be called here
        likeButton.setOnAction(e -> System.out.println("Liked post " + post.getUserId()));
        commentButton.setOnAction(this::addComment);
    }
}