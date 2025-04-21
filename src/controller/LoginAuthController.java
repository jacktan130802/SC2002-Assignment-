package controller;

import entity.roles.*;
import interfaces.IPasswordManagement;
import interfaces.IUserVerification;
import java.util.*;
import utility.NRICValidator;

public class LoginAuthController implements IUserVerification, IPasswordManagement {
    private Map<String, User> userMap;

    public LoginAuthController(Map<String, User> userMap) {
        this.userMap = userMap;
    }

    @Override
    public User verifyLogin(String nric, String password) {
        if (!NRICValidator.isValidNRIC(nric)) {
            return null;
        }
        
        User user = userMap.get(nric);
        if (user == null) {
            return null;
        }
        
        return user.authenticate(password) ? user : null;
    }

    @Override
    public boolean changePassword(String nric, String oldPassword, String newPassword) {
        if (!NRICValidator.isValidNRIC(nric)) {
            return false;
        }

        User user = userMap.get(nric);
        if (user != null && user.authenticate(oldPassword)) {
            user.changePassword(newPassword);
            userMap.put(user.getNRIC(), user);
            return true;
        }
        return false;
    }
}