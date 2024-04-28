package com.example.tasksphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tasksphere.modelo.entidad.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText emailText, passText;

    Button botonLogin;
    TextView botonRegistro;

    SharedPreferences sharedPreferences;

    User usuario;


    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        emailText = findViewById(R.id.cajaCorreo);
        passText = findViewById(R.id.cajaPass);

        botonLogin = findViewById(R.id.botonLogin);
        botonLogin.setOnClickListener(v -> {
            //LOGIN EN FIREBASE

            String email = emailText.getText().toString();
            String password = passText.getText().toString();

            if (email.isEmpty()){
                emailText.setError("Campo obligatorio");
            }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailText.setError("Email incorrecto");
            }else if(password.length() < 6){
                passText.setError("La contraseña debe tener al menos 6 caracteres");
            }else{
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    obtainUserFromDatabase();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        botonRegistro = findViewById(R.id.crearCuenta);
        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });
        /*
        botonRegistro = findViewById(R.id.crearCuenta);
        botonRegistro.setOnClickListener(v -> {
            //CREAR USUARIO EN FIREBASE

            String email = emailText.getText().toString();
            String password = passText.getText().toString();

            if (email.isEmpty()){
                emailText.setError("Campo obligatorio");
            }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailText.setError("Email incorrecto");
            }else if(password.length() < 6){
                passText.setError("La contraseña debe tener al menos 6 caracteres");
            }else{
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(Login.this, "Usuario registrado", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

         */
/*
        @Override
        public void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null){
                reload();
            }
        }
*/



    }

    private void obtainUserFromDatabase(){

        usuario = new User();
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(task -> {
                    Log.d("NONONO", "funciona");
                    if(task.isSuccessful()){

                        DocumentSnapshot doc = task.getResult();

                        if(doc.exists()){
                            usuario.setUserId(doc.getId());
                            usuario.setNombre(doc.getString("nombre"));
                            usuario.setApellidos(doc.getString("apellidos"));
                            usuario.setDireccion(doc.getString("direccion"));
                            usuario.setDni(doc.getString("dni"));
                            usuario.setEmail(mAuth.getCurrentUser().getEmail());
                            usuario.setRol(doc.getString("rol"));
                            usuario.setFechaNac(doc.getString("fechaNacimiento"));
                            usuario.setTelefono(doc.getString("telefono"));
                            usuario.setProfileImage(doc.getString("profile_img_path"));
                            Log.d("SISISIISISISI", usuario.getDni());
                            setDatosUsuario();
                        }else
                            Log.d("NONONO", "no funciona");
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Login.this, "No se han podido obtener los datos de usuario", Toast.LENGTH_SHORT).show();
                });

    }

    private void setDatosUsuario(){
        Gson gson = new Gson();
        String userJson = gson.toJson(usuario);
        sharedPreferences = getSharedPreferences("usuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userJson", userJson);
        editor.apply();

        // Sign in success, update UI with the signed-in user's information
        Intent intent = new Intent(Login.this, MainActivity2.class);
        startActivity(intent);

    }
}