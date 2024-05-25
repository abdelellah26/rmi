
package com.remote.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import com.remote.client.InterfaceClient;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServer extends UnicastRemoteObject implements InterfaceServer{
    private final ArrayList<InterfaceClient> clients; //liste contient tous les clients mais qui ne sont pas bloqué 
    private final ArrayList<InterfaceClient> blockedClients; //liste contient tous les clients bloqués
    
    //constructeur
    public ChatServer() throws RemoteException{
        super();
        this.clients = new ArrayList<>();
        blockedClients = new ArrayList<>();
    
    }
    
    /**
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    @Override
    public synchronized Connection getConnection() throws RemoteException{
        Connection con = null;
        try {
        con = DriverManager.getConnection("jdbc:mysql://localhost/chat", "root", "");
            return con;
        } catch (SQLException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        

    }
    
    @Override 
    public synchronized boolean isEmailExists(String email) throws RemoteException {
        boolean exists = false;
        try {
            try (Connection conn = getConnection()) {
                PreparedStatement st = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?");
                st.setString(1, email);
                ResultSet rs = st.executeQuery();

                // Vérification du résultat de la requête
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        exists = true;
                    }
                }
            }
        } catch (SQLException ex) {
            // Propager l'exception pour que le code appelant puisse la gérer
            throw new RemoteException("Erreur lors de la vérification de l'existence de l'email", ex);
        }
        return exists;
    }
    
    @Override
    public synchronized String getName(String email) throws RemoteException {
        String name = null;
        try {
            try (Connection conn = getConnection()) {
                PreparedStatement st = conn.prepareStatement("SELECT name FROM users WHERE email = ?");
                st.setString(1, email);
                ResultSet rs = st.executeQuery();

                // Vérification si un résultat est renvoyé
                if (rs.next()) {
                    name = rs.getString("name");
                }
            }
        } catch (SQLException ex) {
            // Propager l'exception pour que le code appelant puisse la gérer
            throw new RemoteException("Erreur lors de la récupération du nom de l'utilisateur", ex);
        }
        return name;
    }



    // Méthode pour enregistrer un nouvel utilisateur dans la base de données
    @Override 
    public synchronized void registerUser(String name, String email, String password) throws RemoteException {
        // Vérifier d'abord si l'e-mail existe déjà
        if (isEmailExists(email)) {
            System.out.println("Email already exists.");
            return;
        }

        // Connexion à la base de données et insertion de l'utilisateur
        try (Connection conn = getConnection()) {
            // Requête SQL pour insérer un nouvel utilisateur
            String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, password);

            // Exécution de la requête
            statement.executeUpdate();
            System.out.println("User registered successfully.");
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    @Override
    public synchronized boolean login(String email, String password) throws RemoteException {
        try (Connection conn = getConnection()) {
            // Requête SQL pour récupérer l'utilisateur avec l'e-mail et le mot de passe donnés
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, password);

            // Exécution de la requête
            ResultSet resultSet = statement.executeQuery();

            // Si l'utilisateur existe dans la base de données, retourner true, sinon false
            return resultSet.next();
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
            return false;
        }
    }
    
    
    @Override
    public synchronized Integer getId(String email) throws RemoteException{
             Integer id = null;
        try {
            try (Connection conn = getConnection()) {
                PreparedStatement st = conn.prepareStatement("SELECT id FROM users WHERE email = ?");
                st.setString(1, email);
                ResultSet rs = st.executeQuery();

                // Vérification si un résultat est renvoyé
                if (rs.next()) {
                    id = rs.getInt("id");
                }
            }
        } catch (SQLException ex) {
            // Propager l'exception pour que le code appelant puisse la gérer
            throw new RemoteException("Erreur lors de la récupération du id de l'utilisateur", ex);
        }
        return id;
    }
    
        @Override
    public synchronized String getEmail(int id) throws RemoteException{
             String email = null;
        try {
            try (Connection conn = getConnection()) {
                PreparedStatement st = conn.prepareStatement("SELECT email FROM users WHERE id = ?");
                st.setInt(1, id);
                ResultSet rs = st.executeQuery();

                // Vérification si un résultat est renvoyé
                if (rs.next()) {
                    email = rs.getString("email");
                }
            }
        } catch (SQLException ex) {
            // Propager l'exception pour que le code appelant puisse la gérer
            throw new RemoteException("Erreur lors de la récupération du email de l'utilisateur", ex);
        }
        return email;
    }
    
    
    
    @Override
    public synchronized void broadcastM(String message,String email,List<String> list) throws RemoteException {

            for (InterfaceClient client : clients) {
                for(int i=0;i<list.size();i++){
                    if(client.getEmail().equals(list.get(i))){
                        //POUR INSERER LES DONNES DANS DB
                                

                                // Connexion à la base de données et insertion de l'utilisateur
                                try (Connection conn = getConnection()) {
                                    Integer id_sender = getId(email);
                                    Integer id_resever = getId(list.get(i));

                                    // Requête SQL pour insérer un nouvel utilisateur
                                    String sql = "INSERT INTO messages (message,id_sender,id_resever) VALUES (?, ?, ?)";
                                    PreparedStatement statement = conn.prepareStatement(sql);
                                    statement.setString(1, message);
                                    statement.setInt(2, id_sender);
                                    statement.setInt(3, id_resever);


                                    // Exécution de la requête
                                    statement.executeUpdate();
                                } catch (SQLException ex) {
                                    System.out.println("Error: " + ex.getMessage());
                                }
                        
                    }
                }
            }
    }
    
@Override
public synchronized Object[][] getMessages(String email) throws RemoteException {
    List<Object[]> messagesList = new ArrayList<>();
    int id = getId(email);

    try (Connection conn = getConnection()) {
        // Préparer la requête SQL pour récupérer les messages envoyés et reçus par l'utilisateur
        String sql = "SELECT message, id_sender, id_resever FROM messages WHERE id_sender = ? OR id_resever = ?";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            st.setInt(2, id);

            // Exécuter la requête SQL
            try (ResultSet rs = st.executeQuery()) {
                // Parcourir les résultats et ajouter les messages à la liste
                while (rs.next()) {
                    String message = rs.getString("message");
                    int idSender = rs.getInt("id_sender");
                    int idReceiver = rs.getInt("id_resever");

                    // Déterminer l'expéditeur et le destinataire du message
                    String otherUserEmail = id == idSender ? getEmail(idReceiver) : getEmail(idSender);

                    Object[] messageInfo = {message, otherUserEmail};
                    messagesList.add(messageInfo);
                }
            }
        }
    } catch (SQLException ex) {
        // Gérer les exceptions de la base de données
        throw new RemoteException("Erreur lors de la récupération des messages", ex);
    }

    // Convertir la liste de messages en un tableau 2D
    Object[][] messages = new Object[messagesList.size()][];
    for (int i = 0; i < messagesList.size(); i++) {
        messages[i] = messagesList.get(i);
    }

    return messages;
}
    //cette fonction pour distribuer le message vers tous les clients connectes, ou une a liste presicé par le client (disscution privée)
    @Override
    public synchronized void broadcastMessage(String message,List<String> list) throws RemoteException {
       if(list.isEmpty()){
            int i= 0;
            while (i < clients.size()){
                clients.get(i++).retrieveMessage(message);
            }
        }else{
            for (InterfaceClient client : clients) {
                for(int i=0;i<list.size();i++){
                    if(client.getEmail().equals(list.get(i))){

                        client.retrieveMessage(message);
                    }
                }
            }
        }
    }
    
    //cette fonction pour distribuer un fichier vers tous les clients connectes, ou une a liste presicé par le client (disscution privée)
    @Override
    public synchronized void broadcastMessage(ArrayList<Integer> inc, List<String> list,String filename) throws RemoteException {
        if(list.isEmpty()){
            int i= 0;
            while (i < clients.size()){
                clients.get(i++).retrieveMessage(filename,inc);
            }
        }else{
            for (InterfaceClient client : clients) {
                for(int i=0;i<list.size();i++){
                    if(client.getName().equals(list.get(i))){
                        client.retrieveMessage(filename,inc);
                    }
                }
            }
        }
    }
        
    //cette fonction pour ajouter un client connectes a la liste des clients sur le serveur
    @Override
    public synchronized void addClient(InterfaceClient client) throws RemoteException {
        this.clients.add(client);
        this.registerUser(client.getName(), client.getEmail(), client.getPassword());
        
    }
    
    //cette fonction pour recupere le nom des clients connectes
    @Override
    public synchronized Vector<String> getListClientByEmail(String email) throws RemoteException {
        Vector<String> list = new Vector<>();

        // Requête SQL pour sélectionner les emails des utilisateurs non bloqués et non ayant bloqué l'utilisateur actuel
        String sql = "SELECT email FROM users WHERE email <> ? AND id NOT IN " +
                     "(SELECT blocked_id FROM blocked_users WHERE blocker_id = ?) AND id NOT IN " +
                     "(SELECT blocker_id FROM blocked_users WHERE blocked_id = ?)";

        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            // Définir les paramètres de la requête
            int userId = getId(email);
            st.setString(1, email);
            st.setInt(2, userId); // ID du bloqueur (basé sur son email)
            st.setInt(3, userId); // ID de l'utilisateur actuel (basé sur son email)

            try (ResultSet rs = st.executeQuery()) {
                // Parcourir les résultats et ajouter les emails à la liste
                while (rs.next()) {
                    String clientEmail = rs.getString("email");
                    list.add(clientEmail);
                }
            }
        } catch (SQLException ex) {
            // Gérer les exceptions de la base de données
            throw new RemoteException("Erreur lors de la récupération des emails", ex);
        }

        return list;
    }


    
    //cette fonction pour blocker un client d"envoyer un message, mais il peut recu les messages
    @Override
    public synchronized void blockClient(String email,List<String> clients) throws RemoteException{
   
               String sql = "INSERT INTO blocked_users (blocker_id, blocked_id) VALUES (?, ?)";

        try (
             Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            // Obtention de l'ID du bloqueur
            int blockerId = getId(email);

            // Parcourir la liste des utilisateurs bloqués
            for (String blockedEmail : clients) {
                // Obtention de l'ID de l'utilisateur bloqué
                int blockedId = getId(blockedEmail);

                // Insertion des informations de blocage dans la table blocked_users
                st.setInt(1, blockerId);
                st.setInt(2, blockedId);
                st.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RemoteException("Erreur lors du blocage des utilisateurs", ex);
        }
    }
    
    //cette fonction pour supprimer totalement une liste des clients de chat (kick-out)
    @Override
    public synchronized void removeClient(List<String> clients){
        for(int j=0;j<this.clients.size();j++){
            for(int i=0;i<clients.size();i++){
                try {
                    if(this.clients.get(j).getName().equals(clients.get(i))){
                        this.clients.get(j).closeChat(clients.get(i) + " you are removed from the chat");
                        this.clients.remove(j);
                    }
                } catch (RemoteException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
    }
    
    //cette fonction pour supprimer totalement un seul client de chat (kick-out)
    @Override
    public synchronized void removeClient(String clients){
        for(int j=0;j<this.clients.size();j++){
            try {
                if(this.clients.get(j).getEmail().equals(clients)){
                    this.clients.remove(j);
                }
            } catch (RemoteException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    //cette fonction pour activer un client dans chat, d'apres etre dans le cas de "block"
    @Override
    public synchronized void reactiveClient(List<String> clients) throws RemoteException {
        for(int j=0;j<this.blockedClients.size();j++){
            for(int i=0;i<clients.size();i++){
                try {
                    if(this.blockedClients.get(j).getName().equals(clients.get(i))){
                        this.blockedClients.get(j).openChat();
                        this.blockedClients.remove(j);
                    }
                } catch (RemoteException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
    }
    //cette fonction pour verfifier est que un username existe deja dans le serveur ou non, car username est l'identificateur sur chat
    @Override
    public boolean checkUser(String email) throws RemoteException {
        boolean exist = false;
        for(int i=0;i<clients.size();i++){
            if(clients.get(i).getEmail().equals(email)){
                exist = true;
            }
        }
        for(int i=0;i<blockedClients.size();i++){
            if(blockedClients.get(i).getEmail().equals(email)){
                exist = true;
            }
        }
        return exist;
    }
}