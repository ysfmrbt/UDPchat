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
        Server server = new Server(InetAddress.getByName("localhost"), 1234);
        try {
            server.start();
            server.handleClientRequest();
        } catch (Exception e) {
            System.out.println("Error starting server: " + e.getMessage());
        } finally {
            server.stop();
        }
    }

    public Server(InetAddress serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        Server.clients = new ArrayList<Client>();
    }


    public void start() throws Exception {
        try {
            this.serverSocket = new DatagramSocket(this.serverPort);
            System.out.println("Server started on " + this.serverSocket.getLocalAddress().getHostAddress() + ":" + this.serverPort);

        } catch (Exception e) {
            System.out.println("Error creating server socket: " + e.getMessage());
            throw e;
        }
    }

    public void handleClientRequest() {
        String username = "";
        while (true) {
            try {
                DatagramPacket packet = Shared.receivePacket(65535, this.serverSocket);
                String data = Shared.data(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                addClient(address, port);
                if (data.startsWith("!setUsername")) {
                    username = data.substring(13);
                    System.out.println("Client " + address.getHostAddress() + ":" + port + " set their username to " + username);
                    continue;
                }
                System.out.println(Shared.formatMessage(data, username));
                Shared.sendPacket("Hello from server".getBytes(), this.serverSocket, address, port);
            } catch (Exception e) {
                System.out.println("Error handling client request: " + e.getMessage());
                break;
            }
        }
    }

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

    public void stop() {
        this.serverSocket.close();
        System.out.println("Server stopped");
    }
}
