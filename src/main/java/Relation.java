import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Relation {

    private String nomRelation;
    private int nbColonnes;
    private List<ColInfo> colonnes;
    private boolean varchar;
    private PageId headerPageId; // identifiant de Header Page de la relation
    private DiskManager diskManager;
    private BufferManager bufferManager;

    public Relation(String nomRelation, int nbColonnes, PageId headerPageId, DiskManager diskManager, BufferManager bufferManager) {
        this.nomRelation = nomRelation;
        this.nbColonnes = nbColonnes;
        this.headerPageId = headerPageId;
        this.diskManager = diskManager;
        this.bufferManager = bufferManager;
    }
    public Relation(String nomRelation, int nbColonnes, PageId headerPageId, DiskManager diskManager, BufferManager bufferManager,List<ColInfo> colonnes) {
        this.nomRelation = nomRelation;
        this.nbColonnes = nbColonnes;
        this.colonnes = colonnes;
        this.headerPageId = headerPageId;
        this.diskManager = diskManager;
        this.bufferManager = bufferManager;
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
            if(element instanceof String){
                for( char c : element.toString().toCharArray() ) {
                    buff.putChar(c);
                }
            }
            else if (element instanceof Character){
                buff.putChar((Character)element);

            }
            else if( element instanceof Integer){
                buff.putInt((Integer)element);
            }
            else if (element instanceof Float){
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
        int i=0;
        int anciennePosition;


        buff.position(pos);
        while ((i<nbColonnes) && ( buff.hasRemaining() )  ){ // On boucle tant que le buffer a encore des elements
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

                record.ajouteValeurTuple(sb.toString()); // on ajoute le String complet
            }
            else if( type.equals("INT") || type.equals("INTEGER") ){
                record.ajouteValeurTuple(buff.getInt());
            }
            else if( type.equals("REAL") ){
                record.ajouteValeurTuple(buff.getFloat());
            }
            octetLus+=buff.position()-anciennePosition;

            i++;
            System.out.println(buff.position()+"   "+ pos);
            System.out.println(" tuple :"+record);
        }
        buff.flip();

        return octetLus;
    }

    private int readBufferVariable(Record record, ByteBuffer buff, int pos) {
        String type;
        int octetLus = 0;
        int i = 0;
        buff.position(pos);
        int offsetPos = buff.position();
        int currentPos;
        while ((i<nbColonnes)&& ( buff.hasRemaining() ) ){ // On boucle tant que le buffer a encore des elements
            currentPos = buff.getInt(offsetPos);

            buff.position(currentPos);

            type =colonnes.get(i).getTypeColonne();

            // Pour les strings

            if( (type.equals("CHAR") ) || ( type.equals("VARCHAR") ) || ( type.equals("char") ) || ( type.equals("varchar") ) ){
                StringBuilder sb= new StringBuilder();
                int fin= buff.getInt(offsetPos+4);
                buff.position(currentPos);
                while(buff.position()<fin){
                    sb.append(buff.getChar());
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

        }

        buff.flip();
        return octetLus;
    }

    public void addDataPage(){
        System.out.println("**************  AJout d'une page de données   *********************");

        int taillePage =(int) diskManager.getDbConfig().getPagesize();
        ByteBuffer buff1  = ByteBuffer.allocate(taillePage);  // va servir à lire le contenu du headerPage pour plus tard rajouter les elements d'une case d'une nouvelle page de donnée, et ensuite réecrire la headerpage avec les changements
        diskManager.ReadPage(headerPageId,buff1); // Lecture de la header page

        // Vérification que la header page a assez de place pour ajouter une nouvelle page (plus tard si j'ai la foi, faudra chainer la header page à une autre)

        if ((buff1.getInt(4)+ 12)>taillePage){ //  l'emplacement de l'octet où l'on peut écrire une case de page + 12 (la taille que fais les 8 octets d'une pageId + 4 octets pour le bombre d'octets disponible dans la page de donnée)
            System.out.println("ERREUR : Il n'y a plus assez de place dans la header page pour accueilir une nouvelle page de donnée");

        }else{

            // PARTIE MISE À JOUR DE LA HEADER PAGE

            PageId nouvellePageDonnee  = diskManager.AllocPage(); // allocation de la nouvelle page de donnée
            System.out.println("TEST : On a alloué la page de données à l'emplacement"+nouvellePageDonnee);

            int indice_dernierOctetLecture = buff1.getInt(4); // on se place au 4ème octet pour lire le dernier octet lu. pour l'initialisation ça devra etre 8 pour être juste apres les 4 octets reservés par le nombre de page dans la header page et les 4 octets reservéeds pour le dernier indice

            buff1.position(indice_dernierOctetLecture); // On se met à la position du dernier octet avec une valeur
            buff1.putInt(nouvellePageDonnee.getFileIdx()); // on mets le numero de fichier
            buff1.putInt(nouvellePageDonnee.getPageIdx()); // on merts le numero de page

            int octetDisponibles  = (int) diskManager.getDbConfig().getPagesize()  -4*2; // il faut ecrire le nombre d'octets disponible dans le fichier.
            buff1.putInt(octetDisponibles);
            int nombrePagesDonnees= buff1.getInt(0);
            buff1.putInt(0,nombrePagesDonnees+1);
            buff1.putInt(4, buff1.position()); // on mets à jout l'indice du dernier octet vraiment lu
            diskManager.WritePage(headerPageId,buff1); // on remplace la headerpage par la header page mis à jour

            // PARTIE INITIALISATION DE LA NOUVELLE PAGE DE DONNÉES

            // Il faut aussi initialiser dans la page de donnée en elle même certains trucs : initialsier la position pour écrire le premier record à 0, et le nombre d'entrée du slot directory à 0

            ByteBuffer buff2 = ByteBuffer.allocate( (int) diskManager.getDbConfig().getPagesize());

            buff2.position(octetDisponibles); // On va se positionnner à l'indice 4088 pour mettre à la fin de la page les 8 octets pour l'initialisation
            buff2.putInt(0); // On initialise ici le nombre d'entrée slot directory (donc 0)
            buff2.putInt(0); // on initialise ici la position du premier record à écrire (donc 0)


            // Note : apres différents test j'ai l'impression qu'initialisé les 2 '0' à la fin de la page de donnée ne sert à rien vu que quand on écrit les pages ça revient à mettre tous les bits non explicitement placé, à 0. À voir

            diskManager.WritePage(nouvellePageDonnee,buff2);
        }



    }

    public PageId getFreeDataPageId(int sizeRecord){
        PageId pageDisponible=null;
        boolean pageTrouve =false;
        int currentPosition =16;
        int octetRestantPage; // variable qui prend le nombre d'octets totaux d'une page de données de la header page, (il faudra y soustraire les octets que prendront la position du record et la taille du recordpour déterminer si un record peut être insérer
        int octetNecessaireInsertion = sizeRecord+8; // Il faut tenir compte de la taille du record mais en plus 4 octets pour la position du record + 4 octet pour la taille du record
        ByteBuffer buff = ByteBuffer.allocate( (int) diskManager.getDbConfig().getPagesize() );
        diskManager.ReadPage(headerPageId,buff);
        int i=0,n  =buff.getInt(0); // on récupère le nombre pages de données contenue dans le headerPage

        buff.position(currentPosition); // On se positionne au premier octet qui décrit l’espace disponible de la première page de donnée
        while( (i<n) && (!pageTrouve) ){
            octetRestantPage= buff.getInt();
            System.out.println("TEST GET FREEE : nb  : "+octetRestantPage);
            if (octetRestantPage>= octetNecessaireInsertion){
                buff.position(buff.position()-12); // on se postionne 12 octets plus bas pour récupérer à la suite le numero de fichier et de page
                int numeroFichier= buff.getInt();  // on récupère le numéro de fichier
                int numeroPage= buff.getInt(); // on récupère le numéro de la page
                pageDisponible = new PageId(numeroFichier,numeroPage);
                pageTrouve=true;
            }
            i++;
            currentPosition+=12; // Il faut ignorer les 12 octets qui suivent l'octet correspondant à la place restante (4 octet pour la place que prend l'espace dispo + 4 octet pour le n° fichier + 4 octet pour le n° page)
            buff.position(currentPosition);
        }
        System.out.println("Page trouvé : "+pageDisponible);
        return pageDisponible; // on retourne la page trouvé ou null si aucune n'a été trouvé
    }

    public RecordId writeRecordToDataPage(Record record, PageId pageId){

        System.out.println("\n**************  Ecriture d'un record dans la page de donnée   *********************");
        ByteBuffer buff = ByteBuffer.allocate( (int) diskManager.getDbConfig().getPagesize());
        diskManager.ReadPage(pageId,buff); // On lit tout le contenu de la page de données

        int offsetNombreSlot = (int) diskManager.getDbConfig().getPagesize() -8; // on se positionne au premier octet du nombre de slot
        buff.position(offsetNombreSlot);

        // On récupère le nombre de slot actuelle + la position pour écrire le record
        int nombreSlot = buff.getInt();
        int positionEcrireRecord = buff.getInt();
        buff.flip();
        int tailleRecord= writeRecordToBuffer(record,buff,positionEcrireRecord); // On écrit le record dans le buffer qui représente la page de données + On sauvegarde la taille du record
        buff.limit( buff.capacity()); // on se remets une limite de la taille de la capacité du buffer, la limite avait changer à cuase de la fonction writeRecordToBuffer
        int positiontEcrireSlot = (int) diskManager.getDbConfig().getPagesize() -8 -nombreSlot*8 - 8; // nombre d'octet d'une page - les 8 octets (du nombre de slot + la position d'ecriture d'un futur record)  -m*8 octets représentes les  m slots - 8 (se laisser de la place pour ecrire la position et la taille du record)
        buff.position(positiontEcrireSlot);
        buff.putInt(positionEcrireRecord); // On ecrit la position du premier octet du record
        buff.putInt(tailleRecord); // on écrit la taille du record

        // Mise à jour du nombre de cases m du slots (corresponds aux nombres de records) + réajustement de la position pour l'écriture d'un nouveau record

        buff.position(offsetNombreSlot);
        buff.putInt(nombreSlot+1);  // on augmente de 1 le nombre de cases
        buff.putInt(positionEcrireRecord+tailleRecord);  // on se décale de la taille du record par rapport à la position
        buff.flip();
        System.out.println("Page de données "+Arrays.toString(buff.array()));
        diskManager.WritePage(pageId,buff); // On réecrit la page avec le contenu mise à jour

        // Il faut aussi modifier le nombre d'octet dispoible dans la case de page de données correspondate dans la headerPage

        int numeroFichier,numeroPage;
        boolean pageTrouve=false;
        ByteBuffer buffHeader = ByteBuffer.allocate( (int) diskManager.getDbConfig().getPagesize() );
        diskManager.ReadPage(headerPageId,buffHeader);
        int i=0,n=buffHeader.getInt(0);
        buffHeader.position(8); // On se positionne au premier octet qui décrit l’espace disponible de la première page de donnée
        while((i<n) && (!pageTrouve)){
            numeroFichier = buffHeader.getInt();    // numéro de fichier
            numeroPage = buffHeader.getInt();       // numéro de Page
            if ((pageId.getFileIdx()== numeroFichier) && (pageId.getPageIdx()== numeroPage)){
                pageTrouve=true;
                int octetRestantDispo = buffHeader.getInt();
                buffHeader.position(buffHeader.position()-4);
                buffHeader.putInt( octetRestantDispo- tailleRecord -8);
               System.out.println("Octet Restant desormais: " +(octetRestantDispo - tailleRecord -8));
            }
            i++;
            buffHeader.position(buffHeader.position()+4);

        }
        buffHeader.flip();
        System.out.println("Header Page"+Arrays.toString(buffHeader.array()));
        diskManager.WritePage(headerPageId,buffHeader);
        System.out.println("\n**************  FIN Ecriture d'un record dans la page de donnée   *********************");

        return new RecordId(pageId,positionEcrireRecord); // retour du recordID
    }

    public ArrayList<Record> getRecordsInDataPage(PageId pageId){
        int nombreSlot; // le nombre de slot ,utile pour la boucle
        Record record ;
        ArrayList<Record> listeRecord = new ArrayList<>();
        ByteBuffer buffData = ByteBuffer.allocate( (int) diskManager.getDbConfig().getPagesize());
        diskManager.ReadPage(pageId,buffData);
        buffData.position( (int) diskManager.getDbConfig().getPagesize() -8);
        nombreSlot = buffData.getInt();
        int currentPosition = buffData.position()-12; // la position de la première case indiquant la position du premier record à prendre
        buffData.position(currentPosition); // on se met à l'offset du slot
        int i=0;
        int positionRecord;
        while (i<nombreSlot){
            positionRecord= buffData.getInt();
            if (positionRecord!=-1){
                record = new Record();
                readFromBuffer(record,buffData,positionRecord);
                buffData.limit(buffData.capacity());
                listeRecord.add(record);
            }
            currentPosition-=8;
            buffData.position(currentPosition);
            i++;
        }

        return listeRecord;
    }

    public List<PageId> getDataPages(){
        List<PageId> dataPages = new ArrayList<>();
        ByteBuffer buffHeader = ByteBuffer.allocate( (int) diskManager.getDbConfig().getPagesize() );
        diskManager.ReadPage(headerPageId, buffHeader);
        int nbDataPage = buffHeader.getInt();
        buffHeader.position(buffHeader.position()+4);
        for(int i=0;i<nbDataPage;i++) {
            int fid = buffHeader.getInt();
            int pid = buffHeader.getInt();
            PageId pageId = new PageId(fid,pid);
            dataPages.add(pageId);
            buffHeader.position(buffHeader.position()+4);
        }
        return dataPages;
    }


    public RecordId InsertRecord(Record record){
        RecordId rid=null;
        int octetCumulerRecord=0;
        for( ColInfo c : colonnes){
            octetCumulerRecord +=c.getTailleColonne();
        }
        if(varchar){
            octetCumulerRecord+= 4* (getNbColonnes()+1);
        }
        PageId pageDispo =getFreeDataPageId(octetCumulerRecord);
        if (pageDispo!=null){
            rid =writeRecordToDataPage(record,pageDispo);
            System.out.println("Insertion du record réussi !!  "+rid);
        }else{
            System.out.println(" !!!! Erreur lors de l'insertion d'un record : Aucune page ne semble disponible !!!!");
        }
        return rid;
    }


    public List<Record> GetAllRecords(){
        List<Record> records = new ArrayList<>();
        List<PageId> listePageDonnees = getDataPages();

        for (PageId pageDonnee: listePageDonnees) {
            records.addAll(getRecordsInDataPage(pageDonnee));
        }
        return records;
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
