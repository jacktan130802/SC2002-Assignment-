package boundary;

import java.util.*;

/**
 * Boundary class for handling manager-related user interactions.
 * This class provides methods for displaying menu options and prompting user input.
 */
public class ManagerMenu {
    private Scanner sc = new Scanner(System.in);

    /**
     * Displays the menu options for the manager and prompts for a choice.
     *
     * @return The selected menu option.
     */
    public int showManagerOptions() {
        System.out.println("--- HDB Manager Menu ---");
        System.out.println("1. Show All Projects");
        System.out.println("2. Show My Projects");
        System.out.println("3. View Projects with Filters");
        System.out.println("4. View Project Details");
        System.out.println("5. Create Project");
        System.out.println("6. Edit/Delete Project");
        System.out.println("7. Toggle Project Visibility");
        System.out.println("8. Approve Officer Registration");
        System.out.println("9. Approve/Reject Applications/Withdrawals");
        System.out.println("10. View & Reply Enquiries");
        System.out.println("11. Generate Report");
        System.out.println("12. Logout");
        
        int choice = -1;
        try {
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // Clear buffer
        } catch (InputMismatchException e) {
            // Just let it return -1, which will be handled in the controller
            sc.nextLine(); // Clear invalid input
        }
        return choice;
    }
    /**
     * Prompts the user to enter the name of a project.
     *
     * @return The entered project name.
     */
    public String promptProjectName() {
        sc.nextLine();
        System.out.print("Enter Project Name: ");
        return sc.nextLine();
    }

    /**
     * Prompts the user to enter the neighborhood of a project.
     *
     * @return The entered neighborhood.
     */
    public String promptNeighborhood() {
        System.out.print("Enter Neighborhood: ");
        return sc.nextLine();
    }

    /**
     * Prompts the user to enter the number of units for a specific room type.
     *
     * @param roomType The type of room (e.g., "2-Room").
     * @return The entered number of units.
     */
    public int promptUnitCount(String roomType) {
        System.out.print("Enter number of " + roomType + " units: ");
        return sc.nextInt();
    }

    /**
     * Prompts the user to enter a date with a specific label.
     *
     * @param label The label for the date (e.g., "Opening Date").
     * @return The entered date as a string.
     */
    public String promptDate(String label) {
        System.out.print("Enter " + label + " date (YYYY-MM-DD): ");
        return sc.nextLine();
    }

    /**
     * Prompts the user to enter the selling price for a specific flat type.
     *
     * @param flatType The type of flat (e.g., "2-Room").
     * @return The entered selling price.
     */
    public double promptPrice(String flatType) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter selling price for " + flatType + ": ");
        return sc.nextDouble();
    }
}
