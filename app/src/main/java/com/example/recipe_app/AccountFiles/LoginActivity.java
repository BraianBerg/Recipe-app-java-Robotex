package com.example.recipe_app.AccountFiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipe_app.MainActivity;
import com.example.recipe_app.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    EditText email;
    EditText password;
    Button toRegister;
    Button logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.editTextTextEmailAddress2);
        logIn = findViewById(R.id.LoginNow);
        password = findViewById(R.id.editTextTextPassword2);
        toRegister = findViewById(R.id.toRegisterPage);
        firebaseAuth = FirebaseAuth.getInstance();

        toRegister.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        logIn.setOnClickListener(view -> LogInMethod());
    }

    private void LogInMethod() {
        String emailValue = email.getText().toString().trim();
        String passwordValue = password.getText().toString().trim();
        if (TextUtils.isEmpty(emailValue)){
            email.setError("Email can not be empty");
            email.requestFocus();
        }
        else if (TextUtils.isEmpty(passwordValue)){
            password.setError("Password can not be empty");
            password.requestFocus();
        }else {
            firebaseAuth.signInWithEmailAndPassword(emailValue, passwordValue).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "User logged in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }else{
                    Toast.makeText(LoginActivity.this, "Login error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}