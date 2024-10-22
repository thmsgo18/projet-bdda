import java.nio.ByteBuffer;
import java.util.List;

public class Relation {

    private String nomRelation;
    private int nbColonnes;
    private List<ColInfo> colonnes;
    private int tailleColonneMax;
    private boolean varchar;

    public Relation(String nomRelation, int nbColonnes) {
        this.nomRelation = nomRelation;
        this.nbColonnes = nbColonnes;
    }
    public Relation(String nomRelation, int nbColonnes,List<ColInfo> colonnes) {
        this.nomRelation = nomRelation;
        this.nbColonnes = nbColonnes;
        this.colonnes = colonnes;
        tailleColonneMax = tailleColonneMax();
        this.varchar=possedeUnVarchar();
    }


    public int writeRecordToBuffer(Record record, ByteBuffer buff,int pos) {
        return (varchar) ? writeVariable(record, buff, pos): writeFixe(record, buff, pos);
    }

    public int readFromBuffer(Record record, ByteBuffer buff, int pos) {
        return (varchar)? readBufferVariable(record,buff,pos): readBufferFixe(record,buff,pos);
    }

    public int writeFixe(Record r, ByteBuffer buff, int pos){
        int save;
        buff.position(pos);
        for( Object element : r.getTuple()){
            save = buff.position();
            if ( element instanceof String){
                System.out.println("VERIF : L'element "+element+" est une chaine de caractère ");
                for( char c : element.toString().toCharArray() ) {
                    buff.putChar(c);
                }
            }else if( element instanceof Integer){
                System.out.println("VERIF : L'element "+element+" est un int ");
                buff.putInt((Integer)element);
            }
            else if( element instanceof Float){
                buff.putFloat((Float)element);
                System.out.println("VERIF : L'element "+element+" est un float ");

            }else if (element instanceof Character){
                System.out.println("VERIF : l'élément est un char ");
                buff.putChar((Character)element);
            }
            buff.position(save+tailleColonneMax);
        }
        int reponse= buff.position()-pos;
        buff.flip();
        return reponse;
    }

    public int writeVariable(Record r, ByteBuffer buff, int pos){
        int offsetDirectory = pos;
        int currentPos = pos + (getNbColonnes()+1)* 4; // faudra prendre en compte le cas où pos=0 peut etre pas
        for(Object element : r.getTuple()){
            buff.position(offsetDirectory);
            buff.putInt(currentPos);
            buff.position(currentPos);
            System.out.println("offsetDirectory : "+offsetDirectory+"currentPos : "+currentPos);
            if(element instanceof String){
                System.out.println("VERIF : L'element "+element+" est une chaine de caractère ");
                for( char c : element.toString().toCharArray() ) {
                    buff.putChar(c);
                }
            }
            else if (element instanceof Character){
                System.out.println("VERIF : l'élément est un char ");
                buff.putChar((Character)element);

            }
            else if( element instanceof Integer){
                System.out.println("VERIF : L'element "+element+" est un int ");
                buff.putInt((Integer)element);
            }
            else if (element instanceof Float){
                System.out.println("VERIF : L'element "+element+" est un float ");
                buff.putFloat((Float)element);
            }
            currentPos = buff.position();
            offsetDirectory+=4;
        }

        buff.position(offsetDirectory);
        buff.putInt(currentPos);
        buff.position(currentPos);

        int reponse= buff.position()-pos;
        buff.flip();
        return reponse;
    }



