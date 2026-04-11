package com.example.ungdungbantraicay.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.DAO.CartDAO;
import com.example.ungdungbantraicay.Helper.ImageHelper;
import com.example.ungdungbantraicay.Model.CartItem;
import com.example.ungdungbantraicay.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context context;
    private List<CartItem> list;
    private CartUpdateListener listener;

    // Interface đầy đủ các hành động nghiệp vụ
    public interface CartUpdateListener {
        void onIncreaseQuantity(CartItem item, int position);
        void onDecreaseQuantity(CartItem item, int position);
        void onDeleteItem(CartItem item, int position);
    }

    public CartAdapter(Context context, List<CartItem> list, CartUpdateListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = list.get(position);

        // Hiển thị dữ liệu (Chỉ giữ lại phần hiển thị)
        holder.tvName.setText(item.getFruitName());
        holder.tvSize.setText("Size: " + item.getSizeName());
        holder.tvPrice.setText(String.format("%,d VND", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        int resId = context.getResources().getIdentifier(item.getFruitImage(), "drawable", context.getPackageName());
        ImageHelper.loadFruitImage(context, item.getFruitImage(), holder.imgFruit);
        // Bắt sự kiện và gửi ngược lại cho Fragment/Activity xử lý
        holder.btnPlus.setOnClickListener(v -> {
            if (listener != null) listener.onIncreaseQuantity(item, position);
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (listener != null) listener.onDecreaseQuantity(item, position);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteItem(item, position);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFruit, btnDelete;
        TextView tvName, tvSize, tvPrice, tvQuantity, btnPlus, btnMinus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFruit = itemView.findViewById(R.id.imgCartItem);
            tvName = itemView.findViewById(R.id.tvCartName);
            tvSize = itemView.findViewById(R.id.tvCartSize);
            tvPrice = itemView.findViewById(R.id.tvCartPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlusCart);
            btnMinus = itemView.findViewById(R.id.btnMinusCart);
            btnDelete = itemView.findViewById(R.id.btnDeleteCart);
        }
    }
}
