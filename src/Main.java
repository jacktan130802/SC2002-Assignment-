import boundary.*;
import controller.*;
import database.Database;
import entity.roles.*;
import java.util.*;
import utility.NRICValidator;

/**
 * Main class for the BTO Management System.
 * This class serves as the entry point for the application.
 */



public class Main {
    public static void main(String[] args) {
        // Load data
        Database.loadAll();
        // Controllers
        LoginAuthController loginCtrl = new LoginAuthController(Database.getUsers());
        ApplicationController appCtrl = new ApplicationController();
        BTOProjectController projectCtrl = new BTOProjectController(Database.getProjects());
        OfficerRegistrationController regCtrl = new OfficerRegistrationController();
        EnquiryController enquiryCtrl = new EnquiryController();
        ReceiptController receiptCtrl = new ReceiptController();

        // Boundary menus
        LoginMenu loginMenu = new LoginMenu();
        MainMenu mainMenu = new MainMenu();
        ApplicantMenu applicantMenu = new ApplicantMenu();
        OfficerMenu officerMenu = new OfficerMenu();
        ManagerMenu managerMenu = new ManagerMenu();
        LogoutMenu logoutMenu = new LogoutMenu();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("===== BTO Management System =====");
            System.out.println("1. Login");
            System.out.println("2. Change Password");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            int opt = sc.nextInt();
            sc.nextLine();

            if (opt == 3) {
                Database.saveAll();
                System.out.println("All data saved. Goodbye.");
                break;
            }

            if (opt == 1) {
                String[] login = loginMenu.displayLoginPrompt();
                if (login == null) {
                    // Just go back to the main menu
                    System.out.println(); 
                    continue; 
                }
                if (!NRICValidator.isValidNRIC(login[0])) {
                    System.out.println("Invalid NRIC format.\n");
                    continue;
                }
                User currentUser = loginCtrl.verifyLogin(login[0], login[1]);
                if (currentUser == null) {
                    System.out.println("Invalid credentials.\n");
                    continue;
                }
                mainMenu.displayMainMenu(currentUser);

                if (currentUser instanceof Applicant && !(currentUser instanceof HDBOfficer)) {
                    ApplicantController.run((Applicant) currentUser, appCtrl, projectCtrl, enquiryCtrl, applicantMenu, logoutMenu, sc);
                } else if (currentUser instanceof HDBOfficer) {
                    OfficerController.run((HDBOfficer) currentUser, projectCtrl, enquiryCtrl, receiptCtrl, officerMenu, logoutMenu, sc, appCtrl);
                } else if (currentUser instanceof HDBManager) {
                    ManagerController.run((HDBManager) currentUser, managerMenu,logoutMenu, appCtrl, enquiryCtrl, regCtrl, sc);
                }
            } else if (opt == 2) {
                System.out.print("Enter NRIC (E.g S1234567D): ");
                String nric = sc.nextLine();
                if (!NRICValidator.isValidNRIC(nric)) {
                    System.out.println("Invalid NRIC format.\n");
                    continue;
                }
                User user = Database.getUsers().get(nric);
                if (user == null) {
                    System.out.println("User not found.\n");
                    continue;
                }
                String[] pwds = loginMenu.promptChangePassword();
                if (!user.authenticate(pwds[0])) {
                    System.out.println("Incorrect current password.\n");
                    continue;
                } else {
                    user.changePassword(pwds[1]);
                    System.out.println("Password changed successfully.\n");
                    Database.saveAll();
                }
            }
        }
    }

}