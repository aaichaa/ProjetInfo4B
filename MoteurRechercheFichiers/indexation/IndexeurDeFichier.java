import MoteurRechercheFichiers.indexation.IndexInverse;
import java.io.*;
import java.util.*;

public class IndexeurDeFichier {
    // les Mots à enlever (stop-words)
    private Set<String> stopwords = new HashSet<>(Arrays.asList(
         "le", "la", "les", "de", "du", "des", "un", "une",
        "et", "en", "au", "aux", "ce", "qui", "que", "est",
        "il", "elle", "ils", "elles", "on", "nous", "vous",
        "the", "is", "are", "and", "or", "of", "in", "to",
        "a", "an", "for", "on", "at", "by", "it", "its"
    ));

    // Attributs
    private IndexInverse indexInverse;
    

    public IndexeurDeFichier(IndexInverse indexInverse){
        this.indexInverse = indexInverse;
    }

    // Extration de mots dans fichier texte (les .txt) et leur ajout dans le IndexInverse

    private void indexerFichierTexte(String CheminFichier) throws IOException{
        BufferedReader Lecteur = new BufferedReader(new FileReader(CheminFichier)); // Ouvre le fichier et prepare la lecture ligne par ligne
        String ligne;
        while (ligne = Lecteur.readLine() != null) {
            String[] Mots = ExtraireMots(ligne);
            for(String mot : Mots){
                if(estValide(mot)){
                    indexInverse.ajouterMot(mot, CheminFichier);
                }
            }
            
        }
        Lecteur.close();
        System.out.println("Fichier tecte indexé : "+ CheminFichier );
    }

    // Classe ExtraireMots 

    private String[] ExtraireMots(String Ligne){
        Ligne = Ligne.replaceAll("[^a-zA-ZÀ-ÿ0-9]", " "); // remplace tout caractère n'etant pas compris entre A-Z ou 0-9 ou avec des accents
        return Ligne.toLowerCase().split("\\s+"); // converti chaque caractere en minuscule, decoupe en mots apres chq espace( un ou plrs espaces (\\s+)) et renvoie le tout comme un tableau de String
    }


    // Extraction de mots dans un fichier Pdf avec pdf2txt

    private void IndexerFichierpdf(String CheminFichier) throws IOException{
        // Lancer Pdf via Pipe
        Process processus = Runtime.getRuntime().exec("pdf2txt" + CheminFichier);  // partie pas trop compris. j'ai utilisé l'IA pour faire. 
        
        BufferedReader Lecteur = new BufferedReader(
            new InputStreamReader(processus.getInputStream())
        );
        String Ligne; 
        while (Ligne = Lecteur.readLine() != null) {
            String[] Mots = ExtraireMots(Ligne);
            for(String mot : Mots){
                if(estValide(mot)){
                    indexInverse.ajouterMot(mot, CheminFichier);
                }
            }
        }
        Lecteur.close();
        System.out.println("Fichier PDF indexé : " + CheminFichier);
    }


    // classe estValide

    private boolean estValide(String mot){
        return mot.length() > 2 && !stopwords.contains(mot); // Le mot est valide que si il a plus de 2 caracère et il n'est pas dans les stop-words
    }

    // ajouter un stop-words personalisé

    public void ajouterStopWords(String mot){
        stopwords.add(mot.toLowerCase().trim());
    }

    // Réccupéré la liste des stop-words

    public set<String> getStopWords(){
        return new HashSet<>(stopwords);
    }
}