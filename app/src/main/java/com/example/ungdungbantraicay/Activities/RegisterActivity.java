package com.example.ungdungbantraicay.Activities;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUser, edtPass, edtConfirm, edtFull, edtEmail, edtPhone, edtAddr;
    Button btnReg;
    UserDAO userDAO;

    // 🔥 Token Firebase
    private String fcmToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_register);

        initViews();
        userDAO = new UserDAO(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    fcmToken = token;
                    Log.d("FCM_TOKEN", token);

                });

        btnReg.setOnClickListener(v -> handleRegister());
    }

    private void initViews() {
        edtUser = findViewById(R.id.edtUsername);
        edtPass = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);
        edtFull = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddr = findViewById(R.id.edtAddress);

        // edtToken = findViewById(R.id.edtToken);

        btnReg = findViewById(R.id.btnRegister);
    }

    private void handleRegister() {
        String user = edtUser.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String confirm = edtConfirm.getText().toString().trim();
        String full = edtFull.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String addr = edtAddr.getText().toString().trim();

        // 🔥 KHÔNG lấy token từ EditText nữa
        String token = fcmToken;

        // ================= VALIDATION =================
        if (user.length() < 5) {
            edtUser.setError("Tên đăng nhập tối thiểu 5 ký tự");
            return;
        }
        if (pass.length() < 6) {
            edtPass.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        }
        if (!pass.equals(confirm)) {
            edtConfirm.setError("Mật khẩu xác nhận không khớp");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không đúng định dạng");
            return;
        }
        if (phone.length() < 10 || phone.length() > 11) {
            edtPhone.setError("Số điện thoại không hợp lệ");
            return;
        }

        // 🔥 Đảm bảo token đã có
        if (token.isEmpty()) {
            Toast.makeText(this, "Đang lấy token, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ================= CHECK USER =================
        if (userDAO.checkUsername(user)) {
            Toast.makeText(this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_LONG).show();
            return;
        }

        // ================= INSERT =================
        User newUser = new User(
                0,
                user,
                pass,
                full,
                email,
                phone,
                addr,
                "user",
                token,   // 🔥 token thật
                1
        );

        if (userDAO.insertUser(newUser)) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
        }
    }
}