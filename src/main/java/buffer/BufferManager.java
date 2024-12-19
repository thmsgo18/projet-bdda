package buffer;

import espaceDisque.DBConfig;
import espaceDisque.DiskManager;
import espaceDisque.PageId;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//
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
                    //System.out.println("BUFFER MANAGER : GET PAGE : La page ID : "+bufferMap.get(i).get(0)+" est deja présent dans le buffer "+i);
                    pinCount=  (Integer) bufferMap.get(i).get(2);
                    bufferMap.get(i).set(2, pinCount+1); // Incrémentation du pin count
                    //System.out.println("BUFFFER MANAGER :GET PAGE : buffer info :"+bufferMap.get(i)+ " "+bufferPool[i]);
                    //System.out.println("BUFFER MANAGER : GET PAGE : buffer contenu (donnée changer normalement) : "+Arrays.toString(bufferPool[i].array()));
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
                //System.out.println("$$$$$$$$$$$$$$$$$ PCO a des éléments $$$$$$$$$$$$$$$$$$$$$");
                int indiceBuffer= indicePolicy(framePC0); // Indice de la page à remplacer selon la politique
                //System.out.println("$$$$$$$$$$$$$$$$$ indice element PCO a enlever "+indiceBuffer+" $$$$$$$$$$$$$$$$$$$$$" +bufferMap.get(indiceBuffer)+"$$$$"+"le buffer en question"+ Arrays.toString(bufferPool[indiceBuffer].array()));

                if ( bufferMap.get(indiceBuffer ).get(1).equals(true) ){ // Vérification du buffer pour voir s'il a été modifer (dirty à 1)
                    // dirty = true

                    diskManager.WritePage((PageId) bufferMap.get(indiceBuffer).get(0),bufferPool[indiceBuffer]); // on inscrit les changements que le précédent buffer a fait sur le disque
                    bufferMap.get(indiceBuffer ).set(1,false); // on met le dirty à faux
                    //System.out.println("BUFFER MANAGER : GET PAGE : page modifier : "+bufferMap.get(indiceBuffer ).get(1));
                    //System.out.println("BUFFER MANAGER : GET PAGE : page modifier contenu (ça doit être les vrais valeurs ) : "+Arrays.toString(bufferPool[indiceBuffer].array()));

                }else{ // dirty = false
                }
                bufferMap.get( indiceBuffer ).set(2,1); // Mise du pin count à 1
                bufferMap.get(indiceBuffer ).set(0,pageId); // Rajout de la pageId correspondante dans la map.

                diskManager.ReadPage(pageId,bufferPool[indiceBuffer]); // Strockage de la page dans le buffer
                return bufferPool[indiceBuffer]; // Retourne le buffer
            }else{// Plus de frame dispo dans le buffer et aucune frame avec un pin count=0
                //System.out.println("ERREUR (futur exception) : BUFFER MANAGER : GET PAGE : Aucune pages n'est disponible (plus de frames disponibles ni aucunes frames avec un pin count = 0");

                return null;
            }
        } else{ // frameDispo n'est pas vide
            int indiceBuffer = indicePolicy(frameDispo); // Choix de l'indice du buffer à remplacer en fonction de la politique de remplacement
            //System.out.println("BUFFER MANAGER : GET PAGE : On est dans le cas où il y a des frames Dispo ( frameDispo empty ), frameDispo : "+ Arrays.toString(frameDispo.toArray()) + " indiceBuffer : "+indiceBuffer);


            bufferMap.get( indiceBuffer ).set(2,1); // Mise du pin count à 1
            bufferMap.get(indiceBuffer ).set(0,pageId); // On ajout le pageId correspondante dans la map.
            //System.out.println("indiceBuffer : "+indiceBuffer+"  bufferMap.get (indiceBuffer)+ : " +bufferMap.get(indiceBuffer )+"buffPosition(indiceBuffer) : "+bufferPool[indiceBuffer].position());
            //System.out.println("buffer a retourner pour le getPage() : "+bufferPool[indiceBuffer]);
            diskManager.ReadPage(pageId,bufferPool[indiceBuffer]); // Strockage de la page dans le buffer
            return bufferPool[indiceBuffer]; // Retourne le buffer

        }
    }


    public void FreePage(PageId pageId, boolean valDirty) { // valDirty => page modifié

        for(int i=0; i<bufferMap.size(); i++){
           //System.out.println("**************  "+bufferMap.get(i).get(0));
            if (bufferMap.get(i).get(0)!=null) {
                if (pageId.egale((PageId) bufferMap.get(i).get(0))) { // Trouve le buffer contenant la pageId
                    //System.out.println("BUFFER MANAGER : FREE PAGE : la pageID "+pageId+" a été trouvé pour être libérer");
                    int pinCount =  (Integer) bufferMap.get(i).get(2);
                    bufferMap.get(i).set(1,valDirty);
                    bufferMap.get(i).set(2,pinCount-1);  // Décrémentation de pin count
                    if( (int) bufferMap.get(i).get(2)==0){
                        bufferMap.get(i).set(3,System.currentTimeMillis());
                    }
                    bufferPool[i].position(0); // il faut remettre au premier octet
                    bufferPool[i].limit(bufferPool[i].capacity()); // Remets la limite à jour
                }
            }

        }
    }

    public void SetCurrentReplacementPolicy (String policy){

        if ( ( !policy.equals("LRU") && ( !policy.equals("MRU") ) && ( !policy.equals("FIFO") ) && ( !policy.equals("LIFO") )) ){ // Vérification de la politique de remplacement
            System.out.println(policy+" ne fait partie des politiques acceptés : LRU / MRU/FIFO/LIFO");
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
            if (bufferMap.get(i).get(1).equals(true)){ // Si le dirty = true, Écriture dans sa page des modifications
                diskManager.WritePage((PageId) bufferMap.get(i).get(0),bufferPool[i]); // Écriture du buffer dans la page
                bufferMap.get(i).set(1,false);
            }
            bufferMap.get(i).set(0,null);
            bufferMap.get(i).set(2,0);
            bufferMap.get(i).set(3,0L);
        }
        for(int i =0;i<bufferPool.length;i++){
            bufferPool[i].clear();
        }

    }

    private void initBufferPoolAndMap() {
        // Initialisation du tableau de buffer et de la map
        long zero = (long) 0;
        for (int i = 0; i < config.getBm_buffercount(); i++) {
            List<Object> bufferInfo = new ArrayList<>();
            bufferInfo.add(null);  // pageId (initialisé à null)
            bufferInfo.add(false); // dirty (initialisé à false)
            bufferInfo.add(0);     // pin_count (initialisé à 0)
            bufferInfo.add(zero); //le temps
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

    private int indicePolicy(List<Integer> frames){
        // Pour LRU: on prends la première frame qui a un pin count =0 dans la liste trié par anciennté
        if (getPolicy().equals("LRU")) {
            frames.sort((a, b) -> {
                long timeA = (long) bufferMap.get(a).get(3);
                long timeB = (long) bufferMap.get(b).get(3);
                return Long.compare(timeA, timeB);
            });
            return frames.get(0);
        }
        else if (getPolicy().equals("MRU")) {
        // Pour MRU, on prends la dernière frame qui a un pin count =0 dans la liste trié par anciennté
            frames.sort((a, b) -> {
                long timeA = (long) bufferMap.get(a).get(3);
                long timeB = (long) bufferMap.get(b).get(3);
                return Long.compare(timeA, timeB);
            });
            return frames.get(frames.size()-1);
            // Pour FIFO et LIFO on ne trie pas par ancienneté mais simplement dans un ordre d'arrivé
        }else if (getPolicy().equals("FIFO")) {
            return frames.get(0);
        } else if (getPolicy().equals("LIFO")) { // last in first out
            return frames.get(frames.size() - 1);
        }else{
            System.out.println(getPolicy()+" ne correspond pas aux algos de remplacement de pages pris en compte : {LRU,MRU,FIFO,LIFO}");
            return 0;
        }

        // plus tard si le code est bon, on rajoutera surement d'autres politiques pour prendre le bonus
    }

    public HashMap<Integer, List<Object>> getBufferMap(){
        return bufferMap;
    }


    public int getIndiceBufferMap(PageId pageId){ // retourne l'indice du buffer contenant la page indiqué en argumant
       for(int i =0; i<bufferMap.size(); i++){
           //System.out.println("i = "+i+" "+bufferMap.get(i));
           if (bufferMap.get(i).get(0)!=null) {
               //System.out.println(bufferMap.get(i).get(0));
               if (pageId.egale((PageId) bufferMap.get(i).get(0))) {
                   return i;
               }
           }
       }
       return -1;
    }


    public boolean getDirtyPage(PageId pageId){
        int indicePage =getIndiceBufferMap(pageId);
        //System.out.println("indicePage : "+indicePage);
        //System.out.println(bufferMap);
        boolean rep =(boolean) bufferMap.get(indicePage).get(1) ;
        /*if(rep){
            System.out.println("RELATION : BUffER MANAGER : GET DIRTY PAGE : le dirty de la header page était anciennement true, donc il restera true");
        }else{
            System.out.println("RELATION : GET FREE DATA PAGE ID : GET DIRTY PAGE : le dirty de la header page était anciennement false, il est donc pas nécessaire de le mettre à true, il restera à false");

        }*/
        return rep;
    }

    public ByteBuffer [] getBufferPool(){
        return bufferPool;
    }


}
