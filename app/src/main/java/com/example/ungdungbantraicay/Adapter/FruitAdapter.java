package com.example.ungdungbantraicay.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ungdungbantraicay.Model.Fruit;
import com.example.ungdungbantraicay.R;
import java.util.ArrayList;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {

    Context context;
    ArrayList<Fruit> list;

    // 1. Định nghĩa Interface để Fragment lắng nghe
    public interface OnFruitItemClickListener {
        void onFruitClick(Fruit fruit);
    }

    private OnFruitItemClickListener listener;

    // 2. Cập nhật Constructor để nhận listener
    public FruitAdapter(Context context, ArrayList<Fruit> list, OnFruitItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fruit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Fruit fruit = list.get(position);
        holder.tvName.setText(fruit.getName());

        int resId = context.getResources().getIdentifier(fruit.getImage(), "drawable", context.getPackageName());
        holder.imgFruit.setImageResource(resId);

        // 3. Thay vì viết Intent ở đây, ta gọi listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFruitClick(fruit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFruit;
        TextView tvName;
        public ViewHolder(View itemView) {
            super(itemView);
            imgFruit = itemView.findViewById(R.id.imgFruit);
            tvName = itemView.findViewById(R.id.tvFruitName);
        }
    }
}