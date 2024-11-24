package utils;

import connections.Client;
import connections.Server;

public abstract class Command {
    String name;
    String[] options;
    String description;

    public Command(String name, String[] options, String description) {
        this.name = name;
        this.options = options;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getOptions() {
        StringBuilder optionsBuilder = new StringBuilder();
        for (int i = 0; i < options.length; i++) {
            optionsBuilder.append(options[i]);
            if (i < options.length - 1) {
                optionsBuilder.append(" ");
            }
        }
        return optionsBuilder.toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public abstract void execute(Server server, Client client, String[] args);

    public String getDescription() {
        return this.description;
    }
}
