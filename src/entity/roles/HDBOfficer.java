package entity.roles;

import entity.Application;
import entity.btoProject.*;
import entity.enquiry.Enquiry;
import enums.ApplicationStatus;
import enums.MaritalStatus;
import enums.OfficerRegistrationStatus;
import java.util.*;
import controller.Database;

public class HDBOfficer extends Applicant {
    private List<RegisteredProject> registeredProjects = new ArrayList<>();
    private List<ApprovedProject> approvedProjects = new ArrayList<>();

    public HDBOfficer(String name, String NRIC, String password, int age, MaritalStatus maritalStatus) {
        super(name, NRIC, password, age, maritalStatus);
    }

    public List<ApprovedProject> getApprovedProjects() {
        return approvedProjects;
    }

    public List<RegisteredProject> getRegisteredProjects() {
        return registeredProjects;
    }

    public List<BTOProject> getRegisteredProjectNamesOnly() {
        List<BTOProject> projects = new ArrayList<>();
        for (RegisteredProject rp : registeredProjects) {
            projects.add(rp.getProject());
        }
        return projects;
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
        for (RegisteredProject rp : registeredProjects) {
            if (rp.getProject().equals(project)) {
                System.out.println("Already registered for this project.");
                return false;
            }
        }
        boolean success = project.registerOfficer(this);
        System.out.println("in registerToProject");
        if (success) 
        {
            RegisteredProject newRP = new RegisteredProject(UUID.randomUUID().toString(), project, this, OfficerRegistrationStatus.PENDING);
            registeredProjects.add(newRP);
            Database.getRegisteredMap().put(newRP.getId(), newRP);  // <== ADD THIS LINE

            return success;
        }
        else
        {
            System.out.println("Fail to register");
            return false;
        }
    }

    public boolean isRegisteredForOverlappingProject(BTOProject newProject) {
        for (ApprovedProject ap : approvedProjects) {
            BTOProject p = ap.getProject();
            if (p.isWithinApplicationPeriod(newProject.getOpeningDate()) ||
                p.isWithinApplicationPeriod(newProject.getClosingDate())) {
                return true;
            }
        }
        return false;
    }

    public void approveForProject(BTOProject project) {
        for (RegisteredProject rp : registeredProjects) {
            if (rp.getProject().equals(project)) {
                if (project.approveOfficer(this)) {
                    rp.setStatus(OfficerRegistrationStatus.APPROVED);
                    approvedProjects.add(new ApprovedProject(UUID.randomUUID().toString(), project, this));
                    System.out.println("Officer approved for project: " + project.getProjectName());
                } else {
                    rp.setStatus(OfficerRegistrationStatus.REJECTED);
                    System.out.println("Approval failed. Either not registered or max slots reached.");
                }
                return;
            }
        }
        System.out.println("You are not registered for this project.");
    }

    public boolean isHandlingProject(BTOProject p) {
        for (ApprovedProject ap : approvedProjects) {
            if (ap.getProject().equals(p)) return true;
        }
        return false;
    }

    public void viewRegistrationStatus(BTOProject project) {
        for (RegisteredProject rp : registeredProjects) {
            if (rp.getProject().equals(project)) {
                System.out.println("Registration Status for " + project.getProjectName() + ": " + rp.getStatus());
                return;
            }
        }
        System.out.println("You have not registered for this project.");
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
