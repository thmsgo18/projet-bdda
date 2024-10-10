import java.util.ArrayList;
import java.util.List;

public class Record {

    List<Object> valeurs;

    public Record(List<Object> valeurs) {
        this.valeurs = valeurs;
    }

    public Record(){
        valeurs = new ArrayList<Object>();
    }

    public Object getValeur(int i) {
        return valeurs.get(i);
    }

    public void setValeur(int i, Object valeur) {
        valeurs.set(i, valeur);
    }

    public String toString(){
        StringBuilder retour = new StringBuilder();
        retour.append("[");
        for (Object valeur : valeurs) {
            retour.append(valeur);
            retour.append(",");
        }
        retour.append("]");
        return retour.toString();
    }
}
