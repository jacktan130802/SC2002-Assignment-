import boundary.*;
import controller.*;
import entity.*;
import entity.btoProject.ApprovedProject;
import entity.btoProject.BTOProject;
import entity.btoProject.RegisteredProject;
import entity.enquiry.Enquiry;
import entity.roles.*;
import enums.*;
import utility.NRICValidator;

import java.time.LocalDate;
import java.util.*;

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
        BTOMenu btoMenu = new BTOMenu();
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
                    runApplicantFlow((Applicant) currentUser, appCtrl, projectCtrl, enquiryCtrl, btoMenu, logoutMenu, sc);
                } else if (currentUser instanceof HDBOfficer) {
                    runOfficerFlow((HDBOfficer) currentUser, projectCtrl, enquiryCtrl, receiptCtrl, officerMenu, logoutMenu, sc, appCtrl);
                } else if (currentUser instanceof HDBManager) {
                    runManagerFlow((HDBManager) currentUser, managerMenu,logoutMenu, appCtrl, enquiryCtrl, regCtrl, sc);
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



    /**
     * Run the applicant flow for the given user.
     *
     * @param user       The applicant user.
     * @param appCtrl    The application controller.
     * @param projectCtrl The project controller.
     * @param enqCtrl    The enquiry controller.
     * @param menu       The menu for the applicant.
     * @param sc         The scanner for user input.
     */

    private static void runApplicantFlow(Applicant user, ApplicationController appCtrl, BTOProjectController projectCtrl, EnquiryController enqCtrl, BTOMenu menu, LogoutMenu logoutMenu, Scanner sc) {
        while (true) {
            int opt = menu.showApplicantOptions(user);
            if (opt == 1) { // View BTO Projects
                List<BTOProject> viewable = projectCtrl.getVisibleProjectsFor(user);

                System.out.println("Visible Projects (" + viewable.size() + "):");
                for (BTOProject p : viewable) {
                    System.out.println("- " + p.getProjectName());

                    if (user.getMaritalStatus().toString().equalsIgnoreCase("SINGLE") && user.getAge() >= 35) {
                        System.out.println("  Eligible Flat Type: 2-Room (" + p.getTwoRoomUnits() + " units left)");
                    } else if (user.getMaritalStatus().toString().equalsIgnoreCase("MARRIED") && user.getAge() >= 21) {
                        if (p.hasTwoRoom())
                            System.out.println("  2-Room: " + p.getTwoRoomUnits() + " units");
                        if (p.hasThreeRoom())
                            System.out.println("  3-Room: " + p.getThreeRoomUnits() + " units");
                    } else {
                        System.out.println("  [Not eligible for any flat type]");
                    }
                }

                System.out.println();


            }
            else if (opt == 2) { // Apply for BTO Projects
                String name = menu.promptProjectName();
                BTOProject p = projectCtrl.getProjectByName(name);

                if (p == null) {
                    System.out.println("Project not found.");
                    continue;
                }

                if (!p.isVisible()) {
                    System.out.println("You are not allowed to apply: Project is not visible.");
                    continue;
                }

                boolean isSingle = user.getMaritalStatus().toString().equalsIgnoreCase("SINGLE");
                boolean isMarried = user.getMaritalStatus().toString().equalsIgnoreCase("MARRIED");
                int age = user.getAge();

                if (isSingle && age >= 35) {
                    if (!p.hasTwoRoom()) {
                        System.out.println("No available 2-Room flats in this project.");
                        continue;
                    }
                    if (appCtrl.apply(user, p, FlatType.TWO_ROOM)) {
                        System.out.println("Successfully applied for 2-Room flat.");
                    } else {
                        System.out.println("Application failed.");
                    }
                } else if (isMarried && age >= 21) {
                    if (!p.hasTwoRoom() && !p.hasThreeRoom()) {
                        System.out.println("No flats available in this project.");
                        continue;
                    }

                    FlatType type = null;
                    while (type == null) {
                        type = menu.chooseFlatType(user.getMaritalStatus());

                        if (type == FlatType.TWO_ROOM && !p.hasTwoRoom()) {
                            System.out.println("No 2-Room flats available. Please choose another type.");
                            type = null; // reset to loop again
                        } else if (type == FlatType.THREE_ROOM && !p.hasThreeRoom()) {
                            System.out.println("No 3-Room flats available. Please choose another type.");
                            type = null;
                        }
                    }

                    if (appCtrl.apply(user, p, type)) {
                        System.out.println("Successfully applied for " + type + " flat.");
                    } else {
                        System.out.println("Application failed.");
                    }
                }
                 else {
                    System.out.println("You are not eligible to apply for any flats in this project.");
                }
            }

            else if (opt == 3) { // View Application
                Application app = user.getApplication();
                if (app == null) {
                    System.out.println("No application found.");
                    System.out.println("");
                }
                 else {
                    System.out.println("===== Application =====");
                    String projectName = app.getProject().getProjectName();
                    System.out.println("Project Name: " + projectName);
                    System.out.println("Flat Type: " + app.getFlatType());
                    System.out.println("Status: " + app.getStatus());
                    System.out.println("");
                }
            }

            else if (opt == 4) { // Enquiry
                while (true) {
                    System.out.println("--- Enquiry Menu ---");
                    System.out.println("1. Submit New Enquiry");
                    System.out.println("2. View My Enquiries");
                    System.out.println("3. Edit Enquiry");
                    System.out.println("4. Delete Enquiry");
                    System.out.println("5. Back");
                    System.out.print("Choose option: ");
                    int choice = sc.nextInt();
                    sc.nextLine(); // clear buffer

                    if (choice == 1) { // Submit New Enquiry
                        String msg = menu.promptEnquiryMessage();
                        String projectName = menu.promptProjectName();
                        BTOProject proj = projectCtrl.getProjectByName(projectName);

                        if (proj != null) {
                            enqCtrl.submitEnquiry(user, proj, msg);
                            System.out.println("Enquiry submitted.");
                            Database.saveAll(); // Save immediately
                        } else {
                            System.out.println("Project not found.");
                        }
                    }

                    else if (choice == 2) { // View Enquuiry
                        List<Enquiry> list = user.getEnquiries();
                        if (list.isEmpty()) {
                            System.out.println("No enquiries found.");
                        } else {
                            for (int i = 0; i < list.size(); i++) {
                                Enquiry e = list.get(i);
                                System.out.printf("[%d] Project: %s | Message: %s | Replied: %s\n",
                                    i + 1, e.getProject().getProjectName(), e.getMessage(), e.isReplied() ? "Yes" : "No");
                            }
                        }

                    }

                     else if (choice == 3) { // Edit Enquiry
                        List<Enquiry> list = user.getEnquiries();
                        if (list.isEmpty()) {
                            System.out.println("No enquiries to edit.");
                            continue;
                        }
                        System.out.print("Enter enquiry number to edit: ");
                        int index = sc.nextInt() - 1;
                        sc.nextLine();

                        if (index < 0 || index >= list.size()) {
                            System.out.println("Invalid index.");
                        } else if (list.get(index).isReplied()) {
                            System.out.println("Cannot edit a replied enquiry.");
                        } else {
                            System.out.print("Enter new message: ");
                            String newMsg = sc.nextLine();
                            enqCtrl.editEnquiry(list.get(index), newMsg);
                            System.out.println("Enquiry updated.");
                            Database.saveAll(); // Save immediately
                        }

                    } else if (choice == 4) { // Delete Enquiry
                        List<Enquiry> list = user.getEnquiries();
                        if (list.isEmpty()) {
                            System.out.println("No enquiries to delete.");
                            continue;
                        }
                        System.out.print("Enter enquiry number to delete: ");
                        int index = sc.nextInt() - 1;
                        sc.nextLine();

                        if (index < 0 || index >= list.size()) {
                            System.out.println("Invalid index.");
                        } else if (list.get(index).isReplied()) {
                            System.out.println("Cannot delete a replied enquiry.");
                        } else {
                            enqCtrl.deleteEnquiry(user, list.get(index));
                            System.out.println("Enquiry deleted.");
                            Database.saveAll(); // Save immediately
                        }
                    }
                    else {
                        break;
                    }
                }
            }
            else if (opt == 5) { // Withdraw Application
                appCtrl.withdraw(user);
                System.out.println("Withdrawn.");
            }
            else if (opt == 6){ // Logout
                logoutMenu.displayLogoutMenu(user);
                break;
            }
            else{
                System.out.println("Invalid option");
            }
        }
    }



    private static void runOfficerFlow(HDBOfficer user, BTOProjectController projCtrl, EnquiryController enqCtrl, ReceiptController receiptCtrl, OfficerMenu menu, LogoutMenu logoutMenu, Scanner sc, ApplicationController appCtrl) {
        // --- Debug: Print projects applied for and registered to handle ---


        while (true) {
            System.out.println("\n=== Officer Overview ===");

        // Application as Applicant
        if (user.getApplication() != null) {
            System.out.println("Project applied for as Applicant: " + user.getApplication().getProject().getProjectName());
        } else {
            System.out.println("Project applied for as Applicant: 0");
        }

        // Registered Projects
        List<BTOProject> registeredProjects = user.getRegisteredProjectNamesOnly();
        if (!registeredProjects.isEmpty()) {
            System.out.println("Project registered to Handle as an Officer:");
            for (BTOProject p : registeredProjects) {
                System.out.println("- " + p.getProjectName());
            }
        } else {
            System.out.println("Project registered to Handle as an Officer: 0");
        }

        // Approved Projects
        List<ApprovedProject> approvedProjects = user.getApprovedProjects();
        if (!approvedProjects.isEmpty()) {
            System.out.println("Projects approved to Handle as an Officer:");
            for (ApprovedProject ap : approvedProjects) {
                System.out.println("- " + ap.getProject().getProjectName());
            }
        } else {
            System.out.println("Projects approved to Handle as an Officer: 0");
        }


        System.out.println("===============================\n");
            int opt = menu.showOfficerOptions();
                        if (opt == 1) {
                List<BTOProject> viewable = projCtrl.getVisibleProjectsFor(user);

                System.out.println("Visible Projects (" + viewable.size() + "):");
                for (BTOProject p : viewable) {
                    System.out.println("- " + p.getProjectName());

                    if (user.getMaritalStatus().toString().equalsIgnoreCase("SINGLE") && user.getAge() >= 35) {
                        System.out.println("  Eligible Flat Type: 2-Room (" + p.getTwoRoomUnits() + " units left)");
                    } else if (user.getMaritalStatus().toString().equalsIgnoreCase("MARRIED") && user.getAge() >= 21) {
                        if (p.hasTwoRoom())
                            System.out.println("  2-Room: " + p.getTwoRoomUnits() + " units");
                        if (p.hasThreeRoom())
                            System.out.println("  3-Room: " + p.getThreeRoomUnits() + " units");
                    } else {
                        System.out.println("  [Not eligible for any flat type]");
                    }
                }

                System.out.println();


            }
            else if (opt == 2) {
                String name = menu.promptProjectName();
                BTOProject p = projCtrl.getProjectByName(name);

                if (p == null) {
                    System.out.println("Project not found.");
                    continue;
                }

                if (!p.isVisible()) {
                    System.out.println("You are not allowed to apply: Project is not visible.");
                    continue;
                }

                boolean isSingle = user.getMaritalStatus().toString().equalsIgnoreCase("SINGLE");
                boolean isMarried = user.getMaritalStatus().toString().equalsIgnoreCase("MARRIED");
                int age = user.getAge();

                if (isSingle && age >= 35) {
                    if (!p.hasTwoRoom()) {
                        System.out.println("No available 2-Room flats in this project.");
                        continue;
                    }
                    if (appCtrl.apply(user, p, FlatType.TWO_ROOM)) {
                        System.out.println("Successfully applied for 2-Room flat.");
                    } else {
                        System.out.println("Application failed.");
                    }
                } else if (isMarried && age >= 21) {
                    if (!p.hasTwoRoom() && !p.hasThreeRoom()) {
                        System.out.println("No flats available in this project.");
                        continue;
                    }

                    FlatType type = null;
                    while (type == null) {
                        type = menu.chooseFlatType(user.getMaritalStatus());

                        if (type == FlatType.TWO_ROOM && !p.hasTwoRoom()) {
                            System.out.println("No 2-Room flats available. Please choose another type.");
                            type = null; // reset to loop again
                        } else if (type == FlatType.THREE_ROOM && !p.hasThreeRoom()) {
                            System.out.println("No 3-Room flats available. Please choose another type.");
                            type = null;
                        }
                    }

                    if (appCtrl.apply(user, p, type)) {
                        System.out.println("Successfully applied for " + type + " flat.");
                    } else {
                        System.out.println("Application failed.");
                    }
                }
                 else {
                    System.out.println("You are not eligible to apply for any flats in this project.");
                }
            }

            else if (opt == 3) {
                Application app = user.getApplication();
                if (app == null) {
                    System.out.println("No application found.");
                    System.out.println("");
                }
                 else {
                    System.out.println("===== Application =====");
                    String projectName = app.getProject().getProjectName();
                    System.out.println("Project Name: " + projectName);
                    System.out.println("Flat Type: " + app.getFlatType());
                    System.out.println("Status: " + app.getStatus());
                    System.out.println("");
                }
            }

            else if (opt == 4) {
                while (true) {
                    System.out.println("--- Enquiry Menu ---");
                    System.out.println("1. Submit New Enquiry");
                    System.out.println("2. View My Enquiries");
                    System.out.println("3. Edit Enquiry");
                    System.out.println("4. Delete Enquiry");
                    System.out.println("5. Back");
                    System.out.print("Choose option: ");
                    int choice = sc.nextInt();
                    sc.nextLine(); // clear buffer

                    if (choice == 1) { // Submit New Enquiry
                        String msg = menu.promptEnquiryMessage();
                        String projectName = menu.promptProjectName();
                        BTOProject proj = projCtrl.getProjectByName(projectName);

                        if (proj != null) {
                            enqCtrl.submitEnquiry(user, proj, msg);
                            System.out.println("Enquiry submitted.");
                            Database.saveAll(); // Save immediately
                        } else {
                            System.out.println("Project not found.");
                        }
                    }

                    else if (choice == 2) {
                        List<Enquiry> list = user.getEnquiries();
                        if (list.isEmpty()) {
                            System.out.println("No enquiries found.");
                        } else {
                            for (int i = 0; i < list.size(); i++) {
                                Enquiry e = list.get(i);
                                System.out.printf("[%d] Project: %s | Message: %s | Replied: %s\n",
                                    i + 1, e.getProject().getProjectName(), e.getMessage(), e.isReplied() ? "Yes" : "No");
                            }
                        }

                    }

                     else if (choice == 3) { // Edit Enquiry
                        List<Enquiry> list = user.getEnquiries();
                        if (list.isEmpty()) {
                            System.out.println("No enquiries to edit.");
                            continue;
                        }
                        System.out.print("Enter enquiry number to edit: ");
                        int index = sc.nextInt() - 1;
                        sc.nextLine();

                        if (index < 0 || index >= list.size()) {
                            System.out.println("Invalid index.");
                        } else if (list.get(index).isReplied()) {
                            System.out.println("Cannot edit a replied enquiry.");
                        } else {
                            System.out.print("Enter new message: ");
                            String newMsg = sc.nextLine();
                            enqCtrl.editEnquiry(list.get(index), newMsg);
                            System.out.println("Enquiry updated.");
                            Database.saveAll(); // Save immediately
                        }

                    } else if (choice == 4) { // Delete Enquiry
                        List<Enquiry> list = user.getEnquiries();
                        if (list.isEmpty()) {
                            System.out.println("No enquiries to delete.");
                            continue;
                        }
                        System.out.print("Enter enquiry number to delete: ");
                        int index = sc.nextInt() - 1;
                        sc.nextLine();

                        if (index < 0 || index >= list.size()) {
                            System.out.println("Invalid index.");
                        } else if (list.get(index).isReplied()) {
                            System.out.println("Cannot delete a replied enquiry.");
                        } else {
                            enqCtrl.deleteEnquiry(user, list.get(index));
                            System.out.println("Enquiry deleted.");
                            Database.saveAll(); // Save immediately
                        }
                    }
                    else {
                        break;
                    }
                }
            }
            else if (opt == 5) {
                appCtrl.withdraw(user);
                System.out.println("Withdrawn.");
            }




            else if (opt == 6) { // Register to Handle Project
                String projectName = menu.promptProjectName();
                BTOProject p = projCtrl.getProjectByName(projectName);
                if (p == null) {
                    System.out.println("Project not found.");
                } else if (user.registerToProject(p)) {
                    System.out.println("Successfully registered to project. Pending approval.");
                    Database.saveAll();
                }
            }
            else if (opt == 7) { // View Registration Status
                for (RegisteredProject rp : user.getRegisteredProjects()) {
                    BTOProject p = rp.getProject();
                    System.out.println("Registration Status for " + p.getProjectName() + ": " + rp.getStatus());
                }
            }

            else if (opt == 8) { // View Assigned Projects
                for (BTOProject p : projCtrl.getVisibleProjectsFor(user)) {
                    if (user.isHandlingProject(p)) {
                        System.out.println("Handling: " + p.getProjectName());
                    }
                }
            } else if (opt == 9) { // Book flat for applicant
                String nric = menu.promptApplicantNRIC();
                User u = Database.getUsers().get(nric);
                if (u instanceof Applicant appUser) {
                    Application app = appUser.getApplication();
                    if (app != null && app.getStatus() == ApplicationStatus.SUCCESSFUL) {
                        user.bookFlatForApplicant(app, app.getFlatType().toString());
                        Database.saveAll();
                    }
                }
            } else if (opt == 10) { // Reply to Enquiry
                for (BTOProject p : projCtrl.getVisibleProjectsFor(user)) {
                    if (user.isHandlingProject(p)) {
                        for (Enquiry e : user.getEnquiries()) {
                            if (e.getProject().equals(p)) {
                                e.view();
                                String reply = menu.promptEnquiryReply();
                                enqCtrl.replyToEnquiry(e, reply);
                                Database.saveAll();
                            }
                        }
                    }
                }
            } else if (opt == 11) { // Generate Receipt
                String nric = menu.promptApplicantNRIC();
                User u = Database.getUsers().get(nric);
                if (u instanceof Applicant appUser) receiptCtrl.generateReceipt(appUser.getApplication());
            }  else if (opt == 12) { // View Flat Availability
                String projectName = menu.promptProjectName();
                BTOProject p = projCtrl.getProjectByName(projectName);
                if (p != null) {
                    user.viewFlatAvailability(p);
                }
            }
            else if (opt == 13){ // Logout
                logoutMenu.displayLogoutMenu(user);
                break;
            } 
            else{
                System.out.println("Invalid option");
            }
        }
    }


    private static void runManagerFlow(HDBManager mgr, ManagerMenu menu, LogoutMenu logoutMenu,ApplicationController appCtrl, EnquiryController enqCtrl, OfficerRegistrationController regCtrl, Scanner sc) {
        while (true) {
            int opt = menu.showManagerOptions();
            if (opt == 1) {
                String name = menu.promptProjectName();
                String hood = menu.promptNeighborhood();
                int two = menu.promptUnitCount("2-Room");
                int three = menu.promptUnitCount("3-Room");
                LocalDate open = LocalDate.parse(menu.promptDate("Opening"));
                LocalDate close = LocalDate.parse(menu.promptDate("Closing"));
                BTOProject p = new BTOProject(name, hood, two, 350000, three, 450000, open, close, mgr, 10);
                mgr.createProject(p);
            } else if (opt == 2) {
                String name = menu.promptProjectName();
                BTOProject p = mgr.getCreatedProjects().stream().filter(proj -> proj.getProjectName().equals(name)).findFirst().orElse(null);
                if (p != null) mgr.toggleProjectVisibility(p, !p.isVisible());
            } else if (opt == 3) {
                for (User u : Database.getUsers().values()) {
                    if (u instanceof Applicant app && app.getApplication() != null) {
                        BTOProject p = app.getApplication().getProject();
                        if (p.getManagerInCharge().equals(mgr)) {
                            appCtrl.updateStatus(app.getApplication(), ApplicationStatus.SUCCESSFUL);
                            System.out.println("Approved: " + app.getNRIC());
                        }
                    }
                }
            } else if (opt == 4) {
                for (BTOProject p : mgr.getCreatedProjects()) {
                    for (HDBOfficer o : p.getRegisteredOfficers()) {
                        regCtrl.approveOfficer(p, o);
                    }
                }


                /*
                 *
                 * else if
                 */



            }
            else if (opt == 8){ // Logout
                logoutMenu.displayLogoutMenu(mgr);
                break;
            } 
            else{
                System.out.println("Invalid option");
            }
        }
    }
}