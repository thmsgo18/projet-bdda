import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BufferManager {
    private DBConfig config;
    private DiskManager diskManager;
    private HashMap<Integer, List<Object>> bufferMap; // list = [pageId, dirty,pin_count]
    private ByteBuffer [] bufferPool;
    public BufferManager(DBConfig config, DiskManager diskManager) {
        this.config = config;
        this.diskManager = diskManager;
        this.bufferMap = new HashMap<>();
        bufferPool = new ByteBuffer[config.getBm_buffercount()];
        initBufferPoolAndMap();
    }

    public ByteBuffer GetPage(PageId pageId){
        int pinCount;
        List<Integer> frameDispo = new ArrayList<Integer>();
        List<Integer> framePC0 = new ArrayList<Integer>();
        for(int i = 0; i<bufferPool.length; i++){
            // si la page est deja enregistré dans un buffer.
            if (pageId.equals(( bufferMap.get(i).get(0)) )) {
                System.out.println("TEST : La pageId "+ pageId+" est deja dans le buffer "+i+" : "+bufferMap.get(i));
                pinCount=  (Integer) bufferMap.get(i).get(2);
                bufferMap.get(i).set(2, pinCount+1); //on incrémente le pin count
               diskManager.ReadPage(pageId,bufferPool[i]); // on fait lire au buffer les elements de la page
               return bufferPool[i];// on retourne le buffer en question
            }
            // cas ou un buffer n'as pas encore enregistré de page, on le prends en compte pour plus tard
            if(bufferMap.get(i).get(0)==(null)){
                frameDispo.add(i);
            }
            // cas ou un buffer a un pin count =0, on l'enregistre pour plus tard
            if (( bufferMap.get(i).get(2).equals(0) )) {
                framePC0.add(i);
            }
        } // On sort de la boucle

        // s'il n'y a plus de frame libre
        if (frameDispo.isEmpty()) {

            System.out.println("TEST : Il n' y a pas de frames disponibles");
            if(!framePC0.isEmpty()){ // vérifie qu'il y a quand meme au moins 1 case avec un pin count =O
                System.out.println("TEST : Il y a des frames possedant des pin_count à 0");


                // c'est là où on va intégrer un algotithme de remplacement de pages (pour l'instznt j'ai la flemme
                // là c'est un pseudo LRU
                // prend le premier element des frames ayant des pin count à 0
                int indiceBuffer= framePC0.get(0);
                if ( bufferMap.get(indiceBuffer ).get(1).equals(true) ){ // on vérifie si ce buffer  été modifer (dirty à 1)
                    System.out.println("TEST : Dirty = true, la page a été modifié");
                    //dirty=1

                    diskManager.WritePage((PageId) bufferMap.get(indiceBuffer).get(0),bufferPool[indiceBuffer]); // on inscrit les changements que le précédent buffer a fait sur le disque
                    bufferMap.get(indiceBuffer ).set(1,false); // on met le dirty à faux
                }
                bufferMap.get( indiceBuffer ).set(2,1); // on mets le pin count à 1
                bufferMap.get(indiceBuffer ).set(0,pageId); // on injecte la pageId correspondante dans la map.
                diskManager.ReadPage(pageId,bufferPool[indiceBuffer]); // on fait lire au buffer les elements de la page
                return bufferPool[indiceBuffer];// On retourne le buffer
            }else{

                System.out.println("TEST : Il n'y a aucune frames disponibles, mais en plus aucunes frames avec un pin count =0");
                return null;
            }
        } else{

            // frameDispo n'est pas vide
            System.out.println("TEST : Des frames sont disponibles");
            int indiceBuffer =frameDispo.get(0);
            bufferMap.get( indiceBuffer ).set(2,1); // on mets le pin count à 1
            bufferMap.get(indiceBuffer ).set(0,pageId); // on injecte la pageId correspondante dans la map.
            diskManager.ReadPage(pageId,bufferPool[indiceBuffer]); // on fait lire au buffer les elements de la page
            return bufferPool[indiceBuffer]; // On retourne le buffer
        }



    }


    public void FreePage(PageId pageId, boolean valDirty) { // ou on peut utilisrer un int pour valDirty

    }

    public void SetCurrentReplacementPolicy (String policy){

    }

    public void FlushBuffer(){
    }

    private void initBufferPoolAndMap() {
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

}
