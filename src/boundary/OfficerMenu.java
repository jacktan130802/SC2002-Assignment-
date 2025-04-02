package boundary;

import java.util.*;

public class OfficerMenu {
    private Scanner sc = new Scanner(System.in);

    public int showOfficerOptions() {
        System.out.println("1. View Available Projects\n" + //
                        "2. Apply for BTO Project\n" + //
                        "3. View Application status\n" + //
                        "4. Submit/View/Edit/Delete Enquiries\n" + //
                        "5. Withdraw Application\n6. Register to Handle Project\n7. View Assigned Projects\n8. Book Flat for Applicant\n9. Reply to Enquiry\n10. Generate Receipt\n11. View Registration Status\n12. View Flat Availability");
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
}