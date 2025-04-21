package interfaces;

import entity.roles.User;

public interface IUserVerification {
    User verifyLogin(String nric, String password);
} 