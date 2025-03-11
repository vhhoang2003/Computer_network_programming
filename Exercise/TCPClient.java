import java.io.*; 
import java.net.*;
import java.util.Scanner; 

class TCPClient { 
  public static void main(String argv[]) throws Exception { 
    String sentence; 
    String modifiedSentence; 
    
    System.out.print("Enter a string to send to the server: ");
    Scanner sc = new Scanner(System.in);
    sentence = sc.nextLine();

    // Kết nối tới server trên localhost, cổng 6789
    Socket clientSocket = new Socket("localhost", 6789); 

    OutputStream output = clientSocket.getOutputStream();
    PrintWriter writer = new PrintWriter(output, true); 
    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    // Gửi dữ liệu tới server
    writer.println(sentence);

    // Nhận dữ liệu từ server
    modifiedSentence = inFromServer.readLine(); 
    System.out.println("Response from server: " + modifiedSentence);

    // Đóng kết nối
    clientSocket.close();
  } 
}
