package application.controller;

import application.model.entity.User;

public class SessionManager {
    private static SessionManager instance;
    private User user;
    private String token;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getToken() {return token;}

    public void setToken(String token) {this.token = token;}

    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}

    public void reset() {
        user = null;
        token = null;
    }

    // for testing purposes only, ability to inject an instance.
    public static void setInstance(SessionManager testInstance) {
        instance = testInstance;
    }
}
