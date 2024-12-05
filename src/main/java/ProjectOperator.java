import jdk.incubator.vector.VectorOperators;

public class ProjectOperator implements IRecordIterator<Record> {

    private IRecordIterator<Record> operateurFils;

    public Record GetNextRecord(){
        return operateurFils.GetNextRecord();
    }

    public void Close(){
        operateurFils.Close();
    }

    public void Reset(){
        operateurFils.Reset();
    }
}
