import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StockageIndex{
    //Attributs 
    private IndexInverse indexInverse; //index à sauvegarder et recharger
    private String cheminFichierIndex;  //chemin du fichier dans lequel on enregistre l'index sur le disque 

    //Constructeur 
    public StockageIndex(IndexInverse indexInverse, String cheminFichierIndex){
  
        this.indexInverse = indexInverse;
        this.cheminFichierIndex = cheminFichierIndex;

    }

    //Méthodes 
    //on verifie que le fichier d'index se trouve dans le disque
    public boolean indexExiste(){
        if(this.cheminFichierIndex == null){
            return false;
        }
        File cheminFichier =  new File(cheminFichierIndex);
        if(cheminFichier.exists()){
            return true;
        }
        return false;
        
    }

    //on va enregistrer l'index dans un fichier sur le disque
    public synchronized void sauvegarderIndex() throws IOException{
        if(indexInverse == null){
            return;
        }
        File fichier =  new File(cheminFichierIndex);
        ObjectOutputStream fluxDeSortie = new ObjectOutputStream(
            new FileOutputStream(fichier));
        fluxDeSortie.writeObject(indexInverse);
        fluxDeSortie.close();
        System.out.println("L'index a été sauvegardé");


    }

    //on charge l'index depuis le disque et on le reconstruit en memoire 
    public synchronized IndexInverse chargerIndex() throws IOException,ClassNotFoundException{
        if(!indexExiste()){
            this.indexInverse = new IndexInverse();
            return this.indexInverse;
        }
        File fichier =  new File(cheminFichierIndex);
        ObjectInputStream fluxDentree = new ObjectInputStream(new FileInputStream(fichier));
        this.indexInverse = (IndexInverse) fluxDentree.readObject();
        fluxDentree.close();
        return this.indexInverse;

    }














































}