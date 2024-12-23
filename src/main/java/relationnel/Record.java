package relationnel;

import java.util.ArrayList;
import java.util.List;

public class Record {

    private List<Object> tuple; // Liste des valeurs du tuple

    public Record(ArrayList<Object> valeurs) {
        this.tuple = valeurs;
    }

    public Record(){
        tuple = new ArrayList<Object>();
    }
//
    public Object getValeurTuple(int i) {
        return tuple.get(i);
    }

    public void setValeurTuple(int i, Object valeur) {
        tuple.set(i, valeur);
    }
    public List<Object> getTuple(){
        return tuple;
    }

    public void setTuple(List<Object> tuple) {
        this.tuple = tuple;
    }

    public void addTuple(List<Object> tuple) {
        this.tuple.addAll(tuple);
    }


    public void ajouteValeurTuple(Object valeur) {
        tuple.add(valeur);
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
