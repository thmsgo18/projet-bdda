import java.nio.ByteBuffer;
import java.util.ArrayList;

public class RelationTest {

    public static void main(String[] args) {
        RelationTest test = new RelationTest();
        //test.writeRecordToBufferTest();
        test.readRecordFromBufferTest();
    }


    public void writeRecordToBufferTest() {

        System.out.println("\n******************** TEST WRITE RECORD TO BUFFER ************************\n");
        System.out.println("Pour l'exemple nous prendrons un record ayant tuple (20.353,10,'X',\"ALi\",\"Valentin\",\"Thomas\",\"Malik\") ");
        System.out.println("Cela nous permettra de tester l'ecriture des 4 types de données : int / float / char / string");
        System.out.println("Les \"VERIF\" que vous voyez ci dessous sont affiché directement via la fonction, il s'agit simplement d'affichage à but explicative, on pourra les supprimer par la suite.");
        System.out.println();
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
        System.out.println("\nOn a ci dessous le buffer contenant les valeurs provenant du record");
        for(int i=0;i<bb.limit();i++) {
            System.out.print(bb.get()+" ");
        }
        System.out.println();
        bb.flip();

        System.out.println("Il reste "+reponseEcriture+" octets disponible dans le buffer");
    }


    public void readRecordFromBufferTest() {
        System.out.println("\n******************** TEST READ RECORD TO BUFFER ************************\n");
        System.out.println("Considerons que nous voulons lire le buffer qui a pour buffer, le buffer écris arbitrairement dans le test Write ");
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
        relation.writeRecordToBuffer(record,bb,100);

        System.out.println("On a le buffer suivant :");
        for(int i=0;i<bb.limit();i++) {
            System.out.print(bb.get()+" ");
        }
        System.out.println("\n");
        bb.flip();

        Record record2 = new Record();
        System.out.println("Le tuple du record vaut avant l'appel de la fonction = "+record2.getTuple());

        System.out.println("On va donc enregistrer dans le tuple vide du record, les valeurs : (20.353,10,'X',\"ALi\",\"Valentin\",\"Thomas\",\"Malik\")" );

        int reponseLecture= relation.readFromBuffer(record2, bb, 100);

        System.out.println("Le tuple du record vaut après l'appel de la fonction = "+record2.getTuple());
        System.out.println(reponseLecture+" octets ont été lu du buffer"); // À préciser qu'on y inclus les bitTypes (Rappel : un bitType par element)
        // Avec les bitsType, on est à 61
        // Sans, on est à 61-7= 54
    }
}
