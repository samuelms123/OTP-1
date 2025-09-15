package application.model.data_objects;

import application.model.entity.User;

// Helper class for returning results
public class LoginResult {
    private boolean success;
    private String message;
    private String token;
    private User user;

    public LoginResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResult(boolean success, String message, String token, User user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {return token;}

    public User getUser() {return user;}
}
