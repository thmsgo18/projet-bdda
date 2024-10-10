import java.nio.ByteBuffer;
import java.util.List;

public class Relation {

    private String nomRelation;
    private int nbColonnes;
    private List<ColInfo> colonnes;
    public Relation(String nomRelation, int nbColonnes) {
        this.nomRelation = nomRelation;
        this.nbColonnes = nbColonnes;
    }

    public String getNomRelation() {
        return nomRelation;
    }

    public int getNbColonnes() {
        return nbColonnes;
    }

    public int writeRecordToBuffer(Record r, ByteBuffer buff,int pos) {
        int offsetDirectory = 0;
        int currentPosition = pos;
        buff.position(offsetDirectory);
        buff.putInt(currentPosition); // On place la position du 1er element dans la premier emplacement du buffer
        offsetDirectory += 4; // on se place 4 octets plus loin pour l'offset (correspond à un int)
        buff.position(pos); // on se met à la position courante pour écrire
        for(Object element : r.tuple){
           if(element instanceof String){
               System.out.println("L'element "+element+" est une chaine de caractère ");
               for( char c : element.toString().toCharArray() ) {
                   buff.putChar(c);
               }
               currentPosition = buff.position(); // on sauvegarde la position actuellle du buffer
               buff.position(offsetDirectory); // on se met à la position du ième offset
               buff.putInt(currentPosition);
               offsetDirectory += 4; // on se place 4 octets plus loin pour l'offset (correspond à un int)
               buff.position(currentPosition); // on se remet à la position courante pour ecrire les elements


           }else if( element instanceof Integer){
               System.out.println("L'element "+element+" est un float ");
               buff.putInt((Integer)element);
               currentPosition = buff.position(); // on sauvegarde la position actuellle du buffer
               buff.position(offsetDirectory); // on se met à la position du ième offset
               buff.putInt(currentPosition);
               offsetDirectory += 4; // on se place 4 octets plus loin pour l'offset (correspond à un int)
               buff.position(currentPosition); // on se remet à la position courante pour ecrire les elements
           }
           else if (element instanceof Float){
               System.out.println("L'element "+element+" est un float ");
               buff.putFloat((Float)element);
               currentPosition = buff.position(); // on sauvegarde la position actuellle du buffer
               buff.position(offsetDirectory); // on se met à la position du ième offset
               buff.putInt(currentPosition);
               offsetDirectory += 4; // on se place 4 octets plus loin pour l'offset (correspond à un int)
               buff.position(currentPosition); // on se remet à la position courante pour ecrire les elements

           }
           else if (element instanceof Character){
               System.out.println("Problème : l'élément est un char ");
                 buff.putChar((Character)element);
               currentPosition = buff.position(); // on sauvegarde la position actuellle du buffer
               buff.position(offsetDirectory); // on se met à la position du ième offset
               buff.putInt(currentPosition);
               offsetDirectory += 4; // on se place 4 octets plus loin pour l'offset (correspond à un int)
               buff.position(currentPosition); // on se remet à la position courante pour ecrire les elements
           }


        }
        currentPosition = buff.position();
        buff.flip();
        return buff.capacity()-currentPosition;



    }

    public int readFromBuffer(ByteBuffer buff, int pos) {


        return 0;
    }

    public List<ColInfo> getColonnes() {
        return colonnes;
    }


}