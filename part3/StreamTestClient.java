import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class StreamTestClient {
    public static void main(String[] args) throws Exception {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String mode = args[2]; // "strings" or "block"
        try (Socket socket = new Socket(host, port)) {
            OutputStream out = socket.getOutputStream();
            if (mode.equals("strings")) {
                out.write("Hello".getBytes(StandardCharsets.UTF_8));
                out.write("World".getBytes(StandardCharsets.UTF_8));
                out.write("TCP".getBytes(StandardCharsets.UTF_8));
                out.write("From".getBytes(StandardCharsets.UTF_8));
                out.write("Emily".getBytes(StandardCharsets.UTF_8));
                out.write("And".getBytes(StandardCharsets.UTF_8));
                out.write("Victoria".getBytes(StandardCharsets.UTF_8));
                System.out.println("Sent 7 strings: 5 + 5 + 3 + 4 + 5 + 3 + 8 = 33 bytes");
            } else {
                out.write(new byte[10000]);
                System.out.println("Sent 1 block of 10000 bytes");
            }
            out.flush();
            Thread.sleep(2000);
        }
    }
}