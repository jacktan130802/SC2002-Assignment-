package interfaces;

import entity.roles.User;

/**
 * Provides methods for verifying user login credentials.
 */
public interface IUserVerification {

    /**
     * Verifies the login credentials of a user.
     *
     * @param nric The NRIC of the user.
     * @param password The password of the user.
     * @return The authenticated {@link User} object if verification is successful, {@code null} otherwise.
     */
    User verifyLogin(String nric, String password);
}
