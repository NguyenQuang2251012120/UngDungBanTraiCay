package com.example.ungdungbantraicay.AdminAdapter;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.R;

import java.text.NumberFormat;
import java.util.Locale;

public class StatisticAdapter extends RecyclerView.Adapter<StatisticAdapter.ViewHolder> {

    private Cursor cursor;
    private int type; // 0: User, 1: Day, 2: Month, 3: Category, 4: Best-selling

    public StatisticAdapter(Cursor cursor, int type) {
        this.cursor = cursor;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistic, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            String label = "";
            long value = 0;

            switch (type) {
                case 0: // Theo khách hàng
                    label = cursor.getString(0); // fullname
                    value = cursor.getLong(2);   // total_spent
                    break;
                case 1: // Theo ngày
                case 2: // Theo tháng
                    label = cursor.getString(0); // order_date hoặc month_year
                    value = cursor.getLong(1);   // revenue
                    break;
                case 3: // Theo danh mục
                    label = cursor.getString(0); // cat_name
                    value = cursor.getLong(1);   // cat_revenue
                    break;
                case 4: // Sản phẩm bán chạy
                    label = cursor.getString(0); // fruit_name
                    value = cursor.getLong(1);   // total_sold (lúc này value là số lượng)
                    break;
            }

            holder.tvLabel.setText(label);

            if (type == 4) {
                holder.tvValue.setText(value + " sản phẩm");
                holder.tvValue.setTextColor(0xFF2E7D32); // Màu xanh cho số lượng
            } else {
                holder.tvValue.setText(formatVND(value));
                holder.tvValue.setTextColor(0xFFD32F2F); // Màu đỏ cho tiền
            }
        }
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    private String formatVND(long amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvValue;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvStatLabel);
            tvValue = itemView.findViewById(R.id.tvStatValue);
        }
    }
}