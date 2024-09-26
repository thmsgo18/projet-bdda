import java.nio.ByteBuffer;

public class DiskManager {

    private DBConfig dbConfig;

    public DiskManager(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public PageId AllocPage(){
        return  new PageId(0,0); // return par défaut
    }

    public void ReadPage(PageId pageId, ByteBuffer buff){

    }

    public void WritePage(PageId pageId,ByteBuffer buff){

    }

    public void DeallocPage(PageId pageId){

    }

    public void SaveState(){

    }

    public void LoadState(){

    }

    public DBConfig getDbConfig() {
        return dbConfig;
    }
    public void setDbConfig(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
}
