package commands;

import connections.Client;
import connections.Server;
import utils.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    final Map<String, Command> commands = new HashMap<>();

    public CommandHandler() {
        registerCommand(new SetUsernameCommand());
        registerCommand(new PrivateMessageCommand());
        registerCommand(new HelpCommand());
        registerCommand(new ConnectedCommand());
        registerCommand(new DisconnectCommand());
    }

    public void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    public void handleCommand(Server server, Client client, String input) {
        String[] parts = input.split(" ");
        String commandName = parts[0].substring(1); // Supprimer le préfixe
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, parts.length - 1);

        // Récupérer la commande
        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(server, client, args);
        } else {
            server.sendServerMessage(client, "Commande invalide: " + commandName);
        }
    }
}
