package espaceDisque;

import buffer.BufferManager;
import inter.IRecordIterator;
import relationnel.Record;

import java.nio.ByteBuffer;
//
public class PageDirectoryIterator  {
    private int nombrePagesVu = 0;
    private BufferManager bufferManager;
    private ByteBuffer bufferHeader;
    private PageId headerPage;
    private int nombrePageDonnees;
    public PageDirectoryIterator( BufferManager bufferManager,PageId headerPage) {
        this.bufferManager = bufferManager;
        this.bufferHeader = bufferManager.GetPage(headerPage);
        this.headerPage = headerPage;
        this.nombrePagesVu = 0;
        this.nombrePageDonnees = bufferHeader.getInt(0);
        bufferManager.FreePage(headerPage, false);
    }

    public PageId GetNextDataPageId(){

        PageId pageDonnee=null; // On vérifie qu'on ne depasse pas le nombre de page de données de la header page, (éviter qu' on retourne des pages de données [0,0] jusqu'a la fin de la page ou un bug
        if (nombrePagesVu<nombrePageDonnees) {
            this.bufferHeader = bufferManager.GetPage(headerPage);
            bufferHeader.position(4 + nombrePagesVu * 12); // on se met au premier octet pour lire la page
            int numeroFichier = bufferHeader.getInt();
            int numeroPage = bufferHeader.getInt();
            bufferManager.FreePage(headerPage,false);

            nombrePagesVu++;
            pageDonnee = new PageId(numeroFichier, numeroPage); // On crée la page de données à partir des 2 int
        }
        return pageDonnee;
    }

    // Cette méthode permet de repartir de la premiere page de donnés à retourner de la headerPage
    public void Reset(){
        this.nombrePagesVu = 0;
    }

    public void Close(){
        boolean dirtyHeaderPage= bufferManager.getDirtyPage(headerPage); // on vérifie l'etat précédent du dirty pour le lui redonner (on le mets pas à true car on  ne modifie pas les elements de la page )
        bufferManager.FreePage(headerPage,dirtyHeaderPage);
    }


}


/* On commence à voir les pages de données à partir de  l'octet 4,
si on veu retourner la page 3 parcequ'on était à la page 2, il faut faire 4 + 2*12 on se retrouvera pile devant l'octet à
 si on veu retourner la page 4 parcequ'on était à la page 3, il faut faire 4 + 3*12 on se retrouvera pile devant l'octet à

tant que nombrePageVu est en dessous de nombre de data pages disponible aux 4 premiers octets de HeaderPage

 */