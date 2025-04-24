package controller;


import entity.btoProject.ApprovedProject;
import entity.btoProject.BTOProject;
import entity.btoProject.RegisteredProject;
import entity.roles.*;
import enums.OfficerRegistrationStatus;
import java.util.*;
import java.util.stream.Collectors;

import database.*;

/**
 * Controller class for managing BTO (Build-To-Order) projects.
 * Provides functionality for retrieving, viewing, and managing project details.
 */
public class BTOProjectController {
    private List<BTOProject> projects;

    /**
     * Constructs a BTOProjectController with a list of BTO projects.
     *
     * @param projects The list of BTO projects to manage.
     */
    public BTOProjectController(List<BTOProject> projects) {
        this.projects = projects;
    }

    /**
     * Retrieves a list of BTO projects visible to a specific applicant based on their eligibility.
     *
     * @param applicant The applicant whose eligibility is being checked.
     * @return A list of BTO projects visible to the applicant.
     */
    public List<BTOProject> getVisibleProjectsFor(Applicant applicant) {
        List<BTOProject> viewable = new ArrayList<>();
        boolean isSingle = applicant.getMaritalStatus().toString().equalsIgnoreCase("SINGLE");
        int age = applicant.getAge();
    
        for (BTOProject p : projects) {
            if (!p.isVisible()) continue;
    
            if (isSingle && age >= 35 && p.hasTwoRoom()) {
                viewable.add(p);
            } else if (!isSingle && age >= 21 && (p.hasTwoRoom() || p.hasThreeRoom())) {
                viewable.add(p);
            }
        }
    
        return viewable;
    }

    /**
     * Retrieves a BTO project by its name.
     *
     * @param name The name of the project to retrieve.
     * @return The BTO project with the specified name, or null if not found.
     */
    public BTOProject getProjectByName(String name) {
        return projects.stream()
                .filter(p -> p.getProjectName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    /**
     * Allows an HDB officer to view details of the projects they are approved for.
     * Displays a list of approved projects and prompts the officer to select one for detailed viewing.
     *
     * @param officer The HDB officer viewing their approved projects.
     */

    public void viewOfficerProjectDetails(HDBOfficer officer) {
        List<BTOProject> approvedProjects = officer.getApprovedProjects()
            .stream()
            .map(ApprovedProject::getProject)
            .collect(Collectors.toList());
    
        if (approvedProjects.isEmpty()) {
            System.out.println("You are not approved for any projects");
            return;
        }
    
        System.out.println("\nYour Approved Projects:");
        for (int i = 0; i < approvedProjects.size(); i++) {
            BTOProject p = approvedProjects.get(i);
            System.out.printf("%d. %s (Manager: %s)%n", 
                i+1, 
                p.getProjectName(), 
                p.getManagerInCharge().getName());
        }
    
        System.out.print("Select project to view (1-" + approvedProjects.size() + "): ");
        try {
            int choice = new Scanner(System.in).nextInt();
            if (choice < 1 || choice > approvedProjects.size()) {
                System.out.println("Invalid selection");
                return;
            }
            printProjectDetails(approvedProjects.get(choice-1));
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number");
        }
    }

    /**
     * Prints detailed information about a specific BTO project.
     * Includes project name, location, manager, assigned officers, unit availability, and application period.
     *
     * @param project The BTO project whose details are to be printed.
     */
    private void printProjectDetails(BTOProject project) {
        System.out.println("\n=== PROJECT DETAILS ===");
        System.out.println("Name: " + project.getProjectName());
        System.out.println("Location: " + project.getNeighborhood());
        System.out.println("Manager: " + project.getManagerInCharge().getName());
        
        System.out.println("\nAssigned Officers:");
        List<HDBOfficer> assignedOfficers = Database.getRegisteredMap().values().stream()
            .filter(rp -> rp.getProject().equals(project))
            .filter(rp -> rp.getStatus() == OfficerRegistrationStatus.APPROVED)
            .map(RegisteredProject::getOfficer)
            .collect(Collectors.toList());
        
        if (assignedOfficers.isEmpty()) {
            System.out.println("No officers currently assigned");
        } else {
            assignedOfficers.forEach(officer -> 
                System.out.println("- " + officer.getName() + " (" + officer.getNRIC() + ")"));
        }
        
        System.out.println("\nUnit Availability:");
        System.out.println("2-Room Units: " + project.getTwoRoomUnits());
        System.out.println("3-Room Units: " + project.getThreeRoomUnits());
        System.out.println("\nApplication Period:");
        System.out.println("Opens: " + project.getOpeningDate());
        System.out.println("Closes: " + project.getClosingDate());
        System.out.println("=========================");
    }
}