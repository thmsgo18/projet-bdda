import java.util.ArrayList;
import java.util.List;

public class Record {

    List<Object> tuple;

    public Record(ArrayList<Object> valeurs) {
        this.tuple = valeurs;
    }

    public Record(){
        tuple = new ArrayList<Object>();
    }

    public Object getValeur(int i) {
        return tuple.get(i);
    }

    public void setValeur(int i, Object valeur) {
        tuple.set(i, valeur);
    }

    public String toString(){
        StringBuilder retour = new StringBuilder();
        retour.append("[");
        for (Object valeur : tuple) {
            retour.append(valeur);
            retour.append(",");
        }
        retour.append("]");
        return retour.toString();
    }
}
