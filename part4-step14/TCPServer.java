import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TCPServer {

    private static final int HISTORY_SIZE = 10;

    private static final DateTimeFormatter TIME_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private int port;
    private boolean running;

    private final java.util.concurrent.atomic.AtomicInteger nextClientId =
        new java.util.concurrent.atomic.AtomicInteger(1);

    private final Deque<String> history = new ArrayDeque<>();

    public TCPServer(int port) {
        this.port = port;
        this.running = false;
    }

    public TCPServer() {
        this(8080);
    }

    public void launch() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            running = true;
            System.out.println("TCP Server started on port " + port);

            while (true) {

                try (Socket socket = serverSocket.accept()) {
                    handleClient(socket);

                } catch (IOException e) {
                    System.err.println("Client error: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());

        } finally {
            running = false;
        }
    }

    private void handleClient(Socket socket) throws IOException {

        String clientIp = socket.getInetAddress().getHostAddress();

        int id = nextClientId.getAndIncrement();
        LocalDateTime start = LocalDateTime.now();
        System.out.println(start.format(TIME_FORMAT) + " Client #" + id +
                           " connected: " + clientIp + ":" + socket.getPort());

        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8));
            PrintWriter writer = new PrintWriter(
                new java.io.OutputStreamWriter(out, StandardCharsets.UTF_8),
                true);

            sendHistory(writer);

            String line;
            while ((line = reader.readLine()) != null) {
                String entry = "[" + clientIp + "] " + line;
                System.out.println("#" + id + " " + entry);
                writer.println(entry);
                addToHistory(entry);
            }

        } finally {
            LocalDateTime end = LocalDateTime.now();
            long ms = Duration.between(start, end).toMillis();
            System.out.println(end.format(TIME_FORMAT) + " Client #" + id +
                " disconnected: " + clientIp + " (session duration: " +
                (ms / 60000) + " min " + ((ms % 60000) / 1000) + "." +
                String.format("%03d", ms % 1000) + " s)");
        }
    }

    private void addToHistory(String entry) {
        history.addLast(entry);
        if (history.size() > HISTORY_SIZE) {
            history.removeFirst();
        }
    }

    private void sendHistory(PrintWriter writer) {
        List<String> snapshot = new ArrayList<>(history);
        writer.println("HISTORY " + snapshot.size());
        for (String entry : snapshot) {
            writer.println(entry);
        }
    }

    @Override
    public String toString() {
        return "TCPServer [port=" + port +
               ", running=" + running + "]";
    }

    public static void main(String[] args) {

        TCPServer server;

        if (args.length > 0) {
            int port = Integer.parseInt(args[0]);
            server = new TCPServer(port);
        } else {
            server = new TCPServer();
        }

        System.out.println(server);
        server.launch();
    }
}