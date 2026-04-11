package com.example.ungdungbantraicay.AdminFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;

public class AdminChangePasswordActivity extends AppCompatActivity {

    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    Button btnChangePassword;
    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_change_password);

        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        userDAO = new UserDAO(this);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(v -> {
            String oldPass = edtOldPassword.getText().toString().trim();
            String newPass = edtNewPassword.getText().toString().trim();
            String confirm = edtConfirmPassword.getText().toString().trim();

            // 1. Kiểm tra không được để trống
            if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Không được để trống mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Lấy thông tin user hiện tại
            SharedPreferences prefs = getSharedPreferences("USER_FILE", MODE_PRIVATE);
            String username = prefs.getString("username", "");
            User user = userDAO.getUserInfo(username);

            if (user == null) return;

            // 3. Kiểm tra mật khẩu cũ (Trim để chính xác)
            if (!oldPass.equals(user.getPassword())) {
                Toast.makeText(this, "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. Kiểm tra mật khẩu mới có trùng mật khẩu cũ không
            if (newPass.equals(oldPass)) {
                Toast.makeText(this, "Mật khẩu mới không được giống mật khẩu cũ", Toast.LENGTH_SHORT).show();
                return;
            }

            // 5. Kiểm tra mật khẩu mới có khớp xác nhận không
            if (!newPass.equals(confirm)) {
                Toast.makeText(this, "Xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // 6. Kiểm tra độ dài mật khẩu (VD: ít nhất 6 ký tự)
            if (newPass.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
                return;
            }

            // 7. Thực hiện đổi
            if (userDAO.changePassword(username, newPass)) {
                Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}