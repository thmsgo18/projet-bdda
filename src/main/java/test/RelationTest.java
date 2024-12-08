package test;

import buffer.BufferManager;
import espaceDisque.DBConfig;
import espaceDisque.DiskManager;
import espaceDisque.PageId;
import relationnel.ColInfo;
import relationnel.Relation;
import relationnel.Record;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RelationTest {

    public static void main(String[] args) {
        ColInfo cI1 = new ColInfo("Nom", "CHAR", 12);
        ColInfo cI2 = new ColInfo("Prenom", "CHAR", 6);
        ColInfo cI3 = new ColInfo("Age", "INT", 4);
        DBConfig config = DBConfig.LoadDBConfig("src/main/json/file-config.json");
        DiskManager diskManager = new DiskManager(config);
        BufferManager bufferManager = new BufferManager(config,diskManager);
        PageId headerPageId =ajouteHeaderPage(diskManager,bufferManager);
        List<ColInfo> listeColonnesInfo= new ArrayList<>();
        listeColonnesInfo.add(cI1);
        listeColonnesInfo.add(cI2);
        listeColonnesInfo.add(cI3);

        Relation relation = new Relation("Etudiant", 3,headerPageId, diskManager, bufferManager, listeColonnesInfo);

        /// Test, insérant des records, et des data pages (va insérer 20 records et 20 data pages , dans file config json j'ai mis une grande taille de page (4096) donc pour écrire les recrds on aura pas besoin de toutes ces pages créer mais seulement d'une)
        /// Le test va aussi affiché les 20 tuples inséré dans le terminal à travers la méthode GetAllRecords
        InsertRecordTest(relation);
        bufferManager.FlushBuffers(); /// "nettoie" les buffers, et mets à jour tous les fichiers bin doit toujours être fait à la fin


        /// ATTENTION !!! fonction permettant de supprimer tous les fichiers bin (F0bin,F1bin,etc..) (à utiliser avec vigilance)
            //Outil.SuprimeTousFichier(diskManager);


        //WriteRecordDataPageTest(relation);

        //GetRecordsInDataPageTest(relation);

        //GetDataPagesTest(relation);




    }


    public static void InsertRecordTest(Relation relation) {
        System.out.println("\n**************  DEBUT INSERT RECORD TEST *********************");

        int i =0,nb=22;
        while (i<20) {
            //System.out.println("Boucle InsertRecordTest : "+i);
            try {
                relation.addDataPage();
            }catch(EOFException e){
                e.printStackTrace();
                System.out.println("ERRRRRRREEEEEEEEEUUUUUUUUUUUUUUUR");
                System.out.println("Boucle InsertRecordTest erreur : "+i);
            }
            ArrayList<Object> a2 = new ArrayList<>(Arrays.asList("Traore", "Ali", nb++));
            Record record = new Record(a2);
            relation.InsertRecord(record);
            i++;
            System.out.println("Boucle Fin InsertRecordTest : "+i);
            System.out.println("GetAllRecord : " + relation.GetAllRecords());


        }
        System.out.println("\n**************  FIN INSERT RECORD TEST *********************");


    }

    public static void GetDataPagesTest(Relation relation) {
        System.out.println("\n**************  Obtention des pages de données d'une Header page *********************");
        List<PageId> listePage=relation.getDataPages();
        System.out.println(listePage);
    }



    public static void GetRecordsInDataPageTest(Relation relation) {
        System.out.println("\n**************  Récupération de record dans des pages de donneés  *********************");
        WriteRecordDataPageTest(relation);
        System.out.println("\n**************  on va récupérer les records de la page 0,1  *********************");

        ArrayList<Record> liste1 = relation.getRecordsInDataPage(new PageId(0,1));
        //ArrayList<relationnel.Record> liste2 =relation.getRecordsInDataPage(new espaceDisque.PageId(1,0));
        System.out.println(" Dans la page 0,1 on a les recors suivants :");
        int i =0;
        for(Record r : liste1) {
            System.out.println("relationnel.Record °"+i+" : "+r.toString());
            i++;
        }
        System.out.println("\n************** Fin Récupération de record dans des pages de donneés  *********************");

        /*
        System.out.println(" Dans la page 1,0 on a les recors suivants :");
        int y =0;
        for(relationnel.Record r : liste2) {
            System.out.println("relationnel.Record °"+y+" : "+r.toString());
            y++;
        }
        */

    }




    public static void WriteRecordDataPageTest(Relation relation){
        System.out.println("\n**************  Écriture de record dans des pages de donneés  *********************");
        System.out .println("(On ajoute 2 pages de données dans ce petit test)");
        // On ajoute une page de donnée
        Record r1,r2,r3;
        PageId pageDispo;
        // On ajoute 2 pages de données au cas où.

        try {
            relation.addDataPage();
            relation.addDataPage();
            relation.addDataPage();
            relation.addDataPage();
        }catch(EOFException e){
            e.printStackTrace();
        }
        // On va la chercher ici
        pageDispo = relation.getFreeDataPageId(38); // On cherche un octet pouvant accueilir {Traore Ali 20}
        System.out.println("Page Dispo : "+pageDispo);

        // {Traore Ali 20}

        r1 = new Record();
        ArrayList<Object> a1 = new ArrayList<>();
        a1.add("abcdef");
        a1.add("Ali");
        a1.add(20);
        r1.setTuple(a1);
        System.out.println("Page Dispo : "+pageDispo);

        relation.writeRecordToDataPage(r1,pageDispo);

        // {Valentin Ponnousammy 21}
        ArrayList<Object> a2 = new ArrayList<>(Arrays.asList("Traore", "Ali", 22));
        r2 = new Record(a2);
        pageDispo = relation.getFreeDataPageId(38);
        System.out.println("Page Dispo : "+pageDispo);

        relation.writeRecordToDataPage(r2,pageDispo);
        // {Thomas Gourmelen 21}
        ArrayList<Object> a3 = new ArrayList<>(Arrays.asList("Traore", "Ali",23));
        r3 = new Record(a3);
        pageDispo = relation.getFreeDataPageId(38);
        System.out.println("Page Dispo : "+pageDispo);
        relation.writeRecordToDataPage(r3,pageDispo);

    }




    public static void ajouteDataPageTest(DiskManager diskManager){

    }

    // Plus à jour, à ne pas réutiliser
    public void writeRecordToBufferTest() {
        System.out.println("\n******************** TEST WRITE RECORD TO BUFFER ************************\n");
        System.out.println("Pour l'exemple nous prendrons un record ayant tuple (20.353,10,'X',\"ALi\",\"Valentin\",\"Thomas\",\"Malik\") ");
        System.out.println("Cela nous permettra de tester l'ecriture des 4 types de données : int / float / char / string");
        System.out.println("Les \"VERIF\" que vous voyez ci dessous sont affiché directement via la fonction, il s'agit simplement d'affichage à but explicative, on pourra les supprimer par la suite.");
        System.out.println();
        DBConfig config;
        config = DBConfig.LoadDBConfig("src/main/json/file-config.json");
        DiskManager dm = new DiskManager(config);
        BufferManager bm = new BufferManager(config,dm);
        PageId headerPageId = new PageId(1, 2);
        DiskManager diskManager = new DiskManager(config);
        BufferManager bufferManager = new BufferManager(config,diskManager);
        Relation relation = new Relation("TestRelation",5,headerPageId, diskManager, bufferManager);
        ByteBuffer bb = ByteBuffer.allocate(4000);
        ArrayList<Object> t1 = new ArrayList<>();
        t1.add(20.353f);    // un float
        t1.add(10);     // un int
        t1.add('X');    // un caractère
        t1.add("Ali");
        t1.add("Valentin");       // 4 strings
        t1.add("Thomas");
        t1.add("Malik");
        Record record = new Record(t1);
        int reponseEcriture = relation.writeRecordToBuffer(record,bb,100);
        System.out.println("\nOn a ci dessous le buffer contenant les valeurs provenant du record");
        for(int i=0;i<bb.limit();i++) {
            System.out.print(bb.get()+" ");
        }
        System.out.println();
        bb.flip();

        System.out.println(reponseEcriture+" octets ont été écris dans le buffer");
    }


    public static PageId ajouteHeaderPage(DiskManager diskManager, BufferManager bufferManager) {
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

    // Plus à jour, à ne pas réutiliser
    public void readRecordFromBufferTest() {
        System.out.println("\n******************** TEST READ RECORD TO BUFFER ************************\n");
        System.out.println("Considerons que nous voulons lire le buffer qui a pour buffer, le buffer écris arbitrairement dans le test Write ");
        DBConfig config;
        config = DBConfig.LoadDBConfig("src/main/json/file-config.json");
        DiskManager dm = new DiskManager(config);
        BufferManager bm = new BufferManager(config,dm);
        PageId headerPageId = new PageId(1, 2);
        DiskManager diskManager = new DiskManager(config);
        BufferManager bufferManager = new BufferManager(config,diskManager);
        Relation relation = new Relation("TestRelation",5, headerPageId, diskManager, bufferManager);
        ByteBuffer bb = ByteBuffer.allocate(4000);
        ArrayList<Object> t1 = new ArrayList<>();
        t1.add(20.353f);    // un float
        t1.add(10);     // un int
        t1.add('X');    // un caractère
        t1.add("Ali");
        t1.add("Valentin");       // 4 strings
        t1.add("Thomas");
        t1.add("Malik");
        Record record = new Record(t1);
        relation.writeRecordToBuffer(record,bb,100);

        System.out.println("On a le buffer suivant :");
        for(int i=0;i<bb.limit();i++) {
            System.out.print(bb.get()+" ");
        }
        System.out.println("\n");
        bb.flip();

        Record record2 = new Record();
        System.out.println("Le tuple du record vaut avant l'appel de la fonction = "+record2.getTuple());

        System.out.println("On va donc enregistrer dans le tuple vide du record, les valeurs : (20.353,10,'X',\"ALi\",\"Valentin\",\"Thomas\",\"Malik\")" );

        int reponseLecture= relation.readFromBuffer(record2, bb, 100);

        System.out.println("Le tuple du record vaut après l'appel de la fonction = "+record2.getTuple());
        System.out.println(reponseLecture+" octets ont été lu du buffer"); // À préciser qu'on y inclus les bitTypes (Rappel : un bitType par element)
        // Avec les bitsType, on est à 61
        // Sans, on est à 61-7= 54
    }



}
