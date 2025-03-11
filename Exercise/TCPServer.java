import java.io.*; 
import java.net.*; 

class TCPServer { 
  public static void main(String argv[]) throws Exception {  
    String clientSentence; 
    String capitalizedSentence; 

    // Tạo server socket trên cổng 6789
    ServerSocket welcomeSocket = new ServerSocket(6789);
    System.out.println("Server is running on port 6789...");

    while (true) {
      // Chấp nhận kết nối từ client
      Socket connectionSocket = welcomeSocket.accept(); 
      System.out.println("Client connected!");

      // Nhận dữ liệu từ client
      BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
      OutputStream output = connectionSocket.getOutputStream();
      PrintWriter writer = new PrintWriter(output, true); 

      clientSentence = inFromClient.readLine(); 
      System.out.println("Received from client: " + clientSentence);

      // Chuyển chuỗi thành chữ hoa
      capitalizedSentence = clientSentence.toUpperCase();

      // Gửi dữ liệu lại cho client
      writer.println(capitalizedSentence);
      System.out.println("Sent to client: " + capitalizedSentence);
    } 
  } 
}
