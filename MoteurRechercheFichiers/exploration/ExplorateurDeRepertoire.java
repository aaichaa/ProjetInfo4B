package MoteurRechercheFichiers.exploration;
import MoteurRechercheFichiers.indexation.IndexeurDeFichiers;
import java.io.File;


public class ExplorateurDeRepertoire{


    //Attributs
    private File repertoireDeBase;
    private IndexeurDeFichiers indexeurDeFichiers;

    //Constructeur
    public ExplorateurDeRepertoire(File repertoireDeBase, IndexeurDeFichiers indexeurDeFichiers)
    {
        this.repertoireDeBase = repertoireDeBase;
        this.indexeurDeFichiers = indexeurDeFichiers;
    }


    //Getters setters

    public File getRepertoireDeBase(){
        return this.repertoireDeBase;
    }

    public void setRepertoireDeBase(File repertoireDeBase){
        this.repertoireDeBase = repertoireDeBase;

    }

    public IndexeurDeFichiers getIndexeurDeFichiers(){
        return this.indexeurDeFichiers;
    }

    public void setIndexeurDeFichiers(IndexeurDeFichiers indexeurDeFichiers){
        this.indexeurDeFichiers = indexeurDeFichiers;
    }

    //Methodes

    //on va explorer les dossiers 

    public void explorer(){
        if(!estRepertoireValide(this.repertoireDeBase)){
            System.out.println("Erreur : le repertoire n'est pas valide");
        }
        else{
            explorerRepertoire(this.repertoireDeBase);
        }
    }

    private boolean estRepertoireValide(File repertoire){
        if(repertoire == null){
            return false;
        }
        else if(!repertoire.exists()){ //on verifie si le repertoire existe
            return false;
        }
        else if(!repertoire.isDirectory()){ // on verifie si cest un dossier
            return false; //si ca ne l'est pas on retourne false 
        }
        else {
            return true;
        }
        
    }


    //parcours de tous les dossiers 
    private void explorerRepertoire(File repertoire){

        File [] elements = repertoire.listFiles(); //on recupere les element de ce repertoire
        if(elements == null){ //en cas de problème d'accès au repertoire on arrete la methode
            return;
        }
       
        for (int i = 0; i< elements.length; i++){
                if(elements[i].isDirectory()){
                    explorerRepertoire(elements[i]);
                }
                else if(elements[i].isFile()){ //si c'est un fichier on l'envoi à lindexeur de fichier pour qu'il le traite
                    indexeurDeFichiers.indexerFichier(elements[i]);

                }

        }    
    }
      

    


    








}