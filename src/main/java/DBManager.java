import java.util.HashMap;

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
        this.databases.get(this.courantDatabase).getTables().remove(nomTable);
    }

    public void RemoveDatabase (String nomBdd){

    }

    public void RemoveTablesFromCurrentDatabase (){

    }

    public void RemoveDatabases (){

    }

    public void ListDatabases (){

    }

    public void ListTablesInCurrentDatabase (){

    }

    public void SaveState(){

    }



}
