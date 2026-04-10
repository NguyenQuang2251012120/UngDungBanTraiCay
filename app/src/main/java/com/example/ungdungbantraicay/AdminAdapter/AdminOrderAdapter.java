package com.example.ungdungbantraicay.AdminAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.R;
import java.text.NumberFormat;
import java.util.Locale;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {
    private Cursor cursor;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onUpdateStatus(int orderId, int currentStatus);
        void onCancelOrder(int orderId);
        void onItemClick(int orderId);
    }

    public AdminOrderAdapter(Cursor cursor, OnOrderActionListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            // Lấy index an toàn
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_ID));
            String receiverName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_RECEIVER_NAME));
            String receiverPhone = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_RECEIVER_PHONE));
            long total = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_TOTAL));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_STATUS));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_DATE));

            holder.tvId.setText("Mã đơn: #" + id);
            // Hiển thị tên và sđt người nhận trực tiếp từ đơn hàng
            holder.tvCustomer.setText("Người nhận: " + receiverName + " (" + receiverPhone + ")");
            holder.tvTotal.setText("Tổng: " + NumberFormat.getCurrencyInstance(new Locale("vi","VN")).format(total));
            holder.tvDate.setText("Ngày đặt: " + date);
            holder.tvStatus.setText("Trạng thái: " + DBHelper.getStatusName(status));

            // Logic ẩn/hiện nút xử lý
            if (status == DBHelper.STATUS_SUCCESS || status == DBHelper.STATUS_CANCELLED) {
                holder.layoutButtons.setVisibility(View.GONE);
            } else {
                holder.layoutButtons.setVisibility(View.VISIBLE);
                // Tùy biến chữ trên nút dựa theo trạng thái hiện tại
                if (status == DBHelper.STATUS_PENDING) {
                    holder.btnNext.setText("Xác nhận đơn");
                } else if (status == DBHelper.STATUS_CONFIRMED) {
                    holder.btnNext.setText("Giao hàng");
                } else if (status == DBHelper.STATUS_SHIPPING) {
                    holder.btnNext.setText("Hoàn thành");
                }
            }

            holder.btnNext.setOnClickListener(v -> listener.onUpdateStatus(id, status));
            holder.btnCancel.setOnClickListener(v -> listener.onCancelOrder(id));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(id));
        }
    }

    @Override
    public int getItemCount() { return cursor != null ? cursor.getCount() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvCustomer, tvStatus, tvTotal, tvDate;
        Button btnNext, btnCancel;
        View layoutButtons;
        public ViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvOrderId);
            tvCustomer = itemView.findViewById(R.id.tvOrderCustomer);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            btnNext = itemView.findViewById(R.id.btnNextStatus);
            btnCancel = itemView.findViewById(R.id.btnCancelOrder);
            layoutButtons = itemView.findViewById(R.id.layoutActionButtons);
        }
    }
}