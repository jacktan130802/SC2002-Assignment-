import boundary.*;
import controller.*;
import entity.*;
import entity.btoProject.ApprovedProject;
import entity.btoProject.BTOProject;
import entity.btoProject.RegisteredProject;
import entity.enquiry.Enquiry;
import entity.roles.*;
import enums.*;
import java.time.LocalDate;
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
                    System.out.println("No application found.\n");
                } else {
                    System.out.println("===== Application =====");
                    System.out.println("Project Name: " + app.getProject().getProjectName());
                    System.out.println("Flat Type: " + app.getFlatType());
                    System.out.println("Status: " + app.getStatus());
                    // Show receipt if application is BOOKED
                    if (app.getStatus() == ApplicationStatus.BOOKED && app.getReceipt() != null) {
                        System.out.println("\n--- Booking Receipt ---");
                        Receipt r = app.getReceipt();
                        System.out.println("Name: " + r.getApplicantName());
                        System.out.println("NRIC: " + r.getNric());
                        System.out.println("Age: " + r.getAge());
                        System.out.println("Marital Status: " + r.getMaritalStatus());
                        System.out.println("Flat Type: " + r.getFlatType());
                        System.out.println("Project Name: " + r.getProjectName());
                        System.out.println("Neighborhood: " + r.getNeighborhood());
                    }
            
                    System.out.println();
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
                appCtrl.requestWithdrawal(user);
                
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
                appCtrl.requestWithdrawal(user);
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


            // CAN BE USED FOR MANAGER TO SEE ALL DETAILS OF ALL PROJECTS
            
            // } else if (opt == 9) { // View Project Details           
            //     if (!(user instanceof HDBOfficer)) {
            //         System.out.println("Access denied: Officer privileges required");
            //         return;
            //     }
            
            //     // Option 2A: If getProjects() returns List<BTOProject>
            //     List<BTOProject> allProjects = Database.getProjects();
                
            //     System.out.println("\nAvailable Projects:");
            //     allProjects.forEach(p -> System.out.println(p.getProjectName()));
            
            //     System.out.print("Enter project name: ");
            //     String projectName = sc.nextLine();
                
            //     // Find project in list
            //     Optional<BTOProject> project = allProjects.stream()
            //         .filter(p -> p.getProjectName().equalsIgnoreCase(projectName))
            //         .findFirst();
                    
            //     if (project.isPresent()) {
            //         projCtrl.getFullProjectDetails(user, projectName);
            //     } else {
            //         System.out.println("Project not found");
            //     }
            
            } else if (opt == 9) { // View Project Details
                if (!(user instanceof HDBOfficer)) {
                    System.out.println("Access denied: Officer privileges required");
                    return;
                }
                projCtrl.viewOfficerProjectDetails((HDBOfficer)user);

            
            

            } else if (opt == 10) { // Book flat for applicant
                List<ApprovedProject> officerApprovedProjects = user.getApprovedProjects();
            
                if (officerApprovedProjects.isEmpty()) {
                    System.out.println("Officer is not approved to handle any projects.");
                } else {
                    List<Application> applicationsToBook = new ArrayList<>();
            
                    for (ApprovedProject ap : officerApprovedProjects) {
                        BTOProject project = ap.getProject();
                        for (User u : Database.getUsers().values()) {
                            if (u instanceof Applicant a) {
                                Application app = a.getApplication();
                                if (app != null && app.getProject().equals(project) && app.getStatus() == ApplicationStatus.SUCCESSFUL) {
                                    applicationsToBook.add(app);
                                }
                            }
                        }
                    }
            
                    if (applicationsToBook.isEmpty()) {
                        System.out.println("No successful applications available for booking in your assigned projects.");
                    } else {
                        System.out.println("=== Successful Applications ===");
                        for (int i = 0; i < applicationsToBook.size(); i++) {
                            Application app = applicationsToBook.get(i);
                            System.out.printf("[%d] NRIC: %s | Project: %s | Flat Type: %s\n",
                                    i + 1, app.getApplicant().getNRIC(), app.getProject().getProjectName(), app.getFlatType());
                        }
            
                        System.out.print("Select application to book flat for (1-" + applicationsToBook.size() + "): ");
                        int choice = sc.nextInt();
                        sc.nextLine(); // clear buffer
            
                        if (choice < 1 || choice > applicationsToBook.size()) {
                            System.out.println("Invalid selection.");
                        } else {
                            Application selectedApp = applicationsToBook.get(choice - 1);
                            BTOProject project = selectedApp.getProject();
                            String flatType = selectedApp.getFlatType().toString();

                            // Officer books the flat
                            user.bookFlatForApplicant(selectedApp, flatType);

                            // Update flat count
                            project.updateFlatCount(selectedApp.getFlatType());

                            // Step 5: Generate and save receipt
                            String receiptId = UUID.randomUUID().toString();
                            Receipt receipt = new Receipt(receiptId, selectedApp);
                            selectedApp.setReceiptId(receiptId);
                            selectedApp.setReceipt(receipt);
                            Database.getReceiptMap().put(receiptId, receipt);
                            System.out.printf("Receipt generated. Applicant %s can now view their receipt via 'View Application'\n", selectedApp.getApplicant().getNRIC());
                            // Save everything immediately
                            Database.saveProjects(Database.getProjects());
                            Database.saveSavedApplications();
                            Database.saveSavedReceipts();
                            Database.saveAll();
                            
                        }
                    }
                }
            
            
            } else if (opt == 11) { // Reply to Enquiry

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
            } else if (opt == 12) { // Generate Receipt
                String nric = menu.promptApplicantNRIC();
                User u = Database.getUsers().get(nric);
                if (u instanceof Applicant appUser) receiptCtrl.generateReceipt(appUser.getApplication());

            }  else if (opt == 13) { // View Flat Availability
                String projectName = menu.promptProjectName();
                BTOProject p = projCtrl.getProjectByName(projectName);
                if (p != null) {
                    user.viewFlatAvailability(p);
                }
            }
            else if (opt == 14){ // Logout
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
            if (opt == 1) { // Create Project
                String name = menu.promptProjectName();
                String hood = menu.promptNeighborhood();
                int two = menu.promptUnitCount("2-Room");
                int three = menu.promptUnitCount("3-Room");
                LocalDate open = LocalDate.parse(menu.promptDate("Opening"));
                LocalDate close = LocalDate.parse(menu.promptDate("Closing"));
                BTOProject p = new BTOProject(name, hood, two, 350000, three, 450000, open, close, mgr, 10);
                mgr.createProject(p);

            } else if (opt == 2) { // Edit/Delete Project
                String name = menu.promptProjectName();
                BTOProject p = mgr.getCreatedProjects().stream()
                        .filter(proj -> proj.getProjectName().equals(name))
                        .findFirst()
                        .orElse(null);
                if (p != null) {
                    boolean newVisibility = !p.isVisible();
                    p.setVisibility(newVisibility);
                    System.out.println("Project \"" + p.getProjectName() + "\" visibility toggled to: " + (newVisibility ? "ON" : "OFF"));
                    Database.saveAll(); // Save changes to the database
                } else {
                    System.out.println("Project not found.");
                }
            }
          else if (opt == 3) { // Toggle Project Visibility for Manager's Current Projects
                List<BTOProject> currentProjects = mgr.getCurrentProjects(); // Retrieve current projects assigned to the manager

                if (currentProjects.isEmpty()) {
                    System.out.println("You are not managing any projects currently.");
                    return;
                }

                System.out.println("\nYour Current Projects:");
                for (int i = 0; i < currentProjects.size(); i++) {
                    BTOProject project = currentProjects.get(i);
                    System.out.printf("%d. %s (Visibility: %s)%n",
                            i + 1,
                            project.getProjectName(),
                            project.isVisible() ? "ON" : "OFF");
                }

                System.out.print("Select a project to toggle visibility (1-" + currentProjects.size() + "): ");
                try {
                    int choice = sc.nextInt();
                    sc.nextLine(); // Consume newline

                    if (choice < 1 || choice > currentProjects.size()) {
                        System.out.println("Invalid selection.");
                        return;
                    }

                    BTOProject selectedProject = currentProjects.get(choice - 1);
                    boolean newVisibility = !selectedProject.isVisible();
                    selectedProject.setVisibility(newVisibility);
                    System.out.println("Visibility for project \"" + selectedProject.getProjectName() + "\" toggled to: " + (newVisibility ? "ON" : "OFF"));

                    Database.saveAll(); // Save changes to the database
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    sc.nextLine(); // Clear invalid input
                }
            }

             else if (opt == 4) { // Approve Officer Registration
                    List<RegisteredProject> pendingList = Database.getRegisteredMap().values().stream()
                        .filter(rp -> rp.getStatus() == OfficerRegistrationStatus.PENDING)
                        .toList();

                    if (pendingList.isEmpty()) {
                        System.out.println("No officer registrations pending approval.");
                    } else {
                        System.out.println("Pending Officer Registrations:");
                        for (int i = 0; i < pendingList.size(); i++) {
                            RegisteredProject rp = pendingList.get(i);
                            System.out.printf("[%d] Officer: %s (%s) | Project: %s\n",
                                i + 1, rp.getOfficer().getName(), rp.getOfficer().getNRIC(), rp.getProject().getProjectName());
                        }


                        System.out.print("Select officer registration to review (1-" + pendingList.size() + "): ");
                        int choice = sc.nextInt();
                        sc.nextLine(); // consume newline


                        if (choice < 1 || choice > pendingList.size()) {
                            System.out.println("Invalid selection.");
                        } else {
                            RegisteredProject selected = pendingList.get(choice - 1);

                            String decision = "";
                            while (!(decision.equals("A") || decision.equals("R"))) {
                                System.out.print("Approve this officer? (A = Approve / R = Reject): ");
                                decision = sc.nextLine().trim().toUpperCase();
                                if (!(decision.equals("A") || decision.equals("R"))) {
                                    System.out.println("Invalid input. Please enter 'A' for Approve or 'R' for Reject.");
                                }
                            }

                            if (decision.equals("A")) {
                                selected.setStatus(OfficerRegistrationStatus.APPROVED);
                                ApprovedProject ap = new ApprovedProject(UUID.randomUUID().toString(),
                                    selected.getProject(), selected.getOfficer());

                                selected.getOfficer().getApprovedProjects().add(ap);
                                Database.getApprovedProjectMap().put(ap.getId(), ap);
                                System.out.println("Officer approved.");
                            } else {
                                selected.setStatus(OfficerRegistrationStatus.REJECTED);
                                System.out.println("Officer rejected.");
                            }

                            // Save immediately
                            Database.saveSavedRegisteredProjects();
                            Database.saveSavedApprovedProjects();
                            Database.saveSavedOfficers();
                        }
                    }
                

            } else if (opt == 5) { // Approve/Reject Applications or Withdrawals
                System.out.println("1. Approve/Reject Applications");
                System.out.println("2. Approve Withdrawal Requests"); // New option
                int subOpt = sc.nextInt();
                
                if (subOpt == 1) {
                    appCtrl.reviewApplications();


                } else if (subOpt == 2) {
                    appCtrl.processWithdrawalRequests();
                    
                } else {
                    System.out.println("Invalid Option");

                }

            }
            else if (opt == 9) { // View All Projects with Filters
                menu.displayProjectsWithFilters(mgr);
            }

            // else if (opt == 6) { // View & Reply to Enquiry //not done yet
            //     for (BTOProject p : mgr.getVisibleProjectsFor(mgr)) {
            //         if (mgr.isHandlingProject(p)) {
            //             for (Enquiry e : user.getEnquiries()) {
            //                 if (e.getProject().equals(p)) {
            //                     e.view();
            //                     String reply = menu.promptEnquiryReply();
            //                     enqCtrl.replyToEnquiry(e, reply);
            //                     Database.saveAll();
            //                 }
            //             }
            //         }
            //     }
            // }
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