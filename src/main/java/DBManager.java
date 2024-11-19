import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class DBManager {
    private DBConfig dbConfig;
    private HashMap<String, Database> databases;
    private String courantDatabase;

    public DBManager(DBConfig dbConfig) {
        this.databases = new HashMap<String, Database>();
        this.dbConfig = dbConfig;
    }

    public void CreateDatabase(String nomBdd){
        this.databases.put(nomBdd, new Database(nomBdd));
    }

    public void SetCurrentDatabase(String nomBdd){
        if (this.databases.containsKey(nomBdd)){
            this.courantDatabase = nomBdd;
        }else{
            System.out.println("ERREUR : DBMANAGER : SET_CURRENT_DATABASE : Le nom de la DataBase n'est pas contenue dans la liste des Databases");
        }
    }

    public void AddTableToCurrentDatabase (Relation tab){
        this.databases.get(this.courantDatabase).addTable(tab);
    }

    public Relation GetTableFromCurrentDatabase (String nomTable){
        return this.databases.get(this.courantDatabase).getTable(nomTable);
    }

    public void RemoveTableFromCurrentDatabase (String nomTable){
        List<Relation> tablesDb = this.databases.get(this.courantDatabase).getTables();
        if(this.databases.get(this.courantDatabase).getTable(nomTable) != null){
            tablesDb.remove(this.databases.get(this.courantDatabase).getTable(nomTable));
            this.databases.get(this.courantDatabase).setTables(tablesDb);
        }else{
            System.out.println("ERREUR : DBMANAGER : REMOVE_TABLE_FROM_CURRENT_DATABASE La table n'existe pas dans la Database courante");
        }
    }

    public void RemoveDatabase (String nomBdd){
        for(String key : this.databases.keySet()){
            if(nomBdd.equals(key)){
                this.databases.remove(key);
            }
        }
    }

    public void RemoveTablesFromCurrentDatabase (){
        this.databases.get(this.courantDatabase).setTables(null);
    }

    public void RemoveDatabases (){
        this.databases = null;

    }

    public void ListDatabases (){
        for(String key : this.databases.keySet()){
            System.out.println("Nom de la base de données : "+key);
        }
    }

    public void ListTablesInCurrentDatabase (){
        for(Relation r: this.databases.get(this.courantDatabase).getTables()){
            System.out.println(r.toString());
        }
    }

    public void SaveState(){
        String chemin = dbConfig.getDbpath()+"/../databases.save.json";
        try{
            FileWriter fw = new FileWriter(chemin);
            BufferedWriter bfw = new BufferedWriter(fw);
            bfw.write("{"); // ouverture de la première accolade
            bfw.newLine(); // revient à la ligne
            bfw.write("    \"Bases de données\":{"); // ouverture accolade Bases de données
            bfw.newLine(); // revient à la ligne
            int j=0;
            for(String key : this.databases.keySet()){
                bfw.write("        \""+j+"\":{"); // ouverture accolade de la base donnée
                j++;
                bfw.newLine();
                bfw.write("             \"Nom BD\": \""+key+"\",");
                bfw.newLine();
                bfw.write("             \"info BD\":{");// ouverture accolade info de la base donnée
                bfw.newLine();
                bfw.write("                \"Relations\":{");
                int k=0;
                bfw.newLine();
                for(Relation r : this.databases.get(key).getTables()){
                    bfw.write("                     \""+k+"\":{");
                    k++;
                    bfw.newLine();
                    bfw.write("                         \"Nom relation\": \"" + r.getNomRelation()+"\",");
                    bfw.newLine();
                    bfw.write("                         \"Header page\": " + r.getHeaderPageId().toString()+",");
                    bfw.newLine();
                    bfw.write("                         \"Nombre colonnes\": " + r.getNbColonnes()+",");
                    bfw.newLine();
                    bfw.write("                         \"Colonnes\":{");
                    bfw.newLine();
                    for(int i=0; i<r.getNbColonnes();i++){
                        bfw.write("                             \""+i+"\":{");
                        bfw.newLine();
                        bfw.write("                             \"Nom Colonne\": \""+r.getColonnes().get(i).getNomColonne()+"\",");
                        bfw.newLine();
                        bfw.write("                             \"Type Colonne\": \""+r.getColonnes().get(i).getTypeColonne()+"\",");
                        bfw.newLine();
                        bfw.write("                             \"Taille Colonne\": \""+r.getColonnes().get(i).getTailleColonne()+"\"");
                        bfw.newLine();
                        if(i<r.getNbColonnes()-1){ // Fermeture accolade n° Colonne
                            bfw.write("                             },");
                        }else{
                            bfw.write("                             }");
                        }
                        bfw.newLine();
                    }
                    bfw.write("                         }");// fermeture accolade Colonnes
                    bfw.newLine();
                    if(k<=this.databases.get(key).getTables().size()-1){ // fermeture accolade n° Relation
                        bfw.write("                     },");
                    }else{
                        bfw.write("                     }");
                    }
                    bfw.newLine();
                }
                bfw.write("                }");
                bfw.newLine();
                bfw.write("             }"); // fermeture accolade Info BD
                bfw.newLine(); // revient à la ligne
            }
            bfw.write("        }"); // fermeture accolade n° Base de données
            bfw.newLine();
            bfw.write("    }"); // fermeture accolade Base de données
            bfw.newLine();
            bfw.write("}"); // fermeture de de la première accolade
            bfw.close();
        }catch (IOException e) {
            System.out.println("DBManager : SAVE STATE : Le fichier n'a pas pu être sauvegarder");
            e.printStackTrace();
        }
    }


}
