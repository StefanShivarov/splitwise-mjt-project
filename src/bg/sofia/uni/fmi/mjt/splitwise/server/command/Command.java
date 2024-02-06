package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.AuthenticationException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;

import java.io.PrintWriter;

public interface Command {

    void execute(String[] inputTokens, PrintWriter out)
            throws InvalidCommandInputException, AuthenticationException;

}
