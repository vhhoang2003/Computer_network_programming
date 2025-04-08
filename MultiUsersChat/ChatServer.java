import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChatServer {
    private static final int PORT = 12345;

    private static final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());
    private static final Map<String, ClientHandler> usernameToClient = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, Set<String>> groups = Collections.synchronizedMap(new HashMap<>());

    // Username - Password
    private static final Map<String, String> userCredentials = Map.of(
        "vuhieuhoang", "1",
        "chauchicuong", "1",
        "tongphamngoctrong", "1"
    );

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("✅ Chat Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("👤 New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Gửi tin nhắn công khai (broadcast) đến tất cả client trừ sender */
    public static void broadcastMessage(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    /** Gửi tin nhắn đến tất cả thành viên trong group */
    public static void broadcastToGroup(String groupName, String message) {
        Set<String> members = groups.get(groupName);
        if (members == null) return;

        for (String username : members) {
            ClientHandler client = usernameToClient.get(username);
            if (client != null) {
                client.sendMessage("[Group " + groupName + "] " + message);
            }
        }
    }

    /** Gửi tin nhắn riêng */
    public static void sendPrivateMessage(String toUsername, String message, String fromUsername) {
        ClientHandler receiver = usernameToClient.get(toUsername);
        if (receiver != null) {
            receiver.sendMessage("[Private from " + fromUsername + "] " + message);
        }
    }

    /** Thêm client vào hệ thống */
    public static void addClient(ClientHandler clientHandler, String username) {
        clients.add(clientHandler);
        usernameToClient.put(username, clientHandler);
    }

    /** Gỡ bỏ client khỏi hệ thống và khỏi các nhóm */
    public static void removeClient(ClientHandler clientHandler, String username) {
        clients.remove(clientHandler);
        usernameToClient.remove(username);

        // Xoá khỏi tất cả group
        for (Set<String> members : groups.values()) {
            members.remove(username);
        }
    }

    /** Xác thực username/password */
    public static boolean authenticate(String username, String password) {
        return userCredentials.containsKey(username) &&
               userCredentials.get(username).equals(password);
    }

    /** Tham gia nhóm */
    public static boolean joinGroup(String groupName, String username) {
        groups.putIfAbsent(groupName, Collections.synchronizedSet(new HashSet<>()));
        return groups.get(groupName).add(username);
    }

    /** Kiểm tra username có trong group không */
    public static boolean isInGroup(String groupName, String username) {
        Set<String> members = groups.get(groupName);
        return members != null && members.contains(username);
    }
}
