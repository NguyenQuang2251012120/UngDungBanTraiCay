package com.example.ungdungbantraicay.Activities;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.R;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUser, edtPass, edtConfirm, edtFull, edtEmail, edtPhone, edtAddr;
    Button btnReg;
    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. ÁNH XẠ
        edtUser = findViewById(R.id.edtUsername);
        edtPass = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);
        edtFull = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddr = findViewById(R.id.edtAddress);
        btnReg = findViewById(R.id.btnRegister);

        userDAO = new UserDAO(this);

        btnReg.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        // Lấy dữ liệu và loại bỏ khoảng trắng thừa
        String user = edtUser.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String confirm = edtConfirm.getText().toString().trim();
        String full = edtFull.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String addr = edtAddr.getText().toString().trim();

        // 2. KIỂM TRA DỮ LIỆU (VALIDATION)
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
        if (full.isEmpty()) {
            edtFull.setError("Vui lòng nhập họ tên");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không đúng định dạng");
            return;
        }

        // 3. KIỂM TRA TRÙNG USERNAME TRONG DATABASE
        if (userDAO.checkUsername(user)) {
            Toast.makeText(this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. THỰC HIỆN ĐĂNG KÝ (Sử dụng DAO đã có)
        userDAO.insertUser(user, pass, full, email, phone, addr);

        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
        finish(); // Quay lại màn hình Login
    }
}