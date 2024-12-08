

public class SelectOperator implements IRecordIterator<Record> {

    private IRecordIterator<Record> operateurFils;
    //private Condition condition; le temps que Ali finit la classe condition
    private boolean condition;

    public SelectOperator(IRecordIterator<Record> operateurFils, Boolean condition) {
        this.operateurFils = operateurFils;
        this.condition = condition;

    }

    //Vérifie si le record venant de l'operateur fils remplit les conditions "WHERE" pour le return

    public SelectOperator(IRecordIterator<Record> operateurFils) {
        this.operateurFils = operateurFils;
    }

    public Record GetNextRecord(){
        Record record = new Record();

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
