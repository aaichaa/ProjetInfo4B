package MoteurRechercheFichiers.indexation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



public class IndexInverse{

    //Attributs

    private Map<String, Map<String, Integer>> index = new HashMap<>(); //mot , fichier, frequence 
    private Set<String> documents = new HashSet<>(); //documents contient les fichiers indexés



    //Methodes


    //Ajout d'un mot dans l'index
    public synchronized void ajouterMot(String mot,String fichier){  

        //cas 1: le mot nexite pas et le fichier non plus  
        if(!index.containsKey(mot)){ 
           Map<String, Integer> mapInterne = new HashMap<>();
            mapInterne.put(fichier, 1);
            index.put(mot, mapInterne);
           
         
        }
        // cas 2: le mot existe mais pas le fichier
        else if(index.containsKey(mot) && !index.get(mot).containsKey(fichier)){ 
            Map<String, Integer> mapInterne = index.get(mot); //donne les elements de la deuxieme map
            mapInterne.put(fichier, 1);
           

        }
        //cas 3: tous deux existent
        else if(index.containsKey(mot) && index.get(mot).containsKey(fichier)){ 
         Map<String, Integer> mapInterne = index.get(mot); 
         Integer LaFrequence = (Integer) mapInterne.get(fichier);
         mapInterne.put(fichier, LaFrequence+1);

        }
        documents.add(fichier);


    }

    //recherche des fichiers et leur frequence a partir d'un mot 
    public synchronized Map<String, Integer> recherche(String mot){
        if(index.containsKey(mot)){
            Map<String, Integer> mapInterne = new HashMap<>(index.get(mot)); //on copie la map interne par securité
            return mapInterne;
        }else{
            return new HashMap<>();
        }
        
    }

    //recuperer tous les documents indxés
    public synchronized Set<String> getDocuments(){
        Set<String> tousLesdocuments = new HashSet<>(documents);
        return tousLesdocuments;

    }

    //Compter le nombre de fichiers(documents) contenant un mot
    public synchronized int compterDocuments(String mot){
     
        if(index.containsKey(mot)){
            Map<String, Integer> fichiers = index.get(mot); 
            Set<String> listeMots= fichiers.keySet();
            return listeMots.size();
     
            }
            return 0;

    }


    //frequence d'un mot dans un fichier
    public synchronized int getFrequence(String mot,String fichier){
        if(index.containsKey(mot)){
            Map<String, Integer> lesFichiers = index.get(mot); 
            if(lesFichiers.containsKey(fichier)){
                return lesFichiers.get(fichier);
            }

        }
        return 0;
    }


    //nombre total de documents
    public synchronized int getNbDocuments(){ 
        return documents.size();

    }




















}