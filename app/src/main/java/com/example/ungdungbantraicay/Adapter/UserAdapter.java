package com.example.ungdungbantraicay.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.Activities.EditUserActivity;
import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> list;
    private UserDAO userDAO;

    public UserAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
        userDAO = new UserDAO(context);
    }

    // ================= VIEW HOLDER =================
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtRole;
        Button btnEdit, btnDelete, btnRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtRole = itemView.findViewById(R.id.txtRole);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnRole = itemView.findViewById(R.id.btnRole);
        }
    }

    // ================= CREATE VIEW =================
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    // ================= BIND DATA =================
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = list.get(position);

        holder.txtName.setText(user.getUsername());
        holder.txtRole.setText("Role: " + user.getRole());

        // ===== XÓA USER =====
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc muốn xóa user này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        boolean result = userDAO.deleteUser(user.getId());
                        if (result) {
                            list.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        // ===== ĐỔI ROLE =====
        holder.btnRole.setOnClickListener(v -> {
            String newRole = user.getRole().equals("admin") ? "user" : "admin";

            boolean result = userDAO.updateRole(user.getId(), newRole);
            if (result) {
                user.setRole(newRole);
                notifyItemChanged(position);
                Toast.makeText(context, "Đã đổi role", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Lỗi đổi role", Toast.LENGTH_SHORT).show();
            }
        });

        // ===== SỬA USER =====
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditUserActivity.class);
            intent.putExtra("user", user);
            context.startActivity(intent);
        });
    }

    // ================= COUNT =================
    @Override
    public int getItemCount() {
        return list.size();
    }
}