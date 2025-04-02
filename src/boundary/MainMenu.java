package boundary;

import entity.roles.User;

public class MainMenu {
    public void displayMainMenu(User user) {
        System.out.println("Welcome, " + user.getName());
    }
}