    private int readBufferFixe(Record record, ByteBuffer buff, int pos){
        String type;
        int octetLus =0;
        int octetMaxALire = getNbColonnes()*tailleColonneMax;
        int i=0;
        int anciennePosition;


        buff.position(pos);
        while ((i<nbColonnes)&& ( buff.hasRemaining() ) && ( octetLus<octetMaxALire ) ){ // On boucle tant que le buffer a encore des elements
            type =colonnes.get(i).getTypeColonne();
            anciennePosition= buff.position();
            if( (type.equals("CHAR") ) ||  ( type.equals("char") )  ){
                StringBuilder sb= new StringBuilder();
                int tailleColonne= colonnes.get(i).getTailleColonne();
                for(int c=0;c<tailleColonne/2;c++){ // on divise par 2 parce qu'un char vaut 2 octets
                    char caractere= buff.getChar();
                    if((caractere!='\0') ) {
                        sb.append(caractere);
                        System.out.println("VERIF : boucle formation de la chaine de caractère = " + sb.toString());}
                }
                buff.position(anciennePosition+tailleColonneMax);
                octetLus+=tailleColonneMax;
                System.out.println(sb.length()+" taille sb");
                System.out.println("Après "+sb.toString()+", on est à l'octet "+buff.position());


                record.ajouteValeurTuple(sb.toString()); // on ajoute le String complet
            }
            else if( type.equals("INT") || type.equals("INTEGER") ){
                record.ajouteValeurTuple(buff.getInt());
            }
            else if( type.equals("REAL") ){
                record.ajouteValeurTuple(buff.getFloat());
                buff.position(anciennePosition+tailleColonneMax);
            }
            buff.position(anciennePosition+tailleColonneMax);
            i++;
            System.out.println(buff.position()+"   "+ pos);
            octetLus=buff.position()-pos;
            System.out.println(" tuple :"+record);
        }
        buff.flip();

        return octetLus;
    }



    private int readBufferVariable(Record record, ByteBuffer buff, int pos) {
        String type;
        int octetLus = 0;
        int octetMaxALire = getNbColonnes() * tailleColonneMax;
        int i = 0;
        buff.position(pos);
        int offsetPos = buff.position();
        int currentPos;
        while ((i<nbColonnes)&& ( buff.hasRemaining() ) && ( octetLus<octetMaxALire ) ){ // On boucle tant que le buffer a encore des elements
            currentPos = buff.getInt(offsetPos);

            buff.position(currentPos);

            type =colonnes.get(i).getTypeColonne();
            System.out.println(type);

            // Pour les strings

            if( (type.equals("CHAR") ) || ( type.equals("VARCHAR") ) || ( type.equals("char") ) || ( type.equals("varchar") ) ){
                StringBuilder sb= new StringBuilder();
                int tailleColonne= colonnes.get(i).getTailleColonne();
                int fin= buff.getInt(offsetPos+4);
                buff.position(currentPos);
                while(buff.position()<fin){
                    sb.append(buff.getChar());
                    System.out.println("sb : "+sb.toString());
                }
                record.ajouteValeurTuple(sb.toString()); // on ajoute le String complet
            }
            // Pour les int

            else if( ( type.equals("INT") ) || ( type.equals("INTEGER") ) ){
                record.ajouteValeurTuple(buff.getInt());
            }
            else if( type.equals("REAL") ){
                record.ajouteValeurTuple(buff.getFloat());
            }
            i++;
            octetLus=buff.position()-pos;
            offsetPos+=4;
            System.out.println(record);

        }

        buff.flip();
        return octetLus;
    }


    public List<ColInfo> getColonnes() {
        return colonnes;
    }

    public String getNomRelation() {
        return nomRelation;
    }

    public int getNbColonnes() {
        return nbColonnes;
    }

    private int tailleColonneMax(){
        int max=0;
        for(ColInfo colonne : colonnes){
            if (max<colonne.getTailleColonne()){
                max=colonne.getTailleColonne();
            }
        }
        return max;
    }

    private boolean possedeUnVarchar(){
        boolean var=false;
        int reponse=0;
        for( ColInfo Col :colonnes ){
            if( (Col.getTypeColonne().equals("VARCHAR")) || (Col.getTypeColonne().equals("varchar")) ) {
                var = true;
                break;
            }
        }
        return var;
    }

}