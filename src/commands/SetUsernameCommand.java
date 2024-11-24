package commands;

import connections.Client;
import connections.Server;
import utils.Command;

public class SetUsernameCommand extends Command {

    public SetUsernameCommand() {
        super("setUsername", new String[]{"username"}, "Définir le nom d'utilisateur");
    }

    @Override
    public void execute(Server server, Client client, String[] args) {
        if (args.length < 1) {
            System.out.println("Nom d'utilisateur manquant.");
            return;
        }
        String username = args[0];
        if (Server.clients.stream().anyMatch(c -> c.getUsername().equals(username))) {
            server.sendServerMessage(client, "Nom d'utilisateur déja existe.");
        } else if (username.equalsIgnoreCase("server")) {
            server.sendServerMessage(client, "Nom d'utilisateur invalide.");
        } else {
            client.setUsername(username);
            // Mettre à jour client dans la liste des clients
            for (int i = 0; i < Server.clients.size(); i++) {
                if (Server.clients.get(i).getAddress().equals(client.getAddress()) && Server.clients.get(i).getPort() == client.getPort()) {
                    Server.clients.set(i, client);
                    break;
                }
            }
            System.out.println("Client " + client.getAddress().getHostAddress() + ":" + client.getPort() + " (" + client.getUsername() + ")" + " a changé son nom d'utilisateur avec succès.");
            server.sendServerMessage(client, "Votre nom d'utilisateur a été changé avec succès.");
        }
    }

}