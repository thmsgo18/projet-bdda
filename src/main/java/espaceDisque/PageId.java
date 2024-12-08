package espaceDisque;

public class PageId {

    private int FileIdx;
    // FileIdx => l'identifiant du fichier le "x" dans Fx
    private int PageIdx;
    //PageIdx => l'indice de la page dans le fichier (O pour la première page, 1 pour la deuxième)

    public PageId(int FileIdx, int PageIdx){
        // Constructeur
        this.FileIdx = FileIdx;
        this.PageIdx = PageIdx;
    }

    public String toString(){
        return "["+FileIdx+","+PageIdx+"]";
    }


    public boolean egale(PageId other){
        return other.FileIdx == this.FileIdx && other.PageIdx == this.PageIdx;
    }

    // Get
    public int getFileIdx() {
        return FileIdx;
    }
    public int getPageIdx() {
        return PageIdx;
    }
    // Set
    public void setFileIdx(int FileIdx) {
        this.FileIdx = FileIdx;
    }
    public void setPageIdx(int PageIdx) {
        this.PageIdx = PageIdx;
    }

    // commit pour le V
}
