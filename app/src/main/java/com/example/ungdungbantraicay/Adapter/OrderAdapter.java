package com.example.ungdungbantraicay.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.Order;
import com.example.ungdungbantraicay.R;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private List<Order> list;

    // --- BƯỚC 1: Định nghĩa Interface ---
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private OnOrderClickListener listener;

    // --- BƯỚC 2: Cập nhật Constructor nhận thêm listener ---
    public OrderAdapter(Context context, List<Order> list, OnOrderClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = list.get(position);

        // Hiển thị dữ liệu
        holder.tvId.setText("Mã đơn: #" + order.getId());
        holder.tvDate.setText("Ngày đặt: " + order.getCreatedAt());
        holder.tvAddress.setText("Giao đến: " + order.getAddress());
        holder.tvTotal.setText(String.format("Tổng tiền: %,d VND", order.getTotalPrice()));

        String statusName = DBHelper.getStatusName(order.getStatus());
        holder.tvStatus.setText(statusName);

        // Màu sắc trạng thái
        updateStatusColor(holder.tvStatus, order.getStatus());

        // --- BƯỚC 3: Bắt sự kiện click vào item và báo cho Fragment ---
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    private void updateStatusColor(TextView tvStatus, int status) {
        switch (status) {
            case DBHelper.STATUS_PENDING: tvStatus.setTextColor(Color.parseColor("#FF9800")); break;
            case DBHelper.STATUS_SUCCESS: tvStatus.setTextColor(Color.parseColor("#4CAF50")); break;
            case DBHelper.STATUS_CANCELLED: tvStatus.setTextColor(Color.RED); break;
            default: tvStatus.setTextColor(Color.GRAY); break;
        }
    }

    @Override
    public int getItemCount() { return list != null ? list.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvStatus, tvDate, tvAddress, tvTotal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            tvAddress = itemView.findViewById(R.id.tvOrderAddress);
            tvTotal = itemView.findViewById(R.id.tvOrderTotal);
        }
    }
}