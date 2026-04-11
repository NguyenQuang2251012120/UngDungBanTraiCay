package com.example.ungdungbantraicay.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtFullname, edtUsername, edtEmail, edtPhone, edtAddress;
    private Button btnUpdate;

    private UserDAO userDAO;
    private User currentUser;
    private String currentUsernamePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        userDAO = new UserDAO(this);

        // Lấy username từ SharedPreferences để xác định ai đang đăng nhập
        SharedPreferences prefs = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        currentUsernamePref = prefs.getString("username", "");

        loadUserData();

        btnUpdate.setOnClickListener(v -> handleUpdate());
    }

    private void initViews() {
        edtFullname = findViewById(R.id.edtFullname);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    private void loadUserData() {
        currentUser = userDAO.getUserInfo(currentUsernamePref);

        if (currentUser != null) {
            edtFullname.setText(currentUser.getFullname());
            edtUsername.setText(currentUser.getUsername());
            edtEmail.setText(currentUser.getEmail());
            edtPhone.setText(currentUser.getPhone());
            edtAddress.setText(currentUser.getAddress());
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleUpdate() {
        if (currentUser == null) return;

        // 1. Lấy dữ liệu
        String newUsername = edtUsername.getText().toString().trim();
        String name = edtFullname.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String oldUsername = currentUser.getUsername();

        // 2. VALIDATION (Đồng bộ logic với RegisterActivity)

        // Kiểm tra họ tên
        if (name.isEmpty()) {
            edtFullname.setError("Họ tên không được để trống");
            return;
        }

        // Kiểm tra username (tối thiểu 5 ký tự)
        if (newUsername.length() < 5) {
            edtUsername.setError("Tên đăng nhập tối thiểu 5 ký tự");
            return;
        }

        // Kiểm tra Email định dạng
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không đúng định dạng");
            return;
        }

        // Kiểm tra Số điện thoại (10-11 số)
        if (phone.length() < 10 || phone.length() > 11) {
            edtPhone.setError("Số điện thoại phải từ 10-11 số");
            return;
        }

        if (address.isEmpty()) {
            edtAddress.setError("Địa chỉ không được để trống");
            return;
        }

        // 3. KIỂM TRA TRÙNG TÊN ĐĂNG NHẬP (Nếu người dùng đổi tên mới)
        if (!newUsername.equals(oldUsername)) {
            // Sử dụng checkUsername giống bên Register để đảm bảo không trùng
            if (userDAO.checkUsername(newUsername)) {
                edtUsername.setError("Tên đăng nhập này đã có người sử dụng!");
                return;
            }
        }

        // 4. THỰC HIỆN CẬP NHẬT
        currentUser.setUsername(newUsername);
        currentUser.setFullname(name);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);

        if (userDAO.updateUserWithId(currentUser)) {
            // Nếu đổi username thành công, phải cập nhật lại SharedPreferences
            // để lần sau vào App vẫn lấy được dữ liệu mới
            if (!newUsername.equals(oldUsername)) {
                SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
                pref.edit().putString("username", newUsername).apply();
            }

            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Lỗi cập nhật, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
        }
    }
}