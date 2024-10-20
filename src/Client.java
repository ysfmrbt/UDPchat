import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
    DatagramSocket clientSocket;
    InetAddress address;
    int port;
    String username;

    public static void main(String[] args) throws Exception {

        // Définition de l'adresse et du port du serveur
        InetAddress serverAddress = InetAddress.getByName("localhost");
        int serverPort = 1234;

        // Création d'un socket pour le client
        try (DatagramSocket cs = new DatagramSocket()) {
            // Création d'un client avec le socket créé
            Client client = new Client(cs);
            System.out.println("Client started on " + client.getAddress().getHostAddress()+ ":" + client.getPort());
            // Création d'un scanner pour lire les entrées de l'utilisateur
            Scanner sc = new Scanner(System.in);
            while (true) {
                // Si le nom d'utilisateur n'est pas défini, demander à l'utilisateur de le définir
                if (client.getUsername().isBlank()) {
                    System.out.print("Enter your username: ");
                    String username = sc.nextLine();
                    client.setUsername(username);
                    String message = "!setUsername " + username;
                    try {
                        Shared.sendPacket(message.getBytes(), client.getClientSocket(), serverAddress, serverPort);
                    } catch (Exception e) {
                        System.out.println("Error sending username: " + e.getMessage());
                        break;
                    }
                }
                // Demander à l'utilisateur d'entrer un message
                System.out.print("Enter a message ('exit' to quit): ");
                String data = sc.nextLine();

                // Si l'utilisateur entre 'exit', quitter la boucle
                if (data.equalsIgnoreCase("exit")) {
                    break;
                }
                // Convertir le message en tableau de bytes et l'envoyer au serveur
                byte[] buff = data.getBytes();
                try {
                    Shared.sendPacket(buff, client.getClientSocket(), serverAddress, serverPort);
                } catch (Exception e) {
                    System.out.println("Error sending packet: " + e.getMessage());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error creating client: " + e.getMessage());
        }
    }


    // Constructeur de la classe Client avec un socket en paramètre
    public Client(DatagramSocket socket)  {
        this.clientSocket = socket;
        this.address = socket.getLocalAddress();
        this.port = socket.getLocalPort();
        this.username = "";
    }

    // Constructeur de la classe Client avec une adresse et un port en paramètre
    public Client(InetAddress address, int port) throws Exception {
        this.address = address;
        this.port = port;
        try {
            this.clientSocket = new DatagramSocket(port, address);
        } catch (Exception e) {
            System.out.println("Error creating client socket: " + e.getMessage());
            throw e;
        }
        this.username = "";
    }

    // Constructeur de la classe Client avec une adresse, un port et un nom d'utilisateur en paramètre
    public Client(InetAddress address, int port, String username) throws Exception {
        this.address = address;
        this.port = port;
        try {
            this.clientSocket = new DatagramSocket(port, address);
        } catch (Exception e) {
            System.out.println("Error creating client socket: " + e.getMessage());
            throw e;
        }
        this.username = username;
    }

    // Méthode pour changer le nom d'utilisateur
    public void setUsername(String username) {
        this.username = username;
    }

    // Méthode pour récupérer le socket du client
    public DatagramSocket getClientSocket() {
        return this.clientSocket;
    }

    // Méthode pour récupérer l'adresse local du client
    public InetAddress getAddress() {
        return this.address;
    }

    // Méthode pour récupérer le port local du client
    public int getPort() {
        return this.port;
    }

    // Méthode pour récupérer le nom d'utilisateur du client
    public String getUsername() {
        return this.username;
    }
}
