import java.util.Map;
import java.util.StringTokenizer;

public class Condition {
    private Relation table;
    private String alias;
    private String operateur;
    private Object constante;
    private int indiceColonne;
    private int indiceColonne2;
    // Il sera important de sauvegarder les emplacements de l'alias et de la constante pour effecuter la verifaction de la condition (ex: 2<3 mais pas 3<2)
    private int placementAlias;
    private int placementAlias2; // cas où l'on compare un élément de la table à un autre élément de la table  (on sauvegarde le placment de la 2ème valeur de la table  par rapport à l'operateur (à gauche ou à droite)

    private int placementConstante; // cas où l'on compare un élément de la table à une constante (on sauvegarde le placment de la cosntante par rapport à l'operateur (à gauche ou à droite)
    // constructeur dans le cas où l'on compare  une valeur d'un record à une autre valeur de ce record
    public Condition (Relation table,String alias,String operateur,int indiceColonne, int indiceColonne2 ,int placementAlias,int placementintAlias2) {
        this.table = table;
        this.alias = alias;
        this.operateur = operateur;
        this.indiceColonne = indiceColonne;
        this.indiceColonne2 = indiceColonne2;
        this.placementAlias = placementAlias;
        this.placementAlias2 = placementAlias2;
    }
    // constructeur dans le cas où l'on compare une valeur d'un record à une constante
    public Condition (Relation table,String alias,String operateur,Object constante,int indiceColonne ,int placementAlias,int placementConstante) {
        this.table = table;
        this.alias = alias;
        this.operateur = operateur;
        this.constante = constante;
        this.indiceColonne = indiceColonne;
        this.placementAlias = placementAlias;
        this.placementConstante = placementConstante;
    }

    // méthode permettant d'initialiser une condition

