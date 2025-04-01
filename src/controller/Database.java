package controller;

import entity.btoProject.BTOProject;
import entity.roles.*;
import enums.MaritalStatus;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Database {
    private static final String APPLICANT_CSV = "src/data/ApplicantList.csv";
    private static final String OFFICER_CSV = "src/data/OfficerList.csv";
    private static final String MANAGER_CSV = "src/data/ManagerList.csv";
    private static final String PROJECT_CSV = "src/data/ProjectList.csv";

    public static void loadUsers(Map<String, User> users) {
        readCSV(users, APPLICANT_CSV, "Applicant");
        readCSV(users, OFFICER_CSV, "Officer");
        readCSV(users, MANAGER_CSV, "Manager");
    }

    public static void loadProjects(List<BTOProject> projects, Map<String, User> users) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECT_CSV))) {
            String line = reader.readLine(); // skip header
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yy");

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                String name = tokens[0];
                String neighborhood = tokens[1];
                String type1 = tokens[2]; // for 2 room flat
                int num1 = Integer.parseInt(tokens[3]);
                double price1 = Double.parseDouble(tokens[4]);
                String type2 = tokens[5]; // for 3 room flat 
                int num2 = Integer.parseInt(tokens[6]);
                double price2 = Double.parseDouble(tokens[7]);
                LocalDate open = LocalDate.parse(tokens[8], formatter);
                LocalDate close = LocalDate.parse(tokens[9], formatter);
                String managerNRIC = tokens[10];
                int maxOfficers = Integer.parseInt(tokens[11]);
                String[] officerNRICs = tokens.length > 12 ? tokens[12].split(";") : new String[0];

                HDBManager manager = (HDBManager) users.get(managerNRIC);
                BTOProject project = new BTOProject(name, neighborhood, num1, price1, num2, price2, open, close, manager, maxOfficers);
                
                for (String officerNRIC : officerNRICs) {
                    officerNRIC = officerNRIC.trim();
                    if (users.containsKey(officerNRIC) && users.get(officerNRIC) instanceof HDBOfficer officer) {
                        project.registerOfficer(officer);
                        project.approveOfficer(officer);
                    }
                }
                projects.add(project);
            }
        } catch (IOException e) {
            System.out.println("Failed to load project list: " + e.getMessage());
        }
    }

    private static void readCSV(Map<String, User> users, String filepath, String role) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                String name = tokens[0];
                String nric = tokens[1];
                int age = Integer.parseInt(tokens[2]);
                MaritalStatus status = MaritalStatus.valueOf(tokens[3].toUpperCase());
                String pwd = tokens[4];

                switch (role) {
                    case "Applicant" -> users.put(nric, new Applicant(nric, pwd, age, status));
                    case "Officer" -> users.put(nric, new HDBOfficer(nric, pwd, age, status));
                    case "Manager" -> users.put(nric, new HDBManager(nric, pwd, age, status));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading " + role + ": " + e.getMessage());
        }
    }
}
