import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DiskManagerTests {
    public static void main(String[] args) {
        DBConfig config;
        config = DBConfig.LoadDBConfig("src/main/json/file-config.json");

        DiskManager dM = new DiskManager(config);
        PageId p = new PageId(2,1);
        dM.DeallocPage(p);
        DiskManagerTests.TestEcriturePage(dM);
        DiskManagerTests.TestLecturePage(dM);


    }

    public void TestAllocPage(DiskManager dm){

    }

    public void TestDeallocPage(DiskManager dm){

    }

    public  static void TestEcriturePage(DiskManager dm){
        System.out.println("*********************************$$\n");
        System.out.println("Test Ecriture Page \n");
        System.out.println("Nous somme dans le cas où les pages font seulement 4 octets et non 4096");
        System.out.println("Prenon une page arbitrairement, à réecrire \n");
        System.out.println("On prend ici la PageID(2,1) ,dcp F2.bin / page 1");
        PageId p = new PageId(2,1);
        // création du ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) dm.getDbConfig().getPagesize());
        // là on remplit un tableau de bytes par des bytes 66
        // changer la valeur pour tester différents cas
        byte [] tabBytes = new byte[(int) dm.getDbConfig().getPagesize()];
        for(int i = 0; i < tabBytes.length; i++){
            tabBytes[i]= 66;
        }
        byteBuffer.put(tabBytes);
        byteBuffer.flip(); // pour revenir à la position 0, on veut écrire les valeurs depuis le debut du ByteBuffer
        System.out.println("Injectons 4 valeurs arbitraires en bytes pour remplir la page : 66 66 66 66 (rappel : 66='B') ");
        System.out.println("Avant l'ecriture dans la page\n");
        affichagePage(dm,p);
        dm.WritePage(p,byteBuffer);
        System.out.println("Après l'ecriture dans la page\n");
        affichagePage(dm,p);
    }

    public static void TestLecturePage(DiskManager dm){
        System.out.println("*********************************$$\n");
        System.out.println("Test Lecture Page \n");
        System.out.println("Nous somme dans le cas où les pages font seulement 4 octets et non 4096");
        System.out.println("Prenon une page arbitrairement, à lire \n");
        System.out.println("On prend ici la PageID(2,0) ,dcp F2.bin / page 1");

        PageId p = new PageId(2,0);
        // création du ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) dm.getDbConfig().getPagesize());
        dm.ReadPage(p,byteBuffer);
        // Affichhe les valeus du ByteBuffer

        System.out.println("Affichons les bytes du ByteBuffer :");
        for (int i = 0; i < byteBuffer.capacity(); i++){
            System.out.print(byteBuffer.get()+" ");
        }
        byteBuffer.flip();
        System.out.println("\nAffichons en caractère le resultat");
        for (int i = 0; i < byteBuffer.capacity(); i++){
            System.out.print((char) byteBuffer.get()+" ");
        }
        byteBuffer.flip();






    }

    public void TestSaveState(DiskManager dm){

    }

    public void TestLoadState(DiskManager dm){

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

    }
}
