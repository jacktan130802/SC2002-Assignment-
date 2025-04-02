package controller;

import entity.Application;
import entity.btoProject.BTOProject;
import entity.roles.Applicant;
import enums.FlatType;
import enums.MaritalStatus;
import enums.ApplicationStatus;

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
        System.out.println("Application withdrawn successfully.");
    }

    public void updateStatus(Application app, ApplicationStatus newStatus) {
        app.setStatus(newStatus);
        System.out.println("Application status updated to " + newStatus);
    }
}