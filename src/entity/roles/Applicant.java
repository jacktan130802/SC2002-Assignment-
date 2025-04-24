package entity.roles;

import entity.Application;
import entity.btoProject.BTOProject;
import entity.enquiry.Enquiry;
import enums.ApplicationStatus;
import enums.FlatType;
import enums.MaritalStatus;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Represents an applicant in the system who can apply for BTO projects,
 * submit enquiries, and manage their applications.
 */
public class Applicant extends User {

    private Application application;
    private List<Enquiry> enquiries = new ArrayList<>();

    // For persistence tracking
    private int applicationId = -1;
    private List<Integer> enquiryIds = new ArrayList<>();

    /**
     * Constructs an Applicant with the specified details.
     *
     * @param name          The name of the applicant.
     * @param NRIC          The NRIC of the applicant.
     * @param password      The password of the applicant.
     * @param age           The age of the applicant.
     * @param maritalStatus The marital status of the applicant.
     */
    public Applicant(String name, String NRIC, String password, int age, MaritalStatus maritalStatus) {
        super(name, NRIC, password, age, maritalStatus);
    }

    /**
     * Gets the NRIC of the applicant.
     *
     * @return The NRIC of the applicant.
     */
    public String getNRIC() {
        return super.getNRIC();
    }

    /**
     * Displays details of a specified BTO project.
     *
     * @param p The BTO project details to view.
     */
    public void projectView(BTOProject p) {
        System.out.println("Project: " + p.getProjectName());
        System.out.println("Location: " + p.getNeighborhood());
        System.out.println("2-Room Units: " + p.getTwoRoomUnits());
        System.out.println("3-Room Units: " + p.getThreeRoomUnits());
    }

    /**
     * Allows the applicant to apply for a specified BTO project and flat type.
     *
     * @param p    The BTO project to apply for.
     * @param type The type of flat to apply for.
     */
    public void projectApply(BTOProject p, FlatType type) {
        if (application != null) {
            System.out.println("You have already applied to a project.");
            return;
        }
        if (maritalStatus == MaritalStatus.SINGLE && type == FlatType.THREE_ROOM) {
            System.out.println("Single applicants cannot apply for 3-Room flats.");
            return;
        }
        application = new Application(this, p, type);
        System.out.println("Application submitted for project: " + p.getProjectName());
    }


    /**
     * Gets the current application of the applicant.
     *
     * @return The current application, or null if none exists.
     */
    public Application getApplication() {
        return application;
    }

    /**
     * Sets the current application of the applicant.
     *
     * @param application The application to set.
     */
    public void setApplication(Application application) {
        this.application = application;
    }

    /**
     * Gets the list of enquiries submitted by the applicant.
     *
     * @return The list of enquiries.
     */
    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    /**
     * Checks if the applicant has applied to a specific BTO project.
     *
     * @param project The BTO project to check.
     * @return True if the applicant has applied to the project, false otherwise.
     */
    public boolean hasAppliedTo(BTOProject project) {
        return application != null && application.getProject().equals(project);
    }

    /**
     * Displays the menu options for the applicant.
     */
    public void displayMenu() {
        System.out.println("--- Applicant Menu ---");
    }
}

