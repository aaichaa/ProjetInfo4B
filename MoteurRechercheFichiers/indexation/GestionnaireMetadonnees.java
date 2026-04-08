package indexation;

import java.util.ArrayList;
import java.util.HashMap;


public class GestionnaireMetadonnees {
    // attibut
    private HashMap<String, ArrayList<String>> indexMeta;  //sert a stocker metadonné

    // constructeur
    public GestionnaireMetadonnees(){
        indexMeta = new HashMap<>();
    }

    public void ajouterMetas(String cheminFichier, String[] metas){
        for(String meta: metas){
            meta = meta.toLowerCase();

            this.indexMeta.putIfAbsent(meta, new ArrayList<>());
            if(!this.indexMeta.get(meta).contains(cheminFichier)){
                this.indexMeta.get(meta).add(cheminFichier);
            }
        }
    }
    public void ajouterMeta(String cheminFichier, String meta){
        meta = meta.toLowerCase();

        this.indexMeta.putIfAbsent(meta, new ArrayList<>());
        
        if(!this.indexMeta.get(meta).contains(cheminFichier)){
            this.indexMeta.get(meta).add(cheminFichier);
        }

    }


    public ArrayList<String> rechercheMeta(String meta){
        meta = meta.toLowerCase();
        if(this.indexMeta.containsKey(meta)){
            return this.indexMeta.get(meta);
        }else{
            return new ArrayList<>();
        }
    }
}