    public static Condition ajouteCondition(Relation table, String alias, String condition){
        String operateur = trouveOperateur(condition);
        System.out.println("OPERATEUR :" + operateur);
        Object constanteL=null;
        StringTokenizer tokenizer = new StringTokenizer(condition,operateur);
        int emplacementAlias = -1,emplacementAlias2 = -1, emplacementConstante = -1;

        int indiceColonne=-1, indiceColonne2= -1;

        String terme1,terme2;
        terme1 = tokenizer.nextToken();
        terme2 = tokenizer.nextToken();

        // 1er cas : L'alias est placé à gauche dans l'operation et la constante à droite retourner 2ème constructeur
        if ( ( terme1.contains(alias+".") ) && ( !terme2.contains(alias+".") ) ){
            terme1 = terme1.replace(alias+".", ""); //Il ne restera que le nom de la colonne
            // On cherche l'indice du nom de colonne présent dans le terme1
            indiceColonne=chercheIndiceColonne(table,terme1);

            if(indiceColonne != -1) { // on vérfir qu'on a bien trouver la colonne correspondante
                constanteL = trouveTypeConstante(table, indiceColonne, terme2);
            }
            emplacementAlias = 1;
            emplacementConstante = 2;

            System.out.println("table = "+table.getNomRelation()+" Alias = "+alias+" operateur = "+operateur+" indiceColonne = "+indiceColonne+ "indiceColonne2 = "+indiceColonne2+ " emplacementAlias = "+emplacementAlias+"emplacementAlias2 = "+emplacementAlias2+" emplacementConstante = "+emplacementConstante);
            return new Condition(table,alias,operateur,constanteL,indiceColonne,emplacementAlias,emplacementConstante); // on retourne le second constructeur

            // 2ème cas : l'alias est placé à droite dans l'operation et la constante à gauche retourner 2ème constructeur
        }else if( ( !terme1.contains(alias+".") ) && (terme2.contains(alias+".") ) ){

            terme2 = terme2.replace(alias+".", "");
            // On cherche l'indice du nom de colonne présent dans le terme1
            indiceColonne = chercheIndiceColonne(table,terme2);

            if(indiceColonne != -1){  // on vérfir qu'on a bien trouver la colonne correspondante
                constanteL = trouveTypeConstante(table,indiceColonne,terme1);
            }
            emplacementAlias = 2;
            emplacementConstante = 1;

            System.out.println("table = "+table.getNomRelation()+" Alias = "+alias+" operateur = "+operateur+" indiceColonne = "+indiceColonne+ "indiceColonne2 = "+indiceColonne2+ " emplacementAlias = "+emplacementAlias+"emplacementAlias2 = "+emplacementAlias2+" emplacementConstante = "+emplacementConstante);
            return new Condition(table,alias,operateur,constanteL,indiceColonne,emplacementAlias,emplacementConstante); // on retourne le second constructeur


            //3ème cas Les 2 termes contienne l'alias faudra retourner le permier constructeur
        }else if( ( terme1.contains(alias+".") ) && ( terme2.contains(alias+".") ) ){

            terme1 = terme1.replace(alias+".", "");
            indiceColonne = chercheIndiceColonne(table,terme1);

            terme2 = terme2.replace(alias+".", "");
            indiceColonne2 = chercheIndiceColonne(table,terme2);

            emplacementAlias = 1;
            emplacementAlias2 = 2;

            System.out.println("table = "+table.getNomRelation()+" Alias = "+alias+" operateur = "+operateur+" indiceColonne = "+indiceColonne+ "indiceColonne2 = "+indiceColonne2+ " emplacementAlias = "+emplacementAlias+"emplacementAlias2 = "+emplacementAlias2+" emplacementConstante = "+emplacementConstante);

            return new Condition(table,alias,operateur,indiceColonne,indiceColonne2,emplacementAlias,emplacementAlias2); // on retourne le premier constructeur

            //Devrait être impossible
        }else{
            System.out.println("table = "+table.getNomRelation()+" Alias = "+alias+" operateur = "+operateur+" indiceColonne = "+indiceColonne+ "indiceColonne2 = "+indiceColonne2+ " emplacementAlias = "+emplacementAlias+"emplacementAlias2 = "+emplacementAlias2+" emplacementConstante = "+emplacementConstante);
            System.out.println("CONDITION : AJOUTE CONDITION : Aucun termes ne semble contenir un alias référençant une colonne ");
            return null;
        }


    }
    //Cherche l'operateur dans le string condition
    private static String trouveOperateur(String condition) {
        String operateur1 ;
        if ( condition.contains("<=") ) {
            operateur1 = "<=";
        }else if (condition.contains(">=") ){
            operateur1 = ">=";
        }else if (condition.contains("<>") ){
            operateur1 = "<>";
        }else if (condition.contains("=") ){
            operateur1 = "=";
        }
        else if (condition.contains("<") ){
            operateur1 = "<";
        }
        else if (condition.contains(">") ){
            operateur1 = ">";
        }
        else {
            operateur1 = "";
            System.out.println("ERREUR");
        }
        return operateur1;
    }
    // Méthode permattant de trouver le type de la constante auquel on compare un élément de la table
    private static Object trouveTypeConstante(Relation table,int indiceColonne,String terme) {
        String typeConstante;
        typeConstante=table.getColonnes().get(indiceColonne).getTypeColonne();
        switch(typeConstante){
            case "INT": return (float) Double.parseDouble(terme); // transtypage pour simplifier les comparaisons
            case "REAL": return (float) Double.parseDouble(terme);
            case "VARCHAR": return terme;
            case "CHAR": return terme;
            default: return null;
        }
    }

    private static int chercheIndiceColonne(Relation table, String terme){
        int indiceColonne= -1;
        for(int i=0;i<table.getNbColonnes();i++){
            if(table.getColonnes().get(i).getNomColonne().equals(terme)) {
                indiceColonne = i;
                break;
            }
        }
        return indiceColonne;
    }

