package espaceDisque;

import buffer.BufferManager;
import inter.IRecordIterator;
import relationnel.Record;
import relationnel.Relation;
import requete.Condition;

import javax.security.sasl.RealmCallback;
import java.util.ArrayList;
import java.util.List;

public class PageOrientedJoinOperator implements IRecordIterator<Record> {
    private Relation table1;
    private Relation table2;
    private BufferManager bufferManager;
    private List<Condition> conditions;
    private PageDirectoryIterator iterateurHeaderPageTable1;
    private DataPageHoldRecordIterator iterateurDataPageTable1;
    private PageDirectoryIterator iterateurHeaderPageTable2;
    private DataPageHoldRecordIterator iterateurDataPageTable2;
    private DiskManager diskManager;
    private PageId dataPageTable1;
    private PageId dataPageTable2;
    private Record recordTable1;
    private Record recordTable2;

    public PageOrientedJoinOperator(DiskManager diskManager, BufferManager bufferManager, Relation table1, Relation table2, List<Condition> conditions) {
        this.diskManager = diskManager;
        this.table1 = table1;
        this.table2 = table2;
        this.bufferManager = bufferManager;
        this.conditions = conditions;

        this.iterateurHeaderPageTable1 = new PageDirectoryIterator(bufferManager, table1.getHeaderPageId());
        this.dataPageTable1  = iterateurHeaderPageTable1.GetNextDataPageId();
        //iterateurHeaderPageTable1.Reset();
        this.iterateurHeaderPageTable2 = new PageDirectoryIterator(bufferManager, table2.getHeaderPageId());
        this.dataPageTable2  = iterateurHeaderPageTable2.GetNextDataPageId();
    //System.out.println("PAGE ORIENTED JOIN OPERATOR : TEST BUFFER : "+bufferManager.getBufferMap());
        this.iterateurDataPageTable1 = new DataPageHoldRecordIterator(diskManager,bufferManager,table1,dataPageTable1); // prend directement la premiere page de donnés
        this.iterateurDataPageTable2 = new DataPageHoldRecordIterator(diskManager,bufferManager,table2,dataPageTable2); // prend directement la premiere page de donnés
        this.recordTable1 = iterateurDataPageTable1.GetNextRecord();
        this.recordTable2 = iterateurDataPageTable2.GetNextRecord();
        //System.out.println("PAGE ORIENTED JOIN OPERATOR : TEST AFFICHE : record1"+recordTable1+" record2"+recordTable2);



        //iterateurDataPageTable1.Reset();
        //iterateurDataPageTable2.Reset();

        //iterateurHeaderPageTable2.Reset();
        //iterateurDataPageTable2.Close();
        //System.out.println("TEST :"+recordTable1);


    }

