package entity.roles;

import enums.MaritalStatus;

/**
 * Represents a user in the system.
 */
public abstract class User {
    // all attributes access level = protected, so subclasses can inherit
    protected String name;
    protected String NRIC;
    protected String password;
    protected int age;
    protected MaritalStatus maritalStatus;

    /**
     * Constructs a new User.
     *
     * @param name The name of the user.
     * @param NRIC The NRIC of the user.
     * @param password The password of the user.
     * @param age The age of the user.
     * @param maritalStatus The marital status of the user.
     */
    public User(String name, String NRIC, String password, int age, MaritalStatus maritalStatus) {
        this.name = name;
        this.NRIC = NRIC;
        this.password = password;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    /**
     * Gets the name of the user.
     *
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the NRIC of the user.
     *
     * @return The NRIC of the user.
     */
    public String getNRIC() {
        return NRIC;
    }

    /**
     * Gets the age of the user.
     *
     * @return The age of the user.
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets the marital status of the user.
     *
     * @return The marital status of the user.
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Gets the password of the user.
     *
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Changes the user's password.
     *
     * @param newPassword The new password to be set.
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * Authenticates the user by comparing the input password with the stored password.
     *
     * @param inputPassword The password to be verified.
     * @return {@code true} if the password matches, {@code false} otherwise.
     */
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    /**
     * Displays the menu for the user.
     */
    public abstract void displayMenu();
}
