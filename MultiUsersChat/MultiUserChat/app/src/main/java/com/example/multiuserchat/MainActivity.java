package com.example.multiuserchat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private TextView chatView;
    private EditText messageInput;
    private EditText recipientInput;
    private String username;
    private final String SERVER_IP = "10.0.2.2";
    private final int SERVER_PORT = 12345;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatView = findViewById(R.id.chatView);
        messageInput = findViewById(R.id.messageInput);
        recipientInput = findViewById(R.id.recipientInput);
        Button sendButton = findViewById(R.id.sendButton);
        Button sendGroupButton = findViewById(R.id.sendGroupButton);

        username = getIntent().getStringExtra("username");

        executorService.execute(this::connectToServer);

        sendButton.setOnClickListener(v -> sendMessage());
        sendGroupButton.setOnClickListener(v -> sendGroupMessage());
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(username); // Gửi username tới server

            // Nhận tin nhắn từ server
            String message;
            while ((message = in.readLine()) != null) {
                String finalMessage = message;
                runOnUiThread(() -> chatView.append(finalMessage + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> chatView.append("⚠️ Could not connect to server!\n"));
        }
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            executorService.execute(() -> {
                try {
                    if (socket != null && out != null) {
                        out.println(message);
                        runOnUiThread(() -> messageInput.setText("")); // Xóa tin nhắn sau khi gửi
                    } else {
                        runOnUiThread(() -> chatView.append("⚠️ Not connected to server!\n"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void sendGroupMessage() {
        String groupName = recipientInput.getText().toString().trim();
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty() && !groupName.isEmpty()) {
            executorService.execute(() -> {
                if (socket != null && out != null) {
                    out.println("#" + groupName + " " + message);
                    runOnUiThread(() -> messageInput.setText(""));
                } else {
                    runOnUiThread(() -> chatView.append("⚠️ Not connected to server!\n"));
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null) socket.close();
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}