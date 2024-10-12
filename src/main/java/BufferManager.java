import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BufferManager {
    private DBConfig config; // configuration du SGBD
    private DiskManager diskManager;
    private HashMap<Integer, List<Object>> bufferMap; // list = [pageId, dirty,pin_count]
    private ByteBuffer [] bufferPool; // Tableau de buffer
    private String policy; // politique de remplacement des buffer

    public BufferManager(DBConfig config, DiskManager diskManager) { // constructeur
        this.config = config;
        this.diskManager = diskManager;
        this.bufferMap = new HashMap<>();
        bufferPool = new ByteBuffer[config.getBm_buffercount()];
        this.policy= config.getBm_policy();
        initBufferPoolAndMap();
    }

    public ByteBuffer GetPage(PageId pageId){
        int pinCount;
        List<Integer> frameDispo = new ArrayList<Integer>();
        List<Integer> framePC0 = new ArrayList<Integer>();
        for(int i = 0; i<bufferPool.length; i++){
            // Recherche dans le bufferPool de la présence de la page

            if (bufferMap.get(i).get(0)!=null) { // Page trouvée dans le tableau de buffer
                if(pageId.egale((PageId) bufferMap.get(i).get(0))){
                    System.out.println("La page ID : "+bufferMap.get(i).get(0)+" est deja présent dans le buffer "+i);
                    pinCount=  (Integer) bufferMap.get(i).get(2);
                    bufferMap.get(i).set(2, pinCount+1); // Incrémentation du pin count
                    diskManager.ReadPage(pageId,bufferPool[i]); // Strockage de la page dans le buffer
                    return bufferPool[i]; // retourne le buffer
                }
            }

            if(bufferMap.get(i).get(0)==(null)){ // Si aucun buffer stocker à la position i
                frameDispo.add(i);
            }

            if (( bufferMap.get(i).get(2).equals(0) )) { // Si la page a un pin count = 0 => Stockage de l'info
                framePC0.add(i);
            }
        } // Sortie de la boucle for

        if (frameDispo.isEmpty()) { // Si plus de frame libre

            if(!framePC0.isEmpty()){ // Vérification de la présence d'au moins un buffer avec un pin count =O

                int indiceBuffer= indicePolicy(framePC0); // Indice de la page à remplacer selon la politique
                if ( bufferMap.get(indiceBuffer ).get(1).equals(true) ){ // Vérification du buffer pour voir s'il a été modifer (dirty à 1)
                    // dirty = 1
                    diskManager.WritePage((PageId) bufferMap.get(indiceBuffer).get(0),bufferPool[indiceBuffer]); // on inscrit les changements que le précédent buffer a fait sur le disque
                    bufferMap.get(indiceBuffer ).set(1,false); // on met le dirty à faux
                }else{ // dirty = 0

                }
                bufferMap.get( indiceBuffer ).set(2,1); // Mise du pin count à 1
                bufferMap.get(indiceBuffer ).set(0,pageId); // Rajout de la pageId correspondante dans la map.
                diskManager.ReadPage(pageId,bufferPool[indiceBuffer]); // Strockage de la page dans le buffer
                return bufferPool[indiceBuffer]; // Retourne le buffer
            }else{// Plus de frame dispo dans le buffer et aucune frame avec un pin count=0
                return null;
            }
        } else{ // frameDispo n'est pas vide
            int indiceBuffer = indicePolicy(framePC0); // Choix de l'indice du buffer à remplacer en fonction de la politique de remplacement
            bufferMap.get( indiceBuffer ).set(2,1); // Mise du pin count à 1
            bufferMap.get(indiceBuffer ).set(0,pageId); // On ajout le pageId correspondante dans la map.
            diskManager.ReadPage(pageId,bufferPool[indiceBuffer]); // Strockage de la page dans le buffer
            return bufferPool[indiceBuffer]; // Retourne le buffer

        }
    }


    public void FreePage(PageId pageId, boolean valDirty) { // valDirty => page modifié
        for(int i=0; i<bufferMap.size(); i++){
            if (bufferMap.get(i).get(0).equals(pageId)) { // Trouve le buffer contenant la pageId
                int pinCount =  (Integer) bufferMap.get(i).get(2);
                bufferMap.get(i).set(1,valDirty);
                bufferMap.get(i).set(2,pinCount-1);  // Décrémentation de pin count
            }
        }
    }

    public void SetCurrentReplacementPolicy (String policy){

        if ( ( !policy.equals("LRU") && ( !policy.equals("MRU") ) ) ){ // Vérification de la politique de remplacement
            System.out.println(policy+" ne fait partie des politiques acceptés : LRU / MRU");
        }else {
            // Vérification que la politique de remplacement renseigné est la même que celle actuelle
            if( policy.equals( getPolicy() ) ){
                System.out.println("On utilise deja la politique "+policy);
            }else{
                setPolicy(policy); // Changement de la variable policy
                System.out.println("La politique utilisé "+getPolicy()+" devient : "+policy);
            }
        }
    }

    public void FlushBuffers(){
        for(int i =0; i<bufferMap.size(); i++){
            // Parcours du tableau de buffer
            if (bufferMap.get(i).get(1).equals(true)){ // Si le dirty = 1, Écriture dans sa page des modifications
                diskManager.WritePage((PageId) bufferMap.get(i).get(0),bufferPool[i]); // Écriture du buffer dans la page
                bufferMap.get(i).set(1,false);
            }
            bufferMap.get(i).set(0,null);
            bufferMap.get(i).set(2,0);
        }
        for(int i =0;i<bufferPool.length;i++){
            bufferPool[i].clear();
        }

    }

    private void initBufferPoolAndMap() {
        // Initialisation du tableau de buffer et de la map
        for (int i = 0; i < config.getBm_buffercount(); i++) {
            List<Object> bufferInfo = new ArrayList<>();
            bufferInfo.add(null);  // pageId (initialisé à null)
            bufferInfo.add(false); // dirty (initialisé à false)
            bufferInfo.add(0);     // pin_count (initialisé à 0)
            bufferMap.put(i, bufferInfo);  // Associe la liste à l'indice dans bufferMap
        }

        // Initialisation du bufferPool avec des instances de ByteBuffer
        for (int i = 0; i < bufferPool.length; i++) {
            bufferPool[i] = ByteBuffer.allocate((int) config.getPagesize());
        }

    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    private int indicePolicy(List<Integer> framePC0){
        // Pour LRU: on prends la première frame qui a un pin count =0
        if (getPolicy().equals("LRU")) {
            return framePC0.get(0);
        }
        else{
        // Pour MRU, on prends la dernière frame qui a un pin count =0
            return framePC0.get(framePC0.size()-1);
        }
        // plus tard si le code est bon, on rajoutera surement d'autres politiques pour prendre le bonus
    }


}
