import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ChatClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            System.out.println("✅ Connected to chat server at " + serverAddress + ":" + port);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            // Giao tiếp xác thực với server
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            out.println(username);

            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            out.println(password);

            // Đọc phản hồi xác thực
            String serverResponse = in.readLine();
            if (serverResponse == null || serverResponse.startsWith("❌") || serverResponse.startsWith("⚠️")) {
                System.out.println(serverResponse);
                socket.close();
                return;
            }

            System.out.println("📘 Available commands:");
            System.out.println("  /quit                     → Exit chat");
            System.out.println("  /sendimg path/to/image   → Send an image");
            System.out.println("  /joingroup groupName     → Join a group");
            System.out.println("  #groupName message       → Send message to group");
            System.out.println("  @username message        → Send private message");

            // Luồng nhận tin nhắn từ server
            new Thread(() -> {
                try {
                    String messageFromServer;
                    while ((messageFromServer = in.readLine()) != null) {
                        if (messageFromServer.startsWith("[IMAGE_FROM ")) {
                            String sender = messageFromServer.substring(13, messageFromServer.indexOf("]"));
                            String base64 = messageFromServer.substring(messageFromServer.indexOf("]") + 1);

                            byte[] imgBytes = Base64.getDecoder().decode(base64);
                            String filename = "received_from_" + sender + "_" + System.currentTimeMillis() + ".jpg";
                            Files.write(new File(filename).toPath(), imgBytes);

                            System.out.println("📥 Image received from " + sender + " → saved as: " + filename);
                        } else {
                            System.out.println(messageFromServer);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("❌ Disconnected from server.");
                }
            }).start();

            // Gửi tin nhắn từ người dùng
            while (true) {
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("/quit")) {
                    out.println("/quit");
                    socket.close();
                    break;

                } else if (input.startsWith("/sendimg")) {
                    String[] parts = input.split(" ", 2);
                    if (parts.length < 2) {
                        System.out.println("⚠️ Usage: /sendimg path/to/image.jpg");
                        continue;
                    }
                    sendImage(parts[1]);

                } else {
                    out.println(input);
                }
            }

        } catch (IOException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void sendImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.out.println("⚠️ File not found: " + imagePath);
                return;
            }

            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

            if (imageBytes.length > 500_000) {
                System.out.println("⚠️ Image too large. Max 500KB.");
                return;
            }

            if (!(imageBytes[0] == (byte)0xFF && imageBytes[1] == (byte)0xD8) &&  // JPEG
                !(imageBytes[0] == (byte)0x89 && imageBytes[1] == (byte)0x50)) {  // PNG
                System.out.println("⚠️ Only JPEG and PNG images are allowed.");
                return;
            }

            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
            out.println("[IMAGE]" + encodedImage);
            System.out.println("📤 Image sent!");

        } catch (IOException e) {
            System.out.println("❌ Failed to send image: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 12345;
        new ChatClient(serverAddress, port);
    }
}
