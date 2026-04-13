package com.example.ungdungbantraicay.AdminFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ungdungbantraicay.AdminAdapter.AdminUserAdapter;
import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;

import java.util.List;

public class AdminUserFragment extends Fragment {
    private RecyclerView rvUser;
    private UserDAO userDAO;
    private AdminUserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user, container, false);
        rvUser = view.findViewById(R.id.rvAdminUser);
        userDAO = new UserDAO(getContext());

        // Thêm nút FAB trong layout fragment_admin_user để thêm mới
        view.findViewById(R.id.fabAddUser).setOnClickListener(v -> showDialog(null));

        loadData();
        return view;
    }

    private void loadData() {
        SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", Context.MODE_PRIVATE);
        String adminNow = pref.getString("username", "");

        List<User> list = userDAO.getAllUsersForAdmin(adminNow);
        adapter = new AdminUserAdapter(getContext(), list, new AdminUserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(User user) { showDialog(user); }
            @Override
            public void onDelete(User user) { confirmDelete(user); }
        });
        rvUser.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUser.setAdapter(adapter);
    }

    private void showDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View v = getLayoutInflater().inflate(R.layout.dialog_add_edit_user, null);
        builder.setView(v);

        EditText edtUser = v.findViewById(R.id.edtUser);
        EditText edtPass = v.findViewById(R.id.edtPass);
        EditText edtFull = v.findViewById(R.id.edtFull);
        EditText edtEmail = v.findViewById(R.id.edtEmail);
        Spinner spnRole = v.findViewById(R.id.spnRole);
        CheckBox chkLocked = v.findViewById(R.id.chkLocked);

        // Setup Spinner cho Role
        String[] roles = {"user", "admin"};
        spnRole.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, roles));

        // Lấy ID Admin đang đăng nhập từ SharedPreferences
        SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", Context.MODE_PRIVATE);
        int currentAdminId = pref.getInt("userId", -1);

        if (user != null) {
            builder.setTitle("Sửa thông tin: " + user.getUsername());
            edtUser.setText(user.getUsername());
            edtUser.setEnabled(false);
            edtPass.setText(user.getPassword());
            edtFull.setText(user.getFullname());
            edtEmail.setText(user.getEmail());
            spnRole.setSelection(user.getRole().equals("admin") ? 1 : 0);
            chkLocked.setChecked(user.getStatus() == 0);

            // BẢO VỆ ADMIN GỐC: Nếu là Admin ID 1 thì không cho đổi Role hoặc Khóa
            if (user.getId() == 1) {
                spnRole.setEnabled(false);
                chkLocked.setEnabled(false);
            }
        } else {
            builder.setTitle("Thêm tài khoản mới");
            chkLocked.setVisibility(View.GONE);
        }

        builder.setPositiveButton("Lưu", null); // Để null để ta tự xử lý click bên dưới
        builder.setNegativeButton("Hủy", (d, w) -> d.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Xử lý sự kiện click nút Lưu mà không làm đóng Dialog nếu có lỗi
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String uName = edtUser.getText().toString().trim();
            String uPass = edtPass.getText().toString().trim();
            String uFull = edtFull.getText().toString().trim();
            String uEmail = edtEmail.getText().toString().trim();

            // 1. CHECK LỖI TRỐNG (Null/Empty)
            if (uName.isEmpty() || uPass.isEmpty() || uFull.isEmpty() || uEmail.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. CHECK ĐỘ DÀI MẬT KHẨU (Nếu muốn)
            if (uPass.length() < 6) {
                edtPass.setError("Mật khẩu phải ít nhất 6 ký tự");
                return;
            }

            // Thực hiện lưu dữ liệu
            User u = (user == null) ? new User() : user;
            u.setUsername(uName);
            u.setPassword(uPass);
            u.setFullname(uFull);
            u.setEmail(uEmail);
            u.setRole(spnRole.getSelectedItem().toString());
            u.setStatus(chkLocked.isChecked() ? 0 : 1);

            boolean success;
            if (user == null) success = userDAO.insertUserAdmin(u);
            else success = userDAO.updateUserAdmin(u);

            if (success) {
                Toast.makeText(getContext(), "Thao tác thành công!", Toast.LENGTH_SHORT).show();
                loadData();
                dialog.dismiss(); // Chỉ đóng dialog khi thành công
            } else {
                Toast.makeText(getContext(), "Thao tác thất bại (Có thể trùng Username)!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void confirmDelete(User user) {
        // Lấy ID Admin hiện tại
        SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", Context.MODE_PRIVATE);
        int currentAdminId = pref.getInt("userId", -1);

        // 1. Chặn tự xóa chính mình
        if (user.getId() == currentAdminId) {
            Toast.makeText(getContext(), "Bạn không thể tự xóa chính mình!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Chặn xóa Admin khác (Chỉ Admin gốc ID 1 mới được quyền tối cao, hoặc chặn tất cả Admin xóa nhau)
        if (user.getRole().equals("admin")) {
            Toast.makeText(getContext(), "Không thể xóa tài khoản Quản trị viên khác!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Chặn xóa tài khoản đang hoạt động (Status = 1)
        if (user.getStatus() == 1) {
            Toast.makeText(getContext(), "Tài khoản đang hoạt động! Hãy khóa (Status = 0) trước khi xóa.", Toast.LENGTH_LONG).show();
            return;
        }

        // 4. Nếu vượt qua các bước trên -> Hiện Dialog xác nhận (Có nút Hủy)
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa vĩnh viễn")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Bạn có chắc muốn xóa " + user.getUsername() + "? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (d, w) -> {
                    if (userDAO.deleteUser(user.getId())) {
                        Toast.makeText(getContext(), "Đã xóa user thành công!", Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(getContext(), "Lỗi: User này đã có dữ liệu đơn hàng, không thể xóa!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Hủy bỏ", (d, w) -> d.dismiss()) // NÚT HỦY
                .show();
    }}