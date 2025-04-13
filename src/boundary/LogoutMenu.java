package boundary;

import entity.roles.User;

public class LogoutMenu {
    public void displayLogoutMenu(User user) {
        System.out.println("Goodbye, " + user.getName());
        System.out.println();
    }
}