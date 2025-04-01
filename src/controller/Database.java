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

    private static Map<String, User> users = new HashMap<>();
    private static List<BTOProject> projects = new ArrayList<>();

    public static void loadAll() {
        loadUsers(users);
        loadProjects(projects, users);
    }

    public static Map<String, User> getUsers() {
        return users;
    }

    public static List<BTOProject> getProjects() {
        return projects;
    }

    public static void saveAll() {
        saveUsers(users);
        saveProjects(projects);
    }

    public static void loadUsers(Map<String, User> users) {
        readCSV(users, APPLICANT_CSV, "Applicant");
        readCSV(users, OFFICER_CSV, "Officer");
        readCSV(users, MANAGER_CSV, "Manager");
    }

    public static void loadProjects(List<BTOProject> projects, Map<String, User> users) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECT_CSV))) {
            String line = reader.readLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yy");

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                String projectName = tokens[0];
                String neighborhood = tokens[1];
                int num1 = Integer.parseInt(tokens[3]);
                double price1 = Double.parseDouble(tokens[4]);
                int num2 = Integer.parseInt(tokens[6]);
                double price2 = Double.parseDouble(tokens[7]);
                LocalDate open = LocalDate.parse(tokens[8], formatter);
                LocalDate close = LocalDate.parse(tokens[9], formatter);
                String managerNRIC = tokens[10];
                int officerSlot = Integer.parseInt(tokens[11]);
                String officerNRICs = tokens.length > 12 ? tokens[12] : "";

                HDBManager manager = (HDBManager) users.get(managerNRIC);
                BTOProject project = new BTOProject(projectName, neighborhood, num1, price1, num2, price2, open, close, manager, officerSlot);

                if (!officerNRICs.trim().isEmpty()) {
                    String[] officerArray = officerNRICs.split(";");
                    for (String officerNRIC : officerArray) {
                        if (users.containsKey(officerNRIC) && users.get(officerNRIC) instanceof HDBOfficer officer) {
                            project.registerOfficer(officer);
                            project.approveOfficer(officer);
                        }
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
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                String nric = tokens[1];
                String name = tokens[0];
                int age = Integer.parseInt(tokens[2]);
                MaritalStatus status = MaritalStatus.valueOf(tokens[3].toUpperCase());
                String pwd = tokens[4];

                switch (role) {
                    case "Applicant" -> users.put(nric, new Applicant(name, nric, pwd, age, status));
                    case "Officer" -> users.put(nric, new HDBOfficer(name, nric, pwd, age, status));
                    case "Manager" -> users.put(nric, new HDBManager(name, nric, pwd, age, status));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading " + role + ": " + e.getMessage());
        }
    }

    private static void saveUsers(Map<String, User> users) {
        Map<String, User> applicants = new HashMap<>();
        Map<String, User> officers = new HashMap<>();
        Map<String, User> managers = new HashMap<>();
    
        for (Map.Entry<String, User> entry : users.entrySet()) {
            User user = entry.getValue();
            if (user instanceof Applicant && !(user instanceof HDBOfficer)) {
                applicants.put(entry.getKey(), user);
            } else if (user instanceof HDBOfficer && !(user instanceof HDBManager)) {
                officers.put(entry.getKey(), user);
            } else if (user instanceof HDBManager) {
                managers.put(entry.getKey(), user);
            }
        }
    
        saveToCSV(applicants, APPLICANT_CSV, "Applicant");
        saveToCSV(officers, OFFICER_CSV, "Officer");
        saveToCSV(managers, MANAGER_CSV, "Manager");
    }
    

    private static void saveToCSV(Map<String, User> users, String filepath, String role) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write("Name,NRIC,Age,Marital Status,Password\n");
            for (User user : users.values()) {
                if (role.equals("Applicant") && user instanceof Applicant applicant) {
                    writer.write(String.format("%s,%s,%d,%s,%s\n", applicant.getName(), applicant.getNRIC(), applicant.getAge(), applicant.getMaritalStatus(), applicant.getPassword()));
                } else if (role.equals("Officer") && user instanceof HDBOfficer officer) {
                    writer.write(String.format("%s,%s,%d,%s,%s\n", officer.getName(), officer.getNRIC(), officer.getAge(), officer.getMaritalStatus(), officer.getPassword()));
                } else if (role.equals("Manager") && user instanceof HDBManager manager) {
                    writer.write(String.format("%s,%s,%d,%s,%s\n", manager.getName(), manager.getNRIC(), manager.getAge(), manager.getMaritalStatus(), manager.getPassword()));
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving " + role + ": " + e.getMessage());
        }
    }

    private static void saveProjects(List<BTOProject> projects) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROJECT_CSV))) {
            writer.write("Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1,Type 2,Number of units for Type 2,Selling price for Type 2,Application opening date,Application closing date,Manager,Officer Slot,Officer\n");
            for (BTOProject project : projects) {
                String officers = String.join(";", project.getRegisteredOfficersNRICs());
                writer.write(String.format("%s,%s,2-Room,%d,%.2f,3-Room,%d,%.2f,%s,%s,%s,%d,%s\n",
                        project.getProjectName(),
                        project.getNeighborhood(),
                        project.getTwoRoomUnits(), project.getPriceTwoRoom(),
                        project.getThreeRoomUnits(), project.getPriceThreeRoom(),
                        project.getOpeningDate(), project.getClosingDate(),
                        project.getManagerInCharge().getNRIC(),
                        project.getOfficerSlot(), officers));
            }
        } catch (IOException e) {
            System.out.println("Error saving projects: " + e.getMessage());
        }
    }
}
