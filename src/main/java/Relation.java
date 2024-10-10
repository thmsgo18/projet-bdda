import java.nio.ByteBuffer;

public class Relation {

    private String nomRelation;
    private int nbColonnes;
     // Ajouter comme attribut CallInfo.
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

    public int writeRecordtoBuffer(Record r, ByteBuffer buff,int pos) {


        return 0;
    }

    public int readFromBuffer(ByteBuffer buff, int pos) {


        return 0;
    }


}