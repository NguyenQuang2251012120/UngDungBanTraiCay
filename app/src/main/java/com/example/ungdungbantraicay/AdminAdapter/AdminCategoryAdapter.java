package com.example.ungdungbantraicay.AdminAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ungdungbantraicay.Model.Category;
import com.example.ungdungbantraicay.R;
import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.ViewHolder> {
    private Context context;
    private List<Category> list;
    private OnAdminCategoryClickListener listener;

    public interface OnAdminCategoryClickListener {
        void onEditClick(Category category);
        void onDeleteClick(Category category);
    }

    public AdminCategoryAdapter(Context context, List<Category> list, OnAdminCategoryClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng layout hầm hố cho admin (item_category_admin.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category cat = list.get(position);
        holder.tvName.setText(cat.getName());

        // Hiển thị trạng thái Hiện/Ẩn
        if (cat.getStatus() == 1) {
            holder.tvStatus.setText("Hiện");
            holder.tvStatus.setTextColor(Color.parseColor("#388E3C"));
        } else {
            holder.tvStatus.setText("Ẩn");
            holder.tvStatus.setTextColor(Color.GRAY);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(cat));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(cat));
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}