package com.example.ungdungbantraicay.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.Model.FruitSize;
import com.example.ungdungbantraicay.R;

import java.util.ArrayList;

public class FruitSizeAdapter extends RecyclerView.Adapter<FruitSizeAdapter.ViewHolder>{

    Context context;
    ArrayList<FruitSize> list;

    public FruitSizeAdapter(Context context, ArrayList<FruitSize> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_size,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        FruitSize size = list.get(position);

        holder.tvSize.setText(size.getSize());
        holder.tvPrice.setText(size.getPrice() + " VND");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvSize,tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);

            tvSize = itemView.findViewById(R.id.tvSize);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}