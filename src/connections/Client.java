package connections;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import utils.Shared;

public class Client {
    DatagramSocket clientSocket;
    InetAddress address;
    int port;
    String username;

    public Client() {

    }

    public static void main(String[] args) throws Exception {

        // Définition de l'adresse et du port du serveur
        InetAddress serverAddress = InetAddress.getByName("localhost");
        int serverPort = 1234;

        // Création d'un client et connexion au serveur
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            Client client = new Client(clientSocket);
            client.connect(serverAddress, serverPort);
        } catch (Exception e) {
            System.out.println("Erreur de connexion au serveur: " + e.getMessage());
        }
    }


    // Constructeur de la classe connections.Client avec un socket en paramètre
    public Client(DatagramSocket socket) {
        this.clientSocket = socket;
        this.address = socket.getLocalAddress();
        this.port = socket.getLocalPort();
        this.username = "";
    }

    // Constructeur de la classe connections.Client avec une adresse et un port en paramètre
    public Client(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.username = "";
    }

    // Constructeur de la classe connections.Client avec une adresse, un port et un nom d'utilisateur en paramètre
    public Client(InetAddress address, int port, String username) {
        this.address = address;
        this.port = port;
        this.username = username;
    }

    // Méthode pour se connecter au serveur
    public void connect(InetAddress serverAddress, int serverPort) {
        final boolean[] running = {true};
        Thread sendThread = new Thread(() -> {
            try {
                System.out.println("Client " + this.address.getHostAddress() + ":" + this.port + " a connecté au serveur " + serverAddress.getHostAddress() + ":" + serverPort);
                Scanner sc = new Scanner(System.in);
                Shared.sendPacket("!connected".getBytes(), this.getClientSocket(), serverAddress, serverPort);
                String pattern = "^((?=[A-Za-z0-9@])(?![_\\\\-]).)*$";
                while (running[0]) {
                    if (this.getUsername().isBlank() | !this.getUsername().matches(pattern)) {
                        String username = sc.nextLine();
                        this.setUsername(username);
                        String message = "!setUsername " + this.getUsername();
                        try {
                            Shared.sendPacket(message.getBytes(), this.getClientSocket(), serverAddress, serverPort);
                        } catch (Exception e) {
                            System.out.println("Erreur d'envoi du nom d'utilisateur: " + e.getMessage());
                        }
                    } else {
                        System.out.println("[Info] Entrer un message, ou 'exit' pour quitter");
                        String data = sc.nextLine();
                        if (data.equalsIgnoreCase("exit")) {
                            Shared.sendPacket("!disconnect".getBytes(), this.getClientSocket(), serverAddress, serverPort);
                            running[0] = false;
                            break;
                        }
                        byte[] buff = data.getBytes();
                        Shared.sendPacket(buff, this.clientSocket, serverAddress, serverPort);
                    }
                }
            } catch (Exception e) {
                System.out.println("Erreur de connexion: " + e.getMessage());
            }
        });

        Thread receiveThread = new Thread(() -> {
            while (running[0]) {
                try {
                    DatagramPacket packet = Shared.receivePacket(65535, this.clientSocket);
                    System.out.println(Shared.data(packet));
                } catch (Exception e) {
                    if (!running[0]) {
                        System.out.println("Erreur de réception: " + e.getMessage());
                    }

                }
            }
        });
        sendThread.start();
        receiveThread.start();
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