    @Override
    public Record GetNextRecord() {
        //System.out.println(" PAGE ORIENTED JOIN OPERATOR : BET NEXT RECORD : INFO : Record1 : "+recordTable1+ "Record2 : "+recordTable2);
        int  i=0;
        while(dataPageTable1!=null){
            while(dataPageTable2!=null){
                //System.out.println(" PAGE ORIENTED JOIN OPERATOR :GET NEXT RECORD : RECORD table1 : "+recordTable1+" RECORD table2 : "+recordTable2);
                while(recordTable1!=null){
                    //System.out.println("PAGE ORIENTED JOIN OPERATOR :GET NEXT RECORD : RECORD table1 : "+recordTable1+" RECORD table2 : "+recordTable2);
                    while(recordTable2!=null){
                        //System.out.println("RECORD table1 : "+recordTable1+"Record2 : "+recordTable2);
                        // on teste la condition et on retourne la fusion des records si c'est bon
                        // si conditions bonne
                        if (ConditionsRespecter(recordTable1,recordTable2)) {
                            Record recordFusion = new Record();
                            recordFusion.addTuple(recordTable1.getTuple());
                            recordFusion.addTuple(recordTable2.getTuple());
                            recordTable2 = iterateurDataPageTable2.GetNextRecord();
                            //System.out.println("RECORD FUSION : "+recordFusion);
                            return recordFusion;
                        }
                        // si condition fausse : on itere au prochain record de la table 2
                        recordTable2 = iterateurDataPageTable2.GetNextRecord();
                    }
                    //System.out.println("PAGE ORIENTED JOIN OPERATOR : Record table 2 null, on passe au prochain de record de table 1");


                    iterateurDataPageTable2.Reset(); // On reset l'iterateur à TS1
                    recordTable2 = iterateurDataPageTable2.GetNextRecord(); // On se met au premier record d'un page de données de S
                    recordTable1 = iterateurDataPageTable1.GetNextRecord(); // On avance d'un record dans une page de donnée de R
                }
                // le record d'une page de données de la table 1 vaut null
                //System.out.println("PAGE ORIENTED JOIN OPERATOR : Tous les records de la page de donnée de table 1 on ete pris, on passe a la prochaine page de donnee de la table 2");

                // Changement pour la table 1
                iterateurDataPageTable1.Reset(); // On se remet au premier record d'un page de données de R
                recordTable1 = iterateurDataPageTable1.GetNextRecord();

                // Changement pour la table 2
                //On va changer de pages de données
                //System.out.println("Data page à retrouver "+dataPageTable2);
                iterateurDataPageTable2.Close(); // Vu qu'on va changer de page de données il faut liberer la page de données
                dataPageTable2 = iterateurHeaderPageTable2.GetNextDataPageId(); // itererer au prochain pageId de la headerPage de la table 2


                if(dataPageTable2!=null){
                    iterateurDataPageTable2 = new DataPageHoldRecordIterator(diskManager ,bufferManager,table2, dataPageTable2); // ON change de page de données
                    recordTable2 = iterateurDataPageTable2.GetNextRecord(); // On retourne le premier record de la page
                    //System.out.println("PAGE ORIENTED JOIN OPERATOR : fffff  "+ recordTable2); // ça devrait etre null mais la premiere fois ça a des valeurs
                }

            }
            //Plus de pages de données dans la header page de S dataPageTable2 = null
            //System.out.println("PAGE ORIENTED JOIN OPERATOR : Plus de pages de données dans la header page de la table 2dataPageTable2 = null");
            //System.out.println("PAGE ORIENTED JOIN OPERATOR : On passe à la prochaine page de données de la table 1");


            //Changement pour la table 1

            iterateurDataPageTable1.Close(); // Vu qu'on va changer de page de données il faut liberer la page de données
            dataPageTable1 = iterateurHeaderPageTable1.GetNextDataPageId(); // itererer au prochain pageId de la headerPage de la table 2

            if (dataPageTable1!=null){ // Si c'est null on va quitter la boucle juste après de toute façon
                iterateurDataPageTable1 = new DataPageHoldRecordIterator(diskManager,bufferManager,table1,dataPageTable1); // On  change de page de données
                recordTable1 = iterateurDataPageTable1.GetNextRecord();
            }


            // Changement pour la table 2
            iterateurHeaderPageTable2.Reset();
            dataPageTable2 = iterateurHeaderPageTable2.GetNextDataPageId();
            // iterateurDataPageTable2.Close(); // pas besoin de close cette iterateur vu qu'on que l'on le clause avant
            //System.out.println("tour "+ ++i);
            iterateurDataPageTable2 = new DataPageHoldRecordIterator(diskManager,bufferManager,table2,dataPageTable2);
            recordTable2 = iterateurDataPageTable2.GetNextRecord();

        }
        //System.out.println("Fin");
        // On a parcourut l'ensemble des records contenus dans l'ensemble des header page de table 11 avec l'ensemble ... de table 2
        // On libère les headers pages, on les utilises plus

        iterateurDataPageTable2.Close();


        return null;
    }

    @Override
    public void Close() {

    }

    @Override
    public void Reset() {

    }
    private boolean ConditionsRespecter(Record record1, Record record2) {
        boolean result = true;
        for( Condition c : conditions){
            if( !c.estRespecter(record1, record2)){
                result = false;
                break;
            }
        }
        return result;
    }
}
