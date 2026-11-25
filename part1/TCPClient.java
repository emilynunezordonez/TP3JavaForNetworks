import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class TCPClient {

    private String host;
    private int port;

    public TCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public TCPClient() {
        this("localhost", 8080);
    }

    public void start() {

        try (Socket socket = new Socket(host, port)) {

            System.out.println("Connected to " + host + ":" + port +
                               " (type /quit or Ctrl+D to stop)");

            BufferedReader fromServer = new BufferedReader(
                new InputStreamReader(socket.getInputStream(),
                                      StandardCharsets.UTF_8));
            PrintWriter toServer = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(),
                                       StandardCharsets.UTF_8),
                true);

            BufferedReader keyboard = null;
            if (System.console() == null) {
                keyboard = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            }

            while (true) {

                System.out.print("> ");
                System.out.flush();

                String line = (keyboard != null)
                    ? keyboard.readLine()
                    : System.console().readLine();

                if (line == null || line.equals("/quit")) {
                    System.out.println();
                    break;
                }

                toServer.println(line);

                String answer = fromServer.readLine();

                if (answer == null) {
                    System.err.println("The server closed the connection.");
                    break;
                }

                System.out.println(answer);
            }

            System.out.println("Disconnected from " + host + ":" + port);

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);

        } catch (ConnectException e) {
            System.err.println("Cannot connect to " + host + ":" + port +
                ": no server is listening there (is it started?).");

        } catch (NoRouteToHostException e) {
            System.err.println("No route to host " + host + ".");

        } catch (SocketException e) {
            System.err.println("Connection lost: " + e.getMessage());

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        TCPClient client;

        if (args.length >= 2) {
            try {
                client = new TCPClient(args[0], Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                System.err.println("Invalid port: " + args[1]);
                return;
            }
        } else {
            client = new TCPClient();
        }

        client.start();
    }
}