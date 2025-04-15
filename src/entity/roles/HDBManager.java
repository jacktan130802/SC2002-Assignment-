package entity.roles;

import controller.Database;
import entity.Application;
import entity.btoProject.BTOProject;
import entity.enquiry.Enquiry;
import enums.ApplicationStatus;
import enums.MaritalStatus;
import enums.FlatType;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class HDBManager extends User {
    private List<BTOProject> createdProjects = new ArrayList<>();

    public HDBManager(String name, String NRIC, String password, int age, MaritalStatus maritalStatus) {
        super(name, NRIC, password, age, maritalStatus);
    }

    public boolean canCreateProject(BTOProject newProject) {
        for (BTOProject p : createdProjects) {
            if (p.isWithinApplicationPeriod(newProject.getOpeningDate()) && p.isVisible()) {
                return false;
            }
        }
        return true;
    }

    public void createProject(BTOProject project) {
        if (canCreateProject(project)) {
            createdProjects.add(project);
            System.out.println("Project created: " + project.getProjectName());
        } else {
            System.out.println("Cannot create project: already managing one during this application period with visibility ON.");
        }
    }

    public void editProject(BTOProject project, String newName, String newNeighborhood, int newTwoRoomUnits, int newThreeRoomUnits) {
        if (!createdProjects.contains(project)) {
            System.out.println("Project not found in your list.");
            return;
        }
        if (!project.isWithinApplicationPeriod(java.time.LocalDate.now())) {
            project.setVisibility(false); // optional lock
            project.setProjectName(newName);
            project.setNeighborhood(newNeighborhood);
            project.setTwoRoomUnits(newTwoRoomUnits);
            project.setThreeRoomUnits(newThreeRoomUnits);
            System.out.println("Project edited successfully.");
        } else {
            System.out.println("Cannot edit project during application period.");
        }
    }

    public void deleteProject(BTOProject project) {
        if (!createdProjects.contains(project)) {
            System.out.println("Project not found in your list.");
            return;
        }
        if (!project.isWithinApplicationPeriod(java.time.LocalDate.now())) {
            createdProjects.remove(project);
            System.out.println("Project deleted successfully.");
        } else {
            System.out.println("Cannot delete project during application period.");
        }
    }

    public void toggleProjectVisibilityForAllProjects(Scanner sc) {
        List<BTOProject> allProjects = Database.getProjects(); // Retrieve all projects

        if (allProjects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        System.out.println("\nAll Projects:");
        for (int i = 0; i < allProjects.size(); i++) {
            BTOProject project = allProjects.get(i);
            System.out.printf("%d. %s (Visibility: %s)%n",
                    i + 1,
                    project.getProjectName(),
                    project.isVisible() ? "ON" : "OFF");
        }

        System.out.print("Select a project to toggle visibility (1-" + allProjects.size() + "): ");
        try {
            int choice = sc.nextInt();
            if (choice < 1 || choice > allProjects.size()) {
                System.out.println("Invalid choice. Please select a valid project number.");
                return;
            }

            BTOProject selectedProject = allProjects.get(choice - 1);
            boolean newVisibility = !selectedProject.isVisible();
            selectedProject.setVisibility(newVisibility);
            System.out.println("Visibility for project \"" + selectedProject.getProjectName() + "\" toggled to: " + (newVisibility ? "ON" : "OFF"));

            Database.saveAll(); // Save changes to the database
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            sc.nextLine(); // Clear invalid input
        }
    }
    public void approveOfficer(BTOProject project, HDBOfficer officer) {
        if (project.getManagerInCharge() != this) {
            System.out.println("You are not the manager of this project.");
            return;
        }
        if (project.approveOfficer(officer)) {
            System.out.println("Officer approved successfully.");
        } else {
            System.out.println("Officer approval failed. Either not registered or slot limit reached.");
        }
    }

    public void approveRejectApplication(Application app, boolean approve) {
        if (!createdProjects.contains(app.getProject())) {
            System.out.println("You are not the manager of this application’s project.");
            return;
        }
        if (approve) {
            if ((app.getFlatType() == FlatType.TWO_ROOM && app.getProject().hasTwoRoom()) ||
                (app.getFlatType() == FlatType.THREE_ROOM && app.getProject().hasThreeRoom())) {
                app.setStatus(ApplicationStatus.SUCCESSFUL);
                System.out.println("Application approved.");
            } else {
                app.setStatus(ApplicationStatus.UNSUCCESSFUL);
                System.out.println("No units left. Application rejected.");
            }
        } else {
            app.setStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Application rejected by manager.");
        }
    }

    public void generateReport(List<Application> allApplications, String filterMaritalStatus) {
        System.out.println("--- Booking Report for Marital Status: " + filterMaritalStatus + " ---");
        for (Application app : allApplications) {
            if (app.getStatus() == ApplicationStatus.BOOKED &&
                app.getApplicant().getMaritalStatus().toString().equalsIgnoreCase(filterMaritalStatus)) {
                System.out.println("Name: " + app.getApplicant().getNRIC());
                System.out.println("Age: " + app.getApplicant().getAge());
                System.out.println("Flat Type: " + app.getFlatType());
                System.out.println("Project: " + app.getProject().getProjectName());
                System.out.println();
            }
        }
    }

    public void viewEnquiries(List<Enquiry> allEnquiries) {
        System.out.println("--- All Project Enquiries ---");
        for (Enquiry e : allEnquiries) {
            System.out.println("Enquiry ID: " + e.getEnquiryID());
            System.out.println("From: " + e.getApplicant().getNRIC());
            System.out.println("Project: " + e.getProject().getProjectName());
            System.out.println("Message: " + e.getMessage());
            System.out.println("Reply: " + (e.isReplied() ? e.getReply() : "[No reply yet]"));
            System.out.println();
        }
    }

    public void replyEnquiry(Enquiry e, String reply) {
        e.setReply(reply);
        System.out.println("Replied to enquiry ID " + e.getEnquiryID());
    }

    public List<BTOProject> getCreatedProjects() {
        return createdProjects;
    }

    @Override
    public void displayMenu() {
        System.out.println("--- HDB Manager Menu ---");
    }
}
