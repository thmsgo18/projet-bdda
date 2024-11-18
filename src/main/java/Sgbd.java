import java.util.Scanner;

public class Sgbd {
    private DBConfig config;
    private DiskManager diskManager;
    private BufferManager bufferManager;
    //private DBManager dbManager;
    public Sgbd(DBConfig config) {
        this.config = config;
        this.diskManager = new DiskManager(config);
        this.bufferManager = new BufferManager(config, diskManager);
        //this.dbManager = new DBManager(config);
        diskManager.LoadState();
        //DBManager.LoadState();

    }

    public static void main(String[]args){
        DBConfig config;
        config = DBConfig.LoadDBConfig("src/main/json/file-config.json");
        Sgbd sgbd = new Sgbd(config);
        sgbd.run();
    }

    public void run(){
        System.out.println("***********************Bienvenue dans la SGBD***********************");
        String texteCommande = "";
        boolean quit = false;

        while(!quit){
            System.out.println("Entrez votre commande !");
            Scanner sc = new Scanner(System.in);
            texteCommande = sc.nextLine();
            if(texteCommande.contains("CREATE DATABASE")){
                System.out.println("La commande choisi est "+texteCommande);
                ProcessCreateDatabaseCommand(texteCommande);
            }
            else if(texteCommande.contains("CREATE TABLE")){
                System.out.println("La commande choisi est "+texteCommande);
                ProcessCreateTableCommand(texteCommande);
            }
            else if(texteCommande.contains("SET DATABASE")){
                System.out.println("La commande choisi est "+texteCommande);
                ProcessSetDatabaseCommand(texteCommande);
            }
            else if(texteCommande.contains("LIST TABLES")){
                System.out.println("La commande choisi est "+texteCommande);
                ProcessListTablesCommand(texteCommande);
            }
            else if(texteCommande.contains("DROP TABLE")){
                System.out.println("La commande choisi est "+texteCommande);
                ProcessDropTableCommand(texteCommande);

            }
            else if(texteCommande.contains("DROP TABLES")){
                System.out.println("La commande choisi est "+texteCommande);
                ProcessDropTablesCommand(texteCommande);

            }
            else if(texteCommande.contains("DROP DATABASE")){
                System.out.println("La commande choisi est "+texteCommande);
                ProcessDropDatabaseCommand(texteCommande);

            }
            else if(texteCommande.contains("QUIT")){
                System.out.println("La commande choisi est "+texteCommande);
                quit = true;
                ProcessQuitCommand(texteCommande);
            }
            else{
                System.out.println("Vous avez taper la mauvaise commande");

            }

        }
        System.out.println("Vous allez quitter le SGBD");


    }
    public void ProcessCreateDatabaseCommand(String texteCommande){

    }

    public void ProcessCreateTableCommand(String texteCommande){

    }

    public void ProcessSetDatabaseCommand(String texteCommande){

    }

    public void ProcessListTablesCommand(String texteCommande){

    }
    public void ProcessDropTableCommand(String texteCommande){

    }

    public void ProcessDropTablesCommand(String texteCommande){

    }

    public void ProcessDropDatabaseCommand(String texteCommande){

    }

    public void ProcessQuitCommand(String texteCommande){

    }



}
