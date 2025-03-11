import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class connection {
    public static void main(String[] args) {
        String host = "tuoitre.vn";
        int port = 80; // Port HTTP usually use for test connection

        try {
            // Get IP
            InetAddress address = InetAddress.getByName(host);
            System.out.println("Address of " + host + " is: " + address.getHostAddress());

            // Connection with port 80
            try (Socket socket = new Socket(address, port)) {
                System.out.println("Connection successful " + host + " in port " + port);
            } catch (IOException e) {
                System.out.println("Cannot connect to " + host + " in port " + port);
            }

        } catch (UnknownHostException e) {
            System.out.println("IP not found " + host);
        }
    }
}
