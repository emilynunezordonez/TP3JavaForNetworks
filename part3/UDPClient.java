import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UDPClient {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: java UDPClientConnected <host> <port>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            InetAddress serverAddress = InetAddress.getByName(host);

            BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8)
            );

            try (DatagramSocket socket = new DatagramSocket()) {

                socket.connect(serverAddress, port);

                String message;

                while ((message = in.readLine()) != null) {

                    byte[] data = message.getBytes(StandardCharsets.UTF_8);

                    DatagramPacket packet =
                        new DatagramPacket(data, data.length);

                    try {
                        socket.send(packet);
                        System.out.println("Sent: " + message);

                    } catch (PortUnreachableException e) {
                        System.err.println("Port unreachable: no server on "
                            + host + ":" + port);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}