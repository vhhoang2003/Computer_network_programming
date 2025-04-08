import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Nhập username
            out.println("Enter username:");
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                out.println("⚠️ Invalid username.");
                socket.close();
                return;
            }

            // Nhập password
            out.println("Enter password:");
            String password = in.readLine();
            if (!ChatServer.authenticate(username, password)) {
                out.println("❌ Invalid credentials. Connection closed.");
                socket.close();
                return;
            }

            ChatServer.addClient(this, username);
            System.out.println("✅ " + username + " authenticated and joined.");
            ChatServer.broadcastMessage("🔔 " + username + " joined the chat.", this);

            // Lắng nghe và xử lý tin nhắn
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }

                // Lệnh tham gia nhóm
                if (message.startsWith("/joingroup")) {
                    String[] parts = message.split(" ", 2);
                    if (parts.length == 2) {
                        String groupName = parts[1].trim();
                        boolean joined = ChatServer.joinGroup(groupName, username);
                        if (joined) {
                            out.println("✅ You joined group: " + groupName);
                        } else {
                            out.println("ℹ️ You are already in group: " + groupName);
                        }
                    } else {
                        out.println("⚠️ Usage: /joingroup groupName");
                    }

                // Tin nhắn hình ảnh
                } else if (message.startsWith("[IMAGE]")) {
                    String base64Image = message.substring(7);
                    ChatServer.broadcastMessage("[IMAGE_FROM " + username + "]" + base64Image, this);

                // Tin nhắn nhóm
                } else if (message.startsWith("#")) {
                    int spaceIndex = message.indexOf(" ");
                    if (spaceIndex != -1) {
                        String groupName = message.substring(1, spaceIndex);
                        String groupMsg = message.substring(spaceIndex + 1);
                        if (ChatServer.isInGroup(groupName, username)) {
                            ChatServer.broadcastToGroup(groupName, username + ": " + groupMsg);
                        } else {
                            out.println("⚠️ You are not in group '" + groupName + "'. Use /joingroup " + groupName);
                        }
                    } else {
                        out.println("⚠️ Invalid group message format. Usage: #groupName message");
                    }

                // Tin nhắn riêng
                } else if (message.startsWith("@")) {
                    int spaceIndex = message.indexOf(" ");
                    if (spaceIndex != -1) {
                        String toUser = message.substring(1, spaceIndex);
                        String privateMsg = message.substring(spaceIndex + 1);
                        ChatServer.sendPrivateMessage(toUser, privateMsg, username);
                    } else {
                        out.println("⚠️ Invalid private message format. Usage: @username message");
                    }

                // Tin nhắn công khai
                } else {
                    ChatServer.broadcastMessage(username + ": " + message, this);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void closeConnection() {
        try {
            ChatServer.removeClient(this, username);
            if (socket != null) socket.close();
            System.out.println("❌ " + username + " left the chat.");
            ChatServer.broadcastMessage("❌ " + username + " left the chat.", this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
