// Updated Database.java with SavedApplicantList, SavedApplicationList, SavedEnquiryList full implementation
package controller;

import entity.Application;
import entity.btoProject.ApprovedProject;
import entity.btoProject.BTOProject;
import entity.btoProject.RegisteredProject;
import entity.enquiry.Enquiry;
import entity.roles.*;
import enums.ApplicationStatus;
import enums.FlatType;
import enums.MaritalStatus;
import enums.OfficerRegistrationStatus;

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

    private static final String SAVED_APPLICANT_CSV = BASE_PATH + "SavedApplicantList.csv";
    private static final String SAVED_ENQUIRY_CSV = BASE_PATH + "SavedEnquiryList.csv";
    private static final String SAVED_APPLICATION_CSV = BASE_PATH + "SavedApplicationList.csv";

    private static final String SAVED_OFFICER_CSV = BASE_PATH + "SavedOfficerList.csv";
    private static final String SAVED_REGISTERED_CSV = BASE_PATH + "SavedRegisteredProjectList.csv";
    private static final String SAVED_APPROVED_CSV = BASE_PATH + "SavedApprovedProjectList.csv";

    private static Map<String, RegisteredProject> registeredMap = new HashMap<>();
    public static Map<String, RegisteredProject> getRegisteredMap() {
        return registeredMap;
    }
    
    private static Map<String, ApprovedProject> approvedMap = new HashMap<>();

    private static Map<String, User> users = new HashMap<>();
    private static List<BTOProject> projects = new ArrayList<>();
    

    private static Map<Integer, Enquiry> enquiryMap = new HashMap<>();
    private static Map<Integer, Application> applicationMap = new HashMap<>();

    public static void loadAll() {
        loadUsers(users);
        loadProjects(projects, users);

        if (new File(SAVED_APPLICANT_CSV).exists()) {
            loadSavedApplications();
            loadSavedEnquiries();
            loadSavedApplicants();
        }

        if (new File(SAVED_OFFICER_CSV).exists()) {
        loadSavedRegisteredProjects();
        loadSavedApprovedProjects();
        loadSavedOfficers(); // Link IDs to projects via map
        }


        for (User u : users.values()) {
            if (u instanceof Applicant a) {
                System.out.println("[DEBUG] Applicant " + a.getNRIC() + " has " + a.getEnquiries().size() + " enquiries loaded.");
            }
        }
        
    }

    public static void loadUsers(Map<String, User> users) {
        
        readCSV(users, APPLICANT_CSV, "Applicant");
        readCSV(users, OFFICER_CSV, "Officer");
        readCSV(users, MANAGER_CSV, "Manager");
    }



    public static void saveAll() {
        saveUsers(users);
        saveProjects(projects);
        saveSavedApplications();
        saveSavedEnquiries();
        saveSavedApplicants();
        saveSavedOfficers();
        saveSavedRegisteredProjects();
        saveSavedApprovedProjects();

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
    
            for (BTOProject project : projects) {
                String officers = String.join(";", project.getApprovedOfficersNRICs());
    
                // Get manager name safely (handle null case)
                String managerName = (project.getManagerInCharge() != null)
                    ? project.getManagerInCharge().getName()
                    : "";
    
                writer.write(String.format("%s,%s,2-Room,%d,%.2f,3-Room,%d,%.2f,%s,%s,%s,%d,\"%s\",%b\n",
                    project.getProjectName(),
                    project.getNeighborhood(),
                    project.getTwoRoomUnits(), project.getPriceTwoRoom(),
                    project.getThreeRoomUnits(), project.getPriceThreeRoom(),
                    project.getOpeningDate(), project.getClosingDate(),
                    managerName,
                    project.getOfficerSlot(), officers,
                    project.isVisible()));
            }
    
        } catch (IOException e) {
            System.out.println("Error saving projects: " + e.getMessage());
        }
    }
    
    public static Map<String, User> getUsers() {
        return users;
    }

    public static List<BTOProject> getProjects() {
        return projects;
    }

    private static void readCSV(Map<String, User> users, String filepath, String role) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                String name = tokens[0];
                String nric = tokens[1];
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

    public static void loadProjects(List<BTOProject> projects, Map<String, User> users) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PROJECT_CSV))) {
            String header = reader.readLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> tokens = parseCSVLine(line);
                String name = tokens.get(0);
                String neighborhood = tokens.get(1);
                int num1 = Integer.parseInt(tokens.get(3));
                double price1 = Double.parseDouble(tokens.get(4));
                int num2 = Integer.parseInt(tokens.get(6));
                double price2 = Double.parseDouble(tokens.get(7));
                LocalDate open = LocalDate.parse(tokens.get(8), formatter);
                LocalDate close = LocalDate.parse(tokens.get(9), formatter);

                int slots = Integer.parseInt(tokens.get(11));
                String officers = tokens.size() > 12 ? tokens.get(12).replace("\"", "").trim() : "";
                boolean visible = tokens.size() > 13 && tokens.get(13).equalsIgnoreCase("true");
                String managerName = tokens.get(10);

                // Loop through users to find the manager by name
                HDBManager m = null;
                for (User u : users.values()) {
                    if (u instanceof HDBManager && u.getName().equalsIgnoreCase(managerName)) {
                        m = (HDBManager) u;
                        break;
                    }
                }
                
                if (m == null) {
                    System.out.println("⚠️ Manager with name '" + managerName + "' not found.");
                }
                

                BTOProject project = new BTOProject(name, neighborhood, num1, price1, num2, price2, open, close, m, slots);
                project.setVisibility(visible);

                if (!officers.isEmpty()) {
                    for (String o : officers.split(";")) {
                        if (users.get(o) instanceof HDBOfficer off) {
                            project.registerOfficer(off);
                            project.approveOfficer(off);
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
            if (c == '"') inQuotes = !inQuotes;
            else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else sb.append(c);
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }

    private static void loadSavedApplicants() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVED_APPLICANT_CSV))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", -1); // ensure empty strings are preserved
    
                if (tokens.length < 6) continue; // skip malformed lines
    
                String nric = tokens[1];
                if (!(users.get(nric) instanceof Applicant a)) continue;
    
                // Application ID
                if (!tokens[5].isBlank()) {
                    try {
                        int appId = Integer.parseInt(tokens[5]);
                        if (applicationMap.containsKey(appId)) {
                            a.setApplication(applicationMap.get(appId));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid application ID for NRIC " + nric);
                    }
                }
    
                // Enquiry IDs
                if (tokens.length > 6 && !tokens[6].isBlank()) {
                    String[] enquiryIds = tokens[6].split(";");
                    for (String id : enquiryIds) {
                        try {
                            int eid = Integer.parseInt(id.trim());
                            if (enquiryMap.containsKey(eid)) {
                                a.getEnquiries().add(enquiryMap.get(eid));
                                System.out.println("Trying to add enquiry: " + eid + " to applicant: " + nric);
                                System.out.println("[DEBUG] Total enquiries for applicant " + nric + ": " + a.getEnquiries().size());

                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid enquiry ID for NRIC " + nric);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading SavedApplicantList: " + e.getMessage());
        }
    }
    

    private static void saveSavedApplicants() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVED_APPLICANT_CSV))) {
            writer.write("Name,NRIC,Age,Marital Status,Password,ApplicationID,EnquiryIDs\n");
            for (User u : users.values()) {
                if (u instanceof Applicant a) {
                    String appId = a.getApplication() != null ? String.valueOf(a.getApplication().getApplicationId()) : "";
                    String enqIds = a.getEnquiries().isEmpty() ? "" : a.getEnquiries().stream()
                        .map(e -> String.valueOf(e.getEnquiryID()))
                        .reduce((a1, a2) -> a1 + ";" + a2).orElse("");
                    writer.write(String.format("%s,%s,%d,%s,%s,%s,%s\n",
                        a.getName(), a.getNRIC(), a.getAge(), a.getMaritalStatus(), a.getPassword(), appId, enqIds));
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving SavedApplicantList: " + e.getMessage());
        }
    }

    private static void loadSavedApplications() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVED_APPLICATION_CSV))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] t = line.split(",");
                int id = Integer.parseInt(t[0]);
                String nric = t[1];
                String projectName = t[2];
                FlatType type = FlatType.valueOf(t[3]);
                ApplicationStatus status = ApplicationStatus.valueOf(t[4]);

                if (users.get(nric) instanceof Applicant a && getProjectByName(projectName) != null) {
                    Application app = new Application(a, getProjectByName(projectName), type);
                    app.setStatus(status);
                    app.setApplicationId(id);
                    applicationMap.put(id, app);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading SavedApplicationList: " + e.getMessage());
        }
    }

    private static void saveSavedApplications() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVED_APPLICATION_CSV))) {
            writer.write("ApplicationID,ApplicantNRIC,ProjectName,FlatType,Status\n");
            for (User u : users.values()) {
                if (u instanceof Applicant a && a.getApplication() != null) {
                    Application app = a.getApplication();
                    writer.write(String.format("%d,%s,%s,%s,%s\n",
                        app.getApplicationId(), app.getApplicant().getNRIC(),
                        app.getProject().getProjectName(), app.getFlatType(), app.getStatus()));
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving SavedApplicationList: " + e.getMessage());
        }
    }

    private static void loadSavedEnquiries() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVED_ENQUIRY_CSV))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] t = line.split(",");
                int id = Integer.parseInt(t[0]);
                String nric = t[1];
                String projectName = t[2];
                String msg = t[3];
                String reply = t.length > 4 ? t[4] : null;

                if (users.get(nric) instanceof Applicant a && getProjectByName(projectName) != null) {
                    Enquiry e = new Enquiry(a, getProjectByName(projectName), msg);
                    e.setReply(reply);
                    e.setEnquiryID(id);
                    enquiryMap.put(id, e);
                    System.out.println("Loaded enquiry: " + id + " for " + nric);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading SavedEnquiryList: " + e.getMessage());
        }
    }

    private static void saveSavedEnquiries() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVED_ENQUIRY_CSV))) {
            writer.write("EnquiryID,ApplicantNRIC,ProjectName,Message,Reply\n");
            for (User u : users.values()) {
                if (u instanceof Applicant a) {
                    for (Enquiry e : a.getEnquiries()) {
                        writer.write(String.format("%d,%s,%s,%s,%s\n",
                            e.getEnquiryID(), e.getApplicant().getNRIC(),
                            e.getProject().getProjectName(), e.getMessage(),
                            e.getReply() != null ? e.getReply() : ""));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving SavedEnquiryList: " + e.getMessage());
        }
    }

    private static BTOProject getProjectByName(String name) {
        for (BTOProject p : projects) if (p.getProjectName().equals(name)) return p;
        return null;
    }

    private static void loadSavedOfficers() {
    try (BufferedReader reader = new BufferedReader(new FileReader(SAVED_OFFICER_CSV))) {
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] t = line.split(",", -1);
            String nric = t[1];
            if (!(users.get(nric) instanceof HDBOfficer o)) continue;

            if (!t[7].isBlank()) {
                for (String id : t[7].split(";")) {
                    RegisteredProject rp = registeredMap.get(id.trim());
                    if (rp != null) o.getRegisteredProjects().add(rp);
                }
            }
            if (!t[8].isBlank()) {
                for (String id : t[8].split(";")) {
                    ApprovedProject ap = approvedMap.get(id.trim());
                    if (ap != null) o.getApprovedProjects().add(ap);
                }
            }
        }
    } catch (IOException e) {
        System.out.println("Error loading officers: " + e.getMessage());
    }
}

