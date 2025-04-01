package boundary;

import java.util.*;
import enums.FlatType;
import enums.MaritalStatus;

public class BTOMenu {
    private Scanner sc = new Scanner(System.in);

    public int showApplicantOptions() {
        System.out.println("1. View Projects\n2. Apply for BTO\n3. View Application\n4. Submit/View/Edit/Delete Enquiries\n5. Withdraw Application");
        System.out.print("Choose option: ");
        return sc.nextInt();
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
        sc.nextLine();
        System.out.print("Enter Project Name: ");
        return sc.nextLine();
    }
}
