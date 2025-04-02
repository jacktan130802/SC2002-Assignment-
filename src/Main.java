import controller.*;
import entity.*;
import entity.btoProject.BTOProject;
import entity.enquiry.Enquiry;
import entity.roles.*;
import boundary.*;
import enums.*;

import java.time.LocalDate;
import java.util.*;

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
                User currentUser = loginCtrl.verifyLogin(login[0], login[1]);
                if (currentUser == null) {
                    System.out.println("Invalid credentials.\n");
                    continue;
                }
                mainMenu.displayMainMenu(currentUser);

                if (currentUser instanceof Applicant && !(currentUser instanceof HDBOfficer)) {
                    runApplicantFlow((Applicant) currentUser, appCtrl, projectCtrl, enquiryCtrl, btoMenu, sc);
                } else if (currentUser instanceof HDBOfficer) {
                    runOfficerFlow((HDBOfficer) currentUser, projectCtrl, enquiryCtrl, receiptCtrl, officerMenu, sc);
                } else if (currentUser instanceof HDBManager) {
                    runManagerFlow((HDBManager) currentUser, managerMenu, appCtrl, enquiryCtrl, regCtrl, sc);
                }
            } else if (opt == 2) {
                System.out.print("Enter NRIC: ");
                String nric = sc.nextLine();
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

    private static void runApplicantFlow(Applicant user, ApplicationController appCtrl, BTOProjectController projectCtrl, EnquiryController enqCtrl, BTOMenu menu, Scanner sc) {
        while (true) {
            int opt = menu.showApplicantOptions();
            if (opt == 1) {
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
            else if (opt == 2) {
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
                    FlatType type = menu.chooseFlatType(user.getMaritalStatus());
            
                    if (type == FlatType.TWO_ROOM && !p.hasTwoRoom()) {
                        System.out.println("No 2-Room flats available.");
                    } else if (type == FlatType.THREE_ROOM && !p.hasThreeRoom()) {
                        System.out.println("No 3-Room flats available.");
                    } else if (appCtrl.apply(user, p, type)) {
                        System.out.println("Successfully applied for " + type + " flat.");
                    } else {
                        System.out.println("Application failed.");
                    }
                } else {
                    System.out.println("You are not eligible to apply for any flats in this project.");
                }
            }
            
            else if (opt == 3) {
                Application app = user.getApplication();
                if (app == null) System.out.println("No application found.");
                else System.out.println("Status: " + app.getStatus());
            } else if (opt == 4) {
                appCtrl.withdraw(user);
                System.out.println("Withdrawn.");
            } else if (opt == 5) {
                System.out.print("Enter enquiry message: ");
                String msg = sc.nextLine();
                String name = menu.promptProjectName();
                BTOProject p = projectCtrl.getProjectByName(name);
                if (p != null) enqCtrl.submitEnquiry(user, p, msg);
            } else break;
        }
    }

    private static void runOfficerFlow(HDBOfficer user, BTOProjectController projCtrl, EnquiryController enqCtrl, ReceiptController receiptCtrl, OfficerMenu menu, Scanner sc) {
        while (true) {
            int opt = menu.showOfficerOptions();
            if (opt == 1) {
                for (BTOProject p : projCtrl.getVisibleProjectsFor(user)) {
                    if (user.isHandlingProject(p)) {
                        for (Enquiry e : user.getEnquiries()) {
                            if (e.getProject().equals(p)) {
                                e.view();
                                String reply = menu.promptEnquiryReply();
                                enqCtrl.replyToEnquiry(e, reply);
                            }
                        }
                    }
                }
            } else if (opt == 2) {
                String nric = menu.promptApplicantNRIC();
                User u = Database.getUsers().get(nric);
                if (u instanceof Applicant appUser) {
                    Application app = appUser.getApplication();
                    if (app != null && app.getStatus() == ApplicationStatus.SUCCESSFUL)
                        user.bookFlatForApplicant(app, app.getFlatType().toString());
                }
            } else if (opt == 3) {
                String nric = menu.promptApplicantNRIC();
                User u = Database.getUsers().get(nric);
                if (u instanceof Applicant appUser) receiptCtrl.generateReceipt(appUser.getApplication());
            } else break;
        }
    }

    private static void runManagerFlow(HDBManager mgr, ManagerMenu menu, ApplicationController appCtrl, EnquiryController enqCtrl, OfficerRegistrationController regCtrl, Scanner sc) {
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



            } else break;
        }
    }
}