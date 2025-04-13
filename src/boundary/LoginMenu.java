package boundary;
import java.util.*;

public class LoginMenu {
    private Scanner sc = new Scanner(System.in);

    public String[] displayLoginPrompt() {
        System.out.print("Enter NRIC (E.g S1234567D): ");
        String nric = sc.nextLine();
        System.out.print("Enter password: ");
        String pwd = sc.nextLine();
        return new String[]{nric, pwd};
    }

    public String[] promptChangePassword() {
        System.out.print("Enter current password: ");
        String oldPwd = sc.nextLine();
        System.out.print("Enter new password: ");
        String newPwd = sc.nextLine();
        return new String[]{oldPwd, newPwd};
    }
    
}
