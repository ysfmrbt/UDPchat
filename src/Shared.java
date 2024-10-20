import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Shared {

    // Méthode pour obtenir les données d'un paquet
    public static String data(DatagramPacket packet) {
        return new String(packet.getData(), 0, packet.getLength());
    }

    // Méthode pour envoyer un paquet
    public static void sendPacket(byte[] data, DatagramSocket socket, InetAddress remoteAddress, int remotePort) throws Exception {
        DatagramPacket packet = new DatagramPacket(data, data.length, remoteAddress, remotePort);
        try {
            socket.send(packet);
        } catch (Exception e) {
            System.out.println("Error sending packet: " + e.getMessage());
            throw e;
        }
    }

    // Méthode pour recevoir un paquet
    public static DatagramPacket receivePacket(int bufferSize, DatagramSocket localSocket) throws Exception {
        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            localSocket.receive(packet);
            return packet;
        } catch (Exception e) {
            System.out.println("Error receiving packet: " + e.getMessage());
            throw e;
        }
    }

    // Méthode pour formater un message
    public static String formatMessage(String message, String username) {
        // Add timestamp to message
        // Add username to message
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return "[" + dtf.format(now) + "] [" + username + "] " + message;
    }
}
