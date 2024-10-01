import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import org.json.*;

public class DiskManager {
    private PageId pageCourante;
    private ArrayList<PageId> pagesDesaloc= new ArrayList<>(); // La liste des pages désalloulés.


    private DBConfig dbConfig;

    public DiskManager(DBConfig dbConfig) {

        this.dbConfig = dbConfig;
        LoadState();
    }


// à changer le type de retour void pour PageId c'est juste pour tester sans retourner de valeurs
    public PageId AllocPage(){
            // Initialisation :
        RandomAccessFile raf = null;
        int numeroFichier =0;
        PageId pageAlloue; // la page qu'on va renvoyer
        long currentSize =0; // la taille courante de l'accumulation des octets des pages , ex : pour page 3 -> currentSize = 4* la taille d'une page
        int pC= 0;

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
                                pC++;

                            }else{
                                pageAlloue = new PageId(numeroFichier,pC);
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
            System.out.println("La pageID : "+pageId.toString()+" a été correctement désalloué");
        }else{
            System.out.println("La pageID : "+pageId.toString()+" est deja desalloué");
        }
    }


    public void SaveState(){
        String chemin = dbConfig.getDbpath()+"/../dm.save.json";
        try{
            FileWriter fw = new FileWriter(chemin);
            BufferedWriter bfw = new BufferedWriter(fw);
            bfw.write("{"); // ouverture de la première accolade
            bfw.newLine(); // saut de ligne
            bfw.write("    \"pageDesalloues\":{"); // ouverture accolade Desalloues
            bfw.newLine(); // saut de ligne
            int courant = 0;
            while(courant<pagesDesaloc.size()){
                bfw.write("        \""+courant+"\": "+pagesDesaloc.get(courant));
                ++courant;
                if(courant<pagesDesaloc.size()) {
                    bfw.write(",");
                    bfw.newLine(); // saut de ligne
                }
            }
            bfw.newLine();
            bfw.write("    },"); // fermeture accolade pageDesalloue
            bfw.newLine(); // saut de ligne
            bfw.write("    \"pageCourante\": "+pageCourante);
            bfw.newLine(); // saut de ligne
            bfw.write("}"); // fermeture de de la première accolade
            bfw.close();
            }catch (IOException e) {
            System.out.println("Le fichier n'a pas pu être sauvegarder");
            e.printStackTrace();

        }


    }

    public void LoadState(){
        String chemin = dbConfig.getDbpath()+"/../dm.save.json";
        try{
            FileReader fr = new  FileReader(chemin); //Utilisation des classes FileReader et BufferReader pour lire le fichier
            BufferedReader bfr = new BufferedReader(fr);
            StringBuilder sb =new StringBuilder();
            String ligne;
            while((ligne = bfr.readLine())!=null){ //line vaut la ligne du fichier(ex: si fichier contient une ligne : "dbpath : ././DB" alors line vaut dbpath. boucle continue jusqu'a plus de ligne.
                sb.append(ligne); // on ajoute la ligne au StringBuffer car StringBuffer est plus flexible d'utilisation.
            }
            bfr.close();//Fermeture de la lecture du fichier
            JSONObject js = new JSONObject(sb.toString());//Creer une instance de JsonObject pour recuperer la ligne qui sera transformer en Json
            pageCourante = new PageId(js.getJSONArray("pageCourante").getInt(0),js.getJSONArray("pageCourante").getInt(1));

            pagesDesaloc.clear();
            JSONObject pagesDesallouesJson= js.getJSONObject("pageDesalloues");
            JSONArray page;
            for(String key : pagesDesallouesJson.keySet()){
                page = pagesDesallouesJson.getJSONArray(key);
                pagesDesaloc.add( new PageId(page.getInt(0),page.getInt(1)));
            }
        }catch(IOException io){
            io.printStackTrace();

        }
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

}
