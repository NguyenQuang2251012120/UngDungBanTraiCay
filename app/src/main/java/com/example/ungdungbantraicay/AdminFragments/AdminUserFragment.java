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

        if (user != null) {
            builder.setTitle("Sửa thông tin");
            edtUser.setText(user.getUsername());
            edtUser.setEnabled(false); // Không cho sửa username

            edtPass.setVisibility(View.VISIBLE); // Cho hiện ô pass để sửa
            edtPass.setText(user.getPassword()); // Điền pass cũ vào ô nhập
            // ----------------------

            edtFull.setText(user.getFullname());
            edtEmail.setText(user.getEmail());
            spnRole.setSelection(user.getRole().equals("admin") ? 1 : 0);
            chkLocked.setChecked(user.getStatus() == 0);
        } else {
            builder.setTitle("Thêm tài khoản");
            chkLocked.setVisibility(View.GONE);
        }

        builder.setPositiveButton("Lưu", (d, w) -> {
            User u = (user == null) ? new User() : user;
            u.setUsername(edtUser.getText().toString());
            u.setPassword(edtPass.getText().toString());
            u.setFullname(edtFull.getText().toString());
            u.setEmail(edtEmail.getText().toString());
            u.setRole(spnRole.getSelectedItem().toString());
            u.setStatus(chkLocked.isChecked() ? 0 : 1);

            if (user == null) userDAO.insertUserAdmin(u);
            else userDAO.updateUserAdmin(u);

            loadData();
        });
        builder.show();
    }

    private void confirmDelete(User user) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa tài khoản")
                .setMessage("Bạn có chắc muốn xóa " + user.getUsername() + "?")
                .setPositiveButton("Xóa", (d, w) -> {
                    if (userDAO.deleteUser(user.getId())) loadData();
                    else Toast.makeText(getContext(), "User này đã có đơn hàng, không thể xóa!", Toast.LENGTH_LONG).show();
                }).show();
    }
}