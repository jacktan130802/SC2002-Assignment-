package boundary;
import controller.Database;
import entity.btoProject.BTOProject;
import entity.roles.HDBManager;
import utility.Filter;

import java.util.*;

public class ManagerMenu {
    private Scanner sc = new Scanner(System.in);

    public int showManagerOptions() {
        System.out.println("1. Create Project");
        System.out.println("2. Edit/Delete Project");
        System.out.println("3. Toggle Project Visibility");
        System.out.println("4. Approve Officer Registration");
        System.out.println("5. Approve/Reject Applications/Withdrawals");
        System.out.println("6. View & Reply Enquiries");
        System.out.println("7. Generate Report");
        System.out.println("8. Logout");
        System.out.println("9. View All Projects with Filters"); // New option
        System.out.print("Choose option: ");
        return sc.nextInt();
    }


    public String promptProjectName() {
        sc.nextLine();
        System.out.print("Enter Project Name: ");
        return sc.nextLine();
    }

    public String promptNeighborhood() {
        System.out.print("Enter Neighborhood: ");
        return sc.nextLine();
    }

    public int promptUnitCount(String roomType) {
        System.out.print("Enter number of " + roomType + " units: ");
        return sc.nextInt();
    }

    public String promptDate(String label) {
        sc.nextLine();
        System.out.print("Enter " + label + " date (YYYY-MM-DD): ");
        return sc.nextLine();
    }


    public void displayProjectsWithFilters(HDBManager manager) {
        List<BTOProject> allProjects = Database.getProjects();
        List<BTOProject> filteredProjects = new ArrayList<>(allProjects);

        while (true) {
            System.out.println("\n=== Project List ===");
            System.out.println("1. Show All Projects");
            System.out.println("2. Show My Projects");
            System.out.println("3. Filter by Neighborhood");
            System.out.println("4. Filter by Visibility");
            System.out.println("5. Back");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Clear buffer

            if (choice == 1) {
                filteredProjects = allProjects;
            } else if (choice == 2) {
                filteredProjects = Filter.filterByManager(allProjects, manager);
            } else if (choice == 3) {
                System.out.print("Enter Neighborhood: ");
                String neighborhood = sc.nextLine();
                filteredProjects = Filter.filterByNeighborhood(allProjects, neighborhood);
            } else if (choice == 4) {
                System.out.print("Show only visible projects? (true/false): ");
                boolean isVisible = sc.nextBoolean();
                filteredProjects = Filter.filterByVisibility(allProjects, isVisible);
            } else if (choice == 5) {
                break;
            } else {
                System.out.println("Invalid option. Try again.");
                continue;
            }

            // Display filtered projects
            if (filteredProjects.isEmpty()) {
                System.out.println("No projects found.");
            } else {
                System.out.println("\nFiltered Projects:");
                for (BTOProject project : filteredProjects) {
                    System.out.printf("- %s (Neighborhood: %s, Visibility: %s)\n",
                            project.getProjectName(),
                            project.getNeighborhood(),
                            project.isVisible() ? "ON" : "OFF");
                }
            }
        }
    }
}

