package boundary;

import controller.LoginController;
import java.util.Scanner;

public class Menu {
    private final LoginController loginController;

    public Menu() {
        this.loginController = new LoginController();
    }

    public void displayMenu() {
        try (Scanner sc = new Scanner(System.in)) {
            int choice;
            do { 
                System.out.print("""
                    1. Login
                    2. Change password
                    3. Exit
                    """);
                choice = sc.nextInt();
                switch (choice) {
                    case 1 -> {
                        loginController.login();
                    }
                    case 2 -> {
                        System.out.println("change password");
                        break;
                    }
                    case 3 -> {
                        System.out.print("Bye!");
                        break;
                    }
                    default -> {
                        System.out.println("Invalid option");
                    }
                }
            } while (choice != 3);
        }
    }

    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.displayMenu();        
    }
}
