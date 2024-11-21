import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;

public class Sgbd {
    private DBConfig config;
    private DiskManager diskManager;
    private BufferManager bufferManager;
    private DBManager dbManager;
    public Sgbd(DBConfig config) {
        this.config = config;
        this.diskManager = new DiskManager(config);
        this.bufferManager = new BufferManager(config, diskManager);
        this.dbManager = new DBManager(config,diskManager,bufferManager);
        diskManager.LoadState();
        dbManager.LoadState();

    }

    public static void main(String[]args) throws EOFException {
        DBConfig config;
        config = DBConfig.LoadDBConfig("src/main/json/file-config.json");
        Sgbd sgbd = new Sgbd(config);
        sgbd.run();
    }

    public void run() throws EOFException {
        System.out.println("***********************Bienvenue dans la SGBD***********************");
        String texteCommande = "";
        boolean quit = false;

        while(!quit) {
            System.out.println("Entrez votre commande !");
            Scanner sc = new Scanner(System.in);
            texteCommande = sc.nextLine();
            if (texteCommande.contains("CREATE DATABASE")) {

                System.out.println("La commande choisi est " + texteCommande);
                ProcessCreateDatabaseCommand(texteCommande);

            } else if (texteCommande.contains("CREATE TABLE")) {

                System.out.println("La commande choisi est " + texteCommande);
                ProcessCreateTableCommand(texteCommande);

            } else if (texteCommande.contains("SET DATABASE")) {

                System.out.println("La commande choisi est " + texteCommande);
                ProcessSetDatabaseCommand(texteCommande);

            } else if (texteCommande.contains("LIST TABLES")) {

                System.out.println("La commande choisi est " + texteCommande);
                ProcessListTablesCommand(texteCommande);

            }else if (texteCommande.contains("LIST DATABASES")) {
                System.out.println("La commande choisi est " + texteCommande);
                ProcessListDatabasesCommand(texteCommande);

            } else if (texteCommande.startsWith("DROP TABLES")) {
                System.out.println("La commande choisi est " + texteCommande);
                ProcessDropTablesCommand(texteCommande);

            } else if (texteCommande.startsWith("DROP TABLE")) {
                System.out.println("La commande choisi est " + texteCommande);
                ProcessDropTableCommand(texteCommande);

            }  else if (texteCommande.startsWith("DROP DATABASES")) {
                System.out.println("La commande choisi est " + texteCommande);
                ProcessDropDatabasesCommand(texteCommande);

            } else if (texteCommande.startsWith("DROP DATABASE")) {
                System.out.println("La commande choisi est " + texteCommande);
                ProcessDropDatabaseCommand(texteCommande);

            } else if (texteCommande.contains("QUIT")) {
                System.out.println("La commande choisi est " + texteCommande);
                quit = true;
                ProcessQuitCommand(texteCommande);
            } else {
                System.out.println("Vous avez taper la mauvaise commande");

            }

        }
        System.out.println("Vous allez quitter le SGBD");


    }
    public void ProcessCreateDatabaseCommand(String texteCommande){
        String[] tok = texteCommande.trim().split("CREATE DATABASE ");
        String nomDB = tok[1];
        System.out.println("Le nom de la database : "+nomDB+" La taille est : "+nomDB.length());
        dbManager.CreateDatabase(nomDB);
        for(String key : dbManager.getDatabases().keySet()){
            System.out.println("Le nom de la database1111 : "+key+" La taille est : "+key.length());
        }
    }

    public void ProcessSetDatabaseCommand(String texteCommande){
        String[] tok = texteCommande.trim().split("SET DATABASE ");
        String nomDB = tok[1];
        System.out.println("Le nom de la database : "+nomDB+" La taille est : "+nomDB.length());
        dbManager.SetCurrentDatabase(nomDB);
        for(String key : dbManager.getDatabases().keySet()){
            System.out.println("Le nom de la database1111 : "+key+" La taille est : "+key.length());
        }

    }

