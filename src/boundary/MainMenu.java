package boundary;

import entity.roles.User;

class MainMenu {
    public void displayMainMenu(User user) {
        System.out.println("Welcome, " + user.getNRIC());
        user.displayMenu();
    }
}