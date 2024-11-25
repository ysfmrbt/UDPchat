package commands;

import connections.Client;
import connections.Server;
import utils.Command;

public class PrivateMessageCommand extends Command {

    public PrivateMessageCommand() {
        super("pm", new String[]{"username", "message"}, "Envoyer un message privé à un utilisateur");
    }

    @Override
    public void execute(Server server, Client client, String[] args) {
        if (args.length < 2) {
            server.sendServerMessage(client,"Nom d'utilisateur ou message manquant.");
            return;
        }
        String username = args[0];
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]);
            if (i < args.length - 1) {
                messageBuilder.append(" ");
            }
        }
        String message = messageBuilder.toString();
        Client receiver = Server.clients.stream().filter(c -> c.getUsername().equals(username)).findFirst().orElse(null);
        if (receiver == null) {
            server.sendServerMessage(client, "Client " + username + " n'existe pas.");
        } else {
            server.sendMessage(client, receiver, message);
            System.out.println(client.getUsername() + " a envoyé un message privé à " + receiver.getUsername() + ": " + message);
        }
    }
}
