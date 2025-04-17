package boundary;
import controller.Database;
import java.util.*;
import utility.NRICValidator;

public class LoginMenu {
    private Scanner sc = new Scanner(System.in);

    public String[] displayLoginPrompt() {
        System.out.print("Enter NRIC (E.g S1234567D): ");
        String nric = sc.nextLine();

        // Validate NRIC format *before* asking for password
        if (!NRICValidator.isValidNRIC(nric)) {
            System.out.println("Invalid NRIC format.");
            return null; 
        }
        // Check if NRIC is in database
        if (!Database.getUsers().containsKey(nric)) {
            System.out.println("NRIC not registered in the system.");
            return null; 
        }

        System.out.print("Enter password: ");
        String pwd = sc.nextLine();

        return new String[]{nric, pwd};
    }

    public String[] promptChangePassword() {
        System.out.print("Enter current password: ");
        String oldPwd = sc.nextLine();
        System.out.print("Enter new password: ");
        String newPwd = sc.nextLine();
        return new String[]{oldPwd, newPwd};
    }
    
}
