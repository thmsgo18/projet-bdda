public class ColInfo {
    private String nomColonne;
    private String typeColonne;
    private int tailleColonne;

    public ColInfo(String nomColonne, String typeColonne, int tailleColonne) {
        this.nomColonne = nomColonne; // Nom de la colonne
        this.typeColonne = typeColonne; // Type des variables de la colonne
        this.tailleColonne = tailleColonne; // Taille de la colonne
    }

    public String getNomColonne() {
        return nomColonne;
    }

    public String getTypeColonne() {
        return typeColonne;
    }

    public int getTailleColonne() {
        return tailleColonne;
    }

}
