package com.example.ungdungbantraicay.Activities;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUser, edtPass, edtConfirm, edtFull, edtEmail, edtPhone, edtAddr;
    Button btnReg;
    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        userDAO = new UserDAO(this);

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

        // 1. VALIDATION (Kiểm tra dữ liệu)
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

        // 2. KIỂM TRA TỒN TẠI (Kể cả status = 0)
        // Trong DAO, hàm checkUsername nên SELECT * WHERE username = ? (không quan tâm status)
        if (userDAO.checkUsername(user)) {
            Toast.makeText(this, "Tên đăng nhập này đã có người sử dụng!", Toast.LENGTH_LONG).show();
            return;
        }

        // 3. THỰC HIỆN ĐĂNG KÝ
        // Thay vì dùng setter, ta dùng Constructor (để trống ID vì DB tự tăng, Status mặc định là 1)
        // Lưu ý: Tùy vào Constructor bạn tạo ở Model User, bạn có thể truyền status = 1 trực tiếp.
        User newUser = new User(0, user, pass, full, email, phone, addr, "user", 1);

        if (userDAO.insertUser(newUser)) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi hệ thống, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
        }
    }
}