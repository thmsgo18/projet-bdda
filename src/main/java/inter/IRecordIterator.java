package inter;

public interface IRecordIterator<T> {

    T GetNextRecord();

    void Close();

    void Reset();

}
