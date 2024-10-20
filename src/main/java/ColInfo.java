public class ColInfo {
    private String nomColonne;
    private String typeColonne;
    private int tailleColonne;

    public ColInfo(String nomColonne, String typeColonne, int tailleColonne) {
        this.nomColonne = nomColonne;
        this.typeColonne = typeColonne;
        this.tailleColonne = tailleColonne;
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
