import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
    DatagramSocket clientSocket;
    InetAddress address;
    int port;
    String username;

    public static void main(String[] args) throws Exception {
        InetAddress serverAddress = InetAddress.getByName("localhost");
        int serverPort = 1234;

        try (DatagramSocket cs = new DatagramSocket()) {
            Client client = new Client(cs);
            System.out.println("Client started on " + client.getAddress().getHostAddress()+ ":" + client.getPort());
            Scanner sc = new Scanner(System.in);
            while (true) {
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
                System.out.print("Enter a message ('exit' to quit): ");
                String data = sc.nextLine();
                if (data.equalsIgnoreCase("exit")) {
                    break;
                }
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

    public Client(DatagramSocket socket)  {
        this.clientSocket = socket;
        this.address = socket.getLocalAddress();
        this.port = socket.getLocalPort();
        this.username = "";
    }

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

    public void setClientSocket(DatagramSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public DatagramSocket getClientSocket() {
        return this.clientSocket;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public String getUsername() {
        return this.username;
    }
}
