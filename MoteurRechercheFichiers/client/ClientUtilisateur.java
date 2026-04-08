package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientUtilisateur {
    
    // Attributs
    private Socket socket;
    private BufferedReader entree;  //pour recevoir les reponses du serveur
    private PrintWriter sortie;  //pour envoyer des requetes au serveur
    private Scanner scanner;   // pour lire les entrés de l'utilisateur

    // Address et port du serveur
    private static final int PORT = 1234;

    // Constructeur
    public ClientUtilisateur(String addresseServeur) throws IOException{
        this.socket = new Socket(addresseServeur, PORT);  // connexion au serveur
        this.entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.sortie = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        this.scanner = new Scanner(System.in);
        System.out.println("Connecté au serveur : "+addresseServeur+": "+PORT);

    }

    //Demarrage du client
    public void demarer(){
        afficherAide();

        boolean active = true;
        while(active){
            System.out.println("\n > entrer une commande : ");
                String commande = scanner.nextLine().trim();

                if(commande.equalsIgnoreCase("quit")){
                    sortie.println("quit");
                    active = false;
                }
                else if(commande.toUpperCase().startsWith("RECHERCHE")){
                    sortie.println(commande);
                    recevoirReponse();

                }
                else if(commande.toUpperCase().startsWith("TELECHARGER")){
                    sortie.println(commande);
                    recevoirFichier(commande);

                }
                else if(commande.equalsIgnoreCase("AIDE")){
                    afficherAide();

                }
                else{
                    System.out.println("Commands inconnue. Taper AIDE pour voir les commandes");
                }
            
        }
        fermer();
    }

    // recevoirReponse()
    private void recevoirReponse(){
        try{
            System.out.println("        Resultats ");
            String ligne;

            //on lit toute les reponses du serveur
            while((ligne = entree.readLine()) != null && !ligne.isEmpty()){
                System.out.println(ligne);
            }
        } catch(IOException e){
            System.out.println("Erreur de la reception de la reponse ");
            e.printStackTrace();
        }
    }

    // methode recevoirFichier
    private void recevoirFichier(String commande){
        try{
            // lire la reponse du serveur
            String statut = entree.readLine();

            if(!statut.equals(("Ok"))){
                System.out.println("Erreur: fichier introuvable sur le serveur ");
                return;
            }

            // Lire le nom et la taille du fichier
            String nomFichier = entree.readLine();
            Long tailleFichier = Long.parseLong(entree.readLine());

            System.out.println("Telechargemetn de : "+nomFichier+"("+tailleFichier+"octets )");

            // recevoir les octets du fichier
            InputStream socketeEntree = socket.getInputStream();
            FileOutputStream fichierSortie = new FileOutputStream("telechargements/"+nomFichier);

            byte[] buffer = new byte[4096];
            long octetsRecus = 0;
            int nbOctetsLus;

            while(octetsRecus < tailleFichier &&
                (nbOctetsLus = socketeEntree.read(buffer)) != -1){
                    fichierSortie.write(buffer, 0, nbOctetsLus);
                    octetsRecus += nbOctetsLus;
            }
            fichierSortie.close();
            System.out.println("Fichier télechargé avec succès !");
        } catch(IOException e){
            System.out.println("Erreur lors du telechargement ");
            e.printStackTrace();
        }
    }

    // fermeture
    private void fermer(){
        try{
            if(entree != null){
                entree.close();
            }
            if(sortie != null){
                sortie.close();
            }
            if(socket != null){
                socket.close();
            }
            if(scanner != null){
                scanner.close();
            }
            System.out.println("Déconnecté du serveur ");
        } catch(IOException e){
            System.out.println("Erreur lors de la fermeture ");
            e.printStackTrace();
        }
    }

    //AIDE

    private void afficherAide(){
        System.out.println(" Commande disponibles");
        System.out.println("------------------------");
        System.out.println("RECHERCHE <mot>             -> chercher un mot");
        System.out.println("RECHERCHE <mot1> <mot2>     -> chercher plusieurs mots");
        //System.out.println("ANNOTER <chemin> <mot clé>");
        System.out.println("TELECHARGER <chemin>        -> telecharger un fichier ");
        System.out.println("AIDE                        -> aide");
        System.out.println("quit                        -> se deconnecté");

    }

    //Main

    public static void main(String[] args) {
        try{
            // localhost si client et serveur sont sur la mm machine
            ClientUtilisateur client = new ClientUtilisateur("localhost");
            client.demarer();

        } catch(IOException e){
            System.out.println("impossible de se connecter au serveur");
            e.printStackTrace();
        }
    }

}
