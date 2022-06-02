package com.example.recipe_app.AccountFiles;

import android.util.Log;


import com.example.recipe_app.Database.Callbacks.FirestoreCallback;
import com.example.recipe_app.Models.RandomRecipeApiResponse;
import com.example.recipe_app.Models.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LikedDbMethods {
    private static final String TAG = "LikedDbMethods";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser user = auth.getCurrentUser();
    private final CollectionReference userCollection = db.collection("user");

    public void AddResToLiked(Recipe recipeModelToAdd){

        userCollection.document(user.getUid()).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()){
                // make document and add to db
                RandomRecipeApiResponse response = snapshot.toObject(RandomRecipeApiResponse.class);
                response.recipes.add(recipeModelToAdd);
                userCollection.document(user.getUid()).update("recipes", response.recipes);
                // update document
            }else{
                // add to document
                RandomRecipeApiResponse response = new RandomRecipeApiResponse();
                response.recipes = new ArrayList<>();
                response.recipes.add(recipeModelToAdd);
                userCollection.document(user.getUid()).set(response);
            }
        });

    }
    public void RemoveResFromLiked(Recipe recipeModelToRemove, FirestoreCallback callback){
        userCollection.document(user.getUid()).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()){
                Log.d(TAG, "onSuccess: unlike method called ");
                RandomRecipeApiResponse response = snapshot.toObject(RandomRecipeApiResponse.class);
                response.recipes = RemoveFromList(response.recipes, recipeModelToRemove);
                Log.d(TAG, "onSuccess: remove id" + recipeModelToRemove.id);
                userCollection.document(user.getUid()).update("recipes", response.recipes);
                callback.GotData(response, "data updated");
            }
        }).addOnFailureListener(e -> callback.DidNotGetData(e.getMessage()));
    }
    private ArrayList<Recipe> RemoveFromList(ArrayList<Recipe> list, Recipe res){
        ArrayList<Recipe> listNew = new ArrayList<>();

        for (Recipe ressing: list){
            if (ressing.id != res.id){
                listNew.add(ressing);
            }
        }

        return listNew;

    }


    public void GetLikedList(FireStoreCallBackLikedList callBack){

        userCollection.document(user.getUid()).get().addOnSuccessListener(snapshot -> {
             if (snapshot.exists()) {
                RandomRecipeApiResponse response = snapshot.toObject(RandomRecipeApiResponse.class);
                callBack.GotLikedList(response);
             }
        });

    }

}
