package interfaces;

/**
 * Provides methods for managing user passwords.
 */
public interface IPasswordManagement {

    /**
     * Changes the password for a user.
     *
     * @param nric The NRIC of the user.
     * @param oldPassword The user's current password.
     * @param newPassword The new password to be set.
     * @return {@code true} if the password change is successful, {@code false} otherwise.
     */
    boolean changePassword(String nric, String oldPassword, String newPassword);
}
