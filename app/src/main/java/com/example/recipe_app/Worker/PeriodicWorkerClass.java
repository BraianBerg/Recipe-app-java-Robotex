package com.example.recipe_app.Worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.example.recipe_app.Models.Recipe;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class PeriodicWorkerClass extends Worker {
    private static final String TAG = "PeriodicWorkerClass";
    public PeriodicWorkerClass(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {
        try {
            DeleteRecipes();
            return Result.success();
        }catch (Exception e){
            return Result.retry();
        }


    }

    private void DeleteRecipes(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference recipes = db.collection("recipes");
        final CollectionReference details = db.collection("details");
        WriteBatch batch = db.batch();
        WriteBatch batch1 = db.batch();
        final int[] id = new int[1];

        recipes.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()){
                List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                Log.d(TAG, "DeleteRecipes: sna[shot" + snapshots.size());
                for (int i  = 0; i < snapshots.size(); i++ ){
                    if (i == 0){
                        Log.d(TAG, "DeleteRecipes: " + i);

                        Recipe res = snapshots.get(i).toObject(Recipe.class);
                        Log.d(TAG, "DeleteRecipes: id " + res.id);
                        id[0] = res.id;
                    }
                    else {
                        DocumentReference documentReference = snapshots.get(i).getReference();
                        batch.delete(documentReference);
                    }
                }
                batch.commit().addOnSuccessListener(unused -> Log.e(TAG, "onSuccess: recipes deleted" ));
                Log.d(TAG, "onSuccess: recipes found and added");

            }
        });

        details.whereNotEqualTo("id", id[0]).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()){
                List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot ds: snapshots){
                    DocumentReference dr = ds.getReference();
                    batch1.delete(dr);
                }
                Log.d(TAG, "onSuccess: details found and added");
                batch1.commit().addOnSuccessListener(unused -> Log.e(TAG, "onSuccess: details deleted" ));
            }
        });







    }



}
