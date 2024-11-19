import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DBManagerTest {

    public static void main(String [] args){
        DBConfig config = DBConfig.LoadDBConfig("src/main/json/file-config.json");
        DiskManager diskManager = new DiskManager(config);
        DBManager dbmanag = new DBManager(config);
        dbmanag.CreateDatabase("Université");
        dbmanag.SetCurrentDatabase("Université");
        ColInfo cI1 = new ColInfo("Nom", "CHAR", 12);
        ColInfo cI2 = new ColInfo("Prenom", "CHAR", 6);
        ColInfo cI3 = new ColInfo("Age", "INT", 4);
        ColInfo cI4 = new ColInfo("Secteur", "CHAR", 12);
        ColInfo cI5 = new ColInfo("Lettre", "CHAR", 6);
        ColInfo cI6 = new ColInfo("Nbr Places", "INT", 4);
        BufferManager bufferManager = new BufferManager(config,diskManager);
        PageId headerPageId =ajouteHeaderPage(diskManager,bufferManager);
        List<ColInfo> listeColonnesInfo= new ArrayList<>();
        listeColonnesInfo.add(cI1);
        listeColonnesInfo.add(cI2);
        listeColonnesInfo.add(cI3);
        List<ColInfo> listeColonnesInfo2= new ArrayList<>();
        listeColonnesInfo2.add(cI4);
        listeColonnesInfo2.add(cI5);
        listeColonnesInfo2.add(cI6);
        dbmanag.AddTableToCurrentDatabase(new Relation("Etudiant", 3,headerPageId, diskManager, bufferManager, listeColonnesInfo));
        dbmanag.AddTableToCurrentDatabase(new Relation("Salle", 3,headerPageId, diskManager, bufferManager, listeColonnesInfo2));
        dbmanag.SaveState();
    }




    public static PageId ajouteHeaderPage(DiskManager diskManager,BufferManager bufferManager) {
        System.out.println("**************  Initialisation d'une headerPage   *********************");
        // On initialisie les valeurs de la header page, le nombre de page est à 0 au début, suivi de l'emplacement de l'octet pour écrire une nouvelle case de page de données
        PageId headerPage = diskManager.AllocPage(); // On alloue une page disponible

        ByteBuffer buff =bufferManager.GetPage(headerPage);

        System.out.println("La header page est placé en "+headerPage);

        int position= (int) ((int) headerPage.getPageIdx()*diskManager.getDbConfig().getPagesize());


        String cheminFichier = diskManager.getDbConfig().getDbpath()+"/F"+headerPage.getFileIdx()+".bin"; // Chemin du fichier à lire
        File fichier = new File(cheminFichier);
        if(fichier.exists()) {
            try {
                RandomAccessFile raf = new RandomAccessFile(fichier, "rw"); // Ouverture du fichier
                raf.seek(position);  // Positionnement sur le premier octet de la page voulu

                //System.out.println(Arrays.toString(buff.array()));

                raf.seek(position);
                System.out.println(raf.readInt());
                System.out.println(raf.readInt());
                raf.close();
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                bufferManager.FreePage(headerPage,false);
            }
        }else{
            System.out.println("Vous tentez de lireun fichier qui n'existe pas");
        }

        return headerPage;

    }
}
