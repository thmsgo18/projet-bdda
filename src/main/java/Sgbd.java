import java.io.*;
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

    public void run() throws EOFException {  //Méthode pour utiliser le SGDB
        System.out.println("***********************Bienvenue dans la SGBD***********************");
        String texteCommande = "";
        boolean quit = false;

        while(!quit) {
            System.out.println("Entrez votre commande !");
            Scanner sc = new Scanner(System.in);
            texteCommande = sc.nextLine(); //Entrez une commande SQL tout en majuscule
            if (texteCommande.startsWith("CREATE DATABASE")) {

                if(texteCommande.replace("CREATE DATABASE", "").length() > 0) { //vérifier si il y a le nom de la BDD apres la commande CREATE DATABASE

                    System.out.println("La commande choisi est " + texteCommande);
                    ProcessCreateDatabaseCommand(texteCommande); //Methode permettant de parser la commande
                }
                else{
                    System.out.println("Vous n'avez pas taper le nom de la database");// retaper la commande en respectant les la syntaxe
                }

            } else if (texteCommande.startsWith("CREATE TABLE")) {

                if(texteCommande.replace("CREATE TABLE", "").length() > 0) {//vérifier si il y a le nom de la BDD apres la commande CREATE TABLE

                    System.out.println("La commande choisi est " + texteCommande);
                    ProcessCreateTableCommand(texteCommande);//Methode permettant de parser la commande (meme chose pour tout commande)
                }
                else{
                    System.out.println("Vous n'avez pas taper le nom de la table");// retaper la commande en respectant les la syntaxe(meme chose pour tout les commandes contenant un nom ou valeur)
                }



            } else if (texteCommande.startsWith("SET DATABASE")) {
                if(texteCommande.replace("SET DATABASE", "").length() > 0) {//vérifier si il y a le nom de la BDD apres la commande CREATE TABLE
                    System.out.println("La commande choisi est " + texteCommande);
                    ProcessSetDatabaseCommand(texteCommande); //Methode permettant de parser la commande
                }
                else{
                    System.out.println("Vous n'avez pas taper le nom de la database");
                }



            } else if (texteCommande.startsWith("LIST TABLES")) {

                System.out.println("La commande choisi est " + texteCommande);
                ProcessListTablesCommand(texteCommande);

            }else if (texteCommande.startsWith("LIST DATABASES")) {
                System.out.println("La commande choisi est " + texteCommande);
                ProcessListDatabasesCommand(texteCommande);

            } else if (texteCommande.startsWith("DROP TABLES")) {
                System.out.println("La commande choisi est " + texteCommande);
                ProcessDropTablesCommand(texteCommande);

            } else if (texteCommande.startsWith("DROP TABLE")) {

                if(texteCommande.replace("DROP TABLE", "").length() > 0) {//vérifier si il y a le nom de la BDD apres la commande DROP TABLE

                    System.out.println("La commande choisi est " + texteCommande);
                    ProcessDropTableCommand(texteCommande);
                }
                else{
                    System.out.println("Vous n'avez pas taper le nom de la table");
                }


            }  else if (texteCommande.startsWith("DROP DATABASES")) {
                System.out.println("La commande choisi est " + texteCommande);
                ProcessDropDatabasesCommand(texteCommande);

            } else if (texteCommande.startsWith("DROP DATABASE")) {

                if(texteCommande.replace("DROP DATABASE", "").length() > 0) {//vérifier si il y a le nom de la BDD apres la commande DROP DATABASE

                    System.out.println("La commande choisi est " + texteCommande);
                    ProcessDropDatabaseCommand(texteCommande);

                }
                else{
                    System.out.println("Vous n'avez pas taper le nom de la database");
                }


            }else if(texteCommande.startsWith("INSERT INTO")) {
                if(texteCommande.replace("INSERT INTO", "").length() > 0) {//vérifier si il y a le nom de la BDD apres la commande INSERT INTO
                    System.out.println("La commande choisi est " + texteCommande);
                    ProcessInsertIntoCommand(texteCommande);
                }
                else{
                    System.out.println("Vous n'avez pas taper le nom de la table");
                }

            }else if(texteCommande.startsWith("BULKINSERT INTO")) {
                System.out.println("La commande choisi est " + texteCommande);
                ProcessBulkInsertIntoCommand(texteCommande);
            }

            else if (texteCommande.contains("QUIT")) {
                System.out.println("La commande choisi est " + texteCommande);
                quit = true;
                ProcessQuitCommand(texteCommande);//on va quitter le SGBD
            } else {
                System.out.println("Vous avez taper la mauvaise commande");

            }

        }
        System.out.println("Vous allez quitter le SGBD");


    }

    public void ProcessCreateDatabaseCommand(String texteCommande){ // Methode permettant de crée une BDD
        String[] tok = texteCommande.trim().split("CREATE DATABASE ");//on recupère que le nom de la BDD
        String nomDB = tok[1];// tok[0] == ""
        System.out.println("Le nom de la database : "+nomDB+" La taille est : "+nomDB.length());
        dbManager.CreateDatabase(nomDB);//methode pour instancier la BDD dans le SGBD
        for(String key : dbManager.getDatabases().keySet()){
            System.out.println("Le nom de la database : "+key+" La taille est : "+key.length());
        }
    }

    public void ProcessSetDatabaseCommand(String texteCommande){
        String[] tok = texteCommande.trim().split("SET DATABASE ");//on recupère que le nom de la BDD
        String nomDB = tok[1];
        System.out.println("Le nom de la database : "+nomDB+" La taille est : "+nomDB.length());
        dbManager.SetCurrentDatabase(nomDB);//methode pour mettre la BDD courant
        for(String key : dbManager.getDatabases().keySet()){
            System.out.println("Le nom de la database1111 : "+key+" La taille est : "+key.length());
        }

    }

    public void ProcessCreateTableCommand(String texteCommande) throws EOFException {
        Relation r;
        String caracAsupp = "(:,)";
        System.out.println("La commande avant :"+texteCommande);

        for(char c : caracAsupp.toCharArray()){
            texteCommande = texteCommande.replace(String.valueOf(c)," "); //On supprime les caractère (:,) pour faciliter le parsing
        }

        texteCommande = texteCommande.replace("CREATE TABLE ","");//on remplace CREATE TABLE par ""

        System.out.println("La commande après :"+texteCommande);
        StringTokenizer stz = new StringTokenizer(texteCommande, " "); //on tokenize, tout la chaine de caractere qui seront délimiter par des " ".
        List<ColInfo> infoColonne = new ArrayList<ColInfo>();
        String nomTab = stz.nextToken();//premier tok toujours le nom

        while(stz.hasMoreTokens()){

            String nom = stz.nextToken();//nom de la colonne
            System.out.println("Le nom : "+nom);
            String type = stz.nextToken();//type de la colonne
            System.out.println("Le type : "+type);

            if(type.equals("REAL")||type.equals("INT")){
                infoColonne.add(new ColInfo(nom,type,4));//cas pour INT ou REAL
            }
            else if(type.equals("VARCHAR")||type.equals("CHAR")){
                int tailleCol = 2*Integer.parseInt(stz.nextToken());//cas pour CHAR ou VARCHAR
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
        PageId headerPage = ajouteHeaderPage(this.diskManager,this.bufferManager);// attribution d'un headerPage
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
        String[]tok = texteCommande.trim().split("DROP TABLE ");//recuperatuin du nom de la table
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
                this.diskManager.DeallocPage(datapage);//desalloué tout les page affecté à cette Page
            }
            this.diskManager.DeallocPage(r.getHeaderPageId());//desalloué la headerPage
        }
        this.dbManager.RemoveTablesFromCurrentDatabase();
    }

    public void ProcessDropDatabaseCommand(String texteCommande){//meme chose que la methode precedente mais on desalloue toute la database courant
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
    public void ProcessDropDatabasesCommand(String texteCommande){//meme chose que la methode precedente mais on desalloue toute les databases
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

    public void ProcessInsertIntoCommand(String texteCommande){//methode pour inserer les valeur dans un relation
        String caracAsupp = "(,)";
        for(char c : caracAsupp.toCharArray()){
            texteCommande = texteCommande.replace(String.valueOf(c)," ");
        }
        texteCommande = texteCommande.replace("INSERT INTO","");
        texteCommande = texteCommande.replace("VALUES","");
        StringTokenizer stz = new StringTokenizer(texteCommande, " ");
        String nom = stz.nextToken();
        System.out.println("Nom :"+nom);
        ArrayList<Object> values = new ArrayList<>();
        Record record = new Record();
        for(Relation r : this.dbManager.getCurentDatabase().getTables()){
            if(r.getNomRelation().equals(nom)){
                for(ColInfo c : r.getColonnes()){
                    if(c.getTypeColonne().equals("CHAR")){
                        String carac = stz.nextToken();
                        carac = carac.replace("\"","");
                        values.add(carac);

                    }
                    else if(c.getTypeColonne().equals("VARCHAR")){
                        String carac = stz.nextToken();
                        carac = carac.replace("\"","");
                        values.add(carac);

                    }
                    else if(c.getTypeColonne().equals("INT") ||c.getTypeColonne().equals("INTEGER") ){
                        Integer number = Integer.parseInt(stz.nextToken());
                        values.add(number);
                    }
                    else if(c.getTypeColonne().equals("REAL") ){
                        float passage =  (float ) Double.parseDouble(stz.nextToken());
                        values.add(passage);
                    }
                    else{
                        System.out.println("Ce type n'existe pas vous allez retourner dans le menu");
                        return;
                    }

                }
                record.setTuple(values);
                r.InsertRecord(record);
                r.GetAllRecords();

            }
        }

        /*while(stz.hasMoreTokens()){
            String element = stz.nextToken();
            if(element.contains("123456789")){
                int num = Integer.parseInt(element);
                values.add(num);
            } else if (element.contains(".")) {
                double num = Double.parseDouble(element);
                values.add(num);
            }
            else{
                values.add(element);
            }
        }

        Record record = new Record(values);
        for(Relation r : this.dbManager.getCurentDatabase().getTables()){
            if(r.getNomRelation().equals(nom)){
                r.InsertRecord(record);
                r.GetAllRecords();
            }
        }*/



    }

    public void ProcessBulkInsertIntoCommand(String  texteCommande){
        String caracAsupp = "(,)";
        for(char c : caracAsupp.toCharArray()){
            texteCommande = texteCommande.replace(String.valueOf(c)," ");
        }
        texteCommande = texteCommande.replace("BULKINSERT INTO ","");
        System.out.println("Texte remplacé :"+texteCommande);
        StringTokenizer stz = new StringTokenizer(texteCommande, " ");
        String nomRelation = stz.nextToken().trim();
        String nomFichier =stz.nextToken().trim();
        System.out.println("nomRelation :"+nomRelation+" nomFichier :"+nomFichier+"     longueur relation = "+nomRelation.length()+" longueur fichier = "+nomFichier.length());

        try {
            // On lit le fichier csv ligne par ligne
            FileReader fr = new FileReader(nomFichier);
            BufferedReader br = new BufferedReader(fr);
            String ligne;
            ArrayList<Object> tuple = new ArrayList<>();
            Record record = new Record();
            Relation table=null;
            for( Relation r : this.dbManager.getCurentDatabase().getTables()){
                if(r.getNomRelation().equals(nomRelation)){
                    table=r;
                    break;
                }
            }
            if (table!=null){
                while( (ligne = br.readLine()) != null ) {
                    tuple.clear();
                    // pour une ligne, plus tard faire une boucle pour toutes les lignes
                    System.out.println("ligne à lire (tuple) = " + ligne);
                    StringTokenizer st = new StringTokenizer(ligne, ";");
                    int i = 0;
                    while (st.hasMoreTokens()) {
                        if (table.getColonnes().get(i).getTypeColonne().equals("CHAR") || table.getColonnes().get(i).getTypeColonne().equals("VARCHAR")) {
                            tuple.add(st.nextToken().trim()); // On ajoute au tuple le token sous le format par defaut string
                        } else if (table.getColonnes().get(i).getTypeColonne().equals("INT") || table.getColonnes().get(i).getTypeColonne().equals("INTEGER")) {
                            tuple.add(Integer.parseInt(st.nextToken().trim())); // On convertit le token en int pour l'ajouter au tuple

                        } else if (table.getColonnes().get(i).getTypeColonne().equals("REAL")) {
                            float passage = (float) Double.parseDouble(st.nextToken().trim()); // On convertit le double en float ( dans le fichier csv il n'y a pas de ".f" pour signifier que le type est un float, il faut faire explicitement la conversion)
                            tuple.add(passage); // On convertit le token en float pour l'ajouter au tuple

                        } else {
                            System.out.println("SGBD : BULK INSERT RECORD INTO COMMAND : Erreur : Le type reçu n'est pas valide et ne fait pas partie des types suivant : CHAR, VARCHAR, INT, REAL");
                        }
                        i++;
                    }

                    // On insère le record correspondant à chaque ligne du fichier
                    System.out.println("SGBD : PROCESS BULK INSERT INTO COMMAND tuple : " + tuple);
                    record.setTuple(tuple);
                    table.InsertRecord(record);
                }
                System.out.println("SGBD : PROCESS BULK INSERT INTO COMMAND l'ensemble des records de la page :"+table.GetAllRecords()); // On tente de lire l'ensemble des records inséré
            }

        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void ProcessQuitCommand(String texteCommande){//methode qui permet d'enregistrer le SGBD
        System.out.println("Vous avez choisit la commande QUIT !");
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
