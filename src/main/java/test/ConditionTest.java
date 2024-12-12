package test;

import buffer.BufferManager;
import espaceDisque.DBConfig;
import espaceDisque.DiskManager;
import espaceDisque.PageId;
import relationnel.ColInfo;
import relationnel.Relation;
import requete.Condition;
import relationnel.Record;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ConditionTest {

    public static void main (String[] args) {

        ColInfo cI1 = new ColInfo("Nom", "VARCHAR", 12);
        ColInfo cI2 = new ColInfo("Prenom", "VARCHAR", 6);
        ColInfo cI3 = new ColInfo("Age", "INT", 4);
        DBConfig config = DBConfig.LoadDBConfig("file-config.json");
        DiskManager diskManager = new DiskManager(config);
        BufferManager bufferManager = new BufferManager(config,diskManager);
        PageId headerPageId =ajouteHeaderPage(diskManager,bufferManager);
        List<ColInfo> listeColonnesInfo= new ArrayList<>();
        listeColonnesInfo.add(cI1);
        listeColonnesInfo.add(cI2);
        listeColonnesInfo.add(cI3);

        Relation table = new Relation("Etudiant", 3,headerPageId, diskManager, bufferManager, listeColonnesInfo);

        ConditionConstanteTest(table);





        ColInfo c2I1 = new ColInfo("Nom", "VARCHAR", 12);
        ColInfo c2I2 = new ColInfo("Prenom", "VARCHAR", 6);
        ColInfo c2I3 = new ColInfo("Age", "INT", 4);
        ColInfo c2I4 = new ColInfo("nombrePreferer", "INT", 4);


        PageId headerPageId2 =ajouteHeaderPage(diskManager,bufferManager);
        List<ColInfo> listeColonnesInfo2= new ArrayList<>();
        listeColonnesInfo2.add(c2I1);
        listeColonnesInfo2.add(c2I2);
        listeColonnesInfo2.add(c2I3);
        listeColonnesInfo2.add(c2I4);
        Relation table2 = new Relation("Etudiant2", 4,headerPageId2, diskManager, bufferManager, listeColonnesInfo2);


        ConditionDeuxValeursRecordTest(table2);


    }


    public static void ConditionDeuxValeursRecordTest( Relation table){
        System.out.println("*********** requete.Condition valRecord OP valRecord ************");

        System.out.println("*************** Condition1 ******************");
        Condition condition = Condition.ajouteCondition(table,"e","e.Age=e.nombrePreferer");

        System.out.println("*************** Condition2 ******************");
        Condition condition2 = Condition.ajouteCondition(table,"e","e.Age<=e.nombrePreferer");

        System.out.println("*************** Condition3 ******************");
        Condition condition3 = Condition.ajouteCondition(table,"e","e.Age<e.nombrePreferer");

        System.out.println("*************** Condition4 ******************");
        Condition condition4 = Condition.ajouteCondition(table,"e","e.Age<>e.nombrePreferer");

        System.out.println("*************** Condition5 ******************");
        Condition condition5 = Condition.ajouteCondition(table,"e","e.Age>=e.nombrePreferer");

        System.out.println("*************** Condition6 ******************");
        Condition condition6 = Condition.ajouteCondition(table,"e","e.nombrePreferer>e.Age");



        List<Condition> listeCondition = new ArrayList<>();
        listeCondition.add(condition);
        listeCondition.add(condition2);
        listeCondition.add(condition3);
        listeCondition.add(condition4);
        listeCondition.add(condition5);
        listeCondition.add(condition6);

        Record record = new Record();
        ArrayList<Object> tuple = new ArrayList<>();
        tuple.add("ali");
        tuple.add("jhon");
        tuple.add(15);
        tuple.add(20);

        record.setTuple(tuple);
        int i=1;
        for (Condition c : listeCondition) {
            System.out.println("\n*************** Est respecté "+i +"++++++++++++++++++++++++");
            System.out.println("booelen : " +c.estRespecter(record));
            i++;
        }
    }
    public static void ConditionConstanteTest(Relation table){
        System.out.println("*********** requete.Condition valRecord OP constnante ************");

        System.out.println("*************** Condition1 ******************");
        Condition condition = Condition.ajouteCondition(table,"e","e.Age=20");

        System.out.println("*************** Condition2 ******************");
        Condition condition2 = Condition.ajouteCondition(table,"e","e.Age<=20.7");

        System.out.println("*************** Condition3 ******************");
        Condition condition3 = Condition.ajouteCondition(table,"e","e.Age<20");

        System.out.println("*************** Condition4 ******************");
        Condition condition4 = Condition.ajouteCondition(table,"e","e.Age<>20");

        System.out.println("*************** Condition5 ******************");
        Condition condition5 = Condition.ajouteCondition(table,"e","e.Age>=20");

        System.out.println("*************** Condition6 ******************");
        Condition condition6 = Condition.ajouteCondition(table,"e","20>e.Age");

        System.out.println("*************** Condition7 ******************");
        Condition condition7 = Condition.ajouteCondition(table,"e","e.Nom=Ali");
        System.out.println("*************** Condition8 ******************");
        Condition condition8 = Condition.ajouteCondition(table,"e","e.Nom=ali");
        System.out.println("*************** Condition9 ******************");
        Condition condition9 = Condition.ajouteCondition(table,"e","e.Nom<>Ali");
        System.out.println("*************** Condition9 ******************");
        Condition condition10 = Condition.ajouteCondition(table,"e","e.Nom>=Ali");




        List<Condition> listeCondition = new ArrayList<>();
        listeCondition.add(condition);
        listeCondition.add(condition2);
        listeCondition.add(condition3);
        listeCondition.add(condition4);
        listeCondition.add(condition5);
        listeCondition.add(condition6);
        listeCondition.add(condition7);
        listeCondition.add(condition8);
        listeCondition.add(condition9);
        listeCondition.add(condition10);


        Record record = new Record();
        ArrayList<Object> tuple = new ArrayList<>();
        tuple.add("ali");
        tuple.add("jhon");
        tuple.add(10);

        record.setTuple(tuple);
        int i=1;
        for (Condition c : listeCondition) {
            System.out.println("\n*************** Est respecté "+i +"++++++++++++++++++++++++");
            System.out.println("booelen : " +c.estRespecter(record));
            i++;
        }
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

}
