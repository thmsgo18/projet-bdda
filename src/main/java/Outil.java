import espaceDisque.DiskManager;

import java.io.File;

public class Outil {

    // Sert uniquement pour les test, ne pas ublier de remettre la page courante à 0.
    public static void SuprimeTousFichier(DiskManager diskManager) {
        // Spécifiez le chemin du répertoire contenant les fichiers à supprimer
        String cheminFichier = diskManager.getDbConfig().getDbpath()+"/BinData";// Initialisation du chemin du fichier
        File directory = new File(cheminFichier);

        // Vérifiez si le répertoire existe et s'il s'agit bien d'un dossier
        if (directory.exists() && directory.isDirectory()) {
            // Liste tous les fichiers dans le répertoire
            File[] files = directory.listFiles();

            // Vérifie que la liste de fichiers n'est pas vide
            if (files != null) {
                for (File file : files) {
                    // Supprime chaque fichier
                    if (file.isFile()) { // Vérifie que c'est bien un fichier, pas un sous-dossier
                        if (file.delete()) {
                            System.out.println("Fichier supprimé : " + file.getName());
                        } else {
                            System.out.println("Échec de suppression : " + file.getName());
                        }
                    }
                }
            } else {
                System.out.println("Le répertoire est vide ou une erreur est survenue.");
            }
        } else {
            System.out.println("Le répertoire spécifié n'existe pas ou n'est pas un dossier.");
        }
    }




}
