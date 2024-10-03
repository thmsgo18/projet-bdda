import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

public class DiskManagerTests {
    public static void main(String[] args) {
        DBConfig config;
        config = DBConfig.LoadDBConfig("src/main/json/file-config.json"); // Création d'un objet DBconfig avec la config d'un fichier json
        DiskManager dM = new DiskManager(config); // Création d'un DiskManager
       /* Normalement a chaque fois que vous allez executez ce programme, une page sera alloué, si vous n'avez pas de fichier 0
          ça va vous en créer un en allouant la 1er page.
          Le programme va écrire la lettre Z pour remplir la page (vous pouvez changer la la lettre si vous voulez
          À chaque execution vous pouvez voir dans dm.save.json que pageCourante change, elle correspond à la derniere pageID alloué
        */

        // je vous conseille pour le test de supprimer vos précédents fichier.bin
        // et de bien vérifier que la pageCourante est [0,0] dans dm.save.json pour une bonne initialisation

        PageId p2 = new PageId(0,0);
        dM.DeallocPage(p2);
       PageId p=dM.AllocPage();

        //DiskManagerTests.TestLoadState(dM);
        ByteBuffer buff=ByteBuffer.allocate((int)dM.getDbConfig().getPagesize());

        for(int i=0;i<buff.capacity();i++){
           buff.put((byte)'A');
        }
        dM.WritePage(p2,buff);
        affichagePage(dM,p2);
       // Noter que vous pouvez tester de desalloué une page apres avoir alloués quelques pages (!!! ON NE DESALOUE PAS UNE PAGE QUI N'A JAMAIS ÉTÉ ALLOUÉ !!!)

    }

    public static void TestAllocPage(DiskManager dm){
        System.out.println("************* Test de AllocPage *************");
        dm.AllocPage();
    }

