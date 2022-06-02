package com.example.recipe_app.Database;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.recipe_app.Database.Callbacks.FirestoreAnalyzedInstructionsCallback;
import com.example.recipe_app.Database.Callbacks.FirestoreCallBackDetails;
import com.example.recipe_app.Listeners.InstructionsListener;
import com.example.recipe_app.Listeners.RecipeDetailsListener;
import com.example.recipe_app.Models.AnalyzedInstruction;
import com.example.recipe_app.Models.InstructionsResponse;
import com.example.recipe_app.Models.Recipe;
import com.example.recipe_app.Models.RecipeDetailsResponce;
import com.example.recipe_app.RequestManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

public class DbMethodsDetails {
    private static final String TAG = "DbMethodsDetails";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference details = db.collection("details");

    public void GetRecipeDetailsFromDb(int id, FirestoreCallBackDetails firestoreCallBackDetails, FirestoreAnalyzedInstructionsCallback instructionsCallback, Context context, Boolean checkIfResExists){
        details.whereEqualTo("id", id).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()){
                DocumentSnapshot documentReference = queryDocumentSnapshots.getDocuments().get(0);
                RecipeDetailsResponce detailsres = documentReference.toObject(RecipeDetailsResponce.class);
                firestoreCallBackDetails.GotDetails(detailsres);
                if (!detailsres.analyzedInstructions.isEmpty()){
                    instructionsCallback.GotAnalyzedInstructions(AnalyzedToInstructions(detailsres.analyzedInstructions));
                }else{
                    // call api
                    CallAnalyzedInstructionsAPI(id, instructionsCallback, context, documentReference);
                }
            }
            else {
                // call details api
                CallDetailsAPI(id, firestoreCallBackDetails,instructionsCallback ,context, checkIfResExists);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: " + e.getMessage());
            firestoreCallBackDetails.DidNotGetDetails(e.getMessage());
        });
    }
    private void CallDetailsAPI(int id, FirestoreCallBackDetails callback,FirestoreAnalyzedInstructionsCallback instructionsCallback, Context context, Boolean checkIfResExists){
        RequestManager manager = new RequestManager(context);
        manager.getRecipeDetails(new RecipeDetailsListener() {
            @Override
            public void didFetch(RecipeDetailsResponce response, String message) {
                //save to db
                response.summary = Html2Text(response.instructions);
                response.instructions = Html2Text(response.instructions);
                //callback
                if (!response.analyzedInstructions.isEmpty()){
                    // kui on nalyzed instructions olemas
                    callback.GotDetails(response);
                    instructionsCallback.GotAnalyzedInstructions(AnalyzedToInstructions(response.analyzedInstructions));
                    SaveDetailsToDb thread2 = new SaveDetailsToDb(response, context);
                    thread2.start();
                }else{
                    // kui pole instructioneid olemas
                    callback.GotDetails(response);

                    CallInstruscionsAndSaveNewDoc(context, id, instructionsCallback, response);

                }

                Log.d(TAG, "didFetch: check bool: " + checkIfResExists);
                if (checkIfResExists){
                    // kutsu kontroll
                    Log.d(TAG, "didFetch: kontroll passed");
                    CheckIfResExistsAndWrite checkIfResExistsAndWrite = new CheckIfResExistsAndWrite(id, response, context);
                    checkIfResExistsAndWrite.start();
                }

            }
            @Override
            public void didError(String message) {
                //callback
                callback.DidNotGetDetails(message);
                Log.e(TAG, "didError: " + message);
            }
        },id);
    }
    private String Html2Text(String html){

        return Jsoup.parse(html).text();
    }
    private static class CheckIfResExistsAndWrite extends Thread{
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();
        private final CollectionReference recipes = db.collection("recipes");
        private final int id;
        private final RecipeDetailsResponce responce;
        private final Context context;
        Handler handler = new Handler(Looper.getMainLooper());

        public CheckIfResExistsAndWrite (int id, RecipeDetailsResponce responce, Context context){
            this.id = id;
            this.responce = responce;
            this.context = context;
        }

        @Override
        public void run() {
            CheckIfResExists(responce, id);
            handler.post(() -> Toast.makeText(context, "Background thread is saving data", Toast.LENGTH_SHORT).show());
        }

        private void CheckIfResExists(RecipeDetailsResponce responce, int id){
            recipes.whereEqualTo("id", id).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.isEmpty()){
                    Log.d(TAG, "onSuccess: andmebaasi kirjutamie hakkab");
                    // kirjuta andmebaasi
                    WriteToDb(responce);
                }
            }).addOnFailureListener(e -> {
                Log.d(TAG, "onFailure: db chekc failed");
                WriteToDb(responce);
            });
        }

        private void WriteToDb(RecipeDetailsResponce responce){

            recipes.add(DetailsToRecipe(responce));
        }
        private Recipe DetailsToRecipe(RecipeDetailsResponce responce){
            Recipe recipe = new Recipe();
            recipe.vegetarian = responce.vegetarian;
            recipe.vegan = responce.vegan;
            recipe.glutenFree = responce.glutenFree;
            recipe.dairyFree = responce.dairyFree;
            recipe.veryHealthy = responce.veryHealthy;
            recipe.cheap = responce.cheap;
            recipe.veryPopular = responce.veryPopular;
            recipe.sustainable = responce.sustainable;
            recipe.lowFodmap = responce.lowFodmap;
            recipe.weightWatcherSmartPoints = responce.weightWatcherSmartPoints;
            recipe.gaps = responce.gaps;
            recipe.preparationMinutes = responce.readyInMinutes;
            recipe.cookingMinutes = responce.readyInMinutes;
            recipe.aggregateLikes = responce.aggregateLikes;
            recipe.healthScore = (int) responce.healthScore;
            recipe.creditsText = responce.creditsText;
            recipe.license = responce.license;
            recipe.sourceName = responce.sourceName;
            recipe.pricePerServing = responce.pricePerServing;
            recipe.extendedIngredients = responce.extendedIngredients;
            recipe.id = responce.id;
            recipe.title = responce.title;
            recipe.readyInMinutes = responce.readyInMinutes;
            recipe.servings = responce.servings;
            recipe.sourceUrl = responce.sourceUrl;
            recipe.image = responce.image;
            recipe.imageType = responce.imageType;
            recipe.summary = responce.summary;
            recipe.cuisines = responce.cuisines;
            recipe.dishTypes = responce.dishTypes;
            recipe.diets = responce.diets;
            recipe.occasions = responce.occasions;
            recipe.instructions = responce.instructions;
            recipe.analyzedInstructions = responce.analyzedInstructions;
            recipe.originalId = responce.id;
            recipe.spoonacularSourceUrl = responce.spoonacularSourceUrl;
            return recipe;
        }


    }
    private static class SaveDetailsToDb extends Thread{
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();
        private final CollectionReference details = db.collection("details");
        private final RecipeDetailsResponce detailsModel;
        private final Context context;
        Handler handler = new Handler(Looper.getMainLooper());


        public SaveDetailsToDb(RecipeDetailsResponce detailsModel,Context context){
            this.detailsModel = detailsModel;
            this.context = context;
        }

        @Override
        public void run() {
            //super.run();
            Log.d(TAG, "run: second thread write db");
            handler.post(() -> Toast.makeText(context,"Background thread is saving details to Db ", Toast.LENGTH_SHORT).show());
            SaveDetails(detailsModel);
        }
        private void SaveDetails(RecipeDetailsResponce detailsModel){
            details.add(detailsModel);
        }
    }
    private void CallAnalyzedInstructionsAPI(int id, FirestoreAnalyzedInstructionsCallback callback, Context context,@Nullable DocumentSnapshot snapshot){
        RequestManager manager = new RequestManager(context);
        manager.getInstructions(new InstructionsListener() {
            @Override
            public void didFetch(List<InstructionsResponse> response, String message) {
                // call thread and save it to db
                ChangeDocumentsInstructions changeDocumentsInstructions = new ChangeDocumentsInstructions(snapshot, response, context);
                changeDocumentsInstructions.start();
                callback.GotAnalyzedInstructions(response);
            }

            @Override
            public void didError(String message) {
                Log.e(TAG, "didError: api call failed " +  message);
                callback.DidNotGetData(message);
            }
        }, id);


    }
    private static class ChangeDocumentsInstructions extends Thread{
        private static final String TAG = "ChangeDocumentsInstruct";
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();
        private final CollectionReference details = db.collection("details");
        private final DocumentSnapshot snapshot;
        private final List<InstructionsResponse> list;
        private final Context context;
        Handler handler = new Handler(Looper.getMainLooper());

        public ChangeDocumentsInstructions(DocumentSnapshot snapshot,List<InstructionsResponse> list, Context context){
            this.snapshot = snapshot;
            this.list = list;
            this.context = context;
        }
        @Override
        public void run() {
            ChangeDocumentInstructions(snapshot, list);

            handler.post(() -> Toast.makeText(context, "Background thread is changing documents", Toast.LENGTH_SHORT ).show());
        }
        private void ChangeDocumentInstructions(DocumentSnapshot snapshot, List<InstructionsResponse> list){
            DocumentReference docRef = details.document(snapshot.getId());
            docRef.update("analyzedInstructions", list)
                    .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: Andmed uuendatud andmebaasi id " + snapshot.getId()))
                    .addOnFailureListener(e -> Log.e(TAG, "onFailure: andemete uuendamien fail " + e.getMessage() ));

        }
    }
    private void CallInstruscionsAndSaveNewDoc(Context context, int id, FirestoreAnalyzedInstructionsCallback callback, RecipeDetailsResponce detailsResponse){
        RequestManager manager = new RequestManager(context);
        manager.getInstructions(new InstructionsListener() {
            @Override
            public void didFetch(List<InstructionsResponse> response, String message) {
                callback.GotAnalyzedInstructions(response);
                detailsResponse.analyzedInstructions = InstructionsToAnalyzed(response);

                //call thread
                SaveDetailsToDb saveDetailsToDb = new SaveDetailsToDb(detailsResponse, context);
                saveDetailsToDb.start();
            }

            @Override
            public void didError(String message) {
            callback.DidNotGetData(message);
            }
        }, id);
    }
    private ArrayList<InstructionsResponse> AnalyzedToInstructions(ArrayList<AnalyzedInstruction> list){
        ArrayList<InstructionsResponse> newList = new ArrayList<>();
        for (AnalyzedInstruction ai : list){
            InstructionsResponse ir = new InstructionsResponse();
            ir.steps = ai.steps;
            ir.name = ai.name;
            newList.add(ir);
        }
        return newList;
    }
    private ArrayList<AnalyzedInstruction> InstructionsToAnalyzed(List<InstructionsResponse> oldList){
        ArrayList<AnalyzedInstruction> newList = new ArrayList<>();
        for (InstructionsResponse ir: oldList){
            AnalyzedInstruction ai = new AnalyzedInstruction();
            ai.name = ir.name;
            ai.steps = ir.steps;
            newList.add(ai);

        }
        return newList;
    }



}
