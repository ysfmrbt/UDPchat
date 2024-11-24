package connections;
import java.io.*;
import java.net.*;

public class Emetteur extends Thread {
    InetAddress groupeIP;
    int port;
    MulticastSocket socketEmission;

    Emetteur(InetAddress groupeIP, int port) throws Exception {
        this.groupeIP = groupeIP;
        this.port = port;;
        socketEmission = new MulticastSocket();
        socketEmission.setTimeToLive(15); // pour un site
        start();
        System.out.println("Multicast started");
    }

    public void run() {
        BufferedReader entreeClavier;

        try {
            entreeClavier = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String texte = entreeClavier.readLine();
                emettre(texte);
            }
        } catch (Exception exc) {
            System.out.println(exc);
        }
    }

    void emettre(String texte) throws Exception {
        byte[] contenuMessage;
        DatagramPacket message;
        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        (new DataOutputStream(sortie)).writeUTF(texte);
        contenuMessage = sortie.toByteArray();
        message = new DatagramPacket(contenuMessage, contenuMessage.length, groupeIP, port);
        socketEmission.send(message);
    }

    public static void main(String[] arg) throws Exception {
        InetAddress groupeIP = InetAddress.getByName("239.255.80.84");
        int port = 8084;
        new Emetteur(groupeIP, port);
    }
}
