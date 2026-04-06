package com.example.ungdungbantraicay.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.Model.FruitSize;
import com.example.ungdungbantraicay.R;

import java.util.List;

public class FruitSizeAdapter extends RecyclerView.Adapter<FruitSizeAdapter.ViewHolder> {

    private Context context;
    private List<FruitSize> list;
    private OnSizeSelectedListener listener;
    private int selectedPosition = 0; // Mặc định chọn vị trí đầu tiên

    public interface OnSizeSelectedListener {
        void onSizeClick(FruitSize fruitSize);
    }

    public FruitSizeAdapter(Context context, List<FruitSize> list, OnSizeSelectedListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_size, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FruitSize fruitSize = list.get(position);
        boolean isAvailable = fruitSize.getStatus() == 1;

        holder.tvSize.setText(fruitSize.getSize());
        holder.tvPrice.setText(String.format("%,d VND", fruitSize.getPrice()));
        holder.tvQuantity.setText(String.valueOf(fruitSize.getQuantity()));

        // 1. Xử lý hiển thị dựa trên Trạng thái (Còn/Hết)
        if (!isAvailable) {
            holder.itemView.setAlpha(0.4f); // Làm mờ
            holder.btnPlus.setEnabled(false);
            holder.btnMinus.setEnabled(false);
        } else {
            holder.itemView.setAlpha(1.0f); // Hiện rõ
            holder.btnPlus.setEnabled(true);
            holder.btnMinus.setEnabled(true);
        }

        // 2. Logic Tăng/Giảm (chỉ chạy nếu còn hàng)
        if (isAvailable) {
            holder.btnPlus.setOnClickListener(v -> {
                fruitSize.setQuantity(fruitSize.getQuantity() + 1);
                notifyItemChanged(position);
                if (selectedPosition == position) listener.onSizeClick(fruitSize);
            });

            holder.btnMinus.setOnClickListener(v -> {
                if (fruitSize.getQuantity() > 1) {
                    fruitSize.setQuantity(fruitSize.getQuantity() - 1);
                    notifyItemChanged(position);
                    if (selectedPosition == position) listener.onSizeClick(fruitSize);
                }
            });
        }

        // 3. Highlight chọn Size
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.bg_size_selected);
            // ... set màu chữ trắng ...
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_size_unselected);
            // ... set màu chữ đen/xám ...
        }

        // 4. Click chọn Size (Vẫn cho click để xem thông báo hết hàng)
        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);

            if (listener != null) listener.onSizeClick(fruitSize);
        });
    }
    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public FruitSize getSelectedSize() {
        if (list != null && !list.isEmpty()) {
            return list.get(selectedPosition);
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSize, tvPrice, tvQuantity;
        View btnPlus, btnMinus; // Dùng View để nhận cả Button hoặc AppCompatButton

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }
}