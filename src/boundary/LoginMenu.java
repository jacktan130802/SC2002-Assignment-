package boundary;
import java.util.*;

class LoginMenu {
    private Scanner sc = new Scanner(System.in);

    public String[] displayLoginPrompt() {
        System.out.print("Enter NRIC: ");
        String nric = sc.nextLine();
        System.out.print("Enter password: ");
        String pwd = sc.nextLine();
        return new String[]{nric, pwd};
    }
}
