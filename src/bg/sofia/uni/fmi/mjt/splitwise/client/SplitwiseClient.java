package bg.sofia.uni.fmi.mjt.splitwise.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SplitwiseClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 7777;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            Thread.currentThread().setName("Client thread " + socket.getLocalPort());

            while (true) {
                printServerResponse(in);
                String input = scanner.nextLine();

                if (input.equals("disconnect")) {
                    System.out.println("Disconnected from server.");
                    break;
                }

                out.println(input);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printServerResponse(BufferedReader in) throws IOException {
        do {
            System.out.println(in.readLine());
        } while (in.ready());
    }

}
