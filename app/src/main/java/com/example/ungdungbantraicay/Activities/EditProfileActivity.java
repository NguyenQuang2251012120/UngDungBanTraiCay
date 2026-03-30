package com.example.ungdungbantraicay.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity {

    EditText edtFullname, edtEmail, edtPhone, edtAddress;
    Button btnUpdate;
    UserDAO userDAO;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // ánh xạ view trước
        edtFullname = findViewById(R.id.edtFullname);
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
            edtEmail.setText(user.getEmail());
            edtPhone.setText(user.getPhone());
            edtAddress.setText(user.getAddress());
        }

        btnUpdate.setOnClickListener(v -> {
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
                user.setFullname(name);
                user.setEmail(email);
                user.setPhone(phone);
                user.setAddress(address);

                if (userDAO.updateUser(user)) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}