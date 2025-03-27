package entity.user;

import entity.btoProject.BTOProject;
import entity.enquiry.Enquiry;

import java.util.Scanner;

public class Applicant extends User { 
    private BTOProject appliedProject;
    private String applicationStatus;
    private String flatType;
    private Enquiry Enquiries[];
    private int maxEnqId = 0;

    public void projectView(BTOProject p) {}
    public void projectApply(BTOProject p) {}
    public void projectWithdraw(BTOProject p) {}

    public void enquirySubmit() {
        // add id assignment to Enquiries
    }

    public void enquiryView() {
        // input enquiry ID
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter ID of enquiry you want to view: ");
        int enqId = sc.nextInt();
        sc.close();
        Enquiry enq = null;
        boolean found = false;

        // find enquiry
        for (int i=0;i<Enquiries.length;i++) {
            if (Enquiries[i].id==enqId) enq = Enquiries[i];
            found = true;
        }

        // check if enquiry exists
        if (!found) {
            System.out.println("Could not find an enquiry with that ID!");
            return;
        }

        enq.view();
    }

    public void enquiryEdit(int enqID, String newMessage) {
        // input enquiry ID
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter ID of enquiry you want to view: ");
        int enqId = sc.nextInt();
        sc.close();
        Enquiry enq = null;
        boolean found = false;

        // find enquiry
        for (int i=0;i<Enquiries.length;i++) {
            if (Enquiries[i].id==enqId) enq = Enquiries[i];
            found = true;
        }

        // check if enquiry exists
        if (!found) {
            System.out.println("Could not find an enquiry with that ID!");
            return;
        }
        enq.editMessage(newMessage);
    }

    public void enquiryDelete(int enqID) {
        // input enquiry ID
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter ID of enquiry you want to view: ");
        int enqId = sc.nextInt();
        sc.close();
        Enquiry enq = null;
        int foundId=-1;

        // find enquiry
        for (int i=0;i<Enquiries.length;i++) {
            if (Enquiries[i].id==enqId) {
                foundId = i;
                break;
            }
        }

        // check if enquiry exists
        if (foundId==-1) {
            System.out.println("Could not find an enquiry with that ID!");
            return;
        }

        Enquiries[foundId] = null;
    }
}