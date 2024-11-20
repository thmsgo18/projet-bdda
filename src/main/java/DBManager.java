import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileReader;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBManager {
    private DBConfig dbConfig;
    private DiskManager diskManager;
    private BufferManager bufferManager;
    private HashMap<String, Database> databases;
    private String courantDatabase;

    public DBManager(DBConfig dbConfig, DiskManager diskManager, BufferManager bufferManager) {
        this.databases = new HashMap<String, Database>();
        this.dbConfig = dbConfig;
        this.diskManager = diskManager;
        this.bufferManager = bufferManager;
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
        this.databases.clear();

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
                    bfw.write("                     \""+k+"\":{"); // ouverture occulade n° relation
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
                        bfw.write("                                 \"Nom Colonne\": \""+r.getColonnes().get(i).getNomColonne()+"\",");
                        bfw.newLine();
                        bfw.write("                                 \"Type Colonne\": \""+r.getColonnes().get(i).getTypeColonne()+"\",");
                        bfw.newLine();
                        bfw.write("                                 \"Taille Colonne\": "+r.getColonnes().get(i).getTailleColonne());
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
                    bfw.write("                     },");
                    bfw.newLine();
                }
                bfw.write("                     \"Nbr Relations\": "+k);
                bfw.newLine();
                bfw.write("                }"); // fermeture accolade Relations
                bfw.newLine();
                bfw.write("             }"); // fermeture accolade Info BD
                bfw.newLine(); // revient à la ligne
            }
            bfw.write("        },"); // fermeture accolade n° Base de données
            bfw.newLine();
            bfw.write("        \"Nbr BD\": "+(j));
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

    public void LoadState(){
        String chemin = dbConfig.getDbpath()+"/../databases.save.json";
        try{
            FileReader fr = new  FileReader(chemin); //Utilisation des classes FileReader et BufferReader pour lire le fichier
            BufferedReader bfr = new BufferedReader(fr);
            StringBuilder sb =new StringBuilder();
            String ligne;
            while((ligne = bfr.readLine())!=null){ //line vaut la ligne du fichier(ex: si fichier contient une ligne : "dbpath : ././DB" alors line vaut dbpath. boucle continue jusqu'a plus de ligne.
                sb.append(ligne); // on ajoute la ligne au StringBuffer car StringBuffer est plus flexible d'utilisation.
            }
            bfr.close();//Fermeture de la lecture du fichier

            JSONObject js = new JSONObject(sb.toString());//Creer une instance de JsonObject pour recuperer la ligne qui sera transformer en Json
            for(int bd=0; bd< js.getJSONObject("Bases de données").getInt("Nbr BD"); bd++){ // Parcours de toutes les bases de données
                String nomBD = js.getJSONObject("Bases de données").getJSONObject(String.valueOf(bd)).getString("Nom BD"); // Nom de la base de donnée
                this.CreateDatabase(nomBD); // Création d'un DB dans DBManager
                this.SetCurrentDatabase(nomBD); // La DB créée devient la CurrentDB
                List<Relation> tables = new ArrayList<Relation>(); // Création de la liste des tables de la DB
                for(int r=0; r<js.getJSONObject("Bases de données").getJSONObject(String.valueOf(bd)).getJSONObject("info BD").getJSONObject("Relations").getInt("Nbr Relations");r++){ // Parcours de toute les relations de la DB
                    String nomRelation = js.getJSONObject("Bases de données").getJSONObject(String.valueOf(bd)).getJSONObject("info BD").getJSONObject("Relations").getJSONObject(String.valueOf(r)).getString("Nom relation"); // Nom de la relation
                    PageId headerPage = new PageId(js.getJSONObject("Bases de données").getJSONObject(String.valueOf(bd)).getJSONObject("info BD").getJSONObject("Relations").getJSONObject(String.valueOf(r)).getJSONArray("Header page").getInt(0),js.getJSONObject("Bases de données").getJSONObject(String.valueOf(bd)).getJSONObject("info BD").getJSONObject("Relations").getJSONObject(String.valueOf(r)).getJSONArray("Header page").getInt(1)); // Header Page de la relation
                    int nbrColonnes = js.getJSONObject("Bases de données").getJSONObject(String.valueOf(bd)).getJSONObject("info BD").getJSONObject("Relations").getJSONObject(String.valueOf(r)).getInt("Nombre colonnes"); //Nombre de colonnes de la relation
                    List<ColInfo> listeColonnesInfo= new ArrayList<>(); // Création de la liste des ColInfo des colonnes de la relation
                    for(int c=0; c<nbrColonnes;c++){ // Parcours de toute les colonnes
                        String nomColonne = js.getJSONObject("Bases de données").getJSONObject(String.valueOf(bd)).getJSONObject("info BD").getJSONObject("Relations").getJSONObject(String.valueOf(r)).getJSONObject("Colonnes").getJSONObject(String.valueOf(c)).getString("Nom Colonne"); // Nom de la colonne
                        String typeColonne = js.getJSONObject("Bases de données").getJSONObject(String.valueOf(bd)).getJSONObject("info BD").getJSONObject("Relations").getJSONObject(String.valueOf(r)).getJSONObject("Colonnes").getJSONObject(String.valueOf(c)).getString("Type Colonne"); // Type de la colonne
                        int tailleColonne = js.getJSONObject("Bases de données").getJSONObject(String.valueOf(bd)).getJSONObject("info BD").getJSONObject("Relations").getJSONObject(String.valueOf(r)).getJSONObject("Colonnes").getJSONObject(String.valueOf(c)).getInt("Taille Colonne"); // Taille de la colonne
                        ColInfo cI = new ColInfo(nomColonne, typeColonne, tailleColonne); // Création de la ColInfo de la colonne
                        listeColonnesInfo.add(cI); // Ajout, de la ColInfo cI, à la liste des ColInfo des colonnes de la relation
                    }
                    Relation relation = new Relation(nomRelation, nbrColonnes, headerPage, this.diskManager, this.bufferManager,listeColonnesInfo); // Création de la relation
                    this.AddTableToCurrentDatabase(relation); // Ajout de la relation à la DB
                }
            }

        }catch(IOException io){
            io.printStackTrace();
        }
    }


}
