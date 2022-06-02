package com.example.recipe_app.AccountFiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipe_app.R;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    EditText email;
    EditText password;
    Button register;
    Button toLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        register = findViewById(R.id.buttonregisterNow);
        toLogin = findViewById(R.id.toLoginPage);
        firebaseAuth = FirebaseAuth.getInstance();


        toLogin.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        register.setOnClickListener(view -> CreateUser());
    }

    private void CreateUser() {
        String emailValue = email.getText().toString().trim();
        String passwordValue = password.getText().toString().trim();

        if (TextUtils.isEmpty(emailValue)){
            email.setError("Email can not be empty");
            email.requestFocus();
        }
        else if (TextUtils.isEmpty(passwordValue)){
            password.setError("Password can not be empty");
            password.requestFocus();
        }else{
            firebaseAuth.createUserWithEmailAndPassword(emailValue, passwordValue).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Account registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }else{
                    Toast.makeText(RegisterActivity.this, "Register error: " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}