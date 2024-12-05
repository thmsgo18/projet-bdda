import java.util.List;

public class RelationScanner implements IRecordIterator<Record> {

    private List<Record> allRecords;
    private int currentRecord;

    public RelationScanner(Relation relation) {
        this.allRecords = relation.GetAllRecords();
        this.currentRecord = 0;
    }

    public Record GetNextRecord() {
        return allRecords.get(this.currentRecord++);
    }

    public void Reset(){
        this.currentRecord = 0;
    }

    public void Close() {
    }



}
