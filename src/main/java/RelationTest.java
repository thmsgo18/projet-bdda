import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RelationTest {

    public static void main(String[] args) {
        ColInfo cI1 = new ColInfo("Nom", "VARCHAR", 12);
        ColInfo cI2 = new ColInfo("Prenom", "CHAR", 6);
        ColInfo cI3 = new ColInfo("Age", "INT", 4);
        DBConfig config = DBConfig.LoadDBConfig("src/main/json/file-config.json");
        DiskManager diskManager = new DiskManager(config);
        BufferManager bufferManager = new BufferManager(config,diskManager);
        PageId headerPageId = ajouteHeaderPage(diskManager);/* test.ajouteHeaderPage(diskManager);
                                                 !!!!!!!!!!!!!!!!!!! ajout d'une nouvelle header page, faire attention cette action est sauvegardé apres la fin du programme
                                                                       dans le cas où l'on souhaite travailler sur la meme header page apres avoir deja appelé cette fonction, il faudra mettre directement la page correspondante, et non plus faire appel à la méthode car ça ne renverra pas les meme indice de page, car la page courante a changé
                                                                    */
        List<ColInfo> listeColonnesInfo= new ArrayList<>();
        listeColonnesInfo.add(cI1);
        listeColonnesInfo.add(cI2);
        listeColonnesInfo.add(cI3);

        Relation relation = new Relation("Etudiant", 3,headerPageId, diskManager, bufferManager, listeColonnesInfo);

        //WriteRecordDataPageTest(relation);

        //GetRecordsInDataPageTest(relation);

        //GetDataPagesTest(relation);

        InsertRecordTest(relation);
        //ByteBuffer bb = ByteBuffer.allocate((int) diskManager.getDbConfig().getPagesize());
        //diskManager.ReadPage(headerPageId,bb);
        //System.out.println("header page apres modif : "+Arrays.toString(bb.array()));
        /*
        Record r1 = new Record();
        ArrayList<Object> a1 = new ArrayList<>();
        a1.add("Traore");
        a1.add("Ali");
        a1.add(20);

        r1.setTuple(a1);


        ByteBuffer bb = ByteBuffer.allocate(4000);


        System.out.println("*************ECRITURE*************");

        int n1= relation.writeRecordToBuffer(r1,bb,0);
        System.out.println(n1+" octets ont été réserver à l'écriture dans le buffer");
        for(int i=0;i<bb.limit();i++) {
            System.out.print(bb.get()+" ");
        }
        System.out.println();
        bb.flip();
        System.out.println("*************LECTURE*************");



        Record r2 = new Record();
        int n2= relation.readFromBuffer(r2,bb,0);
        System.out.println(n2+" octets ont été vraiment lu dans le buffer");
        */



    }


    public static void InsertRecordTest(Relation relation){
        int i =0,nb=22;
        while (i<3) {
            relation.addDataPage();
            ArrayList<Object> a2 = new ArrayList<>(Arrays.asList("Traore", "Ali", nb++));
            Record record = new Record(a2);
            relation.InsertRecord(record);
            System.out.println("GetAllRecord : " + relation.GetAllRecords());
            i++;
        }

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
        //ArrayList<Record> liste2 =relation.getRecordsInDataPage(new PageId(1,0));
        System.out.println(" Dans la page 0,1 on a les recors suivants :");
        int i =0;
        for(Record r : liste1) {
            System.out.println("Record °"+i+" : "+r.toString());
            i++;
        }
        System.out.println("\n************** Fin Récupération de record dans des pages de donneés  *********************");

        /*
        System.out.println(" Dans la page 1,0 on a les recors suivants :");
        int y =0;
        for(Record r : liste2) {
            System.out.println("Record °"+y+" : "+r.toString());
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

        relation.addDataPage();
        relation.addDataPage();
        relation.addDataPage();
        relation.addDataPage();
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


    public static PageId headerPageBidon(DiskManager diskManager){
        System.out.println("**************  Implémentation de fausses valeurs dans un header page bidon  *********************");

        PageId headerPage = ajouteHeaderPage(diskManager);
        System.out.println(headerPage);
        ByteBuffer buff = ByteBuffer.allocate((int) diskManager.getDbConfig().getPagesize());
        diskManager.ReadPage(headerPage,buff);
        System.out.println("header page avant modif : "+Arrays.toString(buff.array()));
        buff.putInt(0,3);
        int i=0;
        int valeur =20;
        int positionPlaceDisponible=16;
        int fileV=0,pageV=0;
        while(i<3){
            buff.position(positionPlaceDisponible);
            buff.putInt(valeur);
            buff.position(buff.position()-12);
            buff.putInt(fileV);
            buff.putInt(pageV);
            positionPlaceDisponible+=12;
            pageV++;
            valeur+=20;
            i++;
        }

        buff.flip();
        diskManager.WritePage(headerPage,buff);
        System.out.println("header page apres modif : "+Arrays.toString(buff.array()));

        return headerPage;
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


    public static PageId ajouteHeaderPage(DiskManager diskManager) {
        System.out.println("**************  Initialisation d'une headerPage   *********************");
        // On initialisie les valeurs de la header page, le nombre de page est à 0 au début, suivi de l'emplacement de l'octet pour écrire une nouvelle case de page de données
        ByteBuffer buff = ByteBuffer.allocate((int) diskManager.getDbConfig().getPagesize());
        buff.putInt(0);  // Nombres de pages dans le headerPage
        buff.flip();

        PageId headerPage = diskManager.AllocPage(); // On alloue une page disponible
        System.out.println("La header page est placé en "+headerPage);

        int position= (int) ((int) headerPage.getPageIdx()*diskManager.getDbConfig().getPagesize());


        String cheminFichier = diskManager.getDbConfig().getDbpath()+"/F"+headerPage.getFileIdx()+".bin"; // Chemin du fichier à lire
        File fichier = new File(cheminFichier);
        if(fichier.exists()) {
            try {
                RandomAccessFile raf = new RandomAccessFile(fichier, "rw"); // Ouverture du fichier
                raf.seek(position);  // Positionnement sur le premier octet de la page voulu

                System.out.println(Arrays.toString(buff.array()));
                buff.flip();
                raf.write(buff.array()); // Écriture du bytebuffer, en passant par un tableau, dans le fichier grace au raf
                raf.seek(position);
                System.out.println(raf.readInt());
                System.out.println(raf.readInt());
                raf.close();
            }catch(IOException e){
                e.printStackTrace();
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
