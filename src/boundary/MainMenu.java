package boundary;

import entity.roles.User;

/**
 * Boundary class for displaying the main menu to the user.
 */
public class MainMenu {

    /**
     * Displays the main menu to the user.
     *
     * @param user The user currently logged in.
     */
    public void displayMainMenu(User user) {
        System.out.println("Welcome, " + user.getName());
    }
}
