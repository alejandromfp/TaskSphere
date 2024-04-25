package com.example.tasksphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    Button botonLogin;

    private FirebaseAuth mAuth;

    EditText emailText, passText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.cajaCorreo);
        passText = findViewById((R.id.cajaPass));

        botonLogin = findViewById(R.id.botonLogin);
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LOGIN EN FIREBASE

                String email = emailText.getText().toString();
                String password = passText.getText().toString();

                if (email.isEmpty()) {
                    emailText.setError("Campo obligatorio");

                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailText.setError("Correo incorrecto");

                } else if (password.length() < 6) {
                    passText.setError("Minimo 6 caracteres");

                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });
    }
}