import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class GetIPAndLocalPort {
    public static void main(String[] args) {
        String host = "dantri.com.vn"; // The target hostname
        int port = 80; // Common HTTP port for checking connection

        try {
            // Get the IP address of the server
            InetAddress address = InetAddress.getByName(host);
            System.out.println("The IP address of " + host + " is: " + address.getHostAddress());

            // Attempt to connect to the server on port 80
            try (Socket socket = new Socket(address, port)) {
                System.out.println("Successfully connected to " + host + " on port " + port);
                // Get and print the local port used for the connection
                System.out.println("The local port used for connection is: " + socket.getLocalPort());
            } catch (IOException e) {
                System.out.println("Unable to connect to " + host + " on port " + port);
            }

        } catch (UnknownHostException e) {
            System.out.println("Could not find the IP address of " + host);
        }
    }
}
