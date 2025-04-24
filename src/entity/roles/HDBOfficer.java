package entity.roles;

import entity.Application;
import entity.btoProject.*;
import entity.enquiry.Enquiry;
import enums.ApplicationStatus;
import enums.MaritalStatus;
import enums.OfficerRegistrationStatus;
import java.util.*;

import database.Database;

/**
 * Represents an HDB Officer who can apply for flats, handle enquiries,
 * and manage assigned BTO projects.
 */
public class HDBOfficer extends Applicant {
    private List<RegisteredProject> registeredProjects = new ArrayList<>();
    private List<ApprovedProject> approvedProjects = new ArrayList<>();

    /**
     * Constructs an HDBOfficer with the specified details.
     *
     * @param name           The name of the officer.
     * @param NRIC           The NRIC of the officer.
     * @param password       The password for the officer's account.
     * @param age            The age of the officer.
     * @param maritalStatus  The marital status of the officer.
     */
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

    /**
     * Registers the officer to handle a specific project.
     *
     * @param project The project to register for.
     * @return true if registration is successful, false otherwise.
     */
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

    /**
     * Checks if the officer is registered for a project that overlaps with the given project's timeline.
     *
     * @param newProject The project to check against.
     * @return true if there is an overlap, false otherwise.
     */
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

    /**
     * Approves the officer for a specific project.
     *
     * @param project The project to approve the officer for.
     */
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

    /**
     * Checks if the officer is handling a specific project.
     *
     * @param p The project to check.
     * @return true if the officer is handling the project, false otherwise.
     */
    public boolean isHandlingProject(BTOProject p) {
        for (ApprovedProject ap : approvedProjects) {
            if (ap.getProject().equals(p)) return true;
        }
        return false;
    }


    /**
     *
     * Allows the officer to view the availability of flats in a specific project.
     * @param project
     * The project to check availability for
     */

    public void viewFlatAvailability(BTOProject project) {
        if (!isHandlingProject(project)) {
            System.out.println("You are not handling this project.");
            return;
        }
        System.out.println("2-Room units left: " + project.getTwoRoomUnits());
        System.out.println("3-Room units left: " + project.getThreeRoomUnits());
    }


    /**
     * Books a flat for an applicant based on their application.
     *
     * @param app      The application for which the flat is being booked.
     * @param flatType The type of flat to book.
     */
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

    /**
     * Displays the menu for the HDB Officer role.
     */
    @Override
    public void displayMenu() {
        System.out.println("--- HDB Officer Menu ---");
    }
}