    // Méthode importante permmettant de vérfier si un record rempli la condition

    // il faudra prendre en compte le cas où l'on compare un alias à un autre

    public boolean estRespecter(Record record){

        if(constante!=null){ // 1er cas où l'on compare une valeur d'un record à une constante
            System.out.println("ValRecord = "+record.getTuple().get(indiceColonne)+"CONSTANTE  = "+constante);
            System.out.println("Test affichage type constante = "+constante.getClass().getName());
            // faire une fonction pour délimiter le code plus tard
            if( constante instanceof String ) {
                return estRespecterConstanteString(record);
            }
            else if( constante instanceof Double || constante instanceof Float ) {
                return estRespecterConstanteNombre(record);
            }
            else {
                System.out.println("CONDITION : EST RESPECTER : ERREUR : La constante n'a pas type de reconnu parmi ( String, Integer et Float");
                return false;
            }
        }else{ // 2ème cas où on l'on compare 2 valeurs du record

            System.out.println("valRecord1 = "+record.getTuple().get(indiceColonne)+" valRecord2 = "+record.getTuple().get(indiceColonne2));
            // on suppose que l'on compare un string à un autre un int à
            if ( record.getTuple().get(indiceColonne) instanceof String ) {
                return estRespecterConstanteString(record);
            }
            else if ( record.getTuple().get(indiceColonne) instanceof Integer ) {
                return estRespecterValeursRecordNombres(record);
            }
            else if ( record.getTuple().get(indiceColonne) instanceof Float ) {
                return estRespecterValeursRecordNombres(record);
            }
            else {
                System.out.println("CONDITION : EST RESPECTER : ERREUR :le record a un type inconnu "+record.getTuple().get(indiceColonne).getClass().getName());
                return false;
            }

        }


    }



    public boolean estRespecterConstanteNombre(Record record) {
        float constanteNombre= (float) constante;
        float element;
        // On utlilise le transtypage pour la comparaison entre int et float
        Object value = record.getTuple().get(indiceColonne);
        if (value instanceof Double) {
            element = ((Double) value).floatValue();
        } else if (value instanceof Float) {
            element = (Float) value;
        }else if (value instanceof Integer) {
            element = ((Integer) value).floatValue();
        }
        else {
            System.out.println("CONDITION : EST RESPECTER Constante Nombre : On ne peut comparer un string et un int ");
            return false;
        }

        switch (operateur){
            case "<=" :
                if ( placementAlias==1){ // l'element du record est a gauche
                    return element< constanteNombre || element == constanteNombre;
                }else { // l'element du record est a droite (placementAlias =2)
                    return constanteNombre< element || constanteNombre == element;
                }
            case ">=":
                if ( placementAlias==1){ // l'element du record est a gauche
                    return element > constanteNombre || element == constanteNombre;
                }else { // l'element du record est a droite (placementAlias =2)
                    return constanteNombre > element || constanteNombre == element;
                }

            case "=":
                return constanteNombre == element ;

            case "<":
                if(placementAlias==1) {
                    return element < constanteNombre ;
                }else{
                    return constanteNombre < element;
                }
            case ">" :

                if(placementAlias==1) {
                    return element > constanteNombre ;
                }else{
                    return constanteNombre > element;
                }
            case "<>":
                return element != constanteNombre ;

            default : System.out.println("CONDITION : EST RESPECTER CONSTANTE Nombre : ERREUR : l'opérateur n'est pas reconnu");
                return false;
        }
    }



