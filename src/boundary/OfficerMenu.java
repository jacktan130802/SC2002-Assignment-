package boundary;

import java.util.*;

public class OfficerMenu {
    private Scanner sc = new Scanner(System.in);

    public int showOfficerOptions() {
        System.out.println("1. Reply to Enquiries");
        System.out.println("2. Book Flat for Applicant");
        System.out.println("3. Generate Receipt");
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