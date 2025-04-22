package boundary;

import entity.roles.User;
import enums.FlatType;
import enums.MaritalStatus;
import java.util.*;

/**
 * Boundary class for handling applicant-related user interactions.
 * This class provides methods for displaying options and prompting user input.
 */
public class ApplicantMenu {

    private Scanner sc = new Scanner(System.in);

    /**
     * Displays the menu options for the applicant and prompts for a choice.
     *
     * @param user The applicant user.
     * @return The selected menu option.
     */
    public int showApplicantOptions(User user) {
        user.displayMenu();
        System.out.println("1. View BTO Projects\n2. View BTO Projects with filter\n3. Apply for BTO Projects\n4. View Application\n5. Submit/View/Edit/Delete Enquiries\n6. Withdraw Application\n7.Logout");
        System.out.print("Choose option: ");
        int opt = sc.nextInt();
        sc.nextLine(); // FIX: consume the leftover newline
        return opt;
    }

    /**
     * Prompts the applicant to choose a flat type based on their marital status.
     *
     * @param status The marital status of the applicant.
     * @return The selected flat type.
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
     *
     * @return The entered project name.
     */
    public String promptProjectName() {
        System.out.print("Enter Project Name: ");
        return sc.nextLine();
    }

    /**
     * Prompts the user to enter an enquiry message.
     *
     * @return The entered enquiry message.
     */
    public String promptEnquiryMessage() {
        System.out.print("Enter enquiry message: ");
        return sc.nextLine();
    }
    
}
