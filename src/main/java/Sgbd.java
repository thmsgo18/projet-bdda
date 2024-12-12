import buffer.BufferManager;
import espaceDisque.DBConfig;
import espaceDisque.DiskManager;
import espaceDisque.PageId;
import espaceDisque.PageOrientedJoinOperator;
import relationnel.ColInfo;
import relationnel.Record;
import relationnel.Relation;
import requete.*;

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
        String cheminFichierConfig="";

        if (args.length == 0) {
            cheminFichierConfig = "file-config.json";
        }else{
            cheminFichierConfig = args[0];
        }

        // /System.out.println("NOM FICHIER CONFIG : "+cheminFichierConfig);
        config = DBConfig.LoadDBConfig(cheminFichierConfig);

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

            }else if(texteCommande.startsWith("SELECT")) {
                System.out.println("La commande choisi est " + texteCommande);
                ProcessSelectCommand(texteCommande);
            }else if (texteCommande.startsWith("TEST")) {
                System.out.println("La commande choisi est " + texteCommande);
                ProcessTestCommand(texteCommande);
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

    private void ProcessTestCommand(String texteCommande) {


    }

    public void ProcessCreateDatabaseCommand(String texteCommande){ // Methode permettant de crée une BDD
        String[] tok = texteCommande.trim().split("CREATE DATABASE ");//on recupère que le nom de la BDD
        String nomDB = tok[1];// tok[0] == ""
        //System.out.println("Le nom de la database : "+nomDB+" La taille est : "+nomDB.length());
        dbManager.CreateDatabase(nomDB);//methode pour instancier la BDD dans le SGBD
        for(String key : dbManager.getDatabases().keySet()){
            //System.out.println("Le nom de la database : "+key+" La taille est : "+key.length());
        }
    }

    public void ProcessSetDatabaseCommand(String texteCommande){
        String[] tok = texteCommande.trim().split("SET DATABASE ");//on recupère que le nom de la BDD
        String nomDB = tok[1];
        //System.out.println("Le nom de la database : "+nomDB+" La taille est : "+nomDB.length());
        dbManager.SetCurrentDatabase(nomDB);//methode pour mettre la BDD courant
        for(String key : dbManager.getDatabases().keySet()){
           // System.out.println("Le nom de la database1111 : "+key+" La taille est : "+key.length());
        }

    }

    public void ProcessCreateTableCommand(String texteCommande) throws EOFException {
        Relation r;
        String caracAsupp = "(:,)";
        //System.out.println("La commande avant :"+texteCommande);

        for(char c : caracAsupp.toCharArray()){
            texteCommande = texteCommande.replace(String.valueOf(c)," "); //On supprime les caractère (:,) pour faciliter le parsing
        }

        texteCommande = texteCommande.replace("CREATE TABLE ","");//on remplace CREATE TABLE par ""

        //System.out.println("La commande après :"+texteCommande);
        StringTokenizer stz = new StringTokenizer(texteCommande, " "); //on tokenize, tout la chaine de caractere qui seront délimiter par des " ".
        List<ColInfo> infoColonne = new ArrayList<ColInfo>();
        String nomTab = stz.nextToken();//premier tok toujours le nom

        while(stz.hasMoreTokens()){

            String nom = stz.nextToken();//nom de la colonne
            //System.out.println("Le nom : "+nom);
            String type = stz.nextToken();//type de la colonne
            //System.out.println("Le type : "+type);

            if(type.equals("REAL")||type.equals("INT")){
                infoColonne.add(new ColInfo(nom,type,4));//cas pour INT ou REAL
            }
            else if(type.equals("VARCHAR")||type.equals("CHAR")){
                int tailleCol = 2*Integer.parseInt(stz.nextToken());//cas pour CHAR ou VARCHAR
                //System.out.println("Le taille : "+tailleCol);
                infoColonne.add(new ColInfo(nom,type,tailleCol));
            }
            else{
                //System.out.println("Le type n'existe pas\nRedirection au Menu SGBD");
            }
        }

        /*for(relationnel.ColInfo c : infoColonne){
            c.affiche_ColInfo();
        }*/
        PageId headerPage = ajouteHeaderPage(this.diskManager);// attribution d'un headerPage
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
        //System.out.println("Le nom de la bdd est : "+nombdd);

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
        //System.out.println("Nom :"+nom);
        ArrayList<Object> values = new ArrayList<>();
        relationnel.Record record = new relationnel.Record();
        for(Relation r : this.dbManager.getCurentDatabase().getTables()){ // On recherche la table correspondant au nom inscrit dans la commande
            if(r.getNomRelation().equals(nom)){ // Si on la trouve, on continue le programme et on fait un break pour éviter de poursuivre la boucle inutilement
                for(ColInfo c : r.getColonnes()){
                    if(c.getTypeColonne().equals("CHAR")){
                        String carac = stz.nextToken();
                        carac = carac.substring(1, carac.length()-1);
                        //System.out.println("Caracteres : "+carac+ "Taille : "+carac.length());
                        values.add(carac);

                    }
                    else if(c.getTypeColonne().equals("VARCHAR")){
                        String carac = stz.nextToken();
                        carac = carac.substring(1, carac.length()-1);
                        values.add(carac);
                        //System.out.println("Caracteres : "+carac+ "Taille : "+carac.length());


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

                System.out.println("SGBD : PROCESS INSERT INTO COMMAND : ensemble des record à cette table "+r.GetAllRecords());
                break;
            }
        }
    }

    public void ProcessBulkInsertIntoCommand(String  texteCommande){
        String caracAsupp = "(,)";
        for(char c : caracAsupp.toCharArray()){
            texteCommande = texteCommande.replace(String.valueOf(c)," ");
        }
        texteCommande = texteCommande.replace("BULKINSERT INTO ","");
        //System.out.println("Texte remplacé :"+texteCommande);
        StringTokenizer stz = new StringTokenizer(texteCommande, " ");
        String nomRelation = stz.nextToken().trim();
        String nomFichier =stz.nextToken().trim();
        //System.out.println("nomRelation :"+nomRelation+" nomFichier :"+nomFichier+"     longueur relation = "+nomRelation.length()+" longueur fichier = "+nomFichier.length());

        try {
            // On lit le fichier csv ligne par ligne
            FileReader fr = new FileReader(nomFichier);
            BufferedReader br = new BufferedReader(fr);
            String ligne;
            ArrayList<Object> tuple = new ArrayList<>();
            relationnel.Record record = new Record();
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
                   // System.out.println("ligne à lire (tuple) = " + ligne);
                    StringTokenizer st = new StringTokenizer(ligne, ";,");
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
                            //System.out.println("SGBD : BULK INSERT RECORD INTO COMMAND : Erreur : Le type reçu n'est pas valide et ne fait pas partie des types suivant : CHAR, VARCHAR, INT, REAL");
                        }
                        i++;
                    }

                    // On insère le record correspondant à chaque ligne du fichier
                   // System.out.println("SGBD : PROCESS BULK INSERT INTO COMMAND tuple : " + tuple);
                    record.setTuple(tuple);
                    table.InsertRecord(record);
                }
                //System.out.println("SGBD : PROCESS BULK INSERT INTO COMMAND l'ensemble des records de la page :"+table.GetAllRecords()); // On tente de lire l'ensemble des records inséré
            }

        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void ProcessSelectCommand(String texteCommande){

        String texteInstance = texteCommande.replace("SELECT","").trim();
        texteInstance = texteInstance.replaceAll("FROM.*","").trim();
        String commande = texteCommande.replace("SELECT","").replace(texteInstance,"").replace("FROM","").replaceAll("WHERE.*","").trim();
        String []joinouPas = commande.split(" ");
        if(joinouPas.length==2){
            SelectMono(texteCommande);
        }else{
            SelectJoin(texteCommande);
        }


    }

    private void SelectMono(String texteCommande){
        String texteInstance = texteCommande.replace("SELECT","").trim();
        texteInstance = texteInstance.replaceAll("FROM.*","").trim();
        List<String> alias = new ArrayList<String>();
        //System.out.println("Texte sans le SELECT ET LES FROM : "+texteInstance);
       // System.out.println(texteCommande);
        if(texteInstance.contains("*")){
           // System.out.println("On va prendre tout les colones car l'alias est une etoile");
            alias.add("*");
        }
        else{
            StringTokenizer stz = new StringTokenizer(texteInstance, ",");
            while(stz.hasMoreTokens()){
                alias.add(stz.nextToken());
            }
        }

        List<Condition> conditions = new ArrayList<>();
        String texteTableEtAlias = texteCommande.replace("SELECT","").replace(texteInstance,"").replace("FROM","").replaceAll("WHERE.*","").trim();
        StringTokenizer stz = new StringTokenizer(texteTableEtAlias," ");
        String nomTable = stz.nextToken();
        Relation table = dbManager.getCurentDatabase().getTable(nomTable);
        String aliasTable = stz.nextToken();
        //System.out.println("aliasTAble :"+aliasTable+" nomTable :"+ nomTable );


        String texteApresTable = texteCommande.replace("SELECT","").replace(texteInstance,"").replace("FROM","").replace(texteTableEtAlias,"");
        //System.out.println("voici le texte apres le table et son alias: "+texteApresTable);
        if(!(texteApresTable.contains("WHERE"))){
           // System.out.println("Il n'y a pas de where ");
        }
        else{
            texteApresTable = texteApresTable.replace("WHERE","").trim();
            //System.out.println("voici le texte apres le where  : "+texteApresTable);
            String[] tableauConditionTexte = texteApresTable.split(" AND ");
            for(String c : tableauConditionTexte){
                //System.out.println("Condition : "+c);
                conditions.add(Condition.ajouteCondition(table,aliasTable,c)); // on ajoute la condition à la liste de conditions
            }
        }
       // System.out.println("La commande SELECT à été bien parser !");

        RelationScanner relationScanner = new RelationScanner(table,conditions);
        ProjectOperator projectOperator = new ProjectOperator(table,aliasTable,relationScanner,alias);
        projectOperator.affiche();
    }

    private void SelectJoin(String texteCommande){
        String texteInstance = texteCommande.replace("SELECT","").trim();
        texteInstance = texteInstance.replaceAll("FROM.*","").trim();
        List<String> alias = new ArrayList<String>();
        //System.out.println("Texte sans le SELECT ET LES FROM : "+texteInstance);
       // System.out.println(texteCommande);
        if(texteInstance.contains("*")){
           // System.out.println("On va prendre tout les colones car l'alias est une etoile");
            alias.add("*");
        }
        else{
            StringTokenizer stz = new StringTokenizer(texteInstance, ",");
            while(stz.hasMoreTokens()){
                alias.add(stz.nextToken());
            }
        }
        List<Condition> conditions = new ArrayList<>();
        String tableETALias = texteCommande.replace("SELECT","").replace(texteInstance,"").replace("FROM","").replaceAll("WHERE.*","").trim();
        String table_alias = tableETALias.replace(","," ");
       // System.out.println("Voici la commande : "+table_alias);
        StringTokenizer stz = new StringTokenizer(table_alias," ");
        String nomTable1 = stz.nextToken();
        Relation table1 = dbManager.getCurentDatabase().getTable(nomTable1);
        String aliasTab1 = stz.nextToken();
        String nomTable2 = stz.nextToken();
        Relation table2 = dbManager.getCurentDatabase().getTable(nomTable2);
        String aliasTab2 = stz.nextToken();
    //    System.out.println("Alias 1 : "+aliasTab1+", NomTable : "+nomTable1);
      //  System.out.println("Alias 2 : "+aliasTab2+", NomTable : "+nomTable2);
        String texteApresWhere = texteCommande.replace("SELECT","").replace(texteInstance,"").replace("FROM","").replace(tableETALias,"");
        if(!texteApresWhere.contains("WHERE")){
           // System.out.println("Il n'y a pas de where ");
        }
        else{
            texteApresWhere = texteApresWhere.replace("WHERE","").trim();
           // System.out.println("voici le texte apres le where  : "+texteApresWhere);
            String[] tableauConditionTexte = texteApresWhere.split(" AND ");
            for(String c : tableauConditionTexte){
           //     System.out.println("Condition : "+c);
                conditions.add(Condition.ajouteCondition(table1,aliasTab1,table2,aliasTab2,c));
            }
            PageOrientedJoinOperator pageOrientedJoinOperator = new PageOrientedJoinOperator(diskManager,bufferManager,table1,table2,conditions);
            Record recordTest = new Record();
            RecordPrinter printer = null;
            while(recordTest!=null){
                recordTest =pageOrientedJoinOperator.GetNextRecord();
                if (recordTest!=null){
                    printer = new RecordPrinter(recordTest);
                    printer.affiche();
                }

            }

        }

    }

    public void ProcessQuitCommand(String texteCommande){//methode qui permet d'enregistrer le SGBD
        System.out.println("Vous avez choisit la commande QUIT !");
        dbManager.SaveState();
        bufferManager.FlushBuffers();
    }


    public static PageId ajouteHeaderPage(DiskManager diskManager) {
        PageId headerPage = diskManager.AllocPage();
        return headerPage;

    }



}