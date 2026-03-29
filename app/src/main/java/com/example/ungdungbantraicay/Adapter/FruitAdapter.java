package com.example.ungdungbantraicay.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.Activities.FruitDetailActivity;
import com.example.ungdungbantraicay.Model.Fruit;
import com.example.ungdungbantraicay.R;

import java.util.ArrayList;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {

    Context context;
    ArrayList<Fruit> list;

    public FruitAdapter(Context context, ArrayList<Fruit> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_fruit, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Fruit fruit = list.get(position);

        holder.tvName.setText(fruit.getName());

        int resId = context.getResources().getIdentifier(
                fruit.getImage(),
                "drawable",
                context.getPackageName()
        );

        holder.imgFruit.setImageResource(resId);

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, FruitDetailActivity.class);

            intent.putExtra("id", fruit.getId());
            intent.putExtra("name", fruit.getName());
            intent.putExtra("description", fruit.getDescription());
            intent.putExtra("image", fruit.getImage());
            intent.putExtra("fruit_id", fruit.getId());

            context.startActivity(intent);
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