package espaceDisque;

import buffer.BufferManager;
import inter.IRecordIterator;
import relationnel.Relation;
import relationnel.Record;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DataPageHoldRecordIterator implements IRecordIterator<Record> {
    private PageId pageDonnee;
    private DiskManager diskManager;
    private BufferManager bufferManager;
    private ByteBuffer bufferData;
    private int taillePage;
    private int nombreRecordVu;
    private Relation table;
    public DataPageHoldRecordIterator(DiskManager diskManager, BufferManager bufferManager, Relation table, PageId pageDonnee) {
        this.diskManager = diskManager;
        this.bufferManager = bufferManager;
        this.pageDonnee = pageDonnee;
        this.bufferData= bufferManager.GetPage(pageDonnee);
        this.taillePage = (int) diskManager.getDbConfig().getPagesize();
        this.table = table;
        this.nombreRecordVu= 0;

    }

    @Override
    public Record GetNextRecord() {
        Record record = new Record();
        int positionRecord;
        if (pageDonnee==null) return null;
        int nombreRecords = bufferData.getInt(taillePage-8);

        if(nombreRecordVu<nombreRecords){
            bufferData.position(taillePage-16 -nombreRecordVu*8);
            positionRecord = bufferData.getInt();
            table.readFromBuffer(record,bufferData,positionRecord);
            nombreRecordVu++;
            bufferData.limit(bufferData.capacity());
            return record;


        }
        bufferData.limit(bufferData.capacity());
        return null;

    }

    @Override
    public void Close() {
        boolean dirtyDataPage= bufferManager.getDirtyPage(pageDonnee); // on vérifie l'etat précédent du dirty pour le lui redonner (on le mets pas à true car on  ne modifie pas les elements de la page )
        bufferManager.FreePage(pageDonnee,dirtyDataPage);
    }

    @Override
    public void Reset() {
        nombreRecordVu= 0;
    }
}
