package com.example.recipe_app.AccountFiles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

import java.util.List;

public class LikedAdapter extends RecyclerView.Adapter<LikeViewHolder>{
        Context context;
        List<Recipe> list;
        RecipeClickListener listener;
        LikeListener likeListener;


        public LikedAdapter(Context context, List<Recipe> list, RecipeClickListener listener, LikeListener likeListener) {
            this.context = context;
            this.list = list;
            this.listener = listener;
            this.likeListener = likeListener;
        }

        @NonNull
        @Override
        public LikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LikeViewHolder(LayoutInflater.from(context).inflate(R.layout.list_liked_row, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull LikeViewHolder holder, int position) {
            holder.textView_title.setText(list.get(position).title);
            holder.textView_title.setSelected(true);
            holder.textView_Likes.setText(list.get(position).aggregateLikes+" Likes");
            holder.textView_Servings.setText(list.get(position).servings+" Servings");
            holder.textView_time.setText(list.get(position).readyInMinutes+" Minutes");
            Picasso.get().load(list.get(position).image).into(holder.imageView_food);
            holder.likeButton.setOnClickListener(view -> {
                holder.starImg.setImageResource(R.drawable.ic_baseline_star_outline_24);
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

    class LikeViewHolder extends RecyclerView.ViewHolder {
        CardView random_list_container;
        TextView textView_title,textView_Servings,textView_Likes,textView_time;
        ImageView imageView_food;
        LinearLayout likeButton;
        ImageView starImg;

        public LikeViewHolder(@NonNull View itemView) {
            super(itemView);
            starImg = itemView.findViewById(R.id.startId2);
            likeButton =itemView.findViewById(R.id.likeLinearLayoutButton2);
            random_list_container = itemView.findViewById(R.id.random_list_container2);
            textView_title = itemView.findViewById(R.id.textView_title2);
            textView_Servings = itemView.findViewById(R.id.textView_Servings2);
            textView_time = itemView.findViewById(R.id.textView_time2);
            textView_Likes = itemView.findViewById(R.id.textView_Likes2);
            imageView_food = itemView.findViewById(R.id.imageView_food2);
        }
    }


