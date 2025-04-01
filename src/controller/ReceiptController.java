package controller;

import entity.Application;

import enums.*;


public class ReceiptController {
    public void generateReceipt(Application app) {
        if (app.getStatus() == ApplicationStatus.BOOKED) {
            System.out.println("--- Receipt ---");
            System.out.println("NRIC: " + app.getApplicant().getNRIC());
            System.out.println("Age: " + app.getApplicant().getAge());
            System.out.println("Flat Type: " + app.getFlatType());
            System.out.println("Project: " + app.getProject().getProjectName());
        }
    }
}