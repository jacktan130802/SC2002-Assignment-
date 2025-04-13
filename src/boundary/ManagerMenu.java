package boundary;
import java.util.*;

public class ManagerMenu {
    private Scanner sc = new Scanner(System.in);

    public int showManagerOptions() {
        System.out.println("1. Create Project\n2. Edit/Delete Project\n3. Toggle Project Visibility\n4. Approve Officer Registration\n5. Approve/Reject Applications/Withdrawals\n6. View & Reply Enquiries\n7. Generate Report\n8. Logout");
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
