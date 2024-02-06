package bg.sofia.uni.fmi.mjt.splitwise.server.command.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;

import java.io.PrintWriter;

public class HelpCommand implements Command {

    @Override
    public void execute(String[] inputTokens, PrintWriter out) {
        out.println(new StringBuilder()
                .append("--- Help ---")
                .append(System.lineSeparator())
                .append("--disconnect").append(System.lineSeparator())
                .append("* Not authenticated: ").append(System.lineSeparator())
                .append("-- login <username> <password>").append(System.lineSeparator())
                .append("-- register <username> <password> <first_name?> <last_name?>")
                .append(System.lineSeparator())
                .append("* Authenticated: ").append(System.lineSeparator())
                .append("-- logout").append(System.lineSeparator())
                .append("-- add-friend <username>").append(System.lineSeparator())
                .append("-- create-group <group_name> <username> <username> <username?> ...")
                .append(System.lineSeparator())
                .append("-- my-friends").append(System.lineSeparator())
                .append("-- my-groups").append(System.lineSeparator())
                .append("-- my-expenses ").append(System.lineSeparator())
                .append("-- split <amount> <username> <description>")
                .append(System.lineSeparator())
                .append("-- split-group <amount> <group_name>")
                .append(System.lineSeparator())
                .append("-- approve-payment <amount> <username>")
                .toString());
    }

}
