package com.example.recipe_app.Database;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.recipe_app.Database.Callbacks.FirestoreCallback;
import com.example.recipe_app.Listeners.RandomRecipeResponseListener;
import com.example.recipe_app.Models.RandomRecipeApiResponse;
import com.example.recipe_app.Models.Recipe;
import com.example.recipe_app.RequestManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class DbMethods {
    private final FirebaseFirestore db= FirebaseFirestore.getInstance();
    private final CollectionReference recipes = db.collection("recipes");

    public void GetResByDishType(FirestoreCallback callback, List<String> dishTypes, Context context){

        recipes.whereArrayContainsAny("dishTypes", dishTypes).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()){
                List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                Log.e("BackRoundDbRandomResWri", "onSuccess: snapshot size " + snapshots.size() );
                if (snapshots.size() > 30){
                    ArrayList<Recipe> reslist = getRecipeArrayList(snapshots);

                    RandomRecipeApiResponse response = new RandomRecipeApiResponse();
                    response.recipes = reslist;
                    callback.GotData(response, "Form Database Successfully");
                }
                else {
                    CallAPIRandom(callback, dishTypes, context, getRecipeArrayList(snapshots));
                }
            }else
            {
                CallAPIRandom(callback, dishTypes, context, null);
            }
        });
    }

    @NonNull
    private ArrayList<Recipe> getRecipeArrayList(List<DocumentSnapshot> snapshots) {
        ArrayList<Recipe> reslist = new ArrayList<>();
        for (DocumentSnapshot ds : snapshots){
            Recipe res = ds.toObject(Recipe.class);
            reslist.add(res);
        }
        return reslist;
    }

    //calls api
    private void CallAPIRandom(FirestoreCallback firestoreCallback, List<String> tags, Context context,@Nullable ArrayList<Recipe> dbResList){
        RequestManager manager = new RequestManager(context);
        manager.getRandomRecipes(new RandomRecipeResponseListener() {
            @Override
            public void didFetch(RandomRecipeApiResponse response, String message) {
                if (dbResList == null) {
                    firestoreCallback.GotData(response, message);
                    Log.e("response", "didFetch: " +  response.recipes.size() );
                    // call db write on second thread
                    BackRoundDbRandomResWrite ss = new BackRoundDbRandomResWrite(response.recipes, context);
                    ss.start();

                }else {
                    // call worker thread db write
                    Log.e("BackRoundDbRandomResWri", "didFetch: dblist size " + dbResList.size() );
                    BackRoundDbRandomResWrite ss = new BackRoundDbRandomResWrite(response.recipes,context);
                    ss.start();
                    // if db has some data
                    response.recipes.addAll(dbResList);
                    firestoreCallback.GotData(response, message);
                }
            }

            @Override
            public void didError(String message) {
                firestoreCallback.DidNotGetData(message);
            }
        }, tags);
    }

    // second thread class
    private static class BackRoundDbRandomResWrite extends Thread {
        private static final String TAG = "BackRoundDbRandomResWri";
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();
        private final CollectionReference recipes = db.collection("recipes");
        private final ArrayList<Recipe> reslist;
        private final Context context;
        Handler handler = new Handler(Looper.getMainLooper());
        
        public BackRoundDbRandomResWrite(ArrayList<Recipe> reslist, Context context){
            this.reslist = reslist;
            this.context = context;
        }
        
        @Override
        public void run() {
            Log.d(TAG, "run: triggered");
            handler.post(() -> Toast.makeText(context, "Background thread is writing data to db", Toast.LENGTH_SHORT).show());
           WriteToDb(reslist);
        }
        
        private void WriteToDb(ArrayList<Recipe> reslist){
            WriteBatch writeBatch = db.batch();
            for (Recipe res : reslist){
                DocumentReference resDocRef = recipes.document();
                writeBatch.set(resDocRef, res);
            }
            writeBatch.commit().addOnSuccessListener(unused -> Log.d(TAG, "WriteToDb: Items Saved to DB On Second Thread")).addOnFailureListener(e -> Log.e(TAG, " Second thread write onFailure:  " + e.getMessage()));
        }
    }






}


