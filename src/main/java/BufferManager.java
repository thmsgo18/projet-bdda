import java.nio.ByteBuffer;

public class BufferManager {
    private DBConfig config;
    private DiskManager diskManager;
    public BufferManager(DBConfig config, DiskManager diskManager) {
        this.config = config;
        this.diskManager = diskManager;
    }
    public ByteBuffer GetPage(PageId pageId){
        return null;
    }

    public void FreePage(PageId pageId, boolean valDirty) { // ou on peut utilisrer un int pour valDirty

    }

    public void SetCurrentReplacementPolicy (String policy){

    }

    public void FlushBuffer(){

    }

}
