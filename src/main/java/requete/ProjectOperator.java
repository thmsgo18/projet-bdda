package requete;

import inter.IRecordIterator;
import relationnel.Record;

public class ProjectOperator implements IRecordIterator<relationnel.Record> {

    private IRecordIterator<Record> operateurFils;

    public ProjectOperator(IRecordIterator<relationnel.Record> operateurFils) {
        this.operateurFils = operateurFils;
    }

    public relationnel.Record GetNextRecord(){
        return operateurFils.GetNextRecord();
    }

    public void Close(){
        operateurFils.Close();
    }

    public void Reset(){
        operateurFils.Reset();
    }
}
