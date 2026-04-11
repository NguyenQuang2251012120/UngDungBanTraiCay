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

public class AdminEditProfileActivity extends AppCompatActivity {

    EditText edtFullname, edtUsername, edtEmail, edtPhone, edtAddress;
    Button btnUpdate;
    UserDAO userDAO;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_profile);

        // ánh xạ view trước
        edtFullname = findViewById(R.id.edtFullname);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnUpdate = findViewById(R.id.btnUpdate);

        userDAO = new UserDAO(this);

        SharedPreferences prefs = getSharedPreferences("USER_FILE",MODE_PRIVATE);
        String username = prefs.getString("username","");

        user = userDAO.getUserInfo(username);

        if(user != null){
            edtFullname.setText(user.getFullname());
            edtUsername.setText(user.getUsername());
            edtEmail.setText(user.getEmail());
            edtPhone.setText(user.getPhone());
            edtAddress.setText(user.getAddress());
        }

        btnUpdate.setOnClickListener(v -> {
            String newUsername = edtUsername.getText().toString().trim();
            String oldUsername = user.getUsername(); // Lấy từ object user đã load lên
            // 1. Lấy dữ liệu và dùng .trim() để xóa khoảng trắng thừa
            String name = edtFullname.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();

            // 2. Kiểm tra dữ liệu trống
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Kiểm tra định dạng Email cơ bản
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. Phòng trường hợp đối tượng user bị null
            if (user != null) {
                // Nếu người dùng thay đổi Username
                if (!newUsername.equals(oldUsername)) {
                    if (userDAO.checkUsernameExists(newUsername)) {
                        Toast.makeText(this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Cập nhật lại SharedPreferences vì App nhận diện User qua Username này
                    SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
                    pref.edit().putString("username", newUsername).apply();
                    user.setUsername(newUsername);
                }

                user.setFullname(edtFullname.getText().toString().trim());
                user.setEmail(edtEmail.getText().toString().trim());
                user.setPhone(edtPhone.getText().toString().trim());
                user.setAddress(edtAddress.getText().toString().trim());

                // Sử dụng hàm update theo ID mà mình vừa viết ở trên
                if (userDAO.updateUserWithId(user)) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}