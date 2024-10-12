import java.nio.ByteBuffer;

public class BufferManagerTests {

    public static void main(String[] args) {
        DBConfig config;
        config = DBConfig.LoadDBConfig("src/main/json/file-config.json");
        DiskManager dm = new DiskManager(config);
        BufferManager bm = new BufferManager(config,dm);
        //TestGetPage( bm, config);
        TestFlushBuffers( bm, config);

    }

    public static void TestGetPage(BufferManager bm, DBConfig config){
        ByteBuffer buffer1 = ByteBuffer.allocate(config.getBm_buffercount());
        ByteBuffer buffer2 = ByteBuffer.allocate(config.getBm_buffercount());
        ByteBuffer buffer3 = ByteBuffer.allocate(config.getBm_buffercount());
        ByteBuffer buffer4 = ByteBuffer.allocate(config.getBm_buffercount());
        ByteBuffer buffer5 = ByteBuffer.allocate(config.getBm_buffercount());
        System.out.println("************ TestGetPage ************");
        PageId p1 =new PageId(0,0);
        PageId p2 =new PageId(0,1);
        PageId p3 =new PageId(0,2);
        PageId p4 =new PageId(1,0);
        PageId p5 =new PageId(1,1);
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
        System.out.println("          * Test plus de frame dispo dans le buffer et aucune frame avec un pin count=0 :");
        buffer5=bm.GetPage(p5);
        System.out.println("************ Lecture du buffer (ici il doit égale à null) ************");
        System.out.println("La valeur contenue dans le buffer est : "+buffer5);
        System.out.println("************ TestFreePage (dirty = false) ************");
        bm.FreePage(p4,false);
        System.out.println("          * Test plus de frame dispo dans le buffer une frame avec un pin count=0 et pas de dirty:");
        buffer5=bm.GetPage(p5);
        System.out.println("************ Lecture du buffer ************");
        for(int i=0;i<buffer5.capacity();i++) {
            System.out.print(buffer5.get()+" ");
        }
        System.out.println();
        buffer5.flip();
        System.out.println("************ TestFreePage (dirty = true) ************");
        bm.FreePage(p5,true);
        System.out.println("          * Test plus de frame dispo dans le buffer une frame avec un pin count=0 et avec un dirty:");
        buffer4=bm.GetPage(p4);
        System.out.println("************ Lecture du buffer ************");
        for(int i=0;i<buffer4.capacity();i++) {
            System.out.print(buffer4.get()+" ");
        }
        System.out.println();
        buffer4.flip();
    }

    public static void TestFlushBuffers(BufferManager bm, DBConfig config){
        ByteBuffer buffer1 = ByteBuffer.allocate(config.getBm_buffercount());
        ByteBuffer buffer2 = ByteBuffer.allocate(config.getBm_buffercount());
        ByteBuffer buffer3 = ByteBuffer.allocate(config.getBm_buffercount());
        ByteBuffer buffer4 = ByteBuffer.allocate(config.getBm_buffercount());
        ByteBuffer buffer5 = ByteBuffer.allocate(config.getBm_buffercount());
        PageId p1 =new PageId(0,0);
        PageId p2 =new PageId(0,1);
        PageId p3 =new PageId(0,2);
        PageId p4 =new PageId(1,0);
        PageId p5 =new PageId(1,1);
        buffer1 = bm.GetPage(p1);
        System.out.println("************ Lecture du buffer1 ************");
        for(int i=0;i<buffer1.capacity();i++) {
            System.out.print(buffer1.get()+" ");
        }
        System.out.println();
        buffer1.flip();
        buffer2=bm.GetPage(p2);
        System.out.println("************ Lecture du buffer2 ************");
        for(int i=0;i<buffer2.capacity();i++) {
            System.out.print(buffer2.get()+" ");
        }
        System.out.println();
        buffer2.flip();
        buffer3=bm.GetPage(p3);
        System.out.println("************ Lecture du buffer3 ************");
        for(int i=0;i<buffer3.capacity();i++) {
            System.out.print(buffer3.get()+" ");
        }
        System.out.println();
        buffer3.flip();
        buffer4=bm.GetPage(p4);
        System.out.println("************ Lecture du buffer4 ************");
        for(int i=0;i<buffer4.capacity();i++) {
            System.out.print(buffer4.get()+" ");
        }
        System.out.println();
        buffer4.flip();
        bm.FlushBuffers();
        buffer5=bm.GetPage(p5);
        System.out.println("************ Lecture du buffer5 ************");
        for(int i=0;i<buffer5.capacity();i++) {
            System.out.print(buffer5.get()+" ");
        }
        System.out.println();
        buffer5.flip();

    }

}
