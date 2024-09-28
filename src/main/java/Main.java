public class Main {
    public static void main(String[] args) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        DBConfig config;
        config = DBConfig.LoadDBConfig("src/main/json/file-config.json");
        DiskManager dM = new DiskManager(config);
       // dM.AllocPage(); pour tester AllocPage, ps: ça va créer un nouveau fichier, augmenter dans file-config la taille max du fichier pour stopper la creation


    }
}
