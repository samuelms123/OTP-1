package application.controller;

import application.model.data_objects.CommonResult;
import application.model.entity.Comment;
import application.model.entity.Post;
import application.model.entity.User;
import application.model.service.PostService;
import application.model.service.UserService;
import application.utils.ImageUtils;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class PostCellController {
    private static final double LIST_CELL_HEIGHT = 24.0;
    @FXML private TextField commentField;
    @FXML private Label authorUsername;
    @FXML private Label authorRealName;
    @FXML private Label contentLabel;
    @FXML private Button likeButton;
    @FXML private Button commentButton;
    @FXML private ListView<String> postComments;
    @FXML private ImageView postProfilePicture;
    @FXML private Button deletePostButton;

    private PostService postService;
    private UserService userService;

    private ObservableList<String> commentItems = FXCollections.observableArrayList();


    private Post post;

    public void deletePost(ActionEvent event) {
        CommonResult result = postService.deletePost(this.post);
        System.out.println(result.getMessage());
    }

    public void setPost(Post post) {
        this.post = post;
        //if current user == post author -> delete button visible
        deletePostButton.setVisible(SessionManager.getInstance().getUser().getId() == post.getUserId());

        User author = userService.getUserById(post.getUserId());
        authorRealName.setText(author != null ? author.getFirstName() + " " + author.getLastName() : "Unknown User");
        authorUsername.setText(author != null ? "@" + author.getUsername() : "Unknown User");
        contentLabel.setText(post.getContent());

        byte[] profilePicture = (author != null ? author.getProfilePicture(): null);
        if (profilePicture != null) {
            postProfilePicture.setImage(ImageUtils.blobToImage(profilePicture));
        }

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

    public void setLikes(int likeCount) {
        String plural = likeCount == 1 ? "" : "s";

        likeButton.setText(likeCount + SceneManager.getSceneManager().getResBundle().getString("post.like") + plural);
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

    public void addLike() {
        // persist like on this post true=newLike, false=removeExistingLike
        if (postService.likePost(SessionManager.getInstance().getUser(), post)) {
            int currentLikes = Integer.parseInt(likeButton.getText().split(" ")[0]);

            setLikes(currentLikes + 1);
            System.out.println("Added like");
        } else{
            int currentLikes = Integer.parseInt(likeButton.getText().split(" ")[0]);

            //can not go under 0.
            if(currentLikes <= 0)
                return;

            setLikes(currentLikes - 1);
            System.out.println("Removed like");
        }
    }

    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    @FXML
    private void initialize() {
        userService = new UserService();

        // set items for observable list view
        postComments.setItems(commentItems);

        // bind height of list view to number of items
        postComments.prefHeightProperty().bind(Bindings.size(commentItems).multiply(LIST_CELL_HEIGHT).add(2));
        // some performance optimization
        postComments.setFixedCellSize(LIST_CELL_HEIGHT);

        // like service should be called here
        likeButton.setOnAction(e -> {
                    System.out.println("Liked post " + post.getId());
                    addLike();
                });
        commentButton.setOnAction(this::addComment);
    }
}