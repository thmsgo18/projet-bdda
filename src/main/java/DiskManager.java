import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class DiskManager {

    private ArrayList<PageId> liste_pages;
    private ArrayList<PageId> pagesDesaloc; // La liste des pages désalloulés.


    private DBConfig dbConfig;

    public DiskManager(DBConfig dbConfig) {

        this.dbConfig = dbConfig;
        this.liste_pages = new ArrayList<PageId>();
        this.pagesDesaloc = new ArrayList<PageId>();
    }


// à changer le type de retour void pour PageId c'est juste pour tester sans retourner de valeurs
    public PageId AllocPage(){
            // Initialisation :
        RandomAccessFile raf = null;
        int numeroFichier =0;
        PageId pageAlloue; // la page qu'on va renvoyer
        long currentSize =0; // la taille courante de l'accumulation des octets des pages , ex : pour page 3 -> currentSize = 4* la taille d'une page
        int pageCourante= 0;

        File repertoire = new File(dbConfig.getDbpath()); // Initialisation du rerpertoire
        String cheminFichier = dbConfig.getDbpath()+"/F"+numeroFichier+".bin"; // Initialisation du chemin du fichier
        File fichier = new File(cheminFichier); // Création d'un object fichier

        if (repertoire.exists()){ // Verification de l'existance du repertoire
            System.out.println("Le repertoire existe");

            if(pagesDesaloc.isEmpty()){ // Vérification de si la liste des pages desalloués est vide
                System.out.println("La liste des pages alloués est vide ");
                
                while(fichier.exists()){ // Vérification
                    System.out.println("Le fichier "+numeroFichier+" existe");
                    System.out.println("La longueur du fichier"+numeroFichier+" est  : "+fichier.length());
                    while(currentSize<dbConfig.getFilesize()){// Parcourt des pages d'un fichier
                        try{
                            raf = new RandomAccessFile(fichier,"rw"); // Ouverture du fichier en lecture/écriture
                            raf.seek(currentSize); // Placement de la tete de lecture/écriture au premier octet de la page

                            System.out.println("Lecture à l'indice "+currentSize+", caractere : "+ (char) raf.read());
                            /* Vérification de si la page contient des valeurs ou non :
                                    -> si oui : Passage à la page suivante
                                    -> sinon : Nous pouvons retourner cette page. Donc allouer
                            */
                            if ((raf.read() !=-1)){
                                currentSize+=dbConfig.getPagesize(); // Calcule de la position de la prochaine page
                                pageCourante++;

                            }else{
                                pageAlloue = new PageId(numeroFichier,pageCourante);
                                byte [] tabBytes = new byte[(int) dbConfig.getPagesize()]; // Création d'un tableau de byte vide
                                raf.write(tabBytes); // Écriture d'un tableau de byte vide
                                return pageAlloue; // Retour la page allouée
                            }
                            raf.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                   }
                    numeroFichier++;
                    cheminFichier = dbConfig.getDbpath()+"/F"+numeroFichier+".bin"; // chemin du nouveau numero de fichier
                    fichier = new File(cheminFichier);
                    System.out.println("Numero = "+numeroFichier+" chemin = "+cheminFichier+"fichier = "+fichier);
                }
                // On sort de la boucle quand on atteint un fichier qui n'existe pas
                try {
                    newFile(numeroFichier); // Création d'un nouveau fichier
                    pageAlloue= new PageId(numeroFichier,0);
                    return pageAlloue; // Retourne la page 0 du nouveau fichier
                    } catch (IOException e) {
                        e.printStackTrace();
                }
            }
            else{
                System.out.println(" La liste des pages désalloués est non-vide"); // Pas besoin de créer une page, il existe deja une page de disponible
                pageAlloue =pagesDesaloc.remove(0);
                return pageAlloue;
            }
        }else{
            System.out.println("Le repertoire n'existe pas");
        }
        return null;
    }

    public void ReadPage(PageId pageId, ByteBuffer buff){
        String cheminFichier = dbConfig.getDbpath()+"/F"+pageId.getFileIdx()+".bin"; // Chemin du fichier à lire
        File fichier = new File(cheminFichier);
        long position = dbConfig.getPagesize()*pageId.getPageIdx(); // Calcule de la position en octet de debut de la page
        try {
            RandomAccessFile raf = new RandomAccessFile(fichier, "rw"); // Ouverture du fichier
            raf.seek(position); // Positionnement sur le premier octet de la page voulu
            byte[] tabBytes = new byte[(int) dbConfig.getPagesize()]; // Création d'un tableau de byte pour les octets de la page
            raf.readFully(tabBytes); // Ajoute de tous les octets de la page dans le tableau de bytes
            buff.put(tabBytes); // Rempli le buffer avec les valeurs du tableau de bytes
            buff.flip(); // Revient à la position de depart du bytebuffer
            raf.close();

        }catch(IOException e ){
            e.printStackTrace();
        }


    }

    public void WritePage(PageId pageId,ByteBuffer buff){
        String cheminFichier = dbConfig.getDbpath()+"/F"+pageId.getFileIdx()+".bin"; // Chemin du fichier à écrire
        File fichier = new File(cheminFichier);
        long position = dbConfig.getPagesize()*pageId.getPageIdx(); // Calcule de la position en octet de debut de la page
        try {
            RandomAccessFile raf = new RandomAccessFile(fichier, "rw"); // Ouverture du fichier
            raf.seek(position);  // Positionnement sur le premier octet de la page voulu
            raf.write(buff.array()); // Écriture du bytebuffer, en passant par un tableau, dans le fichier grace au raf
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    // supprime les elements de la page et mets la page dans pagesDesaloc
    public void DeallocPage(PageId pageId) {
        String cheminFichier = dbConfig.getDbpath()+"/F"+pageId.getFileIdx()+".bin"; // Chemin du fichier à désalouer
        File fichier = new File(cheminFichier); // Création d'un object fichier
        long position = dbConfig.getPagesize()*pageId.getPageIdx(); // Calcule de la position en octet de debut de la page
        if(!pagesDesaloc.contains(pageId)){  // vérifie si la page n'est pas deja désalouée
            pagesDesaloc.add(pageId);
            try {
                RandomAccessFile raf = new RandomAccessFile(fichier, "rw"); // Ouverture du fichier
                raf.seek(position); // Positionnement sur le premier octet de la page voulu
                byte [] byteVide = new byte[(int) dbConfig.getPagesize()];
                raf.write(byteVide); // Écriture d'un tableau vide de longueur d'une page. (recouvrement)
                raf.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }


    public void SaveState(){

    }

    public void LoadState(){

    }

    public DBConfig getDbConfig() {
        return dbConfig;
    }
    public void setDbConfig(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }


    public void newFile(int numeroFichier) throws IOException {
        File nouveauFichier = new File(dbConfig.getDbpath()+"/F"+numeroFichier+".bin"); // Chemin du fichier à désalouer
            if(nouveauFichier.createNewFile()){ // Création du fichier
                System.out.println("Création du fichier : "+ nouveauFichier.getName());
                RandomAccessFile raf = new RandomAccessFile(nouveauFichier, "rw"); // Ouverture du fichier
                raf.seek(0); // Placement  au debut du fichier
                byte[] tabBytes = new byte[(int) dbConfig.getPagesize()]; // Création d'un tableau de bytes vide
                raf.write(tabBytes); // Ajoute du tableau de bytes vides. / Création d'une page
                raf.close();
            }else{
                System.out.println("La création du fichier n'as pas fonctionné, existe t'il deja ?"); // Error
            }

    }
    public ArrayList<PageId> getPagesDesaloc(){
        return pagesDesaloc;
    }

    public ArrayList<PageId> getListes_pages(){
        return liste_pages;
    }


}
