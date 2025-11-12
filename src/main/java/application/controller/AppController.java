package application.controller;

import application.model.data_objects.CommonResult;
import application.model.data_objects.PostResult;
import application.model.entity.Like;
import application.model.entity.Post;
import application.model.entity.User;
import application.model.service.PostService;
import application.model.service.UserService;
import application.utils.Paths;
import com.sun.jdi.event.ExceptionEvent;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;


import java.io.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static application.utils.ImageUtils.blobToImage;
import static application.utils.ImageUtils.imageToBlob;

public class AppController implements PostListener{
    PostService postService;
    UserService userService;
    User currentlyOpenedUserProfile;
    Image defaultProfilePicture;

    public AppController() {

        postService = new PostService();
        userService = new UserService();
        defaultProfilePicture = new Image(getClass().getResourceAsStream("/images/mock/empty-avatar.jpg"));
    }

    // PROFILE
    @FXML
    private ImageView miniProfilePicture;
    @FXML
    private ImageView profilePicture;
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
    @FXML private Button changeAvatarButton;
    @FXML private ImageView inspectImage;
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

    AppController appController = this;

    @FXML
    public void initialize() { // Called automatically when AppController is made aka when switching the scene
        updateFeed();
        updateNavUserInformation();
        postService.addListener(appController);
        if (SessionManager.getInstance().getUser().getProfilePicture() != null) {
            miniProfilePicture.setImage(blobToImage(SessionManager.getInstance().getUser().getProfilePicture()));
        }

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
    public boolean isUserFollowed(User userToCheck) {
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
        if (isUserFollowed(user)) {
            addFriendButton.setText(SceneManager.getSceneManager().getResBundle().getString("profile.removefriend"));
        }
        else {
            addFriendButton.setText(SceneManager.getSceneManager().getResBundle().getString("profile.addfriend"));
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

        if (user.getProfilePicture() != null) {
            profilePicture.setImage(blobToImage(user.getProfilePicture()));
        } else {
            profilePicture.setImage(defaultProfilePicture);
        }
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
        SessionManager.getInstance().reset();
        SceneManager.getSceneManager().switchScene(Paths.LOGIN);
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
        postContent.clear();
        toggleNewPostPanel(actionEvent);
        updateFeed();
    }

    public void toggleUserModifyPanel() {
        User user = SessionManager.getInstance().getUser();
        staticProfile.setVisible(!staticProfile.isVisible());
        modifyProfile.setVisible(!modifyProfile.isVisible());

        modifyName.setText(user.getFirstName());
        modifyLastname.setText(user.getLastName());
        modifyEmail.setText(user.getEmail());
        inspectImage.setImage(null);

        String birthdateString = user.getBirthdate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localBirthDate = LocalDate.parse(birthdateString, formatter);

        modifyBirthdate.setValue(localBirthDate);
    }

    public void closeModifyProfilePanel() {
        staticProfile.setVisible(true);
        modifyProfile.setVisible(false);
    }

    public void changeAvatar(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(SceneManager.getSceneManager().getResBundle().getString("changeinfo.changeavatar"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(SceneManager.getSceneManager().getStage());

        if (selectedFile != null) {
            // Temporary view of the image in profile picture slot
            Image image = new Image(selectedFile.toURI().toString(), 100, 100, false, false);
            inspectImage.setImage(image);
        }


    }

    public void applyProfileChanges() {
        User user = SessionManager.getInstance().getUser();

        if(!Objects.equals(modifyName.getText(), user.getUsername())){
            user.setFirstName(modifyName.getText());
        }

        if(!Objects.equals(modifyLastname.getText(), user.getLastName())){
            user.setLastName(modifyLastname.getText());
        }

        if(!Objects.equals(modifyEmail.getText(), user.getEmail())){
            user.setEmail(modifyEmail.getText());
        }

        if(!Objects.equals(modifyBirthdate.getValue().toString(), user.getBirthdate())){
            user.setBirthdate(modifyBirthdate.getValue().toString());
        }

        // Save picture from inspectPicture ImageView
        if (inspectImage.getImage() != null) {
            byte[] blob = imageToBlob(inspectImage.getImage());
            if (blob != null) {
                user.setProfilePicture(blob);
                miniProfilePicture.setImage(inspectImage.getImage());
            }
            else {
                throw new NullPointerException("inspectImage is Null");
            }
        }

        userService.updateUser(user);

        //Update UI information
        setProfileInfo(user);
        updateNavUserInformation();

        //Open static profile view
        toggleUserModifyPanel();
    }



    public void addFriend(ActionEvent actionEvent) {
        User followerUser = SessionManager.getInstance().getUser();
        User followedUser =  currentlyOpenedUserProfile;

        if (followedUser == null) {
            throw new NullPointerException("followedUser cannot be null in addFriend");
        }

        String buttonText = addFriendButton.getText();

        if (buttonText.equals(SceneManager.getSceneManager().getResBundle().getString("profile.addfriend"))) { // ADD FRIED BUTTON
            CommonResult result = userService.followUser(followerUser, followedUser);
            addFriendButton.setText(SceneManager.getSceneManager().getResBundle().getString("profile.removefriend"));
        }
        else { // REMOVE FRIEND BUTTON
            CommonResult result = userService.unfollowUser(followerUser, followedUser);
            addFriendButton.setText(SceneManager.getSceneManager().getResBundle().getString("profile.addfriend"));
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
                        // Load the post cell using FXML, pass in translations bundle too too
                        FXMLLoader postloader = new FXMLLoader(getClass().getResource(Paths.POST), SceneManager.getSceneManager().getResBundle());
                        Node cellRoot = postloader.load();
                        PostCellController controller = postloader.getController();
                        controller.setPostService(postService);

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

    @Override
    public void notifyDelete() {
        updateFeed();
    }
}
