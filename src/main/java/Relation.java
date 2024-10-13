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
        for(Object element : r.getTuple()){
            if(element instanceof String){
                buff.put((byte) 0);
               System.out.println("VERIF : L'element "+element+" est une chaine de caractère ");
               for( char c : element.toString().toCharArray() ) {
                   buff.putChar(c);
               }
               currentPosition = buff.position(); // on sauvegarde la position actuellle du buffer
               buff.position(offsetDirectory); // on se met à la position du ième offset
               buff.putInt(currentPosition);
               offsetDirectory += 4; // on se place 4 octets plus loin pour l'offset (correspond à un int)
               buff.position(currentPosition); // on se remet à la position courante pour ecrire les elements


            }
            else if (element instanceof Character){
                buff.put((byte) 1);
                System.out.println("VERIF : l'élément est un char ");
                buff.putChar((Character)element);
                currentPosition = buff.position(); // on sauvegarde la position actuellle du buffer
                buff.position(offsetDirectory); // on se met à la position du ième offset
                buff.putInt(currentPosition);
                offsetDirectory += 4; // on se place 4 octets plus loin pour l'offset (correspond à un int)
                buff.position(currentPosition); // on se remet à la position courante pour ecrire les elements
            }

            else if( element instanceof Integer){
                buff.put((byte) 2);
                System.out.println("VERIF : L'element "+element+" est un float ");
                buff.putInt((Integer)element);
                currentPosition = buff.position(); // on sauvegarde la position actuellle du buffer
                buff.position(offsetDirectory); // on se met à la position du ième offset
                buff.putInt(currentPosition);
                offsetDirectory += 4; // on se place 4 octets plus loin pour l'offset (correspond à un int)
                buff.position(currentPosition); // on se remet à la position courante pour ecrire les elements
            }
            else if (element instanceof Float){
                buff.put((byte) 3);
                System.out.println("VERIF : L'element "+element+" est un float ");
                buff.putFloat((Float)element);
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

    public int readFromBuffer(Record record, ByteBuffer buff, int pos) {
        int indiceOffset=0;
        buff.position(indiceOffset);
        int OffsetDebut= buff.getInt(); //on prends la 1ère valeur du offset (ce sra pour nous commencer)
        int OffsetFin=buff.getInt(); // on prends la 2ème valeur du offset (ce sra pour nous stopper)
        System.out.println("VERIF : INIT:     Offset Deb : "+OffsetDebut+" Offset Fin : "+OffsetFin);

        int bitType; // le bitType sera l'entier qui nous indiquera le type de l'element que nous voulons enregistrer dans le record
        // bitType = 0, (String) bitType = 1 (Character), bitTYpe = 2 (Integer), bitType = 3 (Float)
        int currentPosition=pos;
        buff.position(currentPosition); // On se met à l'index du bitType du premier element du buffer à enregistrer

        while (buff.hasRemaining()){ // On boucle tant que le buffer a encore des elements
            bitType = buff.get(); // on récupère le type de l'element à enregistrer

            if(bitType==(byte)0){ // l'element à ajouter est un string
                StringBuilder sb= new StringBuilder();
                for(int i =0;i<(OffsetFin-OffsetDebut)/2; i++){ //On boucle pour rassembler tout les caractères composant le String
                    sb.append(buff.getChar());
                    System.out.println("VERIF : boucle formation de la chaine de caractère = "+sb.toString());
                }
                record.ajouteValeurTuple(sb.toString()); // on ajoute le String complet

            }else if(bitType==(byte)1){ // l'element à ajouter est un char
                record.ajouteValeurTuple(buff.getChar());

            }else if(bitType==(byte)2){ // l'element à ajouter est un int
                record.ajouteValeurTuple(buff.getInt());

            }else if(bitType==(byte)3){ // l'element à ajouter est un float
                record.ajouteValeurTuple(buff.getFloat());

            }
            else{
                System.out.println("VERIF : Problemme chelouu, flemme de l'etudier pour l'instant, il arrivera surement jamais bit = "+bitType);
            }
            currentPosition = buff.position();
            indiceOffset+=4;
            buff.position(indiceOffset);
            OffsetDebut= buff.getInt();
            OffsetFin=buff.getInt();
            buff.position(currentPosition);

        }
        int reponse = buff.position()-pos;
        buff.flip();
        return reponse;
    }

    public List<ColInfo> getColonnes() {
        return colonnes;
    }


}