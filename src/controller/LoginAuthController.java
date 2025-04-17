package controller;


import entity.roles.*;
import java.util.*;
import utility.NRICValidator;


public class LoginAuthController{
    private Map<String, User> userMap;

    public LoginAuthController(Map<String, User> userMap) {
        this.userMap = userMap;
    }

    public User verifyLogin(String NRIC, String password) {
        // Validate NRIC format first 
        if (!NRICValidator.isValidNRIC(NRIC)) {
            return null; // Exit immediately for invalid format
        }
        
        // NRIC lookup (only if format is valid)
        User user = userMap.get(NRIC);
        if (user == null) {
            return null; 
        }
        
        // 3. Password check (only if NRIC exists)
        return user.authenticate(password) ? user : null;
    }

    public void changePassword(User user, String newPassword) {
        user.changePassword(newPassword);
    }
}