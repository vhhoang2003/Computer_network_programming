import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetIP {
    public static void main(String[] args) {
        try {
            String host = "vnexpress.net";
            InetAddress address = InetAddress.getByName(host);
            System.out.println("IP address for " + host + " is: " + address.getHostAddress());
        } catch (UnknownHostException e) {
            System.out.println("Could not find IP address for: " + e.getMessage());
        }
    }
}
