package com.example.ungdungbantraicay.AdminAdapter; // Kiểm tra đúng package của bạn

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ungdungbantraicay.Model.FruitSize;
import com.example.ungdungbantraicay.R;
import java.util.List;

public class AdminFruitSizeAdapter extends RecyclerView.Adapter<AdminFruitSizeAdapter.ViewHolder> {

    private Context context;
    private List<FruitSize> list;
    private OnSizeAction listener;

    public interface OnSizeAction {
        void onEdit(FruitSize size);
        void onDelete(FruitSize size);
    }

    public AdminFruitSizeAdapter(Context context, List<FruitSize> list, OnSizeAction listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin_fruit_size, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FruitSize fs = list.get(position);
        holder.tvName.setText(fs.getSize());
        holder.tvPrice.setText(String.format("%,d VNĐ", fs.getPrice()));

        // Hiện trạng thái ẩn/hiện nếu muốn
        if(fs.getStatus() == 0) holder.tvName.append(" (Đang ẩn)");

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(fs));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(fs));
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageButton btnEdit, btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSizeName);
            tvPrice = itemView.findViewById(R.id.tvSizePrice);
            btnEdit = itemView.findViewById(R.id.btnEditSize);
            btnDelete = itemView.findViewById(R.id.btnDeleteSize);
        }
    }
}