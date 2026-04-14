package com.example.ungdungbantraicay.AdminAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {
    private Context context;
    private List<User> list;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEdit(User user);
        void onDelete(User user);
    }

    public AdminUserAdapter(Context context, List<User> list, OnUserActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_admin, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);
        holder.tvName.setText(user.getFullname());
        holder.tvRole.setText("Vai trò: " + user.getRole());
        holder.tvEmail.setText(user.getEmail());

        holder.tvPassword.setText("Mật khẩu: ********");

        holder.itemView.setAlpha(user.getStatus() == 1 ? 1.0f : 0.5f);
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(user));
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole, tvEmail, tvPassword; // Thêm tvPassword
        ImageButton btnEdit, btnDelete;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminFullName);
            tvRole = itemView.findViewById(R.id.tvAdminUserRole);
            tvEmail = itemView.findViewById(R.id.tvAdminEmail);
            btnEdit = itemView.findViewById(R.id.btnEditUser);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
            tvPassword = itemView.findViewById(R.id.tvAdminPassword);
        }
    }
}