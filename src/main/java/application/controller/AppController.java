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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class AppController {
    PostService postService;
    AuthService authService;
    UserService userService;

    public AppController() {

        postService = new PostService();
        userService = new UserService();
    }

    // PROFILE
    @FXML
    private AnchorPane profilePage;
    @FXML
    private Button changeInfoButton;
    @FXML
    private Button addFriendButton;
    @FXML
    private Label realNameFieldTop;
    @FXML
    private Label usernameFieldTop;
    @FXML
    private Label likeAmountField;
    @FXML
    private Label postAmountField;
    @FXML
    private Label realNameFieldCenter;
    @FXML
    private Label usernameFieldCenter;
    @FXML
    private Label emailFieldCenter;
    @FXML
    private Label birthdayFieldCenter;


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

        // Add searchlist item click listener
        searchFriendList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            User user = userService.getUserByUsername(newValue);
            openGuestProfilePage(user);

            // STILL NEED TO PREVENT FINDING OWN USER
        });
    }

    public void toggleProfilePage() {
        feedPage.setVisible(false);
        profilePage.setVisible(true);
    }


    public void openOwnProfilePage(ActionEvent actionEvent) {
        changeInfoButton.setVisible(true);
        addFriendButton.setVisible(false);
        setProfileInfo(SessionManager.getInstance().getUser());
        toggleProfilePage();
    }

    public void openGuestProfilePage(User user) {
        changeInfoButton.setVisible(false);
        addFriendButton.setVisible(true);
        setProfileInfo(user);
        toggleProfilePage();
    }

    public void setProfileInfo(User user) {
        realNameFieldTop.setText(user.getFirstName() + " " + user.getLastName());
        usernameFieldTop.setText(user.getUsername());
        // set like amounts
        //posts

        realNameFieldCenter.setText(user.getFirstName() + " " + user.getLastName());
        usernameFieldCenter.setText(user.getUsername());
        emailFieldCenter.setText(user.getEmail());
        birthdayFieldCenter.setText(user.getBirthdate());
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
        Post post = new Post(SessionManager.getInstance().getUser().getId(), content, "");
        PostResult result = postService.makePost(post);
        // Make UI error message in case of access denied
        System.out.println(result);
        updateFeed();
    }

    public void changeInfo(ActionEvent actionEvent) {
        System.out.println("changeInfo");
    }

    public void addFriend(ActionEvent actionEvent) {
        System.out.println("addFriend");
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
                        FXMLLoader postloader = new FXMLLoader(getClass().getResource(Paths.POST));
                        Node cellRoot = postloader.load();
                        PostCellController controller = postloader.getController();
                        controller.setPost(post);
                        controller.setComments(postService.getCommentsForPost(post.getId()));
                        setGraphic(cellRoot);
// TODO: If comment cell loading is needed in future, implement usage here.
// FXMLLoader commentloader = new FXMLLoader(getClass().getResource(Paths.COMMENT));
// Node comment = commentloader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        feedPagePostList.getItems().setAll(postService.getAllPosts());
    }
}
