package entity.roles;

import entity.Application;
import entity.btoProject.BTOProject;
import entity.enquiry.Enquiry;
import enums.ApplicationStatus;
import enums.MaritalStatus;
import enums.FlatType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import database.Database;

/**
 * Represents an HDB Manager who manages BTO projects and oversees applications.
 * The manager can create, edit, delete projects, approve/reject applications,
 * and handle project visibility.
 */
public class HDBManager extends User {
    private List<BTOProject> createdProjects = new ArrayList<>();

    /**
     * Constructs an HDBManager with the specified details.
     *
     * @param name           The name of the manager.
     * @param NRIC           The NRIC of the manager.
     * @param password       The password for the manager's account.
     * @param age            The age of the manager.
     * @param maritalStatus  The marital status of the manager.
     */
    public HDBManager(String name, String NRIC, String password, int age, MaritalStatus maritalStatus) {
        super(name, NRIC, password, age, maritalStatus);
    }

    /**
     * Checks if the manager can create a new project based on overlapping application periods.
     *
     * @param newProject The new project to be created.
     * @return true if the project can be created, false otherwise.
     */
    public boolean canCreateProject(BTOProject newProject) {
        for (BTOProject p : createdProjects) {
            if (p.isWithinApplicationPeriod(newProject.getOpeningDate()) && p.isVisible()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new BTO project if allowed.
     *
     * @param project The project to be created.
     * @return true if the project was successfully created, false otherwise.
     */
    public boolean createProject(BTOProject project) {
        if (canCreateProject(project)) {
            createdProjects.add(project);
            System.out.println("Project created: " + project.getProjectName());
            return true;
        } else {
            System.out.println("Cannot create project: already managing one during this application period with visibility ON.");
            return false;
        }
    }

    /**
     * Retrieves the list of BTO projects created by the manager.
     *
     * @return A list of BTO projects created by the manager.
     */
    public List<BTOProject> getCreatedProjects() {
        return createdProjects;
    }

    /**
     * Displays the menu options available to the HDB Manager.
     */
    @Override
    public void displayMenu() {
        System.out.println("--- HDB Manager Menu ---");
    }
}
