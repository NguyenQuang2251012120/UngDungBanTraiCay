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

        // 1. Gán dữ liệu cơ bản
        holder.tvSize.setText(fruitSize.getSize());
        holder.tvPrice.setText(String.format("%,d VND", fruitSize.getPrice()));
        holder.tvQuantity.setText(String.valueOf(fruitSize.getQuantity()));

        // 2. Xử lý Logic Tăng/Giảm số lượng
        holder.btnPlus.setOnClickListener(v -> {
            fruitSize.setQuantity(fruitSize.getQuantity() + 1);
            notifyItemChanged(position);
            // Cập nhật lại giá tiền ở Activity nếu size này đang được chọn
            if (selectedPosition == position && listener != null) {
                listener.onSizeClick(fruitSize);
            }
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (fruitSize.getQuantity() > 1) {
                fruitSize.setQuantity(fruitSize.getQuantity() - 1);
                notifyItemChanged(position);
                if (selectedPosition == position && listener != null) {
                    listener.onSizeClick(fruitSize);
                }
            }
        });

        // 3. Xử lý chọn Item (Highlight)
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.bg_size_selected);
            holder.tvSize.setTextColor(Color.WHITE);
            holder.tvPrice.setTextColor(Color.WHITE);
            holder.tvQuantity.setTextColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_size_unselected);
            holder.tvSize.setTextColor(Color.BLACK);
            holder.tvPrice.setTextColor(Color.GRAY);
            holder.tvQuantity.setTextColor(Color.BLACK);
        }

        // 4. Sự kiện Click chọn Size
        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onSizeClick(fruitSize);
            }
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
}