package requete;

import inter.IRecordIterator;
import relationnel.Record;
import relationnel.Relation;

import java.util.ArrayList;
import java.util.List;

public class RelationScanner implements IRecordIterator<Record> {

    private List<Record> records;
    private List<Condition> conditions;
    private int indiceRecordCourant;

    public RelationScanner(Relation table,List<Condition> conditions) {
        this.records = table.GetAllRecords();
        this.indiceRecordCourant = 0;
        this.conditions=conditions;
    }

    public relationnel.Record GetNextRecord() {
       return records.get(indiceRecordCourant++);
    }

    public void Reset(){
        indiceRecordCourant=0;
    }

    public void Close() {
        // je sais pas encore à quoi ça sert

    }

    public Record getFliteredRecord() {
        Record recordCourant;
        boolean conditionsRespecter;
        while(indiceRecordCourant<records.size()){ // On parcourt l'ensemble des records du GetAllRecord
            conditionsRespecter = true;
            recordCourant =GetNextRecord();

            for(Condition condition : conditions){  // On vérifie que toutes les conditions sont respectés (dans le cas où l'on a pas de conditions, on passera juste cette boucle car la liste de conditions sera vide)
                if (!condition.estRespecter(recordCourant)){
                    conditionsRespecter = false;
                    break;
                }
            }

            if(conditionsRespecter){
                return recordCourant;
            }
        }
        return null;
    }



}
