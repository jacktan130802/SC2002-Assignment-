package controller;

import boundary.*;
import entity.*;
import entity.btoProject.BTOProject;
import entity.roles.Applicant;
import enums.*;
import entity.enquiry.Enquiry;
import utility.Filter;

import java.util.List;
import java.util.Scanner;

/**
 * This class handles the operations available to the Applicant user role.
 * It provides methods to view BTO projects, apply for projects, view applications,
 * manage enquiries, and withdraw applications.
 */
public class ApplicantController {
    public static void run(Applicant user, ApplicationController appCtrl, BTOProjectController projectCtrl, EnquiryController enqCtrl, ApplicantMenu menu, LogoutMenu logoutMenu, Scanner sc) {
        while (true) {// View BTO Projects
            int opt = menu.showApplicantOptions(user);
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
            if (opt == 2) {  // view filtered projects
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

                System.out.println("\nWould you like to filter the projects? (yes/no): ");
                String filterChoice = sc.nextLine().trim().toLowerCase();
                if (filterChoice.equals("yes")) {
                    System.out.print("Enter neighborhood to filter by (or leave blank for any): ");
                    String neighborhood = sc.nextLine().trim();

                    System.out.print("Enter flat type to filter by (2 for 2-Room, 3 for 3-Room, or leave blank for any): ");
                    String flatTypeInput = sc.nextLine().trim();
                    FlatType flatType = null;
                    if (!flatTypeInput.isEmpty()) {
                        if (flatTypeInput.equals("2")) {
                            flatType = FlatType.TWO_ROOM;
                        } else if (flatTypeInput.equals("3")) {
                            flatType = FlatType.THREE_ROOM;
                        }
                    }

                    System.out.print("Enter minimum price (or leave blank for no minimum): ");
                    String minPriceInput = sc.nextLine().trim();
                    Double minPrice = minPriceInput.isEmpty() ? null : Double.parseDouble(minPriceInput);

                    System.out.print("Enter maximum price (or leave blank for no maximum): ");
                    String maxPriceInput = sc.nextLine().trim();
                    Double maxPrice = maxPriceInput.isEmpty() ? null : Double.parseDouble(maxPriceInput);

                    // Apply filters
                    List<BTOProject> filteredProjects = Filter.dynamicFilter(viewable, neighborhood, flatType, minPrice, maxPrice);

                    System.out.println("\nFiltered Projects (" + filteredProjects.size() + "):");
                    for (BTOProject p : filteredProjects) {
                        System.out.println("- " + p.getProjectName());
                        if (flatType == FlatType.TWO_ROOM) {
                            System.out.println("  2-Room Price: " + p.getPriceTwoRoom());
                        } else if (flatType == FlatType.THREE_ROOM) {
                            System.out.println("  3-Room Price: " + p.getPriceThreeRoom());

                        }
                    }
                }
                } else if (opt == 3) { // Apply for BTO Projects
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
                    } else {
                        System.out.println("You are not eligible to apply for any flats in this project.");
                    }
                } else if (opt == 4) { // View Application
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
                } else if (opt == 5) { // Enquiry
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
                        } else if (choice == 2) { // View Enquiry
                            List<Enquiry> list = user.getEnquiries();
                            if (list.isEmpty()) {
                                System.out.println("No enquiries found.");
                            } else {
                                for (int i = 0; i < list.size(); i++) {
                                    Enquiry e = list.get(i);
                                    System.out.printf("[%d] Project: %s\n", i + 1, e.getProject().getProjectName());
                                    System.out.println("Message: " + e.getMessage());
                                    if (e.isReplied()) {
                                        System.out.println("Replied: Yes");
                                        System.out.println("Reply: " + e.getReply());
                                    } else {
                                        System.out.println("Replied: No");
                                    }
                                }
                            }
                        } else if (choice == 3) { // Edit Enquiry
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
                        } else {
                            break;
                        }
                    }
                } else if (opt == 6) { // Withdraw Application
                    appCtrl.requestWithdrawal(user);

                } else if (opt == 7) { // Logout
                    logoutMenu.displayLogoutMenu(user);
                    break;
                } else {
                    System.out.println("Invalid option");
                }
            }

        }
    }

