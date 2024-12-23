package espaceDisque;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

import org.json.*;

public class DiskManager {
    private PageId pageCourante;
    private ArrayList<PageId> pagesDesaloc= new ArrayList<>(); // La liste des pages désalloulés.
    private DBConfig dbConfig;

    public DiskManager(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
        LoadState();
    }

    public PageId AllocPage(){
        // Initialisation :
        PageId pageAlloue; // la page qu'on va renvoyer
        long currentSizeTotalPages = dbConfig.getPagesize()*(pageCourante.getPageIdx()+1); // la taille courante de l'accumulation des octets des pages , ex : pour page 3 -> currentSize = 4* la taille d'une page
        File repertoire = new File(dbConfig.getDbpath()+"/BinData"); // Initialisation du rerpertoire
        String cheminFichier = dbConfig.getDbpath()+"/BinData"+"/F"+pageCourante.getFileIdx()+".bin"; // Initialisation du chemin du fichier
        File fichier = new File(cheminFichier); // Création d'un object fichier

        ByteBuffer buffTemp = ByteBuffer.allocate( (int) dbConfig.getPagesize());  //
        byte [] bb = new byte[(int)dbConfig.getPagesize()]; //


            if(pagesDesaloc.isEmpty()){ // Vérification de si la liste des pages desalloués est vide
                //System.out.println("DISK MANAGER : ALLOC PAGE : La liste des pages alloués est vide ");
                if (repertoire.exists()){ // Verification de l'existance du repertoire
                    //System.out.println("DISK MANAGER : ALLOC PAGE : Le repertoire existe");

                    if(fichier.exists()) { // Vérification
                        //System.out.println("DISK MANAGER : ALLOC PAGE : Le fichier " + pageCourante.getFileIdx() + " existe");
                        //System.out.println("DISK MANAGER : ALLOC PAGE : La longueur du fichier" + pageCourante.getFileIdx() + " est  : " + fichier.length());

                        if (currentSizeTotalPages+ dbConfig.getPagesize() <= dbConfig.getFilesize()) { // vérifie qu'il y a de la place dans un fichier pour crer une page
                            pageCourante = new PageId(pageCourante.getFileIdx(), pageCourante.getPageIdx() + 1);
                            pageAlloue = pageCourante;
                            buffTemp.put(bb);buffTemp.flip();
                            WritePage(pageAlloue,buffTemp);
                            SaveState();
                            return pageAlloue; // Retour la page allouée
                        } else {
                            // partie à modifier pour prendre en compte qu'on ne boucle plus
                            pageCourante = new PageId(pageCourante.getFileIdx()+1, 0); // c'est la pagecouante qu'on va renvoyer mais elle ne sera donc plus la page courante apres la sortie de la focntion
                            newFile(pageCourante.getFileIdx()); // création du nouveau fichier
                            pageAlloue = pageCourante;
                            //Modification ajout d'octes null dans la page

                            buffTemp.put(bb);buffTemp.flip();
                            WritePage(pageAlloue,buffTemp);
                            SaveState();
                            return pageAlloue; // Retourne la page 0 du nouveau fichier
                        }
                    }else{
                        // le cas pour initialiser la premier fichier 0,  car le fichier existera toujours après.
                        newFile(0); // création du nouveau premier fichier
                        pageCourante = new PageId(0, 0);
                        pageAlloue =pageCourante;
                        buffTemp.put(bb);buffTemp.flip();
                        WritePage(pageAlloue,buffTemp);
                        SaveState();
                        return pageAlloue; // Retourne la page 0 du nouveau fichier
                    }
                }else{
                    //System.out.println("DISK MANAGER : ALLOC PAGE : Le repertoire n'existe pas");
                }

            }
            else{
                //System.out.println("DISK MANAGER : ALLOC PAGE : La liste des pages désalloués est non-vide"); // Pas besoin de créer une page, il existe deja une page de disponible
                pageAlloue =pagesDesaloc.remove(0); // Prends un élément des pages libres pour le retourner
                SaveState();
                return pageAlloue;
            }

        return null;
    }

