package com.example.recipe_app.Database.Callbacks;

import com.example.recipe_app.Models.InstructionsResponse;

import java.util.List;

public interface FirestoreAnalyzedInstructionsCallback {
    void GotAnalyzedInstructions(List<InstructionsResponse> response);
    void DidNotGetData(String message);
}
