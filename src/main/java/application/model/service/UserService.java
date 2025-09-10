package application.model.service;

import application.model.data_objects.LoginResult;
import application.model.data_objects.RegistrationResult;
import application.model.entity.User;
import dao.UserDao;

public class UserService {
    UserDao userDao;

    public UserService() {
        userDao = new UserDao();
    }

    public RegistrationResult registerUser(User user) {
        if (!userDao.isUserEmailUnique(user.getEmail())) {
            return new RegistrationResult(false, "Email already taken");
        }

        if (!userDao.isUserNameUnique(user.getUsername())) {
            return new RegistrationResult(false, "Username already taken");
        }

        try {
            userDao.persist(user);
            return new RegistrationResult(true, "User registered successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return new RegistrationResult(false, e.getMessage());
        }
    }

    public LoginResult loginUser(User user) {
        if (userDao.findUser(user.getUsername(), user.getPassword()) == null) {
            return new LoginResult(false, "Username not found or password incorrect");
        }

        return new LoginResult(true, "Login successful!");
    }

}
