package recherche;

import indexation.IndexeurDeFichier;
import indexation.IndexInverse;
import java.util.*;

public class MoteurDeRecherche {
    
    // Attributs
    private IndexInverse indexInverse;
    private IndexeurDeFichier indexeurDeFichier;

    public MoteurDeRecherche(IndexInverse indexInverse, IndexeurDeFichier indexeurDeFichier){
        this.indexInverse = indexInverse;
        this.indexeurDeFichier = indexeurDeFichier ;

    }

    // Recherche par u n seul mot
    public List<String> rechercherMot(String mot){
        mot = mot.toLowerCase().trim();  // transforme le mot en totalement minuscule et efface les espaces au debuts et a la fin
        
        // reccuperer les fichiers contenant le mot avec leur frequence
        Map<String, Integer> fichiers = indexInverse.recherche(mot); 
        
        if(fichiers.isEmpty()){
            System.out.println("Aucun fichier trouvé pour le mot : "+mot);
            return new ArrayList<>();  // on peut pas mettre un return 0 car le type de retour est une List
        }

        //calcule le score TF-IDF pour chaque fichier
        
        Map<String, Double> scores = new HashMap<>();
        for(String fichier : fichiers.keySet()){  // keyset() reccupère tout les noms de fichiers ( la clé )
            double score = calculeTfIdf(mot, fichier);
            scores.put(fichier, score);
        }

        // trier les scores par ordres decroissant
        List<String> resultatTrier = trierParScore(scores);

        return resultatTrier;

    }

    // Les Methodes

    // la methodes calculeTfIdf
    private double calculeTfIdf(String mot, String fichier){
        int frequence = indexInverse.getFrequence(mot, fichier);  // frequence du mot dans le fichier
        int nbDocumentsAvecMot = indexInverse.compterDocuments(mot);  //nbre de document contenant ce mot
        int nbTotalDocuments = indexInverse.getNbDocuments();  // Nbre total de document

        if(nbDocumentsAvecMot == 0 || nbTotalDocuments == 0){
            return 0.0;
        }

        // TF-IDF = frequence * log(total documents / documents contenant le mot)
        double tf = frequence;
        double idf = Math.log((double) nbTotalDocuments / nbDocumentsAvecMot);

        return tf*idf;
    }

    // methode trierParScore
    private List<String> trierParScore(Map<String, Double> scores){
        List<String> fichiers = new ArrayList<>(scores.keySet());

        // trier par ordre decroissant
        for(int i = 0; i<fichiers.size()-1; i++){
            for( int j = i+1; j<fichiers.size(); j++){
                if(scores.get(fichiers.get(j)) > scores.get(fichiers.get(i))){
                    String temp = fichiers.get(i);
                    fichiers.set(i, fichiers.get(j));
                    fichiers.set(j, temp);
                }
            }
        }
        return fichiers;
    }

    // Recherche par plusieurs mots

    public List<String> rechercherPlusieurs(String[] mots){
        // on prend le premeir mot
        Set<String> fichiersCommuns = new HashSet<>(indexInverse.recherche(mots[0]).keySet());  // o, reccupere tout les fichiers avec le mot[0] dans une liste
        // on garde uniquement les fichiers qui contiennent tous les mots
        for(int i = 1; i<mots.length; i++){
            String mot = mots[i].toLowerCase().trim();
            Set<String> fichiersMot = indexInverse.recherche(mot).keySet();
            fichiersCommuns.retainAll(fichiersMot);  // on garde uniquement que les fichiers en commun
        }
        if(fichiersCommuns.isEmpty()){
            System.out.println("Aucun fichier trouver ");
            return new ArrayList<>();
        }
        // sinon on calcule le score total TF-IDF pour chaque fichier commun
        Map<String, Double> scores = new HashMap<>();
        for(String fichier : fichiersCommuns){
            double scoreTotal = 0.0;
            for(String mot : mots){
                scoreTotal += calculeTfIdf(mot.toLowerCase().trim(), fichier);
            }
            scores.put(fichier, scoreTotal);
        }
        return trierParScore(scores);
    }

    // Affichage des resultats

    public void afficherResultats(List<String> resultats){
        if(resultats.isEmpty()){
            System.out.println("Aucun resultat trouver ");
            return;
        }
        
        System.out.println("        Resulats ("+resultats.size()+")");
        for(int i = 0; i<resultats.size(); i++){
            System.out.println((i+1) +"- "+resultats.get(i));
        }
    }

    // donner etat de l'indexeur
    public String donnerEtatIndexeur(){
        return "Indexeur active";
    }

    // //Associer une image a un mot clé
    // public void annoterImage(String chemin, String mot){
    //     mot = mot.toLowercase().trim();
    // }

}
