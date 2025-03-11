import java.net.*;
import java.io.*; 
public class TimeClient {

public static void main(String[] args) {
// TODO Auto-generated method stub
 	String hostname = "time.nist.gov";
      int port = 13;
      try (Socket socket = new Socket(hostname, port)) { 
        InputStream input = socket.getInputStream();
        InputStreamReader reader = new InputStreamReader(input); 
        int character;
        StringBuilder data = new StringBuilder(); 
        while ((character = reader.read()) != -1) {
                      data.append((char) character);
              }
       
        System.out.println(data);  
              } catch (UnknownHostException ex) { 
                  System.out.println("Server not found: " + ex.getMessage()); 
              } catch (IOException ex) {
       
                  System.out.println("I/O error: " + ex.getMessage());
              }
         }
      }