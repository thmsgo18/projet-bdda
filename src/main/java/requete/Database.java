package requete;

import relationnel.Relation;

import java.util.ArrayList;
import java.util.List;

public class Database {//
    private String databaseNom;
    private List<Relation> tables;

    public Database(String databaseNom) {
        this.databaseNom = databaseNom;
        this.tables = new ArrayList<Relation>();
    }
    public Database(String databaseNom, List<Relation> tables) {
        this.databaseNom = databaseNom;
        this.tables = tables;
    }

    public void addTable(Relation table) {
        tables.add(table);
    }

    public List<Relation> getTables() {
        return tables;
    }

    public void setTables(List<Relation> tables) {
        this.tables = tables;
    }

    public Relation getTable(String tableName) {
        for (Relation table : tables) {
            if(tableName.equals(table.getNomRelation())){
                return table;
            }
        }
        return null;
    }



}