    public void ProcessCreateTableCommand(String texteCommande) throws EOFException {
        Relation r;
        String caracAsupp = "(:,)";
        System.out.println("La commande avant :"+texteCommande);

        for(char c : caracAsupp.toCharArray()){
            texteCommande = texteCommande.replace(String.valueOf(c)," ");
        }

        texteCommande = texteCommande.replace("CREATE TABLE ","");

        System.out.println("La commande après :"+texteCommande);
        StringTokenizer stz = new StringTokenizer(texteCommande, " ");
        List<ColInfo> infoColonne = new ArrayList<ColInfo>();
        String nomTab = stz.nextToken();

        while(stz.hasMoreTokens()){

            String nom = stz.nextToken();
            System.out.println("Le nom : "+nom);
            String type = stz.nextToken();
            System.out.println("Le type : "+type);

            if(type.equals("REAL")||type.equals("INT")){
                infoColonne.add(new ColInfo(nom,type,4));
            }
            else if(type.equals("VARCHAR")||type.equals("CHAR")){
                int tailleCol = 2*Integer.parseInt(stz.nextToken());
                System.out.println("Le taille : "+tailleCol);
                infoColonne.add(new ColInfo(nom,type,tailleCol));
            }
            else{
                System.out.println("Le type n'existe pas\nRedirection au Menu SGBD");
            }
        }
        /*for(ColInfo c : infoColonne){
            c.affiche_ColInfo();
        }*/
        PageId headerPage = ajouteHeaderPage(this.diskManager,this.bufferManager);
        r = new Relation(nomTab,infoColonne.size(),headerPage,this.diskManager,this.bufferManager,infoColonne);
        r.addDataPage();
        this.dbManager.AddTableToCurrentDatabase(r);

    }


    public void ProcessListTablesCommand(String texteCommande){
        this.dbManager.ListTablesInCurrentDatabase();
    }

    public void ProcessListDatabasesCommand(String texteCommande){
        this.dbManager.ListDatabases();
    }

    public void ProcessDropTableCommand(String texteCommande){
        String[]tok = texteCommande.trim().split("DROP TABLE ");
        String nomtable = tok[1];

        for(Relation r : this.dbManager.getCurentDatabase().getTables()){
            if(r.getNomRelation().equals(nomtable)){
                for(PageId dataPage : r.getDataPages()){
                    this.diskManager.DeallocPage(dataPage);
                }
                this.diskManager.DeallocPage(r.getHeaderPageId());
            }
        }

        this.dbManager.RemoveTableFromCurrentDatabase(nomtable);

    }

    public void ProcessDropTablesCommand(String texteCommande){
        for(Relation r : this.dbManager.getCurentDatabase().getTables()){
            for(PageId datapage : r.getDataPages()){
                this.diskManager.DeallocPage(datapage);
            }
            this.diskManager.DeallocPage(r.getHeaderPageId());
        }
        this.dbManager.RemoveTablesFromCurrentDatabase();
    }

    public void ProcessDropDatabaseCommand(String texteCommande){
        String[]tok = texteCommande.trim().split("DROP DATABASE ");
        String nombdd = tok[1];
        System.out.println("Le nom de la bdd est : "+nombdd);

        for(Relation r : dbManager.getCurentDatabase().getTables()){
            for(PageId datapage : r.getDataPages()){
                this.diskManager.DeallocPage(datapage);
            }
            this.diskManager.DeallocPage(r.getHeaderPageId());
        }
        this.dbManager.RemoveDatabase(nombdd);



    }
    public void ProcessDropDatabasesCommand(String texteCommande){
        for(String key : this.dbManager.getDatabases().keySet()){
            for(Relation r : dbManager.getDatabases().get(key).getTables()){
                for(PageId datapage : r.getDataPages()){
                    this.diskManager.DeallocPage(datapage);
                }
                this.diskManager.DeallocPage(r.getHeaderPageId());
            }
        }
        this.dbManager.RemoveDatabases();


    }

    public void ProcessQuitCommand(String texteCommande){
        System.out.println("NULLLLLl");
        dbManager.SaveState();
        bufferManager.FlushBuffers();
    }


    public static PageId ajouteHeaderPage(DiskManager diskManager,BufferManager bufferManager) {
        System.out.println("**************  Initialisation d'une headerPage   *********************");
        // On initialisie les valeurs de la header page, le nombre de page est à 0 au début, suivi de l'emplacement de l'octet pour écrire une nouvelle case de page de données
        PageId headerPage = diskManager.AllocPage(); // On alloue une page disponible

        ByteBuffer buff =bufferManager.GetPage(headerPage);

        System.out.println("La header page est placé en "+headerPage);

        int position= (int) ((int) headerPage.getPageIdx()*diskManager.getDbConfig().getPagesize());


        String cheminFichier = diskManager.getDbConfig().getDbpath()+"/F"+headerPage.getFileIdx()+".bin"; // Chemin du fichier à lire
        File fichier = new File(cheminFichier);
        if(fichier.exists()) {
            try {
                RandomAccessFile raf = new RandomAccessFile(fichier, "rw"); // Ouverture du fichier
                raf.seek(position);  // Positionnement sur le premier octet de la page voulu

                //System.out.println(Arrays.toString(buff.array()));

                raf.seek(position);
                System.out.println(raf.readInt());
                System.out.println(raf.readInt());
                raf.close();
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                bufferManager.FreePage(headerPage,false);
            }
        }else{
            System.out.println("Vous tentez de lireun fichier qui n'existe pas");
        }

        return headerPage;

    }



}
