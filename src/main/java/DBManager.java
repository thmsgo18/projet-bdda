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
            System.out.println("Le nom de la DataBase n'est pas contenue dans la liste des Databases");
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
            System.out.println("La table n'existe pas dans la Database courante");
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

    }



}
