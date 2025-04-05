package boundary;

import entity.roles.User;
import enums.FlatType;
import enums.MaritalStatus;
import java.util.*;

public class BTOMenu {
    private Scanner sc = new Scanner(System.in);

    public int showApplicantOptions(User user) {
        user.displayMenu();
        System.out.println("1. View BTO Projects\n2. Apply for BTO Projects\n3. View Application\n4. Submit/View/Edit/Delete Enquiries\n5. Withdraw Application");
        System.out.print("Choose option: ");
        int opt = sc.nextInt();
        sc.nextLine(); // FIX: consume the leftover newline
        return opt;
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
        System.out.print("Enter Project Name: ");
        return sc.nextLine();
    }

    public String promptEnquiryMessage() {
        System.out.print("Enter enquiry message: ");
        return sc.nextLine();
    }
    
}
