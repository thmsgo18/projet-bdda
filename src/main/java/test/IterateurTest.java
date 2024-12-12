package test;

import buffer.BufferManager;
import espaceDisque.DBConfig;
import espaceDisque.DiskManager;
import espaceDisque.PageId;
import relationnel.ColInfo;
import relationnel.Relation;
import relationnel.Record;
import espaceDisque.PageDirectoryIterator;
import espaceDisque.DataPageHoldRecordIterator;

import javax.swing.*;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

import static test.RelationTest.ajouteHeaderPage;

public class IterateurTest {

    public static void main(String[] args) throws EOFException {
        ColInfo cI1 = new ColInfo("Nom", "VARCHAR", 12);
        ColInfo cI2 = new ColInfo("Prenom", "CHAR", 6);
        ColInfo cI3 = new ColInfo("Age", "INT", 4);
        DBConfig config = DBConfig.LoadDBConfig("file-config.json");
        DiskManager diskManager = new DiskManager(config);
        BufferManager bufferManager = new BufferManager(config,diskManager);
        PageId headerPageId =ajouteHeaderPage(diskManager,bufferManager);
        List<ColInfo> listeColonnesInfo= new ArrayList<>();
        listeColonnesInfo.add(cI1);
        listeColonnesInfo.add(cI2);
        listeColonnesInfo.add(cI3);

        Relation relation = new Relation("Etudiant", 3,headerPageId, diskManager, bufferManager, listeColonnesInfo);
        int i=0;
        Record record = new Record();

        while (i<3){
            relation.addDataPage();
            List<Object> valeurs = new ArrayList<>();

            valeurs.add("Traore");
            valeurs.add("Ali");
            valeurs.add(20+i);
            i++;


            record.setTuple(valeurs);
            relation.InsertRecord(record); // normalement on va insérer à la page [0,1]
            System.out.println("Tour "+i);
            System.out.println(relation.GetAllRecords());

        }

        /*
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

         */

        PageDirectoryIterator pageDirectoryIterator = new PageDirectoryIterator(bufferManager,headerPageId);
        PageId pageData = pageDirectoryIterator.GetNextDataPageId();
        System.out.println("Page 1 : "+pageData);

        DataPageHoldRecordIterator dataPageHoldRecordIterator = new DataPageHoldRecordIterator(diskManager,bufferManager,relation,pageData);
        System.out.println("Record 1: "+dataPageHoldRecordIterator.GetNextRecord());
        System.out.println("Record 2: "+dataPageHoldRecordIterator.GetNextRecord());
        System.out.println("Record 3: "+dataPageHoldRecordIterator.GetNextRecord());

        System.out.println("Page 2 : "+pageDirectoryIterator.GetNextDataPageId());
        System.out.println("Page 3 : "+pageDirectoryIterator.GetNextDataPageId());
        System.out.println("Page 4 : "+pageDirectoryIterator.GetNextDataPageId());

    }
}
