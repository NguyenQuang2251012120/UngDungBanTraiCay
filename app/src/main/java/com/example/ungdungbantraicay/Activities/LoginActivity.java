package com.example.ungdungbantraicay.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ungdungbantraicay.AdminFragments.AdminMainActivity;
import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView txtRegister;

    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDAO = new UserDAO(this);

        checkAutoLogin();

        setContentView(R.layout.activity_login);

        // Tao channel nhan tin nhan
        createNotificationChannel();
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
        }

        initViews();
        initEvents();
    }

    private void initViews() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
    }

    private void initEvents() {
        btnLogin.setOnClickListener(v -> handleLogin());

        txtRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void checkAutoLogin() {
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String savedUser = pref.getString("username", "");

        if (!savedUser.isEmpty()) {
            User user = userDAO.getUserInfo(savedUser);
            if (user != null && user.getStatus() == 1) {
                navigateToHome(user.getRole());
            } else {
                pref.edit().clear().apply();
            }
        }
    }

    private void handleLogin() {
        String userStr = edtUsername.getText().toString().trim();
        String passStr = edtPassword.getText().toString().trim();

        if (userStr.isEmpty() || passStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = userDAO.login(userStr, passStr);

        if (user != null) {
            if (user.getStatus() == 1) {

                saveSession(user);

                FirebaseMessaging.getInstance().getToken()
                        .addOnSuccessListener(token -> {
                            userDAO.updateFcmToken(user.getId(), token);
                        });

                Toast.makeText(this, "Chào mừng " + user.getFullname(), Toast.LENGTH_SHORT).show();
                navigateToHome(user.getRole());

            } else {
                showLockedAccountDialog();
            }
        } else {
            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSession(User user) {
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", user.getUsername());
        editor.putInt("userId", user.getId());
        editor.putString("role", user.getRole());
        editor.apply();
    }

    private void navigateToHome(String role) {
        Intent intent;
        if ("admin".equalsIgnoreCase(role)) {
            intent = new Intent(this, AdminMainActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void showLockedAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Tài khoản bị khóa")
                .setMessage("Tài khoản đã bị vô hiệu hóa.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "order_channel",
                    "Order Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}