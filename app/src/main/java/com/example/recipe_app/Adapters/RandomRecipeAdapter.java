package com.example.recipe_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipe_app.Listeners.LikeListener;
import com.example.recipe_app.Listeners.RecipeClickListener;
import com.example.recipe_app.Models.Recipe;
import com.example.recipe_app.R;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.List;

public class RandomRecipeAdapter extends RecyclerView.Adapter<RandomRecipeViewHolder>{
    Context context;
    List<Recipe> list;
    RecipeClickListener listener;
    LikeListener likeListener;


    public RandomRecipeAdapter(Context context, List<Recipe> list, RecipeClickListener listener, LikeListener likeListener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.likeListener = likeListener;
    }

    @NonNull
    @Override
    public RandomRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RandomRecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.list_random_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RandomRecipeViewHolder holder, int position) {
        holder.textView_title.setText(list.get(position).title);
        holder.textView_title.setSelected(true);
        holder.textView_Likes.setText(MessageFormat.format("{0} Likes", list.get(position).aggregateLikes));
        holder.textView_Servings.setText(MessageFormat.format("{0} Servings", list.get(position).servings));
        holder.textView_time.setText(MessageFormat.format("{0} Minutes", list.get(position).readyInMinutes));
        Picasso.get().load(list.get(position).image).into(holder.imageView_food);
        holder.likeButton.setOnClickListener(view -> {
            holder.starImg.setImageResource(R.drawable.ic_baseline_star_24);
            likeListener.onLikeClicked(list.get(holder.getAdapterPosition()));
        });
        holder.random_list_container.setOnClickListener(view ->
                listener.onRecipeClicked(String.valueOf(list.get(holder.getAdapterPosition()).id)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class RandomRecipeViewHolder extends RecyclerView.ViewHolder {
    CardView random_list_container;
    TextView textView_title,textView_Servings,textView_Likes,textView_time;
    ImageView imageView_food;
    LinearLayout likeButton;
    ImageView starImg;

    public RandomRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        starImg = itemView.findViewById(R.id.starImage);
        likeButton =itemView.findViewById(R.id.likeLinearLayoutButton);
        random_list_container = itemView.findViewById(R.id.random_list_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        textView_Servings = itemView.findViewById(R.id.textView_Servings);
        textView_time = itemView.findViewById(R.id.textView_time);
        textView_Likes = itemView.findViewById(R.id.textView_Likes);
        imageView_food = itemView.findViewById(R.id.imageView_food);
    }
}
