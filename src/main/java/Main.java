public class Main {
    public static void main(String[] args) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        DBConfig.LoadDBConfig("src/main/json/file-config.json");
        System.out.println("Hello World");
    }
}
