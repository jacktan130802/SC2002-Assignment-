package controller;


import entity.btoProject.ApprovedProject;
import entity.btoProject.BTOProject;
import entity.roles.*;
import java.util.*;
import java.util.stream.Collectors;

public class BTOProjectController {
    private List<BTOProject> projects;

    public BTOProjectController(List<BTOProject> projects) {
        this.projects = projects;
    }

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

    public BTOProject getProjectByName(String name) {
        return projects.stream()
                .filter(p -> p.getProjectName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }


    //CAN BE USED FOR MANAGER
    //
    // public List<BTOProject> getAllProjects() {
    //     return new ArrayList<>(Database.getProjects());
    // }

    // public static void getFullProjectDetails(User user, String projectname) {
    //     BTOProject project = Database.getProjects().stream()
    //     .filter(p -> p.getProjectName().equals(projectname))
    //     .findFirst()
    //     .orElse(null);
        
    //     if (project == null) {
    //         System.out.println("Project not found");
    //         return;
    //     }
        
    //     if (!(user instanceof HDBOfficer)) {
    //         System.out.println("Error: Only HDB officers can access full project details");
    //         return;
    //     }
        
    //     System.out.println("\n=== FULL PROJECT DETAILS ===");
    //     System.out.println("Project Name: " + project.getProjectName());
    //     System.out.println("Neighbourhood: " + project.getNeighborhood()  );
    //     System.out.println("Number of 2-ROOM units: " + project.getTwoRoomUnits());
    //     System.out.println("Number of 3-ROOM units: " + project.getThreeRoomUnits());
    //     System.out.println("2-ROOM Price: $" + project.getPriceTwoRoom());
    //     System.out.println("3-ROOM Price: $" + project.getPriceThreeRoom());
    //     System.out.println("Application Open Date: " + project.getOpeningDate());
    //     System.out.println("Applcation Closing Date: " + project.getClosingDate());
    //     System.out.println("Manager: " + project.getManagerInCharge());
    //     System.out.println("Officers: " + project.getOfficerSlot());
    //     System.out.println("==========================\n");
    // }

    public void viewOfficerProjectDetails(HDBOfficer officer) {
        List<BTOProject> approvedProjects = officer.getApprovedProjects()
            .stream()
            .map(ApprovedProject::getProject)
            .collect(Collectors.toList());

        if (approvedProjects.isEmpty()) {
            System.out.println("You are not approved for any projects");
            return;
        }

        // Display approved projects
        System.out.println("\nYour Approved Projects:");
        for (int i = 0; i < approvedProjects.size(); i++) {
            System.out.printf("%d. %s%n", i+1, approvedProjects.get(i).getProjectName());
        }

        // Select project to view details
        System.out.print("Select project to view (1-" + approvedProjects.size() + "): ");
        try {
            int choice = new Scanner(System.in).nextInt();
            if (choice < 1 || choice > approvedProjects.size()) {
                System.out.println("Invalid selection");
                return;
            }

            BTOProject selected = approvedProjects.get(choice-1);
            printProjectDetails(selected);
            
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number");
        }
    }

    private void printProjectDetails(BTOProject project) {
        System.out.println("\n=== PROJECT DETAILS ===");
        System.out.println("Name: " + project.getProjectName());
        System.out.println("Location: " + project.getNeighborhood());
        System.out.println("2-Room Units: " + project.getTwoRoomUnits());
        System.out.println("3-Room Units: " + project.getThreeRoomUnits());
        System.out.println("Opening Date: " + project.getOpeningDate());
        System.out.println("Closing Date: " + project.getClosingDate());
        System.out.println("=========================");
    }
}