package serveur;

import indexation.IndexeurDeFichier;
import indexation.IndexInverse;
import recherche.MoteurDeRecherche;
import exploration.ExplorateurDeRepertoire;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
//import java.util.*;

public class ServeurIndexeur {
    // Attributs
    private IndexInverse indexInverse;
    private IndexeurDeFichier indexeurDeFichier;
    private MoteurDeRecherche moteurDeRecherche;
    private ExplorateurDeRepertoire explorateur;
    private ServerSocket serverSocket;
    private boolean enFonctionnement;
    // port d'ecoute du serveur
    private static final int PORT = 1234;

    // fichier de sauvegarde de l'index
    private static final String FICHIER_INDEX = "data/index.ser";

    /// ?????? IA

    // le constructeur de la classe
    public ServeurIndexeur(String repertoireBase) {
        this.indexInverse = chargerIndex();
        this.indexeurDeFichier = new IndexeurDeFichier(indexInverse);
        this.moteurDeRecherche = new MoteurDeRecherche(indexInverse, indexeurDeFichier);
        this.explorateur = new ExplorateurDeRepertoire(new File(repertoireBase), indexeurDeFichier);
        this.enFonctionnement = true;
    }

    // Demarrage du serveur
    public void demarrer() {
        System.out.println("        Demarage du ServeurIndexeurs");
        // exploration et indexation des fichiers
        Thread threadExploration = new Thread(() -> {
            explorateur.explorer();
            System.out.println("        Exploration terminé ");
        });
        threadExploration.start();

        // thread2 : sauvegarde periodique de l'index
        Thread threadSauvegarde = new Thread(() -> {
            sauvegarderPeriodiquement();
        });
        threadSauvegarde.start();

        // thread3: ecoute des connexions clients
        Thread threadServeur = new Thread(() -> {
            ecouterClients();
        });
        threadServeur.start();
    }
    // sauvegarde periodiquement

    private void sauvegarderPeriodiquement() {
        while (enFonctionnement) {
            try {
                Thread.sleep(5 * 60 * 1000); // Attendre 5min
                sauvegarderIndex();
            } catch (InterruptedException e) {
                System.out.println("Sauvegarder Interrompu");
            }
        }
    }

    public void sauvegarderIndex() {
        try {
            // creer un dossier data s'il n'existe pas
            new File("data").mkdirs();

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHIER_INDEX));
            oos.writeObject(indexInverse);
            oos.close();
            System.out.println("Index sauvegarder dans : " + FICHIER_INDEX);
        } catch (IOException e) {
            System.out.println("Eurreur lors de la sauvegardev ");
            e.printStackTrace();
        }

    }

    // chargement de l'index
    private IndexInverse chargerIndex() {
        File fichier = new File(FICHIER_INDEX);

        // si le fichier existe, on charge l'index
        if (fichier.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FICHIER_INDEX));
                IndexInverse index = (IndexInverse) ois.readObject();
                ois.close();
                System.out.println("Index chargé depuis : " + FICHIER_INDEX);
                return index;
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Erreur lors du chargement de l'index");

            }
        }

        // sinon on cré une nouvelle index vide
        System.out.println("Nouvelle index creer");
        return new IndexInverse();
    }

    // Ecoute des clients
    private void ecouterClients(){
        try{
            serverSocket = new ServerSocket(PORT);
            System.out.println("serveur en ecoute sue le port : "+PORT);

            while(enFonctionnement){
               // Attendre que le client se connecte
                    Socket socketClient = serverSocket.accept();
                    System.out.println("Nouveau client connecté : "+socketClient.getInetAddress());

                    // creer un GestionnaireClient pour ce client
                    GestionnaireClient gestionnaire = new GestionnaireClient(socketClient, moteurDeRecherche);
                    gestionnaire.start();
                
            }
        } catch(IOException e){
            System.out.println("Erreur du serveur ");
            e.printStackTrace();
        }
    }

    // Arret du serveur

    public void arreter(){
        enFonctionnement = false;
        sauvegarderIndex(); /// sauvegarde finale avant l'arrêt
        try{
            if(serverSocket != null){
                serverSocket.close();
            }

        } catch (IOException e){
            System.out.println("Erreur lors de l'arret du serveur");
        }
        System.out.println("Serveur arreté");
    }

    // Main
    public static void main(String[] args){
        ServeurIndexeur serveur = new ServeurIndexeur("/home/users/documents");
        serveur.demarrer();
    }

}
