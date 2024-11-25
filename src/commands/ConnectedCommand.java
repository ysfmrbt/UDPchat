package commands;

import connections.Client;
import connections.Server;
import utils.Command;

public class ConnectedCommand extends Command {

        public ConnectedCommand() {
            super("connected", new String[]{}, "Vérifier si le client est connecté");
        }

        @Override
        public void execute(Server server, Client client, String[] args) {
            if (client.getUsername().equals("")) {
                server.sendServerMessage(client, "Bienvenue! Veuillez vous identifiez en entrant votre nom d'utilisateur!");
            } else {
                server.sendServerMessage(client, "Vous êtes connecté en tant que " + client.getUsername());
            }
        }
}
