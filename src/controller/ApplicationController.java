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
        Scanner sc = new Scanner(System.in);
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
}