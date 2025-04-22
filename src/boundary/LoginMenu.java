package boundary;
import java.util.*;

import database.Database;
import utility.NRICValidator;

/**
 * Boundary class for handling login-related user interactions.
 * This class provides methods to prompt the user for login credentials
 * and to change their password.
 *
 * <p>Features include:</p>
 * <ul>
 *   <li>Prompting the user for NRIC and password during login.</li>
 *   <li>Validating NRIC format and checking if the NRIC exists in the database.</li>
 *   <li>Prompting the user to change their password.</li>
 * </ul>
 *
 * @version 1.0
 * @since 2025-04-22
 */
public class LoginMenu {
    private Scanner sc = new Scanner(System.in);

    /**
     * Prompts the user to enter their NRIC and password for login.
     * Validates the NRIC format and checks if the NRIC exists in the database.
     *
     * @return A string array containing the NRIC and password, or null if validation fails.
     */
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

    /**
     * Prompts the user to enter their current password and a new password.
     *
     * @return A string array containing the current password and the new password.
     */
    public String[] promptChangePassword() {
        System.out.print("Enter current password: ");
        String oldPwd = sc.nextLine();
        System.out.print("Enter new password: ");
        String newPwd = sc.nextLine();
        return new String[]{oldPwd, newPwd};
    }
    
}
