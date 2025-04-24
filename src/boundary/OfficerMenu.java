package boundary;

import enums.FlatType;
import enums.MaritalStatus;
import java.util.*;

/**
 * Represents the menu interface for officers to interact with the system.
 */
public class OfficerMenu {
    private Scanner sc = new Scanner(System.in);

    /**
     * Displays the officer menu options and prompts for a choice.
     * @return The selected menu option.
     */
    public int showOfficerOptions() {
        System.out.println("As an applicant");
        System.out.println("===============================");
        System.out.println("1. View Available Projects");
        System.out.println("2. Apply for BTO Project");
        System.out.println("3. View Application status");
        System.out.println("4. Submit/View/Edit/Delete Enquiries");
        System.out.println("5. Withdraw Application");
        System.out.println("===============================");
        System.out.println("As an officer");
        System.out.println("===============================");
        System.out.println("6. Register to Handle Project");
        System.out.println("7. View Registration Status");
        System.out.println("8. View Assigned Projects");
        System.out.println("9. View Project Details");
        System.out.println("10. Book Flat for Applicant, then generate receipt");
        System.out.println("11. Reply to Enquiry");
        System.out.println("12. View Flat Availability");
        System.out.println("===============================");
        System.out.println("14. Logout");
        System.out.print("Choose option: ");
        return sc.nextInt();
    }

    /**
     * Prompts the user to enter a reply message for an enquiry.
     * @return The reply message entered by the user.
     */
    public String promptEnquiryReply() {
        sc.nextLine();
        System.out.print("Enter reply message: ");
        return sc.nextLine();
    }

    /**
     * Prompts the user to choose a flat type based on their marital status.
     * @param status The marital status of the user.
     * @return The selected flat type as a FlatType enum.
     */
    public FlatType chooseFlatType(MaritalStatus status) {
        if (status == MaritalStatus.SINGLE) {
            System.out.println("You are eligible for 2-Room flats only.");
            return FlatType.TWO_ROOM;
        } else {
            System.out.println("Choose flat type: 1. TWO_ROOM  2. THREE_ROOM");
            int choice = sc.nextInt();
            return choice == 2 ? FlatType.THREE_ROOM : FlatType.TWO_ROOM;
        }
    }

    /**
     * Prompts the user to enter the name of a project.
     * @return The project name entered by the user as a String.
     */
    public String promptProjectName() {
    sc.nextLine(); // Clear buffer
    System.out.print("Enter project name: ");
    return sc.nextLine();
}

    /**
     * Prompts the user to enter an enquiry message.
     * @return The enquiry message entered by the user as a String.
     */

    public String promptEnquiryMessage() {
        sc.nextLine(); // Clear buffer
        System.out.print("Enter enquiry message: ");
        return sc.nextLine();
    }

}
