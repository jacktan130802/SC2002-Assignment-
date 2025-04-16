package boundary;
import controller.Database;
import entity.btoProject.BTOProject;
import entity.roles.HDBManager;
import utility.Filter;

import java.util.*;

public class ManagerMenu {
    private Scanner sc = new Scanner(System.in);

    public int showManagerOptions() {
        System.out.println("\n--- HDB Manager Menu ---");
        System.out.println("1. Show All Projects");
        System.out.println("2. Show My Projects");
        System.out.println("3. View Projects with Filters");
        System.out.println("4. Create Project");
        System.out.println("5. Edit/Delete Project");
        System.out.println("6. Toggle Project Visibility");
        System.out.println("7. Approve Officer Registration");
        System.out.println("8. Approve/Reject Applications/Withdrawals");
        System.out.println("9. View & Reply Enquiries");
        System.out.println("10. Generate Report");
        System.out.println("11. Logout");
        System.out.print("Enter your choice: ");
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




}

