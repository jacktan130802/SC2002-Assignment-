package boundary;

import entity.roles.User;

/**
 * Boundary class for handling logout-related user interactions.
 * This class provides a method to display a logout message to the user.
 *
 * @version 1.0
 * @since 2025-04-22
 */
public class LogoutMenu {

    /**
     * Displays a logout message to the user.
     *
     * @param user The user who is logging out.
     */
    public void displayLogoutMenu(User user) {
        System.out.println("Goodbye, " + user.getName());
        System.out.println();
    }
}
