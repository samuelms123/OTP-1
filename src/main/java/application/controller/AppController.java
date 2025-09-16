package application.controller;

import application.model.data_objects.PostResult;
import application.model.entity.Post;
import application.model.entity.User;
import application.model.service.AuthService;
import application.model.service.PostService;
import application.utils.Paths;
import application.view.GUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AppController {
    PostService postService = new PostService();
    AuthService authService = new AuthService();

    @FXML
    private AnchorPane profilePage;
    @FXML
    private VBox feedPage;
    @FXML
    private TextArea postContent;
    @FXML
    private ListView<Post> feedPagePostList;
    @FXML
    private ListView<User> searchFriendList;
    @FXML
    private TextField searchFriendTextField;

    @FXML
    public void initialize() { // Called automatically when AppController is made aka when switching the scene
        updateFeed();
    }

    public void openProfilePage(ActionEvent actionEvent){
        feedPage.setVisible(false);
        profilePage.setVisible(true);
    }

    public void openFeedPage(ActionEvent actionEvent) {
        profilePage.setVisible(false);
        feedPage.setVisible(true);
        updateFeed();
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        GUI.getSceneManager().switchScene(Paths.LOGIN);
        // Reset SessionManger
    }

    public void addPost(ActionEvent actionEvent) {
        String content = postContent.getText();
        if (content.isEmpty()) {
            return;
        }
        // need to get current Subject with Apache Shiro here
        // implementation of images still missing
        Post post = new Post(1,content, "");
        PostResult result = postService.makePost(post);
        updateFeed();
    }

    private void updateFeed() {
        feedPagePostList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);

                if (empty || post == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.POST));
                        Node cellRoot = loader.load();
                        PostCellController controller = loader.getController();
                        controller.setPost(post);
                        setGraphic(cellRoot);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        feedPagePostList.getItems().setAll(postService.getAllPosts());
    }
}
