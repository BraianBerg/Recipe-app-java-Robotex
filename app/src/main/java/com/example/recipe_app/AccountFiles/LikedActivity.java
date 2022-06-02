package com.example.recipe_app.AccountFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.recipe_app.Adapters.RandomRecipeAdapter;
import com.example.recipe_app.Database.Callbacks.FirestoreCallback;
import com.example.recipe_app.Listeners.LikeListener;
import com.example.recipe_app.Listeners.RecipeClickListener;
import com.example.recipe_app.MainActivity;
import com.example.recipe_app.Models.RandomRecipeApiResponse;
import com.example.recipe_app.Models.Recipe;
import com.example.recipe_app.R;
import com.example.recipe_app.RecipeDetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class LikedActivity extends AppCompatActivity {
    private static final String TAG = "LikedActivity";
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    private LikedDbMethods dbMethods = new LikedDbMethods();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked);

        dbMethods.GetLikedList(model -> getDataFormDb(model.recipes));

    }

    private void getDataFormDb(List<Recipe> list){
        recyclerView = findViewById(R.id.recycler_random2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(LikedActivity.this, 1));
        adapter = new LikedAdapter(LikedActivity.this, list, clickListener, likeListener );
        recyclerView.setAdapter(adapter);

    }
    private final FirestoreCallback removeFromListCallBack = new FirestoreCallback() {
        @Override
        public void GotData(RandomRecipeApiResponse model, String messege) {
            getDataFormDb(model.recipes);
        }

        @Override
        public void DidNotGetData(String messege) {
            Log.e(TAG, "DidNotGetData: " + messege );
        }
    };
    private final LikeListener likeListener = recipeModel -> dbMethods.RemoveResFromLiked(recipeModel,removeFromListCallBack );


    private final RecipeClickListener clickListener = id -> startActivity(new Intent(LikedActivity.this, RecipeDetailsActivity.class)
            .putExtra("id", id).putExtra("boolSimilar", "false"));
}