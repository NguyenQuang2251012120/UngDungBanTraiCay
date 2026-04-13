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
    private OnOrderClickListener listener;

    // --- BƯỚC 1: Thêm onCancelClick vào Interface ---
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onCancelClick(Order order);
    }

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

        holder.tvId.setText("Mã đơn: #" + order.getId());
        holder.tvDate.setText("Ngày đặt: " + order.getCreatedAt());
        holder.tvReceiver.setText("Người nhận: " + order.getReceiverName() + " (" + order.getReceiverPhone() + ")");
        holder.tvAddress.setText("Địa chỉ: " + order.getAddress());
        holder.tvTotal.setText(String.format("Tổng tiền: %,d VND", order.getTotalPrice()));

        String methodText = (order.getPaymentMethod() == 1) ? "VNPay" : "Tiền mặt";
        holder.tvStatus.setText(DBHelper.getStatusName(order.getStatus()) + " | " + methodText);
        updateStatusColor(holder.tvStatus, order.getStatus());

        // --- LOGIC: Chỉ hiện nút Hủy khi trạng thái là Chờ xác nhận (0) ---
        if (order.getStatus() == DBHelper.STATUS_PENDING) {
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else {
            holder.btnCancel.setVisibility(View.GONE);
        }

        // Sự kiện click nút Hủy
        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancelClick(order);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(order);
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
        TextView tvId, tvStatus, tvDate, tvAddress, tvTotal, tvReceiver;
        android.widget.Button btnCancel; // Khai báo nút Hủy

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            tvAddress = itemView.findViewById(R.id.tvOrderAddress);
            tvTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvReceiver = itemView.findViewById(R.id.tvOrderReceiver);
            // Ánh xạ nút Hủy từ layout item_order.xml
            btnCancel = itemView.findViewById(R.id.btnCancelOrder);
        }
    }
}