private static void loadSavedRegisteredProjects() {
    try (BufferedReader reader = new BufferedReader(new FileReader(SAVED_REGISTERED_CSV))) {
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] t = line.split(",", -1);
            String id = t[0], proj = t[1], nric = t[2], status = t[3];

            if (users.get(nric) instanceof HDBOfficer o && getProjectByName(proj) != null) {
                RegisteredProject rp = new RegisteredProject(id, getProjectByName(proj), o, OfficerRegistrationStatus.valueOf(status));
                registeredMap.put(id, rp);
            }
        }
    } catch (IOException e) {
        System.out.println("Error loading registered projects: " + e.getMessage());
    }
}

private static void loadSavedApprovedProjects() {
    try (BufferedReader reader = new BufferedReader(new FileReader(SAVED_APPROVED_CSV))) {
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] t = line.split(",", -1);
            String id = t[0], proj = t[1], nric = t[2];

            if (users.get(nric) instanceof HDBOfficer o && getProjectByName(proj) != null) {
                ApprovedProject ap = new ApprovedProject(id, getProjectByName(proj), o);
                approvedMap.put(id, ap);
            }
        }
    } catch (IOException e) {
        System.out.println("Error loading approved projects: " + e.getMessage());
    }
}

private static void saveSavedOfficers() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVED_OFFICER_CSV))) {
        writer.write("Name,NRIC,Age,Marital Status,Password,ApplicationID,EnquiryIDs,RegisteredProjectIDs,ApprovedProjectIDs\n");
        for (User u : users.values()) {
            if (u instanceof HDBOfficer o) {
                String appId = o.getApplication() != null ? String.valueOf(o.getApplication().getApplicationId()) : "";
                String enqIds = o.getEnquiries().isEmpty() ? "" : o.getEnquiries().stream().map(e -> String.valueOf(e.getEnquiryID())).reduce((a, b) -> a + ";" + b).orElse("");
                String regIds = o.getRegisteredProjects().isEmpty() ? "" : o.getRegisteredProjects().stream().map(rp -> rp.getId()).reduce((a, b) -> a + ";" + b).orElse("");
                String apprIds = o.getApprovedProjects().isEmpty() ? "" : o.getApprovedProjects().stream().map(ap -> ap.getId()).reduce((a, b) -> a + ";" + b).orElse("");

                writer.write(String.format("%s,%s,%d,%s,%s,%s,%s,%s,%s\n",
                    o.getName(), o.getNRIC(), o.getAge(), o.getMaritalStatus(), o.getPassword(), appId, enqIds, regIds, apprIds));
            }
        }
    } catch (IOException e) {
        System.out.println("Error saving officers: " + e.getMessage());
    }
}


private static void saveSavedRegisteredProjects() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVED_REGISTERED_CSV))) {
        writer.write("RegisteredProjectID,ProjectName,OfficerNRIC,Status\n");
        for (RegisteredProject rp : registeredMap.values()) {
            writer.write(String.format("%s,%s,%s,%s\n", rp.getId(), rp.getProject().getProjectName(), rp.getOfficer().getNRIC(), rp.getStatus()));
        }
    } catch (IOException e) {
        System.out.println("Error saving registered projects: " + e.getMessage());
    }
}

private static void saveSavedApprovedProjects() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVED_APPROVED_CSV))) {
        writer.write("ApprovedProjectID,ProjectName,OfficerNRIC\n");
        for (ApprovedProject ap : approvedMap.values()) {
            writer.write(String.format("%s,%s,%s\n", ap.getId(), ap.getProject().getProjectName(), ap.getOfficer().getNRIC()));
        }
    } catch (IOException e) {
        System.out.println("Error saving approved projects: " + e.getMessage());
    }
}

}
