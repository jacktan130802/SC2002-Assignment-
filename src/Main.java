import system.Login;
import java.util.List;

public class Main {
    //Add in attributes
    List<String> userData;
    public static void main(String[] args){
        System.out.println("Welcome to BTO System! \nPlease Login");
        new Main().run();
    }

    public void run(){
        start();
        runUntilQuit();
        exit();
    }

    private void start(){
        Login login = new Login();
        userData = login.getData();
    }

    private void runUntilQuit(){
        System.out.println("Getting Data...");
        System.out.printf("Hello, %s, %s!\n", userData.get(0), userData.get(5));

    }

    private void exit(){
        System.exit(0);
    }
}