    public static void TestDeallocPage(DiskManager dm){
        System.out.println("************* Test de DeallocPage *************");
        System.out.println("Prenons ici la pageID (0,0), donc F0bin / page 1");
        PageId p = new PageId(0,0); // Création d'une page n°1 dans le fichier F2 / bin
        dm.DeallocPage(p); // Appel de la fonction de désalocation de page sur la page créé précédement
        dm.SaveState();
        PageId pa = dm.AllocPage();
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) dm.getDbConfig().getPagesize()); // Création du ByteBuffer
        Scanner sc = new Scanner(System.in); // Création d'un scanner

        // Demande du caractère à mettre dans la page :
        System.out.println("Entrez le code Asci du caractère à mettre dans toute la page :");
        int choix = sc.nextInt(); // Récupération du choix de l'utilisateur
        byte [] tabBytes = new byte[(int) dm.getDbConfig().getPagesize()]; // Création d'un tableau de la taille d'une page contenant des bytes
        for(int i = 0; i < tabBytes.length; i++){
            tabBytes[i]= (byte) choix; // Ajout du code binaire du caractère dans le tableau
        }

        // Ajout des caractères dans le buffer :
        byteBuffer.put(tabBytes); // Ajout du tableau dans le buffer
        byteBuffer.flip(); // Retour de la tete de lecture du bytebuffer au debut de ce dernier
        System.out.println("Avant l'ecriture dans la page\n");
        affichagePage(dm,pa); // Affichage de la page avant modification
        dm.WritePage(pa,byteBuffer); // Ecrire dans la page
        System.out.println("Après l'ecriture dans la page\n");
        affichagePage(dm,pa); // Affichage de la page modifier

    }

    public  static void TestEcriturePage(DiskManager dm){
            // Explication du test à l'utilisateur :
        System.out.println("************* Test de Write *************");
        System.out.println("Nous somme dans le cas où les pages font : "+ dm.getDbConfig().getPagesize() + "octets");
        System.out.println("Prenons ici la PageID(2,1) ,donc F2/ sbin / page 1");

            // Création de page / de bytebuffer / d'un scanner :
        PageId p = new PageId(2,1); // Création d'une page n°1 dans le fichier F2 bin
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) dm.getDbConfig().getPagesize()); // Création du ByteBuffer
        Scanner sc = new Scanner(System.in); // Création d'un scanner

            // Demande du caractère à mettre dans la page :
        System.out.println("Entrez le code Asci du caractère à mettre dans toute la page :");
        int choix = sc.nextInt(); // Récupération du choix de l'utilisateur
        byte [] tabBytes = new byte[(int) dm.getDbConfig().getPagesize()]; // Création d'un tableau de la taille d'une page contenant des bytes
        for(int i = 0; i < tabBytes.length; i++){
            tabBytes[i]= (byte) choix; // Ajout du code binaire du caractère dans le tableau
        }

            // Ajout des caractères dans le buffer :
        byteBuffer.put(tabBytes); // Ajout du tableau dans le buffer
        byteBuffer.flip(); // Retour de la tete de lecture du bytebuffer au debut de ce dernier
        System.out.println("Avant l'ecriture dans la page\n");
        affichagePage(dm,p); // Affichage de la page avant modification
        dm.WritePage(p,byteBuffer); // Ecrire dans la page
        System.out.println("Après l'ecriture dans la page\n");
        affichagePage(dm,p); // Affichage de la page modifier
    }

    public static void TestLecturePage(DiskManager dm){
            // Explication du test à l'utilisateur :
        System.out.println("************* Test de Read *************");
        System.out.println("Nous somme dans le cas où les pages font : "+ dm.getDbConfig().getPagesize() + "octets"); // Spécification de la taille d'une page
        System.out.println("Prenons ici la PageID(2,0) ,dcp F2 bin / page 0");

            // Création de page / de bytebuffer :
        PageId p = new PageId(2,0);
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) dm.getDbConfig().getPagesize()); // Création d'un ByteBuffer
        dm.ReadPage(p,byteBuffer); // Mise des valeurs de la page dans le bytebuffer

            // Affichage du contenue du bytebuffer :
        System.out.println("Affichons les bytes du ByteBuffer :");
        for (int i = 0; i < byteBuffer.capacity(); i++){
            System.out.print(byteBuffer.get()+" ");
        }
        byteBuffer.flip();// Retour de la tete de lecture du bytebuffer au debut de ce dernier

            //Affichage du contenue du bytebuffer traduit en caractère :
        System.out.println("\nAffichons en caractère le resultat");
        for (int i = 0; i < byteBuffer.capacity(); i++){
            System.out.print((char) byteBuffer.get()+" ");
        }
        byteBuffer.flip();// Retour de la tete de lecture du bytebuffer au debut de ce dernier

    }

    public static void TestSaveState(DiskManager dm){
      System.out.println("************* Test de Save State ************");
      dm.SaveState();
      System.out.println("Save State reussi, allé voir dans le fichier dm.save.json");

    }

    public static void TestLoadState(DiskManager dm){
        System.out.println("************* Test de Load State ************");

        System.out.println("Affichage pagesDesalloués : "+ dm.getPagesDesaloc().toString());
        System.out.println("**** Load State ****");
        dm.LoadState();
        System.out.printf("Chargement des données effectués avec succès");
        System.out.println("Affichage pagesDesalloués : "+ dm.getPagesDesaloc().toString());

    }

    public static void affichagePage(DiskManager dm, PageId pageId){
        String cheminFichier = dm.getDbConfig().getDbpath()+"/F"+pageId.getFileIdx()+".bin";
        File fichier = new File(cheminFichier);
        long position = dm.getDbConfig().getPagesize()*pageId.getPageIdx(); // positionnement dans le fichier au premier octet d'une page
        try {
            RandomAccessFile raf = new RandomAccessFile(fichier, "rw");
            raf.seek(position); // On se postionnne au bonne endroit de la page d'un fichier
            byte[] tabBytes = new byte[(int) dm.getDbConfig().getPagesize()]; // On crée un tableau de byte pour tous les octets
            raf.readFully(tabBytes); //ajoute d'un seul coup tous les octets de la page dans le tableau de bytes jusqu'à ce qu'il soit plein
            raf.close();
            System.out.println("la taille du tableau de bytes correspondants aux octets = "+tabBytes.length);
            System.out.println("Affichage Test de la liste de bytes : "+ Arrays.toString(tabBytes));
            System.out.print("Affichage en caractères : ");
            for (byte b :tabBytes) {
                System.out.print((char) b + " ");
            }
            System.out.println();

        }catch(IOException e ){
            e.printStackTrace();
        }
 // commit pour le V
    }
}
