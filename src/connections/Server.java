package connections;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import commands.CommandHandler;
import utils.Shared;

public class Server {

    DatagramSocket serverSocket;
    int serverPort;
    InetAddress serverAddress;
    public static List<Client> clients;
    public CommandHandler commandHandler;

    public static void main(String[] args) throws Exception {
        // Création d'un serveur sur le port 1234
        Server server = new Server(InetAddress.getByName("localhost"), 1234);
        try {
            // Démarrage du serveur
            server.start();

            // Gestion des requêtes clients
            server.handleClientRequest();
        } catch (Exception e) {
            System.out.println("Erreur de démarrage du serveur: " + e.getMessage());
        } finally {
            server.stop();
        }
    }

    // Constructeur de la classe Server qui prend en paramètre l'adresse du serveur et le port du serveur
    public Server(InetAddress serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        Server.clients = new ArrayList<>();
        this.commandHandler = new CommandHandler();
    }

    // Méthode pour démarrer le serveur
    public void start() throws Exception {
        try {
            this.serverSocket = new DatagramSocket(this.serverPort);
            System.out.println("Le serveur a commencé à " + this.serverSocket.getLocalAddress().getHostAddress() + ":" + this.serverPort);

        } catch (Exception e) {
            System.out.println("Erreur de création de socket: " + e.getMessage());
            throw e;
        }
    }

    // Méthode pour gérer les requêtes clients
    public void handleClientRequest() {
        while (true) {
            try {
                // Réception des paquets clients
                DatagramPacket packet = Shared.receivePacket(65535, this.serverSocket);
                String data = Shared.data(packet);

                // Récupération de l'adresse et du port du client
                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                // Ajout du client à la liste des clients
                Client client = addClient(address, port);

                // Vérification si le message est une commande
                if (data.startsWith("!")) {
                    commandHandler.handleCommand(this, client, data);
                } else {
                    // Broadcast du message à tous les clients
                    broadcast(client, data);
                    System.out.println(Shared.formatMessage(data, client.getUsername()));
                }

            } catch (Exception e) {
                System.out.println("Erreur de reception: " + e.getMessage());
                break;
            }
        }
    }

    // Méthode pour ajouter un client à la liste des clients
    public Client addClient(InetAddress address, int port) {
        Client client = new Client(address, port);
        if (clients.stream().noneMatch(c -> c.getAddress().equals(address) && c.getPort() == port)) {
            clients.add(client);
        } else if (clients.stream().anyMatch(c -> c.getAddress().equals(address) && c.getPort() == port)) {
            client = clients.stream().filter(c -> c.getAddress().equals(address) && c.getPort() == port).findFirst().get();
        }
        return client;
    }

    public void broadcast(Client client, String message) {
        for (Client c : clients) {
            try {
                if (c == client) {
                    continue;
                }
                Shared.sendPacket(Shared.formatMessage(message, client.getUsername()).getBytes(), this.serverSocket, c.getAddress(), c.getPort());
            } catch (Exception e) {
                System.out.println("Erreur de diffusion: " + e.getMessage());
            }
        }
    }

    // Méthode pour arrêter le serveur
    public void stop() {
        this.serverSocket.close();
        System.out.println("Le serveur a été arrêté.");
    }

    public void sendMessage(Client client, Client receiver, String message) {
        for (Client c : clients) {
            if (c == receiver) {
                break;
            }
        }
        try {
            Shared.sendPacket(Shared.formatPrivateMessage(message, receiver.getUsername(), client.getUsername()).getBytes(), this.serverSocket, receiver.getAddress(), receiver.getPort());
        } catch (Exception e) {
            System.out.println("Erreur d'envoi d'un message: " + e.getMessage());
        }
    }

    public void sendServerMessage(Client client, String message) {
        try {
            Shared.sendPacket(Shared.formatMessage(message, "Server").getBytes(), this.serverSocket, client.getAddress(), client.getPort());
        } catch (Exception e) {
            System.out.println("Erreur d'envoi d'un message: " + e.getMessage());
        }
    }

    public void disconnectClient(Client client) {
        clients.remove(client);
    }
}
