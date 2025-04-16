package controller;

import entity.Application;
import entity.btoProject.BTOProject;
import entity.roles.Applicant;
import enums.ApplicationStatus;
import enums.FlatType;
import enums.MaritalStatus;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationController {
    Scanner sc = new Scanner(System.in);
    public boolean apply(Applicant applicant, BTOProject project, FlatType type) {
        // Check if the applicant already has an application
        if (applicant.getApplication() != null) {
            System.out.println("Applicant already has an application.");
            return false;
        }
        // Check if the applicant is eligible for the selected flat type
        if (type == FlatType.THREE_ROOM && applicant.getMaritalStatus() == MaritalStatus.SINGLE) {
            System.out.println("Single applicants are not eligible for 3-room flats.");
            return false;
        }
        // Create a new application
        Application app = new Application(applicant, project, type);
        applicant.setApplication(app);

        controller.Database.saveAll();
        System.out.println("Application submitted successfully.");
        return true;
    }

    public void withdraw(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app == null) {
            System.out.println("No application found to withdraw.");
            return;
        }
        app.setStatus(ApplicationStatus.UNSUCCESSFUL);
        applicant.setApplication(null);
        controller.Database.saveAll();
        System.out.println("Application withdrawn successfully.");
    }

    public void requestWithdrawal(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app == null) {
            throw new IllegalStateException("No application found.");
        }
        app.setWithdrawalRequested(true); // Flag the application
        controller.Database.saveAll();
        System.out.println("Withdrawal requested. Awaiting approval.");
    }

    public void updateStatus(Application app, ApplicationStatus newStatus) {
        app.setStatus(newStatus);
        System.out.println("Application status updated to " + newStatus);
    }

    // Fetch pending withdrawal requests
    public List<Applicant> getPendingWithdrawalRequests() {
        return Database.getUsers().values().stream()
            .filter(u -> u instanceof Applicant)
            .map(u -> (Applicant) u)
            .filter(a -> a.getApplication() != null)
            .filter(a -> a.getApplication().isWithdrawalRequested())
            .collect(Collectors.toList());
    }

    public void processWithdrawalRequests() {
        List<Applicant> requests = getPendingWithdrawalRequests();
        if (requests.isEmpty()) {
            System.out.println("No pending withdrawals.");
            return;
        }

        // Display requests
        System.out.println("Pending Withdrawals:");
        requests.forEach(a -> System.out.println(
            "NRIC: " + a.getNRIC() + " | Project: " + a.getApplication().getProject().getProjectName()
        ));

        // Approve by NRIC
        System.out.print("Enter NRIC to approve (or 'cancel'): ");
        String input = sc.nextLine();
        if (input.equalsIgnoreCase("cancel")) return;

        requests.stream()
            .filter(a -> a.getNRIC().equals(input))
            .findFirst()
            .ifPresentOrElse(
                a -> {
                    withdraw(a);
                    System.out.println("Approved withdrawal for: " + a.getNRIC());
                },
                () -> System.out.println("Applicant not found.")
            );
    }

private List<Application> getPendingApplications() {
    return Database.getUsers().values().stream()
        .filter(user -> user instanceof Applicant)
        .map(user -> ((Applicant) user).getApplication())
        .filter(Objects::nonNull)
        .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
        .distinct() // ✅ Remove duplicates
        .collect(Collectors.toList());
}

    public void reviewApplications() {
        List<Application> pendingApps = getPendingApplications();
        if (pendingApps.isEmpty()) {
            System.out.println("No pending applications.");
            return;
        }

        // Display pending applications
        System.out.println("Pending Applications:");
        for (Application app : pendingApps) {
            System.out.println(
                "NRIC: " + app.getApplicant().getNRIC() + 
                " | Project: " + app.getProject().getProjectName()
            );
        }

        // Prompt for action
        System.out.print("Enter NRIC to approve/reject (or 'cancel'): ");
        String input = sc.nextLine();
        if (input.equalsIgnoreCase("cancel")) return;

        // Process the selected application
        for (Application app : pendingApps) {
            if (app.getApplicant().getNRIC().equals(input)) {
                processApplication(app);
                return;
            }
        }
        System.out.println("Applicant not found.");
    }

    private void processApplication(Application app) {
        System.out.print("Approve (A) or Reject (R)? ");
        String decision = sc.nextLine().toUpperCase();
    
        switch (decision) {
            case "A":
                if (canApproveApplication(app)) {
                    approveApplication(app);
                    System.out.println("Approved: " + app.getApplicant().getNRIC());
                } else {
                    System.out.println("Cannot approve - no available " + app.getFlatType() + " flats");
                }
                break;
            case "R":
                rejectApplication(app);
                System.out.println("Rejected: " + app.getApplicant().getNRIC());
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private boolean canApproveApplication(Application app) {
        BTOProject project = app.getProject();
        FlatType flatType = app.getFlatType();
        
        switch (flatType) {
            case TWO_ROOM:
                return project.getTwoRoomUnits() > 0;
            case THREE_ROOM:
                return project.getThreeRoomUnits() > 0;
            default:
                return false;
        }
    }
    
    private void approveApplication(Application app) {
        BTOProject project = app.getProject();
        FlatType flatType = app.getFlatType();
        
        // Update available units
        switch (flatType) {
            case TWO_ROOM:
                project.setTwoRoomUnits(project.getTwoRoomUnits() - 1);
                break;
            case THREE_ROOM:
                project.setThreeRoomUnits(project.getThreeRoomUnits() - 1);
                break;
        }
        
        // Update application status
        app.setStatus(ApplicationStatus.SUCCESSFUL);
        controller.Database.saveAll();
    }

    // Reject an application
    public void rejectApplication(Application app) {
        app.setStatus(ApplicationStatus.UNSUCCESSFUL);
        controller.Database.saveAll();
    }
}