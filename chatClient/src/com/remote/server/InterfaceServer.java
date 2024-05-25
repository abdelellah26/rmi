package com.remote.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import com.remote.client.InterfaceClient;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public interface InterfaceServer extends Remote{
    
    //Pour vérifier si Email Exists
    boolean isEmailExists(String email) throws RemoteException;
    
    //
    String getName(String email) throws RemoteException;
   // Méthode pour enregistrer un nouvel utilisateur dans la base de données
    void registerUser(String name, String email, String password) throws RemoteException;
    //Méthode pour login
    boolean login(String email, String password) throws RemoteException;
    //cette fonction pour conecter avec db
    Connection getConnection() throws RemoteException;
    
    Integer getId(String email) throws RemoteException;
    
    String getEmail(int id)throws RemoteException;
    
    //
    void broadcastM(String message,String email,List<String> list) throws RemoteException;
    //
    

    //cette fonction pour distribuer un message vers tous clients connectées
    void broadcastMessage(String message,List<String> list) throws RemoteException;
    

    Object[][] getMessages(String email) throws RemoteException;
    
    //cette fonction pour distribuer un fichier partagée vers tous clients connectes
    void broadcastMessage(ArrayList<Integer> inc,List<String> list,String filename) throws RemoteException;
    
    //cette fonction pour recupere le nom des clients connectes
    Vector<String> getListClientByEmail(String email) throws RemoteException;
    
    //cette fonction pour ajouter un client connectées a la liste des clients connectées
    void addClient(InterfaceClient client) throws RemoteException;
    
    //cette fonction pour blocker un client d"envoyer un message, mais il peut recu les messages
    void blockClient(String email,List<String> clients) throws RemoteException;
    
    //cette fonction pour supprimer totalement une liste des clients de chat (kick-out)
    void removeClient(List<String> clients) throws RemoteException;
    
    //cette fonction pour supprimer totalement un seul client de chat (kick-out)
    void removeClient(String clients) throws RemoteException;
    
    //cette fonction pour activer un client dans chat, d'apres etre dans le cas de "block"
    void reactiveClient(List<String> clients) throws RemoteException;
    
    //cette fonction pour verfifier est que un username existe deja dans le serveur ou non, car username est l'identificateur sur chat
    boolean checkUser(String email) throws RemoteException;
}