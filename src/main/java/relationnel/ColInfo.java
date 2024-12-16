package relationnel;

public class ColInfo {
    private String nomColonne;
    private String typeColonne;
    private int tailleColonne;
    //
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

    public String toString() {
        return "Nom colonne : " + this.nomColonne + ", Type colonne: " + this.typeColonne + ", Taille: " + this.tailleColonne;
    }

    public void affiche_ColInfo(){
        System.out.println("Nom colonne : " + nomColonne);
        System.out.println("Type : " + typeColonne);
        System.out.println("taille Colonne : " + tailleColonne);
    }

}
