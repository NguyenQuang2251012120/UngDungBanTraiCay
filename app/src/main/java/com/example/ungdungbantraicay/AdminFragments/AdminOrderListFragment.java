package com.example.ungdungbantraicay.AdminFragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.AdminAdapter.AdminOrderAdapter;
import com.example.ungdungbantraicay.AdminAdapter.OrderDetailAdapter;
import com.example.ungdungbantraicay.DAO.OrderDAO;
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.OrderItem;
import com.example.ungdungbantraicay.R;

import java.util.List;

public class AdminOrderListFragment extends Fragment implements AdminOrderAdapter.OnOrderActionListener {
    private RecyclerView rvOrders;
    private OrderDAO orderDAO;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo DAO sớm để sẵn sàng sử dụng
        orderDAO = new OrderDAO(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Cần phải inflate layout tại đây
        return inflater.inflate(R.layout.fragment_admin_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Ánh xạ View
        rvOrders = view.findViewById(R.id.rvAdminOrderList);

        // Thiết lập LayoutManager
        if (rvOrders != null) {
            rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại danh sách mỗi khi tab được hiển thị
        loadData();
    }

    private void loadData() {
        // KIỂM TRA QUAN TRỌNG: rvOrders phải khác null mới được setAdapter
        if (orderDAO != null && rvOrders != null) {
            Cursor cursor = orderDAO.getAllOrdersWithUserInfo();
            if (cursor != null) {
                AdminOrderAdapter adapter = new AdminOrderAdapter(cursor, this);
                rvOrders.setAdapter(adapter);
                // Lưu ý: Không đóng cursor ở đây vì Adapter cần nó để hiển thị dữ liệu
            }
        }
    }

    @Override
    public void onUpdateStatus(int orderId, int currentStatus) {
        if (orderDAO != null) {
            // Luồng: Chờ (0) -> Xác nhận (1) -> Đang giao (2) -> Thành công (3)
            if (currentStatus < DBHelper.STATUS_SUCCESS) {
                int nextStatus = currentStatus + 1;
                if (orderDAO.updateOrderStatus(orderId, nextStatus)) {
                    Toast.makeText(getContext(), "Đã cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                    loadData();
                }
            }
        }
    }

    @Override
    public void onItemClick(int orderId) {
        showOrderDetailDialog(orderId);
    }

    private void showOrderDetailDialog(int orderId) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_admin_order_list, null);
        RecyclerView rvDetail = dialogView.findViewById(R.id.rvAdminOrderList); // Dùng lại RV trong layout có sẵn hoặc tạo layout riêng

        // Lấy dữ liệu chi tiết
        List<OrderItem> details = orderDAO.getOrderDetails(orderId);

        OrderDetailAdapter adapter = new OrderDetailAdapter(details);
        rvDetail.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDetail.setAdapter(adapter);

        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Chi tiết đơn hàng #" + orderId)
                .setView(dialogView)
                .setPositiveButton("Đóng", null)
                .show();
    }
    @Override
    public void onCancelOrder(int orderId) {
        if (orderDAO != null) {
            if (orderDAO.updateOrderStatus(orderId, DBHelper.STATUS_CANCELLED)) {
                Toast.makeText(getContext(), "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                loadData();
            }
        }
    }
}