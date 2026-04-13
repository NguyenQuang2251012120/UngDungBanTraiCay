package com.example.ungdungbantraicay.AdminAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ungdungbantraicay.Model.OrderItem;
import com.example.ungdungbantraicay.R;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private List<OrderItem> list;

    public OrderDetailAdapter(List<OrderItem> list) { this.list = list; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_order_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = list.get(position);
        holder.tvName.setText(item.getFruitName());
        holder.tvSize.setText("Phân loại: " + item.getSizeName());

        String priceFormatted = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(item.getPrice());
        holder.tvQtyPrice.setText("x" + item.getQuantity() + " | " + priceFormatted);

        // Xử lý ảnh (nếu bạn dùng resource name)
        int resId = holder.itemView.getContext().getResources().getIdentifier(
                item.getFruitImage(), "drawable", holder.itemView.getContext().getPackageName());
        if (resId != 0) holder.imgFruit.setImageResource(resId);
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSize, tvQtyPrice;
        ImageView imgFruit;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvDetailName);
            tvSize = itemView.findViewById(R.id.tvDetailSize);
            tvQtyPrice = itemView.findViewById(R.id.tvDetailQtyPrice);
            imgFruit = itemView.findViewById(R.id.imgDetailFruit);
        }
    }
}