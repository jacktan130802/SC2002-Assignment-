package interfaces;

public interface IPasswordManagement {
    boolean changePassword(String nric, String oldPassword, String newPassword);
} 