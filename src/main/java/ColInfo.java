public class ColInfo {
    private String nomColonne;
    private String typeColonne;

    public ColInfo(String nomColonne, String typeColonne) {
        this.nomColonne = nomColonne;
        this.typeColonne = typeColonne;
    }

    public ColInfo(String nomColonne) {
        this.nomColonne = nomColonne;
        this.typeColonne = "";
    }

    public String getNomColonne() {
        return nomColonne;
    }

    public String getTypeColonne() {
        return typeColonne;
    }
}
