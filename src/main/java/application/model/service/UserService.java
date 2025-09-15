package application.model.service;

import application.model.data_objects.LoginResult;
import application.model.data_objects.RegistrationResult;
import application.model.entity.User;
import at.favre.lib.crypto.bcrypt.BCrypt;
import config.Config;
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
            String hashedPassword = BCrypt.withDefaults()
                    .hashToString(Integer.parseInt(Config.SALT_ROUNDS), user.getPassword().toCharArray());
            user.setPassword(hashedPassword);
            userDao.persist(user);
            return new RegistrationResult(true, "User registered successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return new RegistrationResult(false, e.getMessage());
        }
    }

    public boolean verifyPassword(String password, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
        return result.verified;
    }

    public LoginResult loginUser(User user) {
        User userResult = userDao.findUser(user.getUsername());

        if (userResult == null) {
            return new LoginResult(false, "User not found");
        }

        if (verifyPassword(user.getPassword(), userResult.getPassword())) {
            return new LoginResult(true, "User logged in successfully");
        }

        return new LoginResult(false, "Password incorrect");
    }

}
