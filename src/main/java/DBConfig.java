import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.json.*;

public class DBConfig{

    private String dbpath; //chemin d'accès sous forme de string
    private double pagesize;
    private double filesize;

    public DBConfig(String dbpath,double pagesize, double filesize){ //Constructeur

        this.dbpath = dbpath;
        this.pagesize = pagesize;
        this.filesize = filesize;
    }


    public static DBConfig LoadDBConfig(String fichier_config){ //méthode qui va lire fichier et le transformé en Json pour acceder à la valeur de la clé dbpath
        try{
            FileReader fr = new  FileReader(fichier_config);//Utilisation des classes FileReader et BufferReader pour lire le fichier
            BufferedReader bfr = new BufferedReader(fr);
            StringBuilder sb =new StringBuilder();
            String ligne ;
            while((ligne = bfr.readLine())!=null){ //line vaut la ligne du fichier(ex: si fichier contient une ligne : "dbpath : ././DB" alors line vaut dbpath. boucle continue jusqu'a plus de ligne.
                sb.append(ligne); // on ajoute la ligne au StringBuffer car StringBuffer est plus flexible d'utilisation.
            }
            bfr.close();//Fermeture de la lecture du fichier
            JSONObject js = new JSONObject(sb.toString());//Creer une instance de JsonObject pour recuperer la ligne qui sera transformer en Json
            return new DBConfig(js.getString("dbpath"),0.0,0.0);//recupere la valeur de la clé dbpath et retourne une nouvelle instance de DBConfig.


        }catch(IOException io){ //Si fichier marche pas donc retourner nul
            System.out.println("Nullllllll");
            io.printStackTrace();
            return null;
        }

    }

    public String getDbpath(){ //getteur
        return dbpath;
    }
    public void setDbpath(String dbpath){//setteur
        this.dbpath = dbpath;
    }
    public double getPagesize(){
        return pagesize;
    }
    public void setPagesize(double pagesize){
        this.pagesize = pagesize;
    }
    public double getFilesize(){
        return filesize;
    }
    public void setFilesize(double filesize){
        this.filesize = filesize;
    }

}