import java.nio.ByteBuffer;
import java.util.ArrayList;

public class TestChelouAli {

    public static void main(String[] args) {
        Relation relation = new Relation("TestRelation",5);
        ByteBuffer bb = ByteBuffer.allocate(4000);
        ArrayList<Object> t1 = new ArrayList<>();
        t1.add(20.353f);    // un float
        t1.add(10);     // un int
        t1.add('X');    // un caractère
        t1.add("Ali");
        t1.add("Valentin");       // 4 strings
        t1.add("Thomas");
        t1.add("Malik");
        Record record = new Record(t1);
       int reponseEcriture = relation.writeRecordToBuffer(record,bb,100);

       System.out.println("reponse : "+reponseEcriture);
        for(int i=0;i<bb.limit();i++) {
            System.out.print(bb.get()+" ");
        }
        System.out.println();
        bb.flip();
        Record record2 = new Record();
        int reponseLecture= relation.readFromBuffer(record2, bb, 100);
        System.out.println(reponseLecture+" ont été lu du buffer"); // À préciser qu'on y inclus les bitTypes (Rappel : un bitType par element)
                                                                // Avec les bitsType, on est à 61
                                                                // Sans, on est à 61-7

    }
}
