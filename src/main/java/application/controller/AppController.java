package application.controller;

import application.model.data_objects.CommonResult;
import application.model.data_objects.PostResult;
import application.model.entity.Like;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AppController {
    PostService postService;
    AuthService authService;
    UserService userService;
    User currentlyOpenedUserProfile;

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
    private Button openNewPostPanelButton;
    @FXML
    private Button cancelNewPostButton;
    @FXML
    private Label realNameFieldTop;
    @FXML
    private Label usernameFieldTop;
    @FXML
    private Label likeAmountField;
    @FXML
    private Label postAmountField;
    @FXML
    private Label friendAmountField;
    @FXML
    private Label realNameFieldCenter;
    @FXML
    private Label usernameFieldCenter;
    @FXML
    private Label emailFieldCenter;
    @FXML
    private Label birthdayFieldCenter;
    @FXML private Label appRealNameField;
    @FXML private Label appUsernameField;
    @FXML private VBox staticProfile;

    // MODIFY PROFILE START
    @FXML private AnchorPane modifyProfile;
    @FXML private TextField modifyName;
    @FXML private TextField modifyLastname;
    @FXML private TextField modifyEmail;
    @FXML private DatePicker modifyBirthdate;
    // MODIFY PROFILE END
    @FXML
    private AnchorPane feedPage;
    @FXML
    private TextArea postContent;
    @FXML
    private ListView<Post> feedPagePostList;
    @FXML
    private ListView<String> searchFriendList;
    @FXML
    private TextField searchFriendTextField;
    @FXML
    private Pane newPostPanel;

    @FXML
    public void initialize() { // Called automatically when AppController is made aka when switching the scene
        updateFeed();
        updateNavUserInformation();

        // Add search listener
        searchFriendTextField.textProperty().addListener((observable, oldText, newText) -> {
            if (newText.isEmpty()) {
                searchFriendList.getItems().clear();
                return;
            }
            List<String> users = userService.searchUsers(newText, SessionManager.getInstance().getUser().getUsername());
            searchFriendList.setItems(FXCollections.observableList(users));
        });

        // Add searchlist item click listener
        searchFriendList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            User user = userService.getUserByUsername(newValue);
            openGuestProfilePage(user);
        });

        openNewPostPanelButton.setOnAction(this::toggleNewPostPanel);
        cancelNewPostButton.setOnAction(this::toggleNewPostPanel);
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

    // This check is just for deciding which button to show in profile add friend/remove friend!
    public boolean IsUserFollowed(User userToCheck) {
        User loggedUser = SessionManager.getInstance().getUser();
        for (User user : loggedUser.getFollowing()) {
            if (user.getUsername().equals(userToCheck.getUsername())) {
                return true;
            }
        }
        return false;
    }

    public void openGuestProfilePage(User user) {
        changeInfoButton.setVisible(false);
        addFriendButton.setVisible(true);
        currentlyOpenedUserProfile = user;
        setProfileInfo(user);
        if (IsUserFollowed(user)) {
            addFriendButton.setText("Remove friend");
        }
        else {
            addFriendButton.setText("Add friend");
        }
        toggleProfilePage();
    }

    public int countFriends(User user) {
        Set<User> following = user.getFollowing();
        return following.size();
    }

    public void setProfileInfo(User user) {
        realNameFieldTop.setText(user.getFirstName() + " " + user.getLastName());
        usernameFieldTop.setText("@" + user.getUsername());
        friendAmountField.setText("" + countFriends(user));
        // set like amounts
        //posts

        realNameFieldCenter.setText(user.getFirstName() + " " + user.getLastName());
        usernameFieldCenter.setText(user.getUsername());
        emailFieldCenter.setText(user.getEmail());
        birthdayFieldCenter.setText(user.getBirthdate());
    }

    public void openFeedPage(ActionEvent actionEvent) {
        closeModifyProfilePanel();

        profilePage.setVisible(false);
        feedPage.setVisible(true);
        currentlyOpenedUserProfile = null;
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
        Post post = new Post(SessionManager.getInstance().getUser().getId(), content, "", Timestamp.from(Instant.now()));
        PostResult result = postService.makePost(post);
        // Make UI error message in case of access denied
        System.out.println(result);
        postContent.clear();
        toggleNewPostPanel(actionEvent);
        updateFeed();
    }

    public void toggleUserModifyPanel() {
        staticProfile.setVisible(!staticProfile.isVisible());
        modifyProfile.setVisible(!modifyProfile.isVisible());

        modifyName.setText(SessionManager.getInstance().getUser().getFirstName());
        modifyLastname.setText(SessionManager.getInstance().getUser().getLastName());
        modifyEmail.setText(SessionManager.getInstance().getUser().getEmail());

        String birthdateString = SessionManager.getInstance().getUser().getBirthdate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localBirthDate = LocalDate.parse(birthdateString, formatter);

        modifyBirthdate.setValue(localBirthDate);
    }

    public void closeModifyProfilePanel() {
        staticProfile.setVisible(true);
        modifyProfile.setVisible(false);
    }

    public void applyProfileChanges() {
        if(!Objects.equals(modifyName.getText(), SessionManager.getInstance().getUser().getUsername())){
            SessionManager.getInstance().getUser().setFirstName(modifyName.getText());
        }

        if(!Objects.equals(modifyLastname.getText(), SessionManager.getInstance().getUser().getLastName())){
            SessionManager.getInstance().getUser().setLastName(modifyLastname.getText());
        }

        if(!Objects.equals(modifyEmail.getText(), SessionManager.getInstance().getUser().getEmail())){
            SessionManager.getInstance().getUser().setEmail(modifyEmail.getText());
        }

        if(!Objects.equals(modifyBirthdate.getValue().toString(), SessionManager.getInstance().getUser().getBirthdate())){
            SessionManager.getInstance().getUser().setBirthdate(modifyBirthdate.getValue().toString());
        }

        userService.updateUser(SessionManager.getInstance().getUser());

        //Update UI information
        setProfileInfo(SessionManager.getInstance().getUser());
        updateNavUserInformation();

        //Open static profile view
        toggleUserModifyPanel();
        System.out.println("Applying changes");
    }



    public void addFriend(ActionEvent actionEvent) {
        User followerUser = SessionManager.getInstance().getUser();
        User followedUser =  currentlyOpenedUserProfile;

        if (followedUser == null) {
            System.out.println("Bug in addFriend, followed user is null");
            return;
        }

        String buttonText = addFriendButton.getText();

        if (buttonText.equals("Add friend")) { // ADD FRIED BUTTON
            CommonResult result = userService.followUser(followerUser, followedUser);
            addFriendButton.setText("Remove friend");
            System.out.println(result.getMessage());
        }
        else { // REMOVE FRIEND BUTTON
            CommonResult result = userService.unfollowUser(followerUser, followedUser);
            addFriendButton.setText("Add friend");
            System.out.println(result.getMessage());
        }
    }

    private void updateFeed() {
        feedPagePostList.setCellFactory(listView -> new ListCell<Post>() {
            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);

                // Handle empty cells (gaps between posts)
                if (empty || post == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    try {
                        // Load the post cell using FXML
                        FXMLLoader postloader = new FXMLLoader(getClass().getResource(Paths.POST));
                        Node cellRoot = postloader.load();
                        PostCellController controller = postloader.getController();

                        // Set the post, likes and its comments in the controller
                        controller.setPost(post);
                        controller.setComments(postService.getCommentsForPost(post.getId()));
                        // Get likes
                        List<Like> likes = postService.getLikesForPost(post.getId());
                        controller.setLikes(likes.size());

                        // Set the graphic for the cell (this will render the post content)
                        setGraphic(cellRoot);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        // add friends only post visibility here, SHOULD NOT GET ALL POSTS
        List<Post> posts = postService.getPostsByFollowers(SessionManager.getInstance().getUser());
        // sort posts by TIMESTAMP HERE!!!!
        // Post implements comparable interface which already reverses post order
        // don't reverse order here or the order will be incorrect
        Collections.sort(posts);

        //Collections.reverse(posts);

        List<Post> postsWithGaps = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            postsWithGaps.add(posts.get(i));  // Add the post itself

            // Add an empty cell (gap) after each post, except after the last one
            if (i < posts.size() - 1) {
                postsWithGaps.add(null);  // Empty cell as a gap between posts
            }
        }

        // Set the items for the ListView
        feedPagePostList.getItems().setAll(postsWithGaps);
    }

    private void updateNavUserInformation(){
        User user = SessionManager.getInstance().getUser();
        appRealNameField.setText(user.getFirstName() + " " + user.getLastName());
        appUsernameField.setText("@" + user.getUsername());
    }

    private void toggleNewPostPanel(ActionEvent actionEvent) {
        postContent.clear();
        newPostPanel.setVisible(!newPostPanel.isVisible());
    }
}
