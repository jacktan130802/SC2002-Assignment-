package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import boundary.LogoutMenu;
import boundary.OfficerMenu;
import entity.Application;
import entity.Receipt;
import entity.btoProject.ApprovedProject;
import entity.btoProject.BTOProject;
import entity.btoProject.RegisteredProject;
import entity.enquiry.Enquiry;
import entity.roles.Applicant;
import entity.roles.HDBOfficer;
import entity.roles.User;
import enums.ApplicationStatus;
import enums.FlatType;

public class OfficerController {
    public static void run(HDBOfficer user, BTOProjectController projCtrl, EnquiryController enqCtrl, ReceiptController receiptCtrl, OfficerMenu menu, LogoutMenu logoutMenu, Scanner sc, ApplicationController appCtrl)
    {
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
            
            
            }
            else if (opt == 11) { // Officer: Reply to Enquiry
                List<Enquiry> allViewable = new ArrayList<>();
                List<Enquiry> replyEligible = new ArrayList<>();

                // Get projects the officer is approved to handle
                List<BTOProject> officerProjects = user.getApprovedProjects().stream()
                    .map(ApprovedProject::getProject)
                    .toList();

                // Collect all relevant enquiries
                for (User u : Database.getUsers().values()) {
                    if (u instanceof Applicant a) {
                        for (Enquiry e : a.getEnquiries()) {
                            if (officerProjects.contains(e.getProject())) {
                                allViewable.add(e); // View all enquiries from handled projects
                                if (!e.isReplied()) {
                                    replyEligible.add(e); // Only unreplied ones are eligible for reply
                                }
                            }
                        }
                    }
                }

                System.out.println("=== Enquiries for Projects You're Handling ===");
                if (allViewable.isEmpty()) {
                    System.out.println("No enquiries for your handled projects.");
                } else {
                    for (int i = 0; i < allViewable.size(); i++) {
                        Enquiry e = allViewable.get(i);
                        System.out.printf("[%d] Project: %s | Message: %s | Replied: %s\n",
                            i + 1, e.getProject().getProjectName(), e.getMessage(), e.isReplied() ? "Yes" : "No");
                        
                        if (e.isReplied()) {
                            System.out.println("Reply: " + e.getReply());
                        }
                    }
                    
                }

                System.out.println("\n=== Enquiries You Can Reply To ===");
                if (replyEligible.isEmpty()) {
                    System.out.println("No unreplied enquiries available to respond.");
                } else {
                    for (int i = 0; i < replyEligible.size(); i++) {
                        Enquiry e = replyEligible.get(i);
                        System.out.printf("[%d] Project: %s | Message: %s\n",
                            i + 1, e.getProject().getProjectName(), e.getMessage());
                    }

                    System.out.print("Choose enquiry number to reply: ");
                    int choice = sc.nextInt();
                    sc.nextLine(); // clear buffer

                    if (choice < 1 || choice > replyEligible.size()) {
                        System.out.println("Invalid choice.");
                    } else {
                        Enquiry e = replyEligible.get(choice - 1);
                        String reply = menu.promptEnquiryReply();
                        enqCtrl.replyToEnquiry(e, reply);
                        System.out.println("Reply submitted.");
                        Database.saveAll(); // Save reply immediately
                    }
                }

            
            }  else if (opt == 12) { // View Flat Availability
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
}