    public void ReadPage(PageId pageId, ByteBuffer buff){
        String cheminFichier = dbConfig.getDbpath()+"/BinData"+"/F"+pageId.getFileIdx()+".bin"; // Chemin du fichier à lire
        File fichier = new File(cheminFichier);
        if(fichier.exists()) {
            long position = dbConfig.getPagesize() * pageId.getPageIdx(); // Calcule de la position en octet de debut de la page
            try {
                RandomAccessFile raf = new RandomAccessFile(fichier, "rw"); // Ouverture du fichier
                raf.seek(position); // Positionnement sur le premier octet de la page voulu
                byte[] tabBytes = new byte[(int) dbConfig.getPagesize()]; // Création d'un tableau de byte pour les octets de la page
                //System.out.println("DISK MANAGER : READ PAGE : pointeur raf fichier : "+raf.getFilePointer()+" longueur tabBytes : "+tabBytes.length);
                //System.out.println("DISK MANAGER : READ PAGE : buffPosition "+buff.position());
                raf.readFully(tabBytes); // Ajoute de tous les octets de la page dans le tableau de bytes
                buff.put(tabBytes); // Rempli le buffer avec les valeurs du tableau de bytes
                buff.flip(); // Revient à la position de depart du bytebuffer
                raf.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            //System.out.println("DISK MANAGER : READ PAGE : Vous tentez de lire un fichier qui n'existe pas");
        }
    }
//
    public void WritePage(PageId pageId,ByteBuffer buff){
        String cheminFichier = dbConfig.getDbpath()+"/BinData"+"/F"+pageId.getFileIdx()+".bin"; // Chemin du fichier à écrire
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
        String cheminFichier = dbConfig.getDbpath()+"/BinData"+"/F"+pageId.getFileIdx()+".bin"; // Chemin du fichier à désalouer
        File fichier = new File(cheminFichier); // Création d'un object fichier
        long position = dbConfig.getPagesize()*pageId.getPageIdx(); // Calcule de la position en octet de debut de la page
        ByteBuffer buffTemp = ByteBuffer.allocate( (int) dbConfig.getPagesize());  //
        byte [] bb = new byte[(int)dbConfig.getPagesize()]; //

        if(!pagesDesaloc.contains(pageId)){  // vérifie si la page n'est pas deja désalouée
            buffTemp.put(bb);buffTemp.flip();
            WritePage(pageId,buffTemp);

            pagesDesaloc.add(pageId);
            //System.out.println("DISK MANAGER : DEALLOCPAGE : La pageID : "+pageId.toString()+" a été correctement désalloué");
            SaveState();
        }else{
            //System.out.println("DISK MANAGER : DEALLOCPAGE : La pageID : "+pageId.toString()+" est deja desalloué");
        }
    }


    public void SaveState(){
        String chemin = dbConfig.getDbpath()+"/dm.save.json";
        try{
            File file = new File(chemin);
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bfw = new BufferedWriter(fw);
            bfw.write("{"); // ouverture de la première accolade
            bfw.newLine(); // revient à la ligne
            bfw.write("    \"pageDesalloues\":{"); // ouverture accolade Desalloues
            bfw.newLine(); // revient à la ligne
            int courant = 0;
            while(courant<pagesDesaloc.size()){
                bfw.write("        \""+courant+"\": "+pagesDesaloc.get(courant));
                ++courant;
                if(courant<pagesDesaloc.size()) {
                    bfw.write(",");
                    bfw.newLine(); // revient à la ligne
                }
            }
            bfw.newLine(); // revient à la ligne
            bfw.write("    },"); // fermeture accolade pageDesalloue
            bfw.newLine(); // revient à la ligne
            bfw.write("    \"pageCourante\": "+pageCourante);
            bfw.newLine(); // revient à la ligne
            bfw.write("}"); // fermeture de de la première accolade
            bfw.close();
        }catch (IOException e) {
            System.out.println("DISK MANAGER : SAVE STATE : Le fichier n'a pas pu être sauvegarder");
            e.printStackTrace();

        }
    }

    public void LoadState(){
        String chemin = dbConfig.getDbpath()+"/dm.save.json";
        try{
            File file = new File(chemin);
            if(!file.exists()){
                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                BufferedWriter bfw = new BufferedWriter(fw);
                bfw.write("{"); // ouverture de la première accolade
                bfw.newLine(); // revient à la ligne
                bfw.write("    \"pageDesalloues\":{},"); // ouverture accolade Desalloues
                bfw.newLine(); // revient à la ligne
                bfw.write("    \"pageCourante\":[0,0]");
                bfw.newLine(); // revient à la ligne
                bfw.write("}"); // fermeture de la première accolade
                bfw.close();
            }
            File dossier = new File(dbConfig.getDbpath()+"/BinData");
            if(dossier.mkdir()){
                //System.out.println("Le dossier BinData est bien créé");
            }else{
                //System.out.println("Le dossier n'a pas été créé");
            }
            FileReader fr = new  FileReader(file); //Utilisation des classes FileReader et BufferReader pour lire le fichier
            BufferedReader bfr = new BufferedReader(fr);
            StringBuilder sb =new StringBuilder();
            String ligne;
            while((ligne = bfr.readLine())!=null){ //line vaut la ligne du fichier(ex: si fichier contient une ligne : "dbpath : ././DB" alors line vaut dbpath. boucle continue jusqu'a plus de ligne.
                sb.append(ligne); // on ajoute la ligne au StringBuffer car StringBuffer est plus flexible d'utilisation.
            }
            bfr.close();//Fermeture de la lecture du fichier
            JSONObject js = new JSONObject(sb.toString());//Creer une instance de JsonObject pour recuperer la ligne qui sera transformer en Json
            pageCourante = new PageId(js.getJSONArray("pageCourante").getInt(0),js.getJSONArray("pageCourante").getInt(1));
            //System.out.println("DISK MANAGER : LoadState : PageCourante: "+pageCourante.toString());
            pagesDesaloc.clear(); // supprime les éléments de la liste des pages désallou&s pour les remplir du fichier dm.save.json
            JSONObject pagesDesallouesJson= js.getJSONObject("pageDesalloues"); // representation de l'objet Json pageDesalloues
            JSONArray page;
            // Parcourt l'ensemble des clés de l'objet JSON, puis enregistre les listes correspondants aux valeurs des pageID
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


    public void newFile(int numeroFichier) {
        File nouveauFichier = new File(dbConfig.getDbpath()+"/BinData"+"/F"+numeroFichier+".bin"); // Chemin du fichier à désalouer
            try {
                if (nouveauFichier.createNewFile()) { // Création du fichier
                    //System.out.println("DISK MANAGER : NEW FILE : Création du fichier : " + nouveauFichier.getName());
                } else {
                   // System.out.println("DISK MANAGER : NEW FILE : La création du fichier n'as pas fonctionné, existe t'il deja ?"); // Error
                }
            }catch(IOException e){
                e.printStackTrace();
            }
    }
    public ArrayList<PageId> getPagesDesaloc(){
        return pagesDesaloc;
    }
    // commit pour le V

}
