package commands;

import connections.Client;
import connections.Server;
import utils.Command;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", new String[]{}, "Liste des commandes");
    }

    @Override
    public void execute(Server server, Client client, String[] args) {
        server.sendServerMessage(client,"Liste des commandes:");
        server.commandHandler.commands.forEach((name, command) -> {
            server.sendServerMessage(client, command.getName() + " " + command.getOptions() + " -> " + command.getDescription());
        });
    }
}
