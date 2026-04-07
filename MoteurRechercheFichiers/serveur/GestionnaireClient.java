package moteurrecherchefichiers.serveur;

import java.net.Socket;
import java.io.*;

public class GestionnaireClient extends Thread{

    private Socket socket;
    private MoteurDeRecherche moteurDeRecherche;
    private BufferedReader entree; //sert a recevoir
    private PrintWriter sortie; //sert a envoyé

    //Constructeur
    public GestionnaireClient(Socket socket, MoteurDeRecherche moteurDeRecherche){
        this.socket = socket;
        this.moteurDeRecherche = moteurDeRecherche;
    }


    //Methode

    public void run(){
         try {
            
            entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sortie = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            boolean active = true;
            String requete;
            while(active &&(requete = entree.readLine()) != null ){
                
                if(requete.equalsIgnoreCase("quit")){
                    sortie.println("Connexion fermée");
                    active = false;
                    
                }else{
                    traiterRequete(requete);
                }

            }  
    }catch(IOException e){
        e.printStackTrace();
    }finally{
        try{
            if(entree != null) entree.close();
            if(sortie != null) sortie.close();
            if(socket != null) socket.close();

        }catch(IOException e){
        e.printStackTrace();
    }
    }
}


private void traiterRequete(String requete){
    if (requete.toUpperCase().startsWith("RECHERCHE_META")){
        String mot = requete.substring(15).trim();
        String resultat = moteurDeRecherche.rechercherMeta(mot);
        sortie.println(resultat);
    }    
    else if(requete.toUpperCase().startsWith("RECHERCHE")){
        String mot = requete.substring(10).trim();
        String resultat = moteurDeRecherche.rechercher(mot);
        sortie.println(resultat);
     
    }else if(requete.toUpperCase().startsWith("TELECHARGER")){
        String chemin = requete.substring(12).trim();
        envoyerFichier(chemin);
    }else if(requete.equalsIgnoreCase("STATUS")){
        String etat = moteurDeRecherche.donnerEtatIndexeur();
        sortie.println(etat);
    }else{
        sortie.println("Commande inconnue");
    }
}

private void envoyerFichier(String chemin){
    try{
        File fichier = new File(chemin);
        if(!fichier.exists() || !fichier.isFile()){
            sortie.println("Erreur");
            return;
        }
        sortie.println("OK");
        sortie.println(fichier.getName());
        sortie.println(fichier.length());
        sortie.flush();

        BufferedInputStream fichierEntree = new BufferedInputStream(new FileInputStream(fichier));
        OutputStream socketSortie = socket.getOutputStream();
        byte [] buffer = new byte [4096];
        int nbOctetLu;
        while((nbOctetLu = fichierEntree.read(buffer))!=-1){
            socketSortie.write(buffer,0,nbOctetLu);
        }
        socketSortie.flush();
        fichierEntree.close();
        }catch(IOException e){
            sortie.println("Erreur");
        }

}
}