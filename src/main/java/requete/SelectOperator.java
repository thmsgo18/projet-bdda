package requete;

import inter.IRecordIterator;
import relationnel.Record;

public class SelectOperator implements IRecordIterator<relationnel.Record> {

    private IRecordIterator<relationnel.Record> operateurFils;
    //private requete.Condition condition; le temps que Ali finit la classe condition
    private boolean condition;

    public SelectOperator(IRecordIterator<Record> operateurFils, Boolean condition) {
        this.operateurFils = operateurFils;
        this.condition = condition;

    }

    //Vérifie si le record venant de l'operateur fils remplit les conditions "WHERE" pour le return

    public SelectOperator(IRecordIterator<relationnel.Record> operateurFils) {
        this.operateurFils = operateurFils;
    }

    public relationnel.Record GetNextRecord(){
        relationnel.Record record = new relationnel.Record();

        while((record = operateurFils.GetNextRecord()) != null){
            if(condition){
                return record;
            }
        }
        return null;
    }


    public void Close(){
        operateurFils.Close();
    }


    public void Reset(){
        operateurFils.Reset();
    }
}
