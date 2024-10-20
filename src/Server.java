import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Server {

    DatagramSocket serverSocket;
    int serverPort;
    InetAddress serverAddress;
    static List<Client> clients;

    public static void main(String[] args) throws Exception {
        // Création d'un serveur sur le port 1234
        Server server = new Server(InetAddress.getByName("localhost"), 1234);
        try {
            // Démarrage du serveur
            server.start();

            // Gestion des requêtes clients
            server.handleClientRequest();
        } catch (Exception e) {
            System.out.println("Error starting server: " + e.getMessage());
        } finally {
            server.stop();
        }
    }

    // Constructeur de la classe Server qui prend en paramètre l'adresse du serveur et le port du serveur
    public Server(InetAddress serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        Server.clients = new ArrayList<>();
    }

    // Méthode pour démarrer le serveur
    public void start() throws Exception {
        try {
            this.serverSocket = new DatagramSocket(this.serverPort);
            System.out.println("Server started on " + this.serverSocket.getLocalAddress().getHostAddress() + ":" + this.serverPort);

        } catch (Exception e) {
            System.out.println("Error creating server socket: " + e.getMessage());
            throw e;
        }
    }

    // Méthode pour gérer les requêtes clients
    public void handleClientRequest() {
        String username = "";
        while (true) {
            try {
                // Réception des paquets clients
                DatagramPacket packet = Shared.receivePacket(65535, this.serverSocket);
                String data = Shared.data(packet);

                // Récupération de l'adresse et du port du client
                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                // Ajout du client à la liste des clients
                addClient(address, port);

                // Vérification si le message est une commande pour changer le nom d'utilisateur
                if (data.startsWith("!setUsername")) {
                    username = data.substring(13);
                    System.out.println("Client " + address.getHostAddress() + ":" + port + " set their username to " + username);
                    continue;
                }

                // Affichage du message du client
                System.out.println(Shared.formatMessage(data, username));

                // Envoi d'un message au client
                Shared.sendPacket("Hello from server".getBytes(), this.serverSocket, address, port);

            } catch (Exception e) {
                System.out.println("Error handling client request: " + e.getMessage());
                break;
            }
        }
    }

    // Méthode pour ajouter un client à la liste des clients
    public void addClient(InetAddress address, int port) {
        Client foundClient = null;
        for (Client client : clients) {
            if (client == null) {
                continue;
            }
            if (client.getAddress().equals(address) && client.getPort() == port) {
                return; // Client already in the list
            } else {
                foundClient = client;
                break;
            }
        }
        clients.add(foundClient);
    }

    // Méthode pour arrêter le serveur
    public void stop() {
        this.serverSocket.close();
        System.out.println("Server stopped");
    }
}
