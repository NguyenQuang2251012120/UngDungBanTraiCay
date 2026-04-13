package com.example.ungdungbantraicay.AdminAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ungdungbantraicay.AdminFragments.AdminFruitSizeActivity;
import com.example.ungdungbantraicay.Model.Fruit;
import com.example.ungdungbantraicay.R;

import java.io.File;
import java.util.List;

public class AdminFruitAdapter extends RecyclerView.Adapter<AdminFruitAdapter.ViewHolder> {

    private Context context;
    private List<Fruit> list;
    private OnFruitActionListener listener;

    public interface OnFruitActionListener {
        void onEdit(Fruit fruit);
        void onDelete(Fruit fruit);
    }

    public AdminFruitAdapter(Context context, List<Fruit> list, OnFruitActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin_fruit, parent, false);
        return new ViewHolder(v);
    }

// Trong AdminFruitAdapter.java -> onBindViewHolder

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fruit f = list.get(position);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminFruitSizeActivity.class);
            intent.putExtra("fruitId", f.getId());
            intent.putExtra("fruitName", f.getName());
            context.startActivity(intent);
        });
        holder.tvName.setText(f.getName());
        holder.tvPrice.setText(String.format("Giá từ: %,dđ", f.getMinPrice()));

        // Logic trạng thái
        if (f.getStatus() == 1) {
            holder.tvStatus.setText("Đang bán");
            holder.tvStatus.setTextColor(Color.GREEN);
        } else {
            holder.tvStatus.setText("Ngừng bán");
            holder.tvStatus.setTextColor(Color.RED);
        }

        // --- SỬA PHẦN LOAD ẢNH ---
        // Kiểm tra xem là ảnh drawable (mẫu) hay ảnh người dùng tải lên
        int resId = context.getResources().getIdentifier(f.getImage(), "drawable", context.getPackageName());
        if (resId != 0) {
            // Nếu là ảnh trong hệ thống (như apple, orange...)
            Glide.with(context).load(resId).into(holder.imgFruit);
        } else {
            // Nếu là ảnh tải lên (lưu trong FilesDir)
            File file = new File(context.getFilesDir(), f.getImage());
            Glide.with(context).load(file).placeholder(R.drawable.apple).into(holder.imgFruit);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(f));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(f));
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFruit;
        TextView tvName, tvPrice, tvStatus;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFruit = itemView.findViewById(R.id.imgFruit);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}