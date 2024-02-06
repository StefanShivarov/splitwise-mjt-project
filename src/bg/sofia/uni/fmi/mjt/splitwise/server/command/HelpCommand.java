package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import java.io.PrintWriter;

public class HelpCommand implements Command {

    @Override
    public void execute(String[] inputTokens, PrintWriter out) {
        out.println("--- Help ---" +
                System.lineSeparator() +
                "--disconnect" +
                "* Not authenticated: " + System.lineSeparator() +
                "-- login <username> <password>" + System.lineSeparator() +
                "-- register <username> <password> <first_name?> <last_name?>" +
                System.lineSeparator() +
                "* Authenticated: " + System.lineSeparator() +
                "-- logout" + System.lineSeparator() +
                "-- add-friend <username>" + System.lineSeparator() +
                "-- create-group <group_name> <username> <username> <username?> ..." +
                System.lineSeparator() +
                "-- my-friends" + System.lineSeparator() +
                "-- my-groups" + System.lineSeparator() +
                "-- my-expenses " + System.lineSeparator() +
                "-- split <amount> <username> <description>" + System.lineSeparator() +
                "-- split-group <amount> <group_name>" + System.lineSeparator() +
                "-- approve-payment <amount> <username>");
    }

}
