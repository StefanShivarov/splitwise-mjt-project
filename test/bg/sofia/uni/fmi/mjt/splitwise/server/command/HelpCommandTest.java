package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.HelpCommand;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelpCommandTest {

    private HelpCommand helpCommand;

    @Test
    void testExecute() {
        helpCommand = new HelpCommand();
        StringWriter stringWriter = new StringWriter();
        helpCommand.execute(new String[]{"help"}, new PrintWriter(stringWriter));

        assertEquals(new StringBuilder()
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
                        .append("-- create-group <group_name> ")
                        .append("<username> <username> <username?> ...")
                        .append(System.lineSeparator())
                        .append("-- my-friends").append(System.lineSeparator())
                        .append("-- my-groups").append(System.lineSeparator())
                        .append("-- my-expenses ").append(System.lineSeparator())
                        .append("-- split <amount> <username> <description>")
                        .append(System.lineSeparator())
                        .append("-- split-group <amount> <group_name>")
                        .append(System.lineSeparator())
                        .append("-- approve-payment <amount> <username>")
                        .append(System.lineSeparator())
                        .toString(),
                stringWriter.toString(),
                "should print correct output!");
    }

}
