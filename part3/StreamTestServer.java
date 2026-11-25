import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class StreamTestServer {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Listening on " + port);
            while (true) {
                try (Socket socket = server.accept()) {
                    System.out.println("Client connected");
                    Thread.sleep(1000);
                    InputStream in = socket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int n, total = 0, calls = 0;
                    while ((n = in.read(buffer)) != -1) {
                        calls++;
                        total += n;
                        System.out.println("read() #" + calls + " returned " + n + " bytes");
                    }
                    System.out.println("Closed by client: " + total + " bytes in " + calls + " read() calls");
                }
            }
        }
    }
}