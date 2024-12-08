package requete;

import inter.IRecordIterator;
import relationnel.ColInfo;
import relationnel.Record;
import relationnel.Relation;

import java.util.ArrayList;
import java.util.List;

public class ProjectOperator implements IRecordIterator<Record> {

    private RelationScanner relationScanner;
    private int indiceCourant;
    private Relation table;
    private String alias;
    private List<Integer> listeIndiceColonnes;
    private List<String> listeColonnes;
// plus tard lorsqu'on fera les jointures ce serait peut etre bien de faire un  Map entre la table et son alias
    public ProjectOperator(Relation table,String alias, RelationScanner relationScanner, List<String> listeColonnes) {
        this.relationScanner = relationScanner;
        this.indiceCourant = 0;
        this.table = table;
        this.alias = alias;
        this.listeIndiceColonnes = new ArrayList<>();
        this.listeColonnes = listeColonnes;
    }

    public Record GetNextRecord(){
        return relationScanner.getFliteredRecord();
    }

    public void Close(){
        // pas d'idée pour l'instant
    }

    public void Reset(){
        indiceCourant=0;
    }



    public void affiche(){
        if(listeColonnes.get(0).equals("*")){
            afficheEtoile();
        }else{
            afficheColonnes();
        }
    }

    public void afficheColonnes(){
        int i,nombreRecords=0; // on commence à 1 à cuase du premier affichage
        Record recordCourant = GetNextRecord();
        trouveIndicesColonnes();
        while(recordCourant != null){
            i=0;
            System.out.print(recordCourant.getTuple().get(listeIndiceColonnes.get(i++))); // on affiche la valeur lié à la premiere colonne désigné dans la ligne de colonnes
            while(i<listeIndiceColonnes.size()){
                System.out.print(" ; "+recordCourant.getTuple().get(listeIndiceColonnes.get(i))); // on affiche la valeur lié à la  colonne désigné dans la ligne de colonnes
                i++;
            }
            System.out.println(".");
            recordCourant = GetNextRecord();
            nombreRecords++;
        }
        System.out.println("Total records="+nombreRecords);
    }

    public void afficheEtoile(){
        int i,nombreRecords=0; // on commence à 1 à cuase du premier affichage
        Record recordCourant = GetNextRecord();
        while(recordCourant != null){
            //System.out.println("PROJECT OPERATOR : AFFICHE ETOILE : recordCourant ="+recordCourant);
            i=0;
            System.out.print(recordCourant.getTuple().get(i++)); // on affiche la valeur lié à la premiere colonne désigné dans la ligne de colonnes
            while(i<recordCourant.getTuple().size()){
                System.out.print(" ; "+recordCourant.getTuple().get(i)); // on affiche la valeur lié à la  colonne désigné dans la ligne de colonnes
                i++;
            }
            System.out.println(".");
            recordCourant = GetNextRecord();
            nombreRecords++;
        }
        System.out.println("Total records="+nombreRecords);
    }


    private void trouveIndicesColonnes(){
        String nomColonne;
        int indiceColonne;
        for(String colonne : listeColonnes){
            nomColonne = colonne.replace(alias+".",""); // on garde seulement le nom de la colonne
            indiceColonne= trouveIndiceColonne(table,nomColonne);
            System.out.println("PROJECT OPERATOR : trouveIndicesColonnes : nom de Colonne à trouver :"+nomColonne);
            if (indiceColonne != -1){
                listeIndiceColonnes.add(indiceColonne);
            }else{
                System.out.println("PROJECT OPERATOR : trouveIndicesColonnes : ERREUR : la colonne n'a pas été trouvé");
            }

        }
    }

    private int trouveIndiceColonne (Relation table,String nomColonne){
        int indiceColonne =-1;
        for(int i =0; i< table.getNbColonnes();i++){
            if( nomColonne.equals( table.getColonnes().get(i).getNomColonne() ) ){ // on vérifie si le nom de la colonne est le meme
                indiceColonne = i;
            }
        }
        return indiceColonne;
    }
}
