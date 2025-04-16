package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;


import boundary.LogoutMenu;
import boundary.ManagerMenu;
import entity.btoProject.ApprovedProject;
import entity.btoProject.BTOProject;
import entity.btoProject.RegisteredProject;
import entity.enquiry.Enquiry;
import entity.roles.Applicant;
import entity.roles.HDBManager;
import entity.roles.User;
import enums.ApplicationStatus;
import enums.OfficerRegistrationStatus;
import utility.Filter;


public class ManagerController {
    public static void run(HDBManager mgr, ManagerMenu menu, LogoutMenu logoutMenu,ApplicationController appCtrl, EnquiryController enqCtrl, OfficerRegistrationController regCtrl, Scanner sc) {
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
                
                if (mgr.createProject(p)){
                    System.out.println("Project Created Successfully");
                }
                else{
                    System.out.println("Unsuccessful");
                }
            } else if (opt == 2) { // Edit/Delete Project
                List<BTOProject> managedProjects = mgr.getCreatedProjects(); // Retrieve all projects managed by the current manager

                if (managedProjects.isEmpty()) {
                    System.out.println("You are not managing any projects.");
                    return;
                }

                System.out.println("\nYour Managed Projects:");
                for (int i = 0; i < managedProjects.size(); i++) {
                    BTOProject project = managedProjects.get(i);
                    System.out.printf("%d. %s",
                            i + 1,
                            project.getProjectName());
                }

                System.out.print("Select a project to edit/delete (1-" + managedProjects.size() + "): ");
                try {
                    int choice = sc.nextInt();
                    sc.nextLine(); // Consume newline

                    if (choice < 1 || choice > managedProjects.size()) {
                        System.out.println("Invalid selection.");
                        return;
                    }

                    System.out.println("Select option");
                    System.out.println(" 1. Edit");
                    System.out.println(" 2. Delete");
                    int option = sc.nextInt();

                    if (option == 1) {//Edit
                        System.out.println("Edit project details");
                        String name = menu.promptProjectName();
                        String hood = menu.promptNeighborhood();
                        int two = menu.promptUnitCount("2-Room");
                        int three = menu.promptUnitCount("3-Room");
                        mgr.editProject(null, name, hood, two, three);

                    }
                    else if (option == 2){//Delete
                        mgr.deleteProject(managedProjects.get(choice - 1));
                        System.out.println("Deleted successfully");

                    }
                    else{
                        System.out.println("Invalid selection.");
                        return;
                    }

                    Database.saveAll(); // Save changes to the database



                    BTOProject selectedProject = managedProjects.get(choice - 1);
                    boolean newVisibility = !selectedProject.isVisible();
                    selectedProject.setVisibility(newVisibility);
                    System.out.println("Visibility for project \"" + selectedProject.getProjectName() + "\" toggled to: " + (newVisibility ? "ON" : "OFF"));

                    Database.saveAll(); // Save changes to the database
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    sc.nextLine(); // Clear invalid input
                }
            }

            else if (opt == 3) { // Toggle Project Visibility for Manager's Current Projects
                // Retrieve all projects from the database
                List<BTOProject> allProjects = Database.getProjects();

                // Use the filter to get projects managed by the current manager
                List<BTOProject> managedProjects = Filter.filterByManager(allProjects, mgr);

                if (managedProjects.isEmpty()) {
                    System.out.println("You are not managing any projects.");
                    return;
                }

                System.out.println("\nYour Managed Projects:");
                for (int i = 0; i < managedProjects.size(); i++) {
                    BTOProject project = managedProjects.get(i);
                    System.out.printf("%d. %s (Visibility: %s)%n",
                            i + 1,
                            project.getProjectName(),
                            project.isVisible() ? "ON" : "OFF");
                }

                System.out.print("Select a project to toggle visibility (1-" + managedProjects.size() + "): ");
                try {
                    int choice = sc.nextInt();
                    sc.nextLine(); // Consume newline

                    if (choice < 1 || choice > managedProjects.size()) {
                        System.out.println("Invalid selection.");
                        return;
                    }

                    BTOProject selectedProject = managedProjects.get(choice - 1);
                    boolean newVisibility = !selectedProject.isVisible();
                    selectedProject.setVisibility(newVisibility);
                    System.out.println("Visibility for project \"" + selectedProject.getProjectName() + "\" toggled to: " + (newVisibility ? "ON" : "OFF"));

                    Database.saveAll(); // Save changes to the database
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    sc.nextLine(); // Clear invalid input
                }
            } else if (opt == 4) { // Approve Officer Registration
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
            else if (opt == 6) { // View & Reply to Enquiry //not done yet
                // 1. Show all enquiries (view-only)
                List<Enquiry> allEnquiries = new ArrayList<>();
                List<Enquiry> replyEligibleEnquiries = new ArrayList<>();

                for (User u : Database.getUsers().values()) {
                    if (u instanceof Applicant a) {
                        for (Enquiry e : a.getEnquiries()) {
                            allEnquiries.add(e);
                            if (e.getProject().getManagerInCharge().equals(mgr) && !e.isReplied()) {
                                replyEligibleEnquiries.add(e);
                            }
                        }
                    }
                }

                // Section 1: View All Enquiries
                System.out.println("=== All Enquiries (View Only) ===");
                if (allEnquiries.isEmpty()) {
                    System.out.println("No enquiries found.");
                } else {
                    for (int i = 0; i < allEnquiries.size(); i++) {
                        Enquiry e = allEnquiries.get(i);
                        System.out.printf("[%d] Project: %s | Applicant: %s | Message: %s | Replied: %s\n",
                            i + 1, e.getProject().getProjectName(), e.getApplicant().getNRIC(),
                            e.getMessage(), e.isReplied() ? "Yes" : "No");
                    }
                }

                // Section 2: Enquiries Manager Can Reply To
                System.out.println("\n=== Enquiries You Can Reply To ===");
                if (replyEligibleEnquiries.isEmpty()) {
                    System.out.println("No enquiries available for you to reply.");
                } else {
                    for (int i = 0; i < replyEligibleEnquiries.size(); i++) {
                        Enquiry e = replyEligibleEnquiries.get(i);
                        System.out.printf("[%d] Project: %s | Applicant: %s | Message: %s\n",
                            i + 1, e.getProject().getProjectName(), e.getApplicant().getNRIC(), e.getMessage());
                    }

                    System.out.print("Enter enquiry number to reply (0 to cancel): ");
                    int choice = sc.nextInt();
                    sc.nextLine(); // consume newline

                    if (choice < 0 || choice > replyEligibleEnquiries.size()) {
                        System.out.println("Invalid selection.");
                    } else if (choice != 0) {
                        Enquiry selected = replyEligibleEnquiries.get(choice - 1);
                        System.out.print("Enter your reply: ");
                        String reply = sc.nextLine();
                        selected.setReply(reply);
                        System.out.println("Reply sent.");
                        Database.saveSavedEnquiries();
                    }
                }

            }
            else if (opt == 8){ // Logout
                logoutMenu.displayLogoutMenu(mgr);
                break;
            }
            else if (opt == 9) { // View All Projects with Filters
                menu.displayProjectsWithFilters(mgr);
            }

            else{
                System.out.println("Invalid option");
            }
     }
    
    }
}
