package boundary;
import java.util.*;

class ManagerMenu {
    private Scanner sc = new Scanner(System.in);

    public int showManagerOptions() {
        System.out.println("1. Create Project");
        System.out.println("2. Toggle Project Visibility");
        System.out.println("3. Approve Applications");
        System.out.println("4. Approve Officer Registrations");
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
}
