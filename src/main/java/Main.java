import espaceDisque.DBConfig;
import espaceDisque.DiskManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        DBConfig config;
        config = DBConfig.LoadDBConfig("src/main/json/file-config.json");
        DiskManager dM = new DiskManager(config);


    }
}