    public boolean estRespecterConstanteString(Record record){
        String constanteString=(String) constante;
        // on gère le cas où l'on souhaite comparer un string à un int ou float
        if (!(record.getTuple().get(indiceColonne) instanceof String)) {
            System.out.println("CONDITION : EST RESPECTER CONSTANTE STRING : On ne peut comparer un string et un int ");
            return false;
        }
        String element = (String) record.getTuple().get(indiceColonne);
        switch (operateur){

            case "<=" :
                if ( placementAlias==1){ // l'element du record est a gauche
                    return element.compareTo(constanteString) < 0 || element.compareTo(constanteString) == 0;
                }else { // l'element du record est a droite (placementAlias =2)

                    return constanteString.compareTo(element) < 0 || constanteString.compareTo(element) == 0;

                }
            case ">=":
                if ( placementAlias==1){ // l'element du record est a gauche
                    return  element.compareTo(constanteString) > 0 || element.compareTo(constanteString) == 0;

                }else { // l'element du record est a droite (placementAlias =2)

                    return constanteString.compareTo(element) > 0 || constanteString.compareTo(element) == 0;

                }
            case "=":
                return( constanteString.equals( record.getTuple().get(indiceColonne)));

            case "<":
                if(placementAlias==1) {
                    return element.compareTo(constanteString) < 0;

                }else{
                    return constanteString.compareTo(element) < 0;
                    // return (constanteString.compareTo((String)record.getTuple().get(indiceColonne))  );
                }
            case ">" :
                if(placementAlias==1) {
                    return element.compareTo(constanteString) > 0;

                }else{
                    return constanteString.compareTo(element) > 0;
                    // return (constanteString.compareTo((String)record.getTuple().get(indiceColonne))  );
                }
            case "<>":
                return( !constanteString.equals( record.getTuple().get(indiceColonne) ));

            default : System.out.println("CONDITION : EST RESPECTER CONSTANTE STRING : ERREUR : l'opérateur n'est pas reconnu");
                return false;
        }

    }

    public boolean estRespecterDeuxValeursRecordString(Record record) {
        String element = (String) record.getTuple().get(indiceColonne);
        String element2 = (String) record.getTuple().get(indiceColonne2);
        // on gère le cas où l'on souhaite comparer un string à un int ou float
        if (!(record.getTuple().get(indiceColonne2) instanceof String)) {
            System.out.println("CONDITION : EST RESPECTER DEUX VALEURS RECORD STRING : On ne peut comparer un string et un int ");
            return false;
        }
        switch (operateur){
            case "<=" :
                return element.compareTo(element2) < 0 || element.compareTo(element2) == 0;

            case ">=":
                return  element.compareTo(element2) > 0 || element.compareTo(element2) == 0;

            case "=":
                return( element.equals( element2 ));

            case "<":

                return element.compareTo(element2) < 0;

            case ">" :

                return element.compareTo(element2) > 0;

            case "<>":
                return( !element.equals(element2) ) ;

            default : System.out.println("CONDITION : EST RESPECTER DEUX VALEURS RECORD STRING : ERREUR : l'opérateur n'est pas reconnu");
                return false;
        }
    }


    public boolean estRespecterValeursRecordNombres(Record record) {
        float element = (int) record.getTuple().get(indiceColonne);
        float element2;
        // On gère le cas où l'on compare un int à un string
        // On utlilise le transtypage pour la comparaison entre int et float
        Object value = record.getTuple().get(indiceColonne);
        if (value instanceof Double) {
            element2 = ((Double) value).floatValue();
        } else if (value instanceof Float) {
            element2 = (Float) value;
        } else if (value instanceof Integer) {
            element2 = ((Integer) value).floatValue();
        }else {
            System.out.println("CONDITION : EST RESPECTER  VALEURS RECORD Nombres : On ne peut comparer un string et un int ");
            return false;
        }
        switch (operateur){
            case "<=" :
                return element < element2 || element == element2;

            case ">=":
                return  element > element2 || element== element2 ;

            case "=":
                return  element== element2;

            case "<":
                return  element < element2 ;

            case ">" :
                return element > element2;

            case "<>":
                return element != element2  ;

            default : System.out.println("CONDITION : EST RESPECTER DEUX VALEURS RECORD INT : ERREUR : l'opérateur n'est pas reconnu");
                return false;
        }
    }





}


