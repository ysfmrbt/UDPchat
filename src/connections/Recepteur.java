package connections;
import java.io.*;
import java.net.*;

public class Recepteur extends Thread {
    InetAddress groupeIP;
    int port;
    MulticastSocket socketReception;

    Recepteur(InetAddress groupeIP, int port) throws Exception {
        this.groupeIP = groupeIP;
        this.port = port;
        socketReception = new MulticastSocket(port);
        socketReception.joinGroup(groupeIP);
        start();
        System.out.println("Recepteur started");
    }

    public void run() {
        DatagramPacket message;
        byte[] contenuMessage;
        String texte;

        while (true) {
            contenuMessage = new byte[1024];
            message = new DatagramPacket(contenuMessage, contenuMessage.length);
            try {
                socketReception.receive(message);
                texte = (new DataInputStream(new ByteArrayInputStream(contenuMessage))).readUTF();
                System.out.println(texte);
            } catch (Exception exc) {
                System.out.println(exc);
            }
        }
    }

    public static void main(String[] arg) throws Exception {
        InetAddress groupeIP = InetAddress.getByName("239.255.80.84");
        int port = 8084;
        new Recepteur(groupeIP, port);
    }
}
