package com.example.multiuserchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new UserDatabaseHelper(this);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginBtn = findViewById(R.id.loginButton);
        Button signupBtn = findViewById(R.id.signupButton);

        loginBtn.setOnClickListener(v -> loginUser());
        signupBtn.setOnClickListener(v -> registerUser());
    }

    private void loginUser() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (dbHelper.authenticateUser(username, password)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "❌ Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (dbHelper.registerUser(username, password)) {
            Toast.makeText(this, "✅ User registered successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "⚠️ Username already exists!", Toast.LENGTH_SHORT).show();
        }
    }
}
