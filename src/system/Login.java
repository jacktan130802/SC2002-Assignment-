package system;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

// Example in userData

/**
 *
 * UserData: (Applicant)
 *
 * [John, S1234567A, 35, Single, password, Applicant]
 *
 */


public class Login {
    String userID;
    String password;
    String[] userTypes = {"Applicant", "Manager", "Officer"};
    ArrayList<String> userData = new ArrayList<>();
    Scanner sc = new Scanner(System.in);

    public Login() {
        do {
            System.out.println("Enter User ID:");
            userID = sc.nextLine();
            System.out.println("Enter Password:");
            password = sc.nextLine();
            System.out.println("Entered User ID: " + userID + ", Entered Password: " + password); // Debug statement
            verifyAccount(userID, password);
            if (userData.isEmpty())
                System.out.println("Invalid User ID or Password!");
        } while (userData.isEmpty());
    }

    // in mvc standard, the checking should be stored in the controller
    private void verifyAccount(String userID, String password) {
        File file = new File("src/user_data/ApplicantList.csv");
        if (!file.exists()) {
            System.out.println("ApplicantList.csv file not found at: " + file.getAbsolutePath());
            return;
        }

        try {
            int type = 0;
            Scanner data = new Scanner(file);
            data.nextLine(); // for skipping the title

            while (data.hasNextLine()) { // Going through all user data
                String line = data.nextLine();
                String[] row = line.split(",");
                System.out.println("Read line: " + line); // Debug statement
                if (row.length == 1) {
                    type++;
                } else if (row.length == 5) {
                    System.out.println("Checking user: " + row[1] + " with password: " + row[4]); // Debug statement
                    if (row[1].equals(userID) && row[4].equals(password)) {
                        Collections.addAll(userData, row); // adding all basic details into userData
                        userData.add(userTypes[type]); // adding type
                        System.out.println("User found: " + userData); // Debug statement
                        break; // Exit loop once user is found
                    }
                }
            }
            data.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    //Todo: displayWelcomeMessage
    void displayWelcomeMessage() {
        System.out.println("Welcome to BTO System! \nPlease Login");
    }




    // in mvc standard, the data should be stored in the model
    public ArrayList<String> getData() {
        return userData;
    }
}