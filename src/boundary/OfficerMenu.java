package boundary;

import java.util.*;
import enums.FlatType;
import enums.MaritalStatus;

public class OfficerMenu {
    private Scanner sc = new Scanner(System.in);

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
        System.out.println("9. Book Flat for Applicant");
        System.out.println("10. Reply to Enquiry");
        System.out.println("11. Generate Receipt");
        System.out.println("12. View Flat Availability");
        System.out.println("===============================");
        System.out.print("Choose option: ");
        return sc.nextInt();
    }

    public String promptEnquiryReply() {
        sc.nextLine();
        System.out.print("Enter reply message: ");
        return sc.nextLine();
    }

    public String promptApplicantNRIC() {
        sc.nextLine();
        System.out.print("Enter applicant NRIC: ");
        return sc.nextLine();
    }

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

    public String promptProjectName() {
    sc.nextLine(); // Clear buffer
    System.out.print("Enter project name: ");
    return sc.nextLine();
}
    public String promptEnquiryMessage() {
        sc.nextLine(); // Clear buffer
        System.out.print("Enter enquiry message: ");
        return sc.nextLine();
    }

}