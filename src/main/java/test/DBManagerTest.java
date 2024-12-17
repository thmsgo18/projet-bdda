package test;

import buffer.BufferManager;
import espaceDisque.DBConfig;
import espaceDisque.DiskManager;
import espaceDisque.PageId;
import relationnel.ColInfo;
import relationnel.Relation;
import requete.DBManager;
import relationnel.Record;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DBManagerTest {

    public static void main(String [] args){
        DBConfig config = DBConfig.LoadDBConfig("file-config.json");
        DiskManager diskManager = new DiskManager(config);
        BufferManager bufferManager = new BufferManager(config,diskManager);
        DBManager dbmanag = new DBManager(config, diskManager, bufferManager);

        dbmanag.CreateDatabase("Université"); // Création de la base de donnée Université
        dbmanag.SetCurrentDatabase("Université"); // Université est mise en Currente DataBase
        PageId headerPageId =ajouteHeaderPage(diskManager,bufferManager); // Création d'un headerPageId

        ColInfo cI1 = new ColInfo("Nom", "CHAR", 18); // Colonne 0
        ColInfo cI2 = new ColInfo("Prenom", "CHAR", 12); // Colonne 1
        ColInfo cI3 = new ColInfo("Age", "INT", 4); // Colonne 2

        List<ColInfo> listeColonnesInfo= new ArrayList<>(); // Création liste de colonnes
        listeColonnesInfo.add(cI1); // Ajout de la colonne 0 à la liste de colonnes
        listeColonnesInfo.add(cI2); // Ajout de la colonne 1 à la liste de colonnes
        listeColonnesInfo.add(cI3); // Ajout de la colonne 2 à la liste de colonnes

        // Même chose pour une deuxième liste de colonnes
        ColInfo cI4 = new ColInfo("Secteur", "CHAR", 6);
        ColInfo cI5 = new ColInfo("Lettre", "CHAR", 14);
        ColInfo cI6 = new ColInfo("Nbr Places", "REAL", 1);

        List<ColInfo> listeColonnesInfo2= new ArrayList<>();
        listeColonnesInfo2.add(cI4);
        listeColonnesInfo2.add(cI5);
        listeColonnesInfo2.add(cI6);

        dbmanag.AddTableToCurrentDatabase(new Relation("Etudiant", 3,headerPageId, diskManager, bufferManager, listeColonnesInfo)); // Création & Ajout d'une table Etudiant
        dbmanag.AddTableToCurrentDatabase(new Relation("Salle", 3,headerPageId, diskManager, bufferManager, listeColonnesInfo2)); // Création & Ajout d'une table Salle
        System.out.println(dbmanag.GetTableFromCurrentDatabase("Etudiant"));
        System.out.println(dbmanag.GetTableFromCurrentDatabase("Salle"));
        dbmanag.SaveState(); // Sauvegarde de toutes DB dans un fichier JSON
        dbmanag.RemoveDatabases(); // Suppression de toutes les DB
        dbmanag.LoadState(); // Récupération de la DB à partir du fichier JSON
        dbmanag.SetCurrentDatabase("Université"); // Université est mise en Currente DataBase
        dbmanag.ListTablesInCurrentDatabase();
        dbmanag.ListTablesInCurrentDatabase();

        dbmanag.CreateDatabase("Université à supprimer");
        dbmanag.SetCurrentDatabase("Université à supprimer"); // Université à supprimer est mise en Currente DataBase
        dbmanag.AddTableToCurrentDatabase(new Relation("Etudiant", 3,headerPageId, diskManager, bufferManager, listeColonnesInfo)); // Création & Ajout d'une table Etudiant
        dbmanag.AddTableToCurrentDatabase(new Relation("Salle", 3,headerPageId, diskManager, bufferManager, listeColonnesInfo2)); // Création & Ajout d'une table Salle


        Scanner scanner = new Scanner(System.in);
        System.out.println("Pour combien de valeur veux tu tester ?? ");
        int nb = scanner.nextInt();

        dbmanag.SetCurrentDatabase("Université");
        Relation relation = dbmanag.GetTableFromCurrentDatabase("Salle");


        for (int i = 0; i < nb; i++) {
            float f = 1.5f;
            ArrayList<Object> val = new ArrayList<>();
            val.add("UFR");
            val.add("Cordier");
            val.add(i+f);
            Record record = new Record(val);
            try{
                relation.addDataPage();
            }catch (Exception e){
                e.printStackTrace();
            }
            relation.InsertRecord(record);
        }
        System.out.println(relation.GetAllRecords());
        System.out.println(dbmanag.GetTableFromCurrentDatabase("Salle").GetAllRecords());

    }




    public static PageId ajouteHeaderPage(DiskManager diskManager, BufferManager bufferManager) {
        System.out.println("**************  Initialisation d'une headerPage   *********************");
        // On initialisie les valeurs de la header page, le nombre de page est à 0 au début, suivi de l'emplacement de l'octet pour écrire une nouvelle case de page de données
        PageId headerPage = diskManager.AllocPage(); // On alloue une page disponible

        ByteBuffer buff =bufferManager.GetPage(headerPage);

        System.out.println("La header page est placé en "+headerPage);

        int position= (int) ( headerPage.getPageIdx()*diskManager.getDbConfig().getPagesize());


        String cheminFichier = diskManager.getDbConfig().getDbpath()+"/BinData"+"/F"+headerPage.getFileIdx()+".bin"; // Chemin du fichier à lire
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
            System.out.println("Vous tentez de lire un fichier qui n'existe pas");
        }

        return headerPage;

    }
}
