package entity.roles;

import entity.Application;
import entity.btoProject.BTOProject;
import entity.enquiry.Enquiry;
import enums.ApplicationStatus;
import enums.MaritalStatus;

import java.util.*;

public class HDBOfficer extends Applicant {
    private List<BTOProject> registeredProjects = new ArrayList<>();
    private List<BTOProject> approvedProjects = new ArrayList<>();

    public HDBOfficer(String name, String NRIC, String password, int age, MaritalStatus maritalStatus) {
        super(name, NRIC, password, age, maritalStatus);
    }

    public boolean registerToProject(BTOProject project) {
        if (hasAppliedTo(project)) {
            System.out.println("Cannot register to a project you've applied to.");
            return false;
        }
        if (isRegisteredForOverlappingProject(project)) {
            System.out.println("Cannot register for overlapping project timeline.");
            return false;
        }
        boolean success = project.registerOfficer(this);
        if (success) registeredProjects.add(project);
        return success;
    }

    public boolean isRegisteredForOverlappingProject(BTOProject newProject) {
        for (BTOProject p : approvedProjects) {
            if (p.isWithinApplicationPeriod(newProject.getOpeningDate()) ||
                p.isWithinApplicationPeriod(newProject.getClosingDate())) {
                return true;
            }
        }
        return false;
    }

    public void approveForProject(BTOProject project) {
        if (project.approveOfficer(this)) {
            approvedProjects.add(project);
            System.out.println("Officer approved for project: " + project.getProjectName());
        } else {
            System.out.println("Approval failed. Either not registered or max slots reached.");
        }
    }

    public boolean isHandlingProject(BTOProject p) {
        return approvedProjects.contains(p);
    }

    public void viewRegistrationStatus(BTOProject project) {
        if (project.getApprovedOfficers().contains(this)) {
            System.out.println("You are an approved officer for this project.");
        } else if (project.getRegisteredOfficers().contains(this)) {
            System.out.println("Your registration is pending approval.");
        } else {
            System.out.println("You have not registered for this project.");
        }
    }

    public void viewFlatAvailability(BTOProject project) {
        if (!isHandlingProject(project)) {
            System.out.println("You are not handling this project.");
            return;
        }
        System.out.println("2-Room units left: " + project.getTwoRoomUnits());
        System.out.println("3-Room units left: " + project.getThreeRoomUnits());
    }

    public void replyToEnquiry(Enquiry e, String reply) {
        if (!isHandlingProject(e.getProject())) {
            System.out.println("You are not handling this project.");
            return;
        }
        e.setReply(reply);
    }

    public void bookFlatForApplicant(Application app, String flatType) {
        BTOProject project = app.getProject();
        if (!isHandlingProject(project)) {
            System.out.println("You are not authorized to manage this project.");
            return;
        }

        if (app.getStatus() == ApplicationStatus.SUCCESSFUL) {
            app.setStatus(ApplicationStatus.BOOKED);
            app.getApplicant().setApplication(app);
            project.updateFlatCount(flatType);
            System.out.println("Flat successfully booked.");
        } else {
            System.out.println("Application is not in SUCCESSFUL status.");
        }
    }

    public void generateReceipt(Application app) {
        if (app.getStatus() != ApplicationStatus.BOOKED) {
            System.out.println("Receipt can only be generated for booked applications.");
            return;
        }

        BTOProject project = app.getProject();
        if (!isHandlingProject(project)) {
            System.out.println("You are not authorized to handle this project.");
            return;
        }

        System.out.println("--- Booking Receipt ---");
        System.out.println("Name: " + app.getApplicant().getNRIC());
        System.out.println("Age: " + app.getApplicant().getAge());
        System.out.println("Marital Status: " + app.getApplicant().getMaritalStatus());
        System.out.println("Flat Type: " + app.getFlatType());
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("------------------------");
    }

    @Override
    public void displayMenu() {
        System.out.println("--- HDB Officer Menu ---");
   }
}