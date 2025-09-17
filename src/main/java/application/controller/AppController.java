package application.controller;

import application.model.data_objects.PostResult;
import application.model.entity.Post;
import application.model.entity.User;
import application.model.service.AuthService;
import application.model.service.PostService;
import application.model.service.UserService;
import application.utils.Paths;
import application.view.GUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import java.util.List;

public class AppController {
    PostService postService;
    AuthService authService;
    UserService userService;

    public AppController() {

        postService = new PostService();
        userService = new UserService();
    }

    @FXML
    private AnchorPane profilePage;
    @FXML
    private VBox feedPage;
    @FXML
    private TextArea postContent;
    @FXML
    private ListView<Post> feedPagePostList;
    @FXML
    private ListView<String> searchFriendList;
    @FXML
    private TextField searchFriendTextField;

    @FXML
    public void initialize() { // Called automatically when AppController is made aka when switching the scene
        updateFeed();
        // Add search listener
        searchFriendTextField.textProperty().addListener((observable, oldText, newText) -> {
            if (newText.isEmpty()) {
                searchFriendList.getItems().clear();
                return;
            }
            List<String> users = userService.searchUsers(newText);
            searchFriendList.setItems(FXCollections.observableList(users));
        });
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

        // implementation of images still missing
        Post post = new Post(1,content, "");
        PostResult result = postService.makePost(post);
        // Make UI error message in case of access denied
        System.out.println(result);
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
