import java.nio.ByteBuffer;

public class BufferManagerTests {

    public static void main(String[] args) {
        DBConfig config;
        config = DBConfig.LoadDBConfig("src/main/json/file-config.json");
        DiskManager dm = new DiskManager(config);
        BufferManager bm = new BufferManager(config,dm);
        TestGetPage(bm, config);
        /*
        System.out.println("***************Lecture du Buffer***************");
        for(int i=0;i<buffer.capacity();i++) {
            System.out.print(buffer.get()+" ");
        }
        System.out.println();

        buffer.flip();
        System.out.println("**************** Tentons maintenant d'acceder à cette page lorsqu'il y a deja un buffer la prenant*****************");
        buffer=bm.GetPage(p);
        System.out.println("***************Lecture du Buffer***************");
        for(int i=0;i<buffer.capacity();i++) {
            System.out.print(buffer.get()+" ");

        }*/

    }

    public static void TestGetPage(BufferManager bm, DBConfig config){
        ByteBuffer buffer1 = ByteBuffer.allocate(config.getBm_buffercount());
        ByteBuffer buffer2 = ByteBuffer.allocate(config.getBm_buffercount());
        ByteBuffer buffer3 = ByteBuffer.allocate(config.getBm_buffercount());
        ByteBuffer buffer4 = ByteBuffer.allocate(config.getBm_buffercount());
        ByteBuffer buffer5 = ByteBuffer.allocate(config.getBm_buffercount());
        System.out.println("************ TestGetPage ************");
        PageId p1 =new PageId(0,0);
        PageId p2 =new PageId(0,0);
        PageId p3 =new PageId(0,0);
        PageId p4 =new PageId(0,0);
        PageId p5 =new PageId(0,0);
        System.out.println("          * Test page charger dans le tableau de buffer => PageID("+p1.getPageIdx()+ ","+ p1.getFileIdx()+") :");
        buffer1 = bm.GetPage(p1);
        System.out.println("************ Lecture du buffer ************");
        for(int i=0;i<buffer1.capacity();i++) {
            System.out.print(buffer1.get()+" ");
        }
        System.out.println();
        buffer1.flip();
        System.out.println("          * Test page trouvée dans le buffer :");
        buffer1 = bm.GetPage(p1);
        System.out.println("************ Lecture du buffer ************");
        for(int i=0;i<buffer1.capacity();i++) {
            System.out.print(buffer1.get()+" ");
        }

        System.out.println();
        buffer1.flip();
        buffer2=bm.GetPage(p2);
        buffer3=bm.GetPage(p3);
        buffer4=bm.GetPage(p4);
        System.out.println("          * Test plus de frame dispo dans le buffer et aucune drames avec un pin count=0 :");
        buffer5=bm.GetPage(p5);
        for(int i=0;i<buffer5.capacity();i++) {
            System.out.print(buffer5.get()+" ");
        }
        System.out.println();
        buffer5.flip();

    }

    public void TestGetPageFind(){

    }

}
