import java.nio.ByteBuffer;

public class BufferManagerTests {
    public static void main(String[] args) {
        DBConfig config;
        config = DBConfig.LoadDBConfig("src/main/json/file-config.json");
        DiskManager dm = new DiskManager(config);
        BufferManager bm = new BufferManager(config,dm);
        ByteBuffer buffer = ByteBuffer.allocate(config.getBm_buffercount());
        PageId p =new PageId(0,0);
        buffer=bm.GetPage(p);
        System.out.println("***************Lecture du Buffer***************");
        for(int i=0;i<buffer.capacity();i++) {
            System.out.print(buffer.get()+" ");

        }
        buffer.flip();
        System.out.println("**************** Tentons maintenant d'acceder à cette page lorsqu'il y a deja un buffer le prenant*****************");
        buffer=bm.GetPage(p);
        System.out.println("***************Lecture du Buffer***************");
        for(int i=0;i<buffer.capacity();i++) {
            System.out.print(buffer.get()+" ");

        }
        buffer.flip();







    }
}
