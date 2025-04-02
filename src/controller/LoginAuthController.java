package controller;


import entity.roles.*;


import java.util.*;


public class LoginAuthController{
    private Map<String, User> userMap;

    public LoginAuthController(Map<String, User> userMap) {
        this.userMap = userMap;
    }

    public User verifyLogin(String NRIC, String password) {
        User user = userMap.get(NRIC);
        return (user != null && user.authenticate(password)) ? user : null;
    }

    public void changePassword(User user, String newPassword) {
        user.changePassword(newPassword);
    }
}