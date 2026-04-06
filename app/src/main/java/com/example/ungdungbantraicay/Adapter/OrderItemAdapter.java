package com.example.ungdungbantraicay.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.DAO.ReviewDAO;
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.OrderItem;
import com.example.ungdungbantraicay.R;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
    private Context context;
    private List<OrderItem> list;
    private int orderStatus;
    private int userId;
    private OnOrderItemActionListener actionListener;
    private OnReviewClickListener reviewListener;

    public interface OnReviewClickListener {
        void onReviewClick(OrderItem item);
    }

    public interface OnOrderItemActionListener {
        void onReview(OrderItem item);
        void onDeleteReview(OrderItem item);
    }

    public OrderItemAdapter(Context context, List<OrderItem> list, int orderStatus, int userId, OnOrderItemActionListener listener) {
        this.context = context;
        this.list = list;
        this.orderStatus = orderStatus;
        this.userId = userId;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = list.get(position);
        holder.tvName.setText(item.getFruitName());
        holder.tvSizeAndQty.setText("Size: " + item.getSizeName() + " | SL: " + item.getQuantity());
        holder.tvPrice.setText(String.format("%,d VND", item.getPrice()));
        ReviewDAO reviewDAO = new ReviewDAO(context);

        int resId = context.getResources().getIdentifier(item.getFruitImage(), "drawable", context.getPackageName());
        holder.imgFruit.setImageResource(resId);
        if (orderStatus == DBHelper.STATUS_SUCCESS) {
            boolean alreadyReviewed = reviewDAO.isAlreadyReviewed(userId, item.getFruitId());

            if (!alreadyReviewed) {
                // TRƯỜNG HỢP 1: CHƯA ĐÁNH GIÁ -> HIỆN NÚT ĐÁNH GIÁ
                holder.btnAction.setVisibility(View.VISIBLE);
                holder.btnAction.setText("Đánh giá");
                holder.btnAction.setTextColor(Color.parseColor("#FF4500"));
                holder.btnAction.setOnClickListener(v -> actionListener.onReview(item));
            } else {
                // TRƯỜNG HỢP 2: ĐÃ ĐÁNH GIÁ -> HIỆN NÚT XÓA ĐÁNH GIÁ
                holder.btnAction.setVisibility(View.VISIBLE);
                holder.btnAction.setText("Xóa đánh giá");
                holder.btnAction.setTextColor(Color.GRAY);
                holder.btnAction.setOnClickListener(v -> actionListener.onDeleteReview(item));
            }
        } else {
            holder.btnAction.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFruit;
        TextView tvName, tvSizeAndQty, tvPrice;
        TextView btnAction;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFruit = itemView.findViewById(R.id.imgOrderDetail);
            tvName = itemView.findViewById(R.id.tvOrderDetailName);
            tvSizeAndQty = itemView.findViewById(R.id.tvOrderDetailSizeQty);
            tvPrice = itemView.findViewById(R.id.tvOrderDetailPrice);
            btnAction = itemView.findViewById(R.id.btnReviewItem);        }
    }
}