package controller;

import entity.roles.*;
import interfaces.IPasswordManagement;
import interfaces.IUserVerification;
import java.util.*;
import utility.NRICValidator;

/**
 * Handles login authentication and password management for users.
 */
public class LoginAuthController implements IUserVerification, IPasswordManagement {
    private Map<String, User> userMap;

    public LoginAuthController(Map<String, User> userMap) {
        this.userMap = userMap;
    }

    /**
     * Verifies the login credentials of a user.
     * @param nric The NRIC of the user.
     * @param password The password of the user.
     * @return The authenticated user, or null if authentication fails.
     */
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
