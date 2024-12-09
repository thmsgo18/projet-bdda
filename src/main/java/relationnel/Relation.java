package relationnel;

import buffer.BufferManager;
import espaceDisque.DiskManager;
import espaceDisque.PageId;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
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
        this.varchar= possedeUnVarchar();
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


    public int writeRecordToBuffer(Record record, ByteBuffer buff, int pos) {
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
            if (element instanceof String){
                //System.out.println("VERIF : L'element "+element+" est une chaine de caractère ");
                for( char c : element.toString().toCharArray() ) {
                    buff.putChar(c);
                }
            }else if( element instanceof Integer){
                //System.out.println("VERIF : L'element "+element+" est un int ");
                buff.putInt((Integer)element);
            }
            else if( element instanceof Float){
                buff.putFloat((Float)element);
                //System.out.println("VERIF : L'element "+element+" est un float ");

            }else if (element instanceof Character){
                //System.out.println("VERIF : l'élément est un char ");
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
                        //System.out.println("VERIF : boucle formation de la chaine de caractère = " + sb.toString());
                    }
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

    public void addDataPage() throws EOFException {

        System.out.println("\n**************  Debut AJout d'une page de données   *********************");

        int taillePage =(int) diskManager.getDbConfig().getPagesize();
        ByteBuffer buffHeader = bufferManager.GetPage(headerPageId);
        int indice_dernierOctetLecture = buffHeader.getInt(0)*12+4; // on se place au 4ème octet pour lire le dernier octet lu. pour l'initialisation ça devra etre 8 pour être juste apres les 4 octets reservés par le nombre de page dans la header page et les 4 octets reservéeds pour le dernier indice


        // Vérification que la header page a assez de place pour ajouter une nouvelle page (plus tard si j'ai la foi, faudra chainer la header page à une autre)

        if ( ( indice_dernierOctetLecture + 12 ) >taillePage ){ //  l'emplacement de l'octet où l'on peut écrire une case de page + 12 (la taille que fais les 8 octets d'une pageId + 4 octets pour le bombre d'octets disponible dans la page de donnée)
            System.out.println("ERREUR : Il n'y a plus assez de place dans la header page pour accueilir une nouvelle page de donnée");
            boolean dirtyPage = bufferManager.getDirtyPage(headerPageId);

            bufferManager.FreePage(headerPageId,dirtyPage);

        }else{

            // PARTIE MISE À JOUR DE LA HEADER PAGE

            PageId nouvellePageDonnee  = diskManager.AllocPage(); // allocation de la nouvelle page de donnée
            System.out.println("TEST addDataPage: On a alloué la page de données à l'emplacement "+nouvellePageDonnee);


            buffHeader.position(indice_dernierOctetLecture); // On se met à la position du dernier octet avec une valeur
            buffHeader.putInt(nouvellePageDonnee.getFileIdx()); // on mets le numero de fichier
            buffHeader.putInt(nouvellePageDonnee.getPageIdx()); // on merts le numero de page

            int octetDisponibles  = (int) diskManager.getDbConfig().getPagesize()  -4*2; // il faut ecrire le nombre d'octets disponible dans le fichier.
            buffHeader.putInt(octetDisponibles);
            int nombrePagesDonnees= buffHeader.getInt(0);
            buffHeader.putInt(0,nombrePagesDonnees+1);

            System.out.println("RELATION : addDataPage : buff info : "+buffHeader);
            //System.out.println("RELATION : addDataPage : buff contenu : "+Arrays.toString(buffHeader.array()));
            bufferManager.FreePage(headerPageId,true); // on libére la page mais elle a été modifié


        }


        System.out.println("**************  Fin AJout d'une page de données   *********************");



    }

    public PageId getFreeDataPageId(int sizeRecord){
        System.out.println("\n**************  Debut get free data pageId   *********************");

        PageId pageDisponible=null;
        boolean pageTrouve =false;
        int currentPosition =12;
        int octetRestantPage; // variable qui prend le nombre d'octets totaux d'une page de données de la header page, (il faudra y soustraire les octets que prendront la position du record et la taille du recordpour déterminer si un record peut être insérer
        int octetNecessaireInsertion = sizeRecord+8; // Il faut tenir compte de la taille du record mais en plus 4 octets pour la position du record + 4 octet pour la taille du record

        ByteBuffer buffHeader = bufferManager.GetPage(headerPageId);
        System.out.println("RELATION : getFreeDataPageId : buff info : "+buffHeader);
        //System.out.println("RELATION : getFreeDataPageId : buff contenu : "+Arrays.toString(buffHeader.array()));
        int i=0, n = buffHeader.getInt(0); // on récupère le nombre pages de données contenue dans le headerPage

        buffHeader.position(currentPosition); // On se positionne au premier octet qui décrit l’espace disponible de la première page de donnée
        while( (i<n) && (!pageTrouve) ){
            octetRestantPage= buffHeader.getInt();
            System.out.println("TEST GET FREEE : nb  : "+octetRestantPage);
            if (octetRestantPage>= octetNecessaireInsertion){
                buffHeader.position(buffHeader.position()-12); // on se postionne 12 octets plus bas pour récupérer à la suite le numero de fichier et de page
                int numeroFichier= buffHeader.getInt();  // on récupère le numéro de fichier
                int numeroPage= buffHeader.getInt(); // on récupère le numéro de la page
                pageDisponible = new PageId(numeroFichier,numeroPage);
                pageTrouve=true;
            }
            i++;
            currentPosition+=12; // Il faut ignorer les 12 octets qui suivent l'octet correspondant à la place restante (4 octet pour la place que prend l'espace dispo + 4 octet pour le n° fichier + 4 octet pour le n° page)
            System.out.println("TEST getFreeDataPageId : positionPlaceDispo  "+  currentPosition);
            if (currentPosition<buffHeader.capacity()) { // on vérifie qu'on ne depasse pas la taille du buffer
                buffHeader.position(currentPosition);
            }
        }

        // Vérifiaction du dirty de la header Page
        boolean dirtyHeaderPage= bufferManager.getDirtyPage(headerPageId);


        bufferManager.FreePage(headerPageId,dirtyHeaderPage);
        System.out.println("RELATION : getFreeDataPageId  : Page trouvé : "+pageDisponible);

        System.out.println("\n**************  Debut get free data pageId   *********************");

        return pageDisponible; // on retourne la page trouvé ou null si aucune n'a été trouvé
    }

    public RecordId writeRecordToDataPage(Record record, PageId pageId){

        System.out.println("\n**************  Ecriture d'un record dans la page de donnée   *********************");
        ByteBuffer buffData = bufferManager.GetPage(pageId); //On obtient un buffer du bufferManager qui prends en compte la page de donnée pageId

        int offsetNombreSlot = (int) diskManager.getDbConfig().getPagesize() -8; // on se positionne au premier octet du nombre de slot
        buffData.position(offsetNombreSlot);

        // On récupère le nombre de slot actuelle + la position pour écrire le record
        int nombreSlot = buffData.getInt();
        int positionEcrireRecord = buffData.getInt(); // la position pour écrire le record
        buffData.flip();
        int tailleRecord= writeRecordToBuffer(record,buffData,positionEcrireRecord); // On écrit le record dans le buffer qui représente la page de données + On sauvegarde la taille du record
        buffData.limit( buffData.capacity()); // on se remets une limite de la taille de la capacité du buffer, la limite avait changer à cuase de la fonction writeRecordToBuffer
        int positiontEcrireSlot = (int) diskManager.getDbConfig().getPagesize() -8 -nombreSlot*8 - 8; // nombre d'octet d'une page - les 8 octets (du nombre de slot + la position d'ecriture d'un futur record)  -m*8 octets représentes les  m slots - 8 (se laisser de la place pour ecrire la position et la taille du record)
        buffData.position(positiontEcrireSlot);
        buffData.putInt(positionEcrireRecord); // On ecrit la position du premier octet du record
        buffData.putInt(tailleRecord); // on écrit la taille du record

        // Mise à jour du nombre de cases m du slots (corresponds aux nombres de records) + réajustement de la position pour l'écriture d'un nouveau record
        buffData.position(offsetNombreSlot);
        buffData.putInt(nombreSlot+1);  // on augmente de 1 le nombre de cases
        buffData.putInt(positionEcrireRecord+tailleRecord);  // on se décale de la taille du record par rapport à la position
        buffData.flip();
        //System.out.println("Page de données "+Arrays.toString(buffData.array()));
        bufferManager.FreePage(pageId,true);

        // Il faut aussi modifier le nombre d'octet dispoible dans la case de page de données correspondate dans la headerPage

        int numeroFichier,numeroPage;
        boolean pageTrouve=false;
        ByteBuffer buffHeader = bufferManager.GetPage(headerPageId);
        int i=0,n=buffHeader.getInt(0);
        buffHeader.position(4); // On se positionne au premier octet qui décrit le fichier  de la première page de donnée
        while((i<n) && (!pageTrouve)){
            numeroFichier = buffHeader.getInt();    // numéro de fichier
            numeroPage = buffHeader.getInt();       // numéro de Page
            if ((pageId.getFileIdx()== numeroFichier) && (pageId.getPageIdx()== numeroPage)){
                pageTrouve=true; // va provoquer la fin de la boucle
                int octetRestantDispo = buffHeader.getInt();
                buffHeader.position(buffHeader.position()-4);
                buffHeader.putInt( octetRestantDispo- tailleRecord -8); // on déduit du nombre d'octet libre, la taille du record plus 8 octets pris par l'espace du slot +taille du record
                System.out.println("Octet Restant desormais: " +(octetRestantDispo - tailleRecord -8));
            }
            i++;
            if(buffHeader.position()+4 < buffHeader.capacity()){
                buffHeader.position(buffHeader.position()+4);
            }

        }
        //System.out.println("Header Page"+Arrays.toString(buffHeader.array()));
        bufferManager.FreePage(headerPageId,true);
        System.out.println("\n**************  FIN Ecriture d'un record dans la page de donnée   *********************");

        return new RecordId(pageId,nombreSlot+1); // retour du recordID
    }

    public ArrayList<Record> getRecordsInDataPage(PageId pageId){
        int nombreSlot; // le nombre de slot ,utile pour la boucle
        Record record ;
        ArrayList<Record> listeRecord = new ArrayList<>();

        ByteBuffer buffData = bufferManager.GetPage(pageId);
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

        boolean dirtyDataPage = bufferManager.getDirtyPage(pageId);



        bufferManager.FreePage(pageId,dirtyDataPage);

        return listeRecord;
    }

    public List<PageId> getDataPages(){
        List<PageId> dataPages = new ArrayList<>();
        ByteBuffer buffHeader = bufferManager.GetPage(headerPageId);
        int nbDataPage = buffHeader.getInt();
        buffHeader.position(4); // Je pourrais rien mettre à la place de 4 mais c'est pur que ce soit plus facile à debugg
        for(int i=0;i<nbDataPage;i++) {
            int fid = buffHeader.getInt();
            int pid = buffHeader.getInt();
            PageId pageId = new PageId(fid,pid);
            dataPages.add(pageId);
            buffHeader.position(buffHeader.position()+4);
        }

        boolean dirtyPage = bufferManager.getDirtyPage(headerPageId);
        bufferManager.FreePage(headerPageId,dirtyPage);
        return dataPages;
    }

    public RecordId InsertRecord(Record record){
        RecordId rid=null;  // initialisation du rid
        int octetCumulerRecord=0;
        for( ColInfo c : colonnes){ // on obtient la somme en terme d'octet que la colonne fait
            octetCumulerRecord +=c.getTailleColonne();
        }
        if(varchar){ // Si il y a un varchar dans les colonnes, alors il faut ajouter les octets que vont prendre les n+1 int pour délimiter les cases
            octetCumulerRecord+= 4* (getNbColonnes()+1);
        }
        PageId pageDispo =getFreeDataPageId(octetCumulerRecord); // On cherche une page disponible
        System.out.println("Page Dispo : "+pageDispo);
        if (pageDispo!=null){ // Si une page est disponible, on "cris le contenu du record dans la page et n sauvegarde le rid
            rid =writeRecordToDataPage(record,pageDispo);
            System.out.println("Insertion du record réussi !!  "+rid);
        }else{
            // On alloue une nouvelle page de données à la relation si la headerPage a assez de place
            ByteBuffer buffHeader = bufferManager.GetPage(headerPageId);
            int nombreMaxPages = buffHeader.capacity()/4 -4;
            int nombrePageCourante = buffHeader.getInt(0);
            if(nombrePageCourante<nombreMaxPages){
                try{
                    addDataPage(); // on ajoute la page de données à la relation
                } catch (EOFException e) {
                    System.out.println(e.getMessage());
                }
                // On recommence l'insertion et cette fois ci, getFreeDataPageId devrait retourner la page qu'on vient de lier

                System.out.println("RELATION : INSERT RECORD : On insère une page de données supplémentaire à la relation pour insérer le record "+record);
                PageId pageDispo2 =getFreeDataPageId(octetCumulerRecord); // On cherche la page de données qu'on vient de lier à la header page
                System.out.println("Page Dispo : "+pageDispo2);
                if (pageDispo!=null) {
                    rid = writeRecordToDataPage(record, pageDispo2);
                    System.out.println("Insertion du record réussi !!  " + rid);
                }
            }else{
                System.out.println("RELATION : INSERT RECORD : !!!! Erreur lors de l'insertion d'un record : Aucune page ne semble disponible (Insert relationnel.Record) !!!!");

            }
            // On remets le dirty dans la position qu'il avait au départ car on a juste lu ici la header page
            boolean dirtyPage = bufferManager.getDirtyPage(headerPageId);
            bufferManager.FreePage(headerPageId,dirtyPage);
        }
        return rid; // retour du rid
    }

    public List<Record> GetAllRecords(){
        System.out.println("\n**************  DEBUT Get All Records   *********************");

        List<Record> records = new ArrayList<>();
        List<PageId> listePageDonnees = getDataPages(); // ON obtient l'ensemble des pages de données de la relation contenu dans la header page
        System.out.println("RELATION : Get All Records : liste des pages disponibles : "+getDataPages());
        for (PageId pageDonnee: listePageDonnees) { // On parcourt l'ensemble des pages afin de remplir la liste de records liés à chacune des pages de données
            records.addAll(getRecordsInDataPage(pageDonnee));
        }
        System.out.println("\n**************  Fin Get All Records   *********************");
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

    public PageId getHeaderPageId() {
        return headerPageId;
    }
    private boolean possedeUnVarchar(){
        boolean var=false;
        for( ColInfo Col :colonnes ){
            if( (Col.getTypeColonne().equals("VARCHAR")) || (Col.getTypeColonne().equals("varchar")) ) {
                var = true;
                break;
            }
        }
        return var;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Nom de la relation: "+this.getNomRelation()+"\n");
        for(int i=0; i<this.getNbColonnes(); i++){
            sb.append("     Nom colonne: " +colonnes.get(i).getNomColonne()+"\n");
            sb.append("         TypeColonne: " +colonnes.get(i).getTypeColonne()+"\n");
            sb.append("         TailleColonne: " +colonnes.get(i).getTailleColonne()+"\n");
        }
        sb.append("\n");
        return sb.toString();
    }
}
