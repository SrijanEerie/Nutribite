package com.example.nutribite;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final List<FoodItem> foodItems;

    public FoodAdapter(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodItems.get(position);
        holder.tvFoodName.setText(item.getName());
        holder.tvFoodDescription.setText(item.getDescription());

        if (item.getTag() != null && !item.getTag().isEmpty()) {
            holder.tvFoodTag.setText(item.getTag());
            holder.tvFoodTag.setVisibility(View.VISIBLE);
        } else {
            holder.tvFoodTag.setVisibility(View.GONE);
        }

        if (item.isAllergen()) {
            holder.tvAllergenInfo.setText("Contains: " + item.getAllergen());
            holder.tvAllergenInfo.setVisibility(View.VISIBLE);
            holder.tvFoodName.setTextColor(Color.RED);
        } else {
            holder.tvAllergenInfo.setVisibility(View.GONE);
            holder.tvFoodName.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvFoodDescription, tvFoodTag, tvAllergenInfo;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodDescription = itemView.findViewById(R.id.tvFoodDescription);
            tvFoodTag = itemView.findViewById(R.id.tvFoodTag);
            tvAllergenInfo = itemView.findViewById(R.id.tvAllergenInfo);
        }
    }
}
