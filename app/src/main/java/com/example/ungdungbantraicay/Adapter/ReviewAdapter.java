package com.example.ungdungbantraicay.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.Activities.FruitDetailActivity;
import com.example.ungdungbantraicay.DAO.ReviewDAO;
import com.example.ungdungbantraicay.Model.Review;
import com.example.ungdungbantraicay.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Context context;
    private List<Review> reviewList;
    private int currentUserId;
    private OnReviewActionListener listener; // Interface để báo về Activity


    public interface OnReviewActionListener {
        void onEdit(Review review, int position);
        void onDelete(Review review, int position);
    }
    // 2. Cập nhật Constructor để nhận 3 tham số
    public ReviewAdapter(Context context, List<Review> reviewList, int currentUserId, OnReviewActionListener listener) {
        this.context = context;
        this.reviewList = reviewList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.tvUserName.setText(review.getUserName());
        holder.ratingBar.setRating(review.getRating());
        holder.tvComment.setText(review.getComment());
        holder.tvDate.setText(review.getCreatedAt());

        // Chỉ hiện nút Sửa/Xóa nếu là chủ sở hữu
        if (review.getUserId() == currentUserId) {
            holder.imgDelete.setVisibility(View.VISIBLE);
            holder.imgEdit.setVisibility(View.VISIBLE);

            holder.imgDelete.setOnClickListener(v -> listener.onDelete(review, position));
            holder.imgEdit.setOnClickListener(v -> listener.onEdit(review, position));
        } else {
            holder.imgDelete.setVisibility(View.GONE);
            holder.imgEdit.setVisibility(View.GONE);
        }
    }

    // 4. Hàm hiển thị hộp thoại xác nhận xóa
    private void showDeleteConfirmDialog(Review review, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xóa đánh giá")
                .setMessage("Bạn có chắc chắn muốn xóa nhận xét này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    ReviewDAO dao = new ReviewDAO(context);
                    if (dao.deleteReview(review.getId())) { // Gọi hàm xóa trong DAO
                        reviewList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, reviewList.size());
                        Toast.makeText(context, "Đã xóa đánh giá!", Toast.LENGTH_SHORT).show();

                        // 5. CỰC KỲ QUAN TRỌNG: Báo cho màn hình chi tiết hiện lại nút "Viết đánh giá"
                        if (context instanceof FruitDetailActivity) {
                            ((FruitDetailActivity) context).checkReviewEligibility(review.getFruitId());
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    private void showEditReviewDialog(Review review, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Tái sử dụng layout dialog_review.xml của bạn
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_review, null);
        builder.setView(view);
        builder.setTitle("Chỉnh sửa đánh giá");

        RatingBar rbReview = view.findViewById(R.id.rbReview);
        EditText edtComment = view.findViewById(R.id.edtReviewComment);

        // ĐỔ DỮ LIỆU CŨ VÀO DIALOG
        rbReview.setRating(review.getRating());
        edtComment.setText(review.getComment());

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            float newRating = rbReview.getRating();
            String newComment = edtComment.getText().toString().trim();

            if (newRating == 0) {
                Toast.makeText(context, "Vui lòng chọn số sao!", Toast.LENGTH_SHORT).show();
                return;
            }

            ReviewDAO dao = new ReviewDAO(context);
            if (dao.updateReview(review.getId(), (int) newRating, newComment)) {
                // Cập nhật lại Object trong danh sách tại chỗ
                review.setRating((int) newRating);
                review.setComment(newComment);
                notifyItemChanged(position);

                Toast.makeText(context, "Đã cập nhật đánh giá!", Toast.LENGTH_SHORT).show();

                // Nếu cần cập nhật lại điểm trung bình ở Activity
                if (context instanceof FruitDetailActivity) {
                    ((FruitDetailActivity) context).loadReviewData(review.getFruitId());
                }
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return (reviewList != null) ? reviewList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RatingBar ratingBar;
        TextView tvComment, tvDate, tvUserName;
        ImageView imgDelete, imgEdit; // Thêm imgEdit

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBarItem);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            imgDelete = itemView.findViewById(R.id.imgDeleteReview);
            imgEdit = itemView.findViewById(R.id.imgEditReview); // Ánh xạ tại đây
        }
    }
}