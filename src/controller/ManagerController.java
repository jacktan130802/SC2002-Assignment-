package controller;

import boundary.LogoutMenu;
import boundary.ManagerMenu;
import database.*;
import entity.Application;
import entity.btoProject.ApprovedProject;
import entity.btoProject.BTOProject;
import entity.btoProject.RegisteredProject;
import entity.enquiry.Enquiry;
import entity.roles.Applicant;
import entity.roles.HDBManager;
import entity.roles.HDBOfficer;
import entity.roles.User;
import enums.ApplicationStatus;
import enums.FlatType;
import enums.MaritalStatus;
import enums.OfficerRegistrationStatus;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;
import utility.Filter;
import utility.ReportFilter;

/**
 * Controller class for managing HDB Manager operations.
 * This class handles functionalities such as viewing, creating, editing, and deleting BTO projects,
 * approving officer registrations, managing applications, replying to enquiries, and generating reports.
 */
public class ManagerController {

    /**
     * Runs the main menu for the HDB Manager and handles user interactions.
     *
     * @param mgr         The HDB Manager user.
     * @param menu        The ManagerMenu boundary object for displaying menu options.
     * @param logoutMenu  The LogoutMenu boundary object for handling logout.
     * @param appCtrl     The ApplicationController for managing applications.
     * @param enqCtrl     The EnquiryController for managing enquiries.
     * @param regCtrl     The OfficerRegistrationController for managing officer registrations.
     * @param sc          The Scanner object for user input.
     */
    public static void run(HDBManager mgr, ManagerMenu menu, LogoutMenu logoutMenu, ApplicationController appCtrl, EnquiryController enqCtrl, OfficerRegistrationController regCtrl, Scanner sc) {
        while (true) {
            int opt = menu.showManagerOptions();
            if (opt == 1) {
                // Show All Projects
                List<BTOProject> allProjects = Database.getProjects();
                if (allProjects.isEmpty()) {
                    System.out.println("No projects available.");
                } else {
                    System.out.println("=== All Projects ===");
                    for (BTOProject project : allProjects) {
                        System.out.printf("Project: %s | Neighborhood: %s | Visibility: %s\n",
                                project.getProjectName(), project.getNeighborhood(), project.isVisible() ? "ON" : "OFF");
                    }
                }
            } else if (opt == 2) { // Show My Projects
                List<BTOProject> managedProjects = Database.getProjects();
                List<BTOProject> myProjects = Filter.filterByManager(managedProjects, mgr);
                if (myProjects.isEmpty()) {
                    System.out.println("You are not managing any projects.");
                } else {
                    System.out.println("=== My Projects ===");
                    for (BTOProject project : myProjects) {
                        System.out.printf("Project: %s | Neighborhood: %s | Visibility: %s\n",
                                project.getProjectName(), project.getNeighborhood(), project.isVisible() ? "ON" : "OFF");
                    }
                }
            }
            else if (opt == 3) { // View All Projects with Filters
                System.out.println("Welcome to the BTO Project Filter!");

                // Prompt for filter criteria
                System.out.print("Enter neighborhood to filter by (or leave blank for any): ");
                //sc.nextLine(); // Clear buffer
                String neighborhood = sc.nextLine().trim();

                System.out.print("Enter flat type to filter by (2 for 2-Room, 3 for 3-Room, or leave blank for any): ");
                String flatTypeInput = sc.nextLine().trim();
                FlatType flatType = null;
                if (!flatTypeInput.isEmpty()) {
                    try {
                        // Handle numeric inputs
                        if (flatTypeInput.equals("2")) {
                            flatType = FlatType.TWO_ROOM;
                        } else if (flatTypeInput.equals("3")) {
                            flatType = FlatType.THREE_ROOM;
                        } else {
                            // Normalize input for textual formats
                            flatType = FlatType.valueOf(flatTypeInput.toUpperCase().replace("-", "_"));
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid flat type. Please enter '2' for 2-Room or '3' for 3-Room.");
                        return;
                    }
                }

                Double minPrice = null;
                Double maxPrice = null;

                try {
                    System.out.print("Enter minimum price (or leave blank for no minimum): ");
                    String minPriceInput = sc.nextLine().trim();
                    if (!minPriceInput.isEmpty()) {
                        minPrice = Double.parseDouble(minPriceInput);
                    }

                    System.out.print("Enter maximum price (or leave blank for no maximum): ");
                    String maxPriceInput = sc.nextLine().trim();
                    if (!maxPriceInput.isEmpty()) {
                        maxPrice = Double.parseDouble(maxPriceInput);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price input. Please enter valid numbers for price.");
                    return;
                }

                // Retrieve all projects and apply the dynamic filter
                List<BTOProject> allProjects = Database.getProjects();
                List<BTOProject> filteredProjects = Filter.dynamicFilter(allProjects, neighborhood, flatType, minPrice, maxPrice);

                // Display the filtered projects
                System.out.println("\n--- Filtered Projects ---");
                if (filteredProjects.isEmpty()) {
                    System.out.println("No projects match your criteria.");
                } else {
                    for (BTOProject project : filteredProjects) {
                        System.out.printf("Project: %s | Neighborhood: %s | 2-Room Price: %.2f | 3-Room Price: %.2f\n",
                                project.getProjectName(),
                                project.getNeighborhood(),
                                project.getPriceTwoRoom(),
                                project.getPriceThreeRoom());
                    }
                }
            } 
            else if (opt == 4) { // View Full Project Details (add this number based on your menu)
                List<BTOProject> myProjects = Database.getProjects().stream()
                    .filter(p -> p.getManagerInCharge() != null && p.getManagerInCharge().equals(mgr))
                    .collect(Collectors.toList());
                
                if (myProjects.isEmpty()) {
                    System.out.println("You are not currently managing any projects.");
                    return;
                }
                
                System.out.println("=== Your Projects ===");
                for (int i = 0; i < myProjects.size(); i++) {
                    System.out.printf("[%d] %s (%s)\n", i + 1, myProjects.get(i).getProjectName(), myProjects.get(i).getNeighborhood());
                }
                
                System.out.print("Enter project number to view details (0 to cancel): ");
                int choice = sc.nextInt();
                sc.nextLine(); // Clear buffer
                
                if (choice == 0) return;
                if (choice < 1 || choice > myProjects.size()) {
                    System.out.println("Invalid choice.");
                    return;
                }
                
                BTOProject selected = myProjects.get(choice - 1);
                
                // Display full project details
                System.out.println("\n=== FULL PROJECT DETAILS ===");
                System.out.println("Project Name: " + selected.getProjectName());
                System.out.println("Neighborhood: " + selected.getNeighborhood());
                System.out.println("Manager: " + selected.getManagerInCharge().getName());
                System.out.println("Visibility: " + (selected.isVisible() ? "Visible" : "Hidden"));
                
                System.out.println("\nFlat Information:");
                System.out.println("2-Room units: " + selected.getTwoRoomUnits());
                System.out.println("2-Room price: $" + selected.getPriceTwoRoom());
                System.out.println("3-Room units: " + selected.getThreeRoomUnits());
                System.out.println("3-Room price: $" + selected.getPriceThreeRoom());
                
                System.out.println("\nApplication Period:");
                System.out.println("Opening Date: " + selected.getOpeningDate());
                System.out.println("Closing Date: " + selected.getClosingDate());
                
                System.out.println("\nAssigned Officers:");

                // Try a different approach using project name matching instead of object equality
                List<HDBOfficer> assignedOfficers = Database.getRegisteredMap().values().stream()
                    .filter(rp -> rp.getProject().getProjectName().equals(selected.getProjectName()))
                    .filter(rp -> rp.getStatus() == OfficerRegistrationStatus.APPROVED)
                    .map(RegisteredProject::getOfficer)
                    .collect(Collectors.toList());

                System.out.println("Officers assigned for this Project: " + assignedOfficers.size());

                if (assignedOfficers.isEmpty()) {
                    System.out.println("No officers currently assigned");
                } else {
                    for (HDBOfficer officer : assignedOfficers) {
                        System.out.println("- " + officer.getName() + " (" + officer.getNRIC() + ")");
                    }
                }
            }
            else if (opt == 5) {
                try {
                    System.out.println("\n=== Create New Project ===");
                    System.out.println("You will be prompted for the following fields:");
                    System.out.println("Project Name, Neighborhood, 2-Room units & price, 3-Room units & price,");
                    System.out.println("Application Opening Date, Application Closing Date, Visibility");
            
                    String name = menu.promptProjectName(); // custom method, likely safe
                    String hood = menu.promptNeighborhood(); // custom method, likely safe
            
                    // Now, explicitly prompt the rest:
                    System.out.print("Enter number of 2-Room units: ");
                    int twoUnits = Integer.parseInt(sc.nextLine().trim());
            
                    System.out.print("Enter price for 2-Room: ");
                    double twoPrice = Double.parseDouble(sc.nextLine().trim());
            
                    System.out.print("Enter number of 3-Room units: ");
                    int threeUnits = Integer.parseInt(sc.nextLine().trim());
            
                    System.out.print("Enter price for 3-Room: ");
                    double threePrice = Double.parseDouble(sc.nextLine().trim());
            
                    System.out.print("Enter Opening Date (YYYY-MM-DD): ");
                    LocalDate openDate = LocalDate.parse(sc.nextLine().trim());
            
                    System.out.print("Enter Closing Date (YYYY-MM-DD): ");
                    LocalDate closeDate = LocalDate.parse(sc.nextLine().trim());
            
                    // Date conflict check
                    boolean overlap = Database.getProjects().stream()
                        .filter(p -> p.getManagerInCharge() != null && p.getManagerInCharge().equals(mgr))
                        .anyMatch(p -> !(closeDate.isBefore(p.getOpeningDate()) || openDate.isAfter(p.getClosingDate())));
            
                    if (overlap) {
                        System.out.println("You are already managing a project within this application period.");
                        continue;
                    }
            
                    System.out.print("Should this project be visible to applicants? (true/false): ");
                    String visInput = sc.nextLine().trim().toLowerCase();
                    boolean isVisible;
                    if (visInput.equals("true")) {
                        isVisible = true;
                    } else if (visInput.equals("false")) {
                        isVisible = false;
                    } else {
                        System.out.println("Invalid visibility input.");
                        continue;
                    }
            
                    // Check for name duplication
                    boolean duplicate = Database.getProjects().stream()
                            .anyMatch(p -> p.getProjectName().equalsIgnoreCase(name));
                    if (duplicate) {
                        System.out.println("Project with this name already exists.");
                        continue;
                    }
            
                    // Project creation
                    BTOProject newProject = new BTOProject(name, hood, twoUnits, twoPrice, threeUnits, threePrice, openDate, closeDate, mgr, 10);
                    newProject.setVisibility(isVisible);
            
                    mgr.createProject(newProject);
                    Database.getProjects().add(newProject);
                    Database.saveProjects(Database.getProjects());
            
                    System.out.println("Project created and saved successfully.\n");
            
                } catch (NumberFormatException | DateTimeParseException e) {
                    System.out.println("Invalid input format. Returning to Manager Menu...\n");
                    continue;
                } catch (Exception e) {
                    System.out.println("Unexpected error: " + e.getMessage());
                    continue;
                }
            }
            
            
                
            
            
            
            else if (opt == 6) { // Edit or Delete Project Listings
                List<BTOProject> myProjects = Database.getProjects().stream()
                    .filter(p -> p.getManagerInCharge() != null && p.getManagerInCharge().equals(mgr))
                    .collect(Collectors.toList());
            
                if (myProjects.isEmpty()) {
                    System.out.println("You are not currently handling any projects.");
                    return;
                }
            
                System.out.println("=== Your Projects ===");
                for (int i = 0; i < myProjects.size(); i++) {
                    System.out.printf("[%d] %s (%s)\n", i + 1, myProjects.get(i).getProjectName(), myProjects.get(i).getNeighborhood());
                }
            
                System.out.print("Enter project number to edit/delete (0 to cancel): ");
                int choice;
                try {
                    choice = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    continue;
                }
                
            
                if (choice == 0) return;
                if (choice < 1 || choice > myProjects.size()) {
                    System.out.println("Invalid choice.");
                    return;
                }
            
                BTOProject selected = myProjects.get(choice - 1);
            
                System.out.print("Do you want to (E)dit or (D)elete this project? ");
                String action = sc.nextLine().trim().toUpperCase();
            
                if (action.equals("D")) {
                    Database.getProjects().remove(selected);
                    mgr.getCreatedProjects().remove(selected); // optional: keep in sync
                    System.out.println("Project deleted.");
                } else if (action.equals("E")) {
                    try {
                        System.out.println("Current Project Name: " + selected.getProjectName());
                        System.out.print("Enter new project name (press enter to keep current): ");
                        String newName = sc.nextLine().trim();
                        if (!newName.isEmpty()) selected.setProjectName(newName);

                        System.out.println("Current neighborhood: " + selected.getNeighborhood());
                        System.out.print("Enter new neighborhood (press enter to keep current): ");
                        String hood = sc.nextLine().trim();
                        if (hood.isEmpty()) hood = selected.getNeighborhood();

                        System.out.println("Current 2-Room units: " + selected.getTwoRoomUnits());
                        System.out.print("Enter number of 2-Room units (press enter to keep current): ");
                        String twoRoomInput = sc.nextLine().trim();
                        int two = twoRoomInput.isEmpty() ? selected.getTwoRoomUnits() : Integer.parseInt(twoRoomInput);

                        System.out.println("Current 2-Room price: " + selected.getPriceTwoRoom());
                        System.out.print("Enter selling price for 2-Room (press enter to keep current): ");
                        String priceTwoInput = sc.nextLine().trim();
                        double priceTwo = priceTwoInput.isEmpty() ? selected.getPriceTwoRoom() : Double.parseDouble(priceTwoInput);

                        System.out.println("Current 3-Room units: " + selected.getThreeRoomUnits());
                        System.out.print("Enter number of 3-Room units (press enter to keep current): ");
                        String threeRoomInput = sc.nextLine().trim();
                        int three = threeRoomInput.isEmpty() ? selected.getThreeRoomUnits() : Integer.parseInt(threeRoomInput);

                        System.out.println("Current 3-Room price: " + selected.getPriceThreeRoom());
                        System.out.print("Enter selling price for 3-Room (press enter to keep current): ");
                        String priceThreeInput = sc.nextLine().trim();
                        double priceThree = priceThreeInput.isEmpty() ? selected.getPriceThreeRoom() : Double.parseDouble(priceThreeInput);

                        System.out.println("Current Opening Date: " + selected.getOpeningDate());
                        System.out.print("Enter Opening Date (yyyy-MM-dd) (press enter to keep current): ");
                        String openDateInput = sc.nextLine().trim();
                        LocalDate open = openDateInput.isEmpty() ? selected.getOpeningDate() : LocalDate.parse(openDateInput);

                        System.out.println("Current Closing Date: " + selected.getClosingDate());
                        System.out.print("Enter Closing Date (yyyy-MM-dd) (press enter to keep current): ");
                        String closeDateInput = sc.nextLine().trim();
                        LocalDate close = closeDateInput.isEmpty() ? selected.getClosingDate() : LocalDate.parse(closeDateInput);
            
                        // Check for overlapping with other projects by same manager
                        boolean conflict = Database.getProjects().stream()
                            .filter(p -> p != selected && p.getManagerInCharge() != null && p.getManagerInCharge().equals(mgr))
                            .anyMatch(p -> !p.getClosingDate().isBefore(open) && !p.getOpeningDate().isAfter(close));
            
                        if (conflict) {
                            System.out.println("Edit aborted. Another project you manage has an overlapping application period.");
                            return;
                        }
            
                        System.out.print("Should this project be visible to applicants? (true/false): ");
                        boolean visible = Boolean.parseBoolean(sc.nextLine());
            
                        // Apply updates
                        selected.setNeighborhood(hood);
                        selected.setTwoRoomUnits(two);
                        selected.setPriceTwoRoom(priceTwo);
                        selected.setThreeRoomUnits(three);
                        selected.setPriceThreeRoom(priceThree);
                        selected.setOpeningDate(open);
                        selected.setClosingDate(close);
                        selected.setVisibility(visible);
            
                        System.out.println("Project updated.");
                    } catch (Exception e) {
                        System.out.println("Invalid input. Edit aborted.");
                    }
                } else {
                    System.out.println("Invalid action.");
                }
            
                Database.saveProjects(Database.getProjects());
            }
            

                 else if (opt == 7) { // Toggle Project Visibility for Manager's Current Projects
                List<BTOProject> allProjects = Database.getProjects();
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
                        continue;
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
            } else if (opt == 8) { // Approve Officer Registration
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
                    int choice;
                    try {
                        choice = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        continue;
                    }
                    

                    if (choice < 1 || choice > pendingList.size()) {
                        System.out.println("Invalid selection.");
                    } else {
                        RegisteredProject selected = pendingList.get(choice - 1);

                        String decision = "";
                        while (!(decision.equals("A") || decision.equals("R"))) {
                            System.out.print("Approve this officer? (A = Approve / R = Reject): ");
                            decision = sc.nextLine().trim().toUpperCase();
                            if (!(decision.equals("A") || decision.equals("R"))) {
                                System.out.println("Invalid input. Please enter A or R.");
                            }
                        }

                        if (decision.equals("A")) {
                            selected.setStatus(OfficerRegistrationStatus.APPROVED);
                            System.out.println("Officer approved.");

                            // Create and add ApprovedProject
                            ApprovedProject approved = new ApprovedProject(UUID.randomUUID().toString(), selected.getProject(), selected.getOfficer());
                            Database.getApprovedProjectMap().put(approved.getId(), approved);

                            // Also reflect it in officer’s list
                            selected.getOfficer().getApprovedProjects().add(approved);

                        } else {
                            selected.setStatus(OfficerRegistrationStatus.REJECTED);
                            System.out.println("Officer rejected.");
                        }

                        Database.saveSavedRegisteredProjects();
                        Database.saveSavedApprovedProjects();
                        Database.saveSavedOfficers();
                    }
                }
            } else if (opt == 9) { // Approve/Reject Applications or Withdrawals
                System.out.println("1. Approve/Reject Applications");
                System.out.println("2. Approve Withdrawal Requests");
                int subOpt;
                try {
                    subOpt = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    continue;
                }
                

                if (subOpt == 1) {
                    appCtrl.reviewApplications();
                } else if (subOpt == 2) {
                    appCtrl.processWithdrawalRequests();
                } else {
                    System.out.println("Invalid Option");
                }
            } else if (opt == 10) { // View & Reply to Enquiry
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
                    int choice;
                    try {
                        choice = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        continue;
                    }
                    

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
            else if (opt == 11)
            {
                List<Application> allBooked = new ArrayList<>();

                // Get the projects managed by this manager
                List<BTOProject> managerProjects = Database.getProjects().stream()
                        .filter(p -> p.getManagerInCharge() != null && p.getManagerInCharge().equals(mgr))
                        .toList();
                
                // If manager is not handling any project, show message and return
                if (managerProjects.isEmpty()) {
                    System.out.println("You are not handling any projects. No application reports available.");
                    continue;
                }
                
                // Filter only applications belonging to manager's projects
                for (User u : Database.getUsers().values()) {
                    if (u instanceof Applicant a) {
                        Application app = a.getApplication();
                        if (app != null && managerProjects.contains(app.getProject())) {
                            ApplicationStatus status = app.getStatus();
                            if (status == ApplicationStatus.PENDING || status == ApplicationStatus.SUCCESSFUL || status == ApplicationStatus.BOOKED) {
                                allBooked.add(app);
                            }
                        }
                    }
                }
                
                    

                    if (allBooked.isEmpty()) {
                        System.out.println("No booked applications found.");
                        continue;
                    }

                    // Step 2: Prompt filter option
                    while (true) {
                        System.out.println("=== Filter Options ===");
                        System.out.println("1. Filter by Marital Status");
                        System.out.println("2. Filter by Flat Type");
                        System.out.println("3. Filter by Project Name");
                        System.out.println("4. Filter by Age Range");
                        System.out.println("5. No Filter (View All)");
                        System.out.println("0. Cancel");
                        System.out.print("Choose a filter option: ");
                        int choice;
                        try {
                            choice = Integer.parseInt(sc.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number.");
                            continue;
                        }
                        

                        List<Application> filtered = new ArrayList<>(allBooked);

                        if (choice == 0) break;

                        switch (choice) {
                            case 1 -> {
                                System.out.println("Select Marital Status:");
                                System.out.println("1. SINGLE");
                                System.out.println("2. MARRIED");
                                System.out.print("Choose option: ");
                                int msOpt = sc.nextInt();
                                sc.nextLine();
                                if (msOpt == 1) {
                                    filtered = ReportFilter.filterByMaritalStatus(allBooked, MaritalStatus.SINGLE);
                                } else if (msOpt == 2) {
                                    filtered = ReportFilter.filterByMaritalStatus(allBooked, MaritalStatus.MARRIED);
                                } else {
                                    System.out.println("Invalid marital status option. Returning to filter menu.\n");
                                    continue;
                                }
                            }

                            case 2 -> {
                                System.out.println("Select Flat Type:");
                                System.out.println("1. TWO_ROOM");
                                System.out.println("2. THREE_ROOM");
                                System.out.print("Choose option: ");
                                int ftOpt = sc.nextInt();
                                sc.nextLine();
                                if (ftOpt == 1) {
                                    filtered = ReportFilter.filterByFlatType(allBooked, FlatType.TWO_ROOM);
                                } else if (ftOpt == 2) {
                                    filtered = ReportFilter.filterByFlatType(allBooked, FlatType.THREE_ROOM);
                                } else {
                                    System.out.println("Invalid flat type option. Returning to filter menu.\n");
                                    continue;
                                }
                            }

                            case 3 -> {
                                System.out.println("Available Projects:");
                                List<String> projectNames = Database.getProjects().stream()
                                        .map(BTOProject::getProjectName).distinct().toList();
                                for (String name : projectNames) System.out.println("- " + name);

                                System.out.print("Enter project name exactly as shown: ");
                                String inputName = sc.nextLine();
                                if (!projectNames.contains(inputName)) {
                                    System.out.println("Invalid project name. Returning to filter menu.\n");
                                    continue;
                                }
                                filtered = ReportFilter.filterByProjectName(allBooked, inputName);
                            }

                            case 4 -> {
                                try {
                                    System.out.print("Enter minimum age: ");
                                    int min = sc.nextInt();
                                    System.out.print("Enter maximum age: ");
                                    int max = sc.nextInt();
                                    sc.nextLine(); // clear
                                    if (min < 0 || max < min) {
                                        System.out.println("Invalid age range. Returning to filter menu.\n");
                                        continue;
                                    }
                                    filtered = ReportFilter.filterByAgeRange(allBooked, min, max);
                                } catch (InputMismatchException e) {
                                    System.out.println("Invalid input. Returning to filter menu.\n");
                                    sc.nextLine(); // clear garbage
                                    continue;
                                }
                            }

                            case 5 -> {
                                // no filtering, already copied allBooked
                            }

                            default -> {
                                System.out.println("Invalid filter choice. Try again.\n");
                                continue;
                            }
                        }

                        // Step 3: Print results
                        if (filtered.isEmpty()) {
                            System.out.println("No results match the selected filter.");
                        } else {
                            System.out.println("\n===== Filtered Report =====");
                            for (Application app : filtered) {
                                System.out.printf("NRIC: %s | Name: %s | Age: %d | Marital Status: %s | Flat Type: %s | Project: %s\n",
                                        app.getApplicant().getNRIC(),
                                        app.getApplicant().getName(),
                                        app.getApplicant().getAge(),
                                        app.getApplicant().getMaritalStatus(),
                                        app.getFlatType(),
                                        app.getProject().getProjectName());
                            }
                        }
                        break; // exit filter loop after valid result
                    }
                }



            else if (opt == 12) { // Logout
                logoutMenu.displayLogoutMenu(mgr);
                break;
            }  else {
                continue;
            }
            
            
     }

    }
}




