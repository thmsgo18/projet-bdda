package test;

import buffer.BufferManager;
import espaceDisque.*;
import relationnel.ColInfo;
import relationnel.Record;
import relationnel.Relation;
import requete.Condition;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static test.RelationTest.ajouteHeaderPage;

public class PageOrientedJoinOperatorTest {
    public static void main (String[] args){
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

        Relation relation = new Relation("Etudiant", 3,headerPageId, diskManager, bufferManager, listeColonnesInfo);
        char lettre1='E';

        int i=0,nb1=20;
        while (i<2) {
            //System.out.println("Boucle InsertRecordTest : "+i);
            try {
                relation.addDataPage();
            }catch(EOFException e){
                e.printStackTrace();
                System.out.println("ERRRRRRREEEEEEEEEUUUUUUUUUUUUUUUR");
                System.out.println("Boucle InsertRecordTest erreur : "+i);
            }
            i++;
            //System.out.println("Boucle Fin InsertRecordTest : "+i);

        }


        ArrayList<Object> b2 = new ArrayList<>(Arrays.asList("Gnaho", "roh", 1));
        relationnel.Record recordb1 = new Record(b2);
        relation.InsertRecord(recordb1);

        ArrayList<Object> b3 = new ArrayList<>(Arrays.asList("Soto", "roh", 2));
        relationnel.Record recordb2 = new Record(b3);
        relation.InsertRecord(recordb2);
        ArrayList<Object> b4 = new ArrayList<>(Arrays.asList("Soror", "roh", 3));
        relationnel.Record recordb3 = new Record(b4);
        relation.InsertRecord(recordb3);
        ArrayList<Object> b5 = new ArrayList<>(Arrays.asList("ameni", "roh", 4));
        relationnel.Record recordb4 = new Record(b5);
        relation.InsertRecord(recordb4);

        System.out.println("Relation 1 : "+relation.GetAllRecords());


        ColInfo cI4 = new ColInfo("Nom", "VARCHAR", 12);
        ColInfo cI5 = new ColInfo("Prenom", "VARCHAR", 6);
        ColInfo cI6 = new ColInfo("Age", "INT", 4);
        PageId headerPageId2 =ajouteHeaderPage(diskManager,bufferManager);
        List<ColInfo> listeColonnesInfo2= new ArrayList<>();
        listeColonnesInfo2.add(cI4);
        listeColonnesInfo2.add(cI5);
        listeColonnesInfo2.add(cI6);

        Relation relation2 = new Relation("Professeur", 3,headerPageId2, diskManager, bufferManager, listeColonnesInfo2);

        int y=0,nb2=20;
        char lettre2='A';

        while (y<2) {
            //System.out.println("Boucle InsertRecordTest : "+i);
            try {
                relation2.addDataPage();
            }catch(EOFException e){
                e.printStackTrace();
                System.out.println("ERRRRRRREEEEEEEEEUUUUUUUUUUUUUUUR");
                System.out.println("Boucle InsertRecordTest erreur : "+y);
            }

            y++;
           // System.out.println("Boucle Fin InsertRecordTest : "+y);

        }


        ArrayList<Object> a2 = new ArrayList<>(Arrays.asList("Traore","Ali", 1));
        relationnel.Record record = new Record(a2);
        relation2.InsertRecord(record);
        ArrayList<Object> a3 = new ArrayList<>(Arrays.asList("Telly","Ali", 2));
        relationnel.Record record2 = new Record(a3);
        relation2.InsertRecord(record2);
        ArrayList<Object> a4 = new ArrayList<>(Arrays.asList("Dicko","Ali", 3));
        relationnel.Record record3 = new Record(a4);
        relation2.InsertRecord(record3);
        ArrayList<Object> a5 = new ArrayList<>(Arrays.asList("Diay","Ali", 4));
        relationnel.Record record4 = new Record(a5);
        relation2.InsertRecord(record4);
        ArrayList<Object> a6 = new ArrayList<>(Arrays.asList("Badra","Ali", 5));
        relationnel.Record record5 = new Record(a6);
        relation2.InsertRecord(record5);


        System.out.println("Relation 2 : "+relation2.GetAllRecords());

        System.out.println("TEST BUFFER : "+ bufferManager.getBufferMap());

        List<Condition> conditions = new ArrayList<>();
    conditions.add( Condition.ajouteCondition(relation,"e",relation2,"p","e.Age=p.Age"));

        PageOrientedJoinOperator pageOrientedJoinOperator = new PageOrientedJoinOperator(diskManager,bufferManager,relation,relation2,conditions);
        int compteur=0;
        Record recordTest = new Record();
        while(recordTest!=null){
            recordTest =pageOrientedJoinOperator.GetNextRecord();
            System.out.println("Compteur : "+compteur+" "+recordTest);
            compteur++;
        }

    }

}
