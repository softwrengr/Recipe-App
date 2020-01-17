package com.fourtitude.recipeapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.fourtitude.recipeapp.R;
import com.fourtitude.recipeapp.models.RecipeModel;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder> {
    private List<RecipeModel> recipeList;
    private Context context;

    public RecipeAdapter(Context context, List<RecipeModel> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }


    @NonNull
    @Override
    public RecipeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_recipelist, parent, false);

        return new RecipeAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final RecipeAdapter.MyViewHolder viewHolder, final int position) {
        final RecipeModel model = recipeList.get(position);

        Glide.with(context).load(model.getImage()).into(viewHolder.ivRecipe);
        viewHolder.tvRecipeTitle.setText(model.getTitle());

    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipe;
        TextView tvRecipeTitle;
        RelativeLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecipe = itemView.findViewById(R.id.iv_recipe);
            tvRecipeTitle = itemView.findViewById(R.id.tv_recipe_tile);
            layout = itemView.findViewById(R.id.recipe_layout);

        }
    }
}
