package requete;
import relationnel.Record;
//
public class RecordPrinter {
    private Record record;
    public RecordPrinter(Record record ) {
        this.record = record;
    }

    public void affiche() {
        int i=0;
        System.out.print(record.getTuple().get(i++)); // on affiche la valeur lié à la premiere colonne désigné dans la ligne de colonnes
        while(i<record.getTuple().size()){
            System.out.print(" ; "+record.getTuple().get(i)); // on affiche la valeur lié à la  colonne désigné dans la ligne de colonnes
            i++;
        }
        System.out.println(".");
    }
}
