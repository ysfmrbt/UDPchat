package commands;

import connections.Client;
import connections.Server;
import utils.Command;

public class DisconnectCommand extends Command {

        public DisconnectCommand() {
            super("disconnect", new String[]{}, "Déconnecter le client");
        }

        @Override
        public void execute(Server server, Client client, String[] args) {
            server.sendServerMessage(client, "Vous avez été déconnecté");
            System.out.println(client.getUsername() + " a été déconnecté");
            server.disconnectClient(client);
        }
}
