package controller;

import entity.btoProject.BTOProject;
import entity.roles.*;
import enums.MaritalStatus;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Database {

    private static final String BASE_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "data" + File.separator;

    private static final String APPLICANT_CSV = BASE_PATH + "ApplicantList.csv";
    private static final String OFFICER_CSV = BASE_PATH + "OfficerList.csv";
    private static final String MANAGER_CSV = BASE_PATH + "ManagerList.csv";
    private static final String PROJECT_CSV = BASE_PATH + "ProjectList.csv";
    

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
            String header = reader.readLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> tokens = parseCSVLine(line);

                String projectName = tokens.get(0);
                String neighborhood = tokens.get(1);
                int num1 = Integer.parseInt(tokens.get(3));
                double price1 = Double.parseDouble(tokens.get(4));
                int num2 = Integer.parseInt(tokens.get(6));
                double price2 = Double.parseDouble(tokens.get(7));

                LocalDate open = LocalDate.parse(tokens.get(8), formatter);
                LocalDate close = LocalDate.parse(tokens.get(9), formatter);

                String managerNRIC = tokens.get(10);
                int officerSlot = Integer.parseInt(tokens.get(11));
                String officerNRICs = tokens.size() > 12 ? tokens.get(12).replace("\"", "").trim() : "";
                boolean isVisible = tokens.size() > 13 && tokens.get(13).trim().equalsIgnoreCase("true");

                HDBManager manager = (HDBManager) users.get(managerNRIC);
                BTOProject project = new BTOProject(projectName, neighborhood, num1, price1, num2, price2, open, close, manager, officerSlot);
                project.setVisibility(isVisible);
                

                if (!officerNRICs.isEmpty()) {
                    String[] officerArray = officerNRICs.split(",");
                    for (String officerNRIC : officerArray) {
                        officerNRIC = officerNRIC.trim();
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

    private static List<String> parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
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
                writer.write(String.format("%s,%s,%d,%s,%s\n",
                        user.getName(), user.getNRIC(), user.getAge(), user.getMaritalStatus(), user.getPassword()));
            }
        } catch (IOException e) {
            System.out.println("Error saving " + role + ": " + e.getMessage());
        }
    }

    private static void saveProjects(List<BTOProject> projects) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROJECT_CSV))) {
            writer.write("Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1,Type 2,Number of units for Type 2,Selling price for Type 2,Application opening date,Application closing date,Manager,Officer Slot,Officer,Visibility\n");
            writer.write("Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1,Type 2,Number of units for Type 2,Selling price for Type 2,Application opening date,Application closing date,Manager,Officer Slot,Officer,Visibility\n");

            for (BTOProject project : projects) {
                String officers = String.join(";", project.getRegisteredOfficersNRICs());
                writer.write(String.format("%s,%s,2-Room,%d,%.2f,3-Room,%d,%.2f,%s,%s,%s,%d,%s,%b\n",
                    project.getProjectName(),
                    project.getNeighborhood(),
                    project.getTwoRoomUnits(), project.getPriceTwoRoom(),
                    project.getThreeRoomUnits(), project.getPriceThreeRoom(),
                    project.getOpeningDate(), project.getClosingDate(),
                    project.getManagerInCharge().getNRIC(),
                    project.getOfficerSlot(), officers,
                    project.isVisible()));
            }

        } catch (IOException e) {
            System.out.println("Error saving projects: " + e.getMessage());
        }
    }
}