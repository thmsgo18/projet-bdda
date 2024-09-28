import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

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
        RandomAccessFile raf = null;
        int numeroFichier =0;
        PageId pageAlloue; // la page qu'on va renvoyer
        long currentSize =0; // la taille courante de l'accumulation des octets des pages , ex : pour page 3 -> currentSize = 4* la taille d'une page
        int pageCourante= 0;
        int test;
        File repertoire = new File(dbConfig.getDbpath());
        String cheminFichier = dbConfig.getDbpath()+"/F"+numeroFichier+".txt";
        File fichier = new File(cheminFichier);
        // On verifie si le repertoire existe
        if (repertoire.exists()){
            System.out.println("Le repertoire existe");
            // on vérifie si la liste des pages desalloués est vide
            if(pagesDesaloc.isEmpty()){
                System.out.println("La liste des pages alloués est vide ");
                while(fichier.exists()){
                    System.out.println("Le fichier "+numeroFichier+" existe");
                    System.out.println("La longueur du fichier"+numeroFichier+" est  : "+fichier.length());
                    System.out.println("************ Rappel: les indices d'octets sont en x2 à cause des '\n'");
                    while(currentSize<dbConfig.getFilesize()){
                        try{
                            raf = new RandomAccessFile(fichier,"rw");
                            // On se place à la premier octet d'une page à chaque fois
                            raf.seek(currentSize);
                            System.out.println("Lecture à l'indice "+currentSize+" = "+ (char) raf.read());
                            /* On vérifie s'il y a un truc dans la page
                                    -> si oui : on incremente la taille courante du montant d'octets de la page
                                    -> sinon : ça veut dire qu'on peut retourner la pageId correspondante à l'endroit
                            */
                            if (((test = raf.read())!=-1)){
                                currentSize+=dbConfig.getPagesize()*2; // on mets le x2 car on test avec un fichier txt où il faut compter les '\n' en plus
                                pageCourante++;
                            }else{
                                pageAlloue = new PageId(numeroFichier,pageCourante);
                                return pageAlloue;
                            }
                            raf.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                   }
                    // passage à l'itération avec le prochain fichier
                    numeroFichier++;
                    cheminFichier = dbConfig.getDbpath()+"/F"+numeroFichier+".txt";
                    fichier = new File(cheminFichier);
                    System.out.println("Numero = "+numeroFichier+" chemin = "+cheminFichier+"fichier = "+fichier);
                }
                // on sort de la boucle car on atteint un fichier qui n'existe pas
                try {
                    creationFichier(numeroFichier);
                    pageAlloue= new PageId(numeroFichier,0);
                    return pageAlloue;
                    } catch (IOException e) {
                        e.printStackTrace();
                }
            }
            else{
                System.out.println(" La liste des pages désalloués est non-vide");
                // pas besoin de créer un fichier, il existe deja
                pageAlloue =pagesDesaloc.remove(0);
                return pageAlloue;
            }
        }else{
            System.out.println("Le repertoire n'existe pas");
        }
        return null;
    }

    public void ReadPage(PageId pageId, ByteBuffer buff){

    }

    public void WritePage(PageId pageId,ByteBuffer buff){
        String cheminFichier = dbConfig.getDbpath()+"/F"+pageId.getFileIdx()+".txt";
        File fichier = new File(cheminFichier);

    }

    // supprime les elements de la page et mets la page dans pagesDesaloc
    public void DeallocPage(PageId pageId) {
        String cheminFichier = dbConfig.getDbpath()+"/F"+pageId.getFileIdx()+".txt";
        File fichier = new File(cheminFichier);
        if(!pagesDesaloc.contains(pageId)){  // vérifie si la page n'est pas deja dans la liste
            pagesDesaloc.add(pageId);
            try {
                RandomAccessFile raf = new RandomAccessFile(fichier, "rw");
                raf.seek(pageId.getPageIdx()*dbConfig.getPagesize()); // on se positionne au premier octet de la page
                byte [] byteVide = new byte[(int) dbConfig.getPagesize()];
                raf.write(byteVide); // on écrit un tableau vide de longueur d'une page, pour 'supprimer' les donnes de la page
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


    public void creationFichier(int numeroFichier) throws IOException {
        File nouveauFichier = new File(dbConfig.getDbpath()+"/F"+numeroFichier+".txt");
        System.out.println("Le fichier "+numeroFichier+" n'existe pas ");
            if(nouveauFichier.createNewFile()){
                System.out.println("Création du fichier : "+ nouveauFichier.getName());
                RandomAccessFile raf = new RandomAccessFile(nouveauFichier,"rw");
                raf.seek(0);
                raf.write('A');
                raf.close();
            }

    }


}
