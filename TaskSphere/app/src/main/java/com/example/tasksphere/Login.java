package com.example.tasksphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tasksphere.modelo.entidad.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailText, passText;
    private CheckBox checkBoxShowPassword;
    String token;
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

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            obtainUserFromDatabase();
        }

        emailText = findViewById(R.id.cajaCorreo);
        passText = findViewById(R.id.cajaPass);
        passText.setTransformationMethod(PasswordTransformationMethod.getInstance()); // Ocultar la contraseña por defecto

        botonLogin = findViewById(R.id.botonLogin);
        botonLogin.setOnClickListener(v -> {
            // LOGIN EN FIREBASE
            String email = emailText.getText().toString();
            String password = passText.getText().toString();

            if (email.isEmpty()) {
                emailText.setError("Campo obligatorio");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailText.setError("Email incorrecto");
            } else if (password.length() < 6) {
                passText.setError("La contraseña debe tener al menos 6 caracteres");
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                obtainUserFromDatabase();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        botonRegistro = findViewById(R.id.crearCuenta);
        botonRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });

        checkBoxShowPassword = findViewById(R.id.checkBoxLogin);
        checkBoxShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Mostrar la contraseña
                passText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // Ocultar la contraseña
                passText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    private void obtainUserFromDatabase() {
        usuario = new User();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                usuario.setUserId(doc.getId());
                                usuario.setNombre(doc.getString("nombre"));
                                usuario.setApellidos(doc.getString("apellidos"));
                                usuario.setDireccion(doc.getString("direccion"));
                                usuario.setDni(doc.getString("dni"));
                                usuario.setLocalidad(doc.getString("ciudad"));
                                usuario.setEmail(currentUser.getEmail());
                                usuario.setRol(doc.getString("rol"));
                                usuario.setFechaNac(doc.getString("fechaNacimiento"));
                                usuario.setTelefono(doc.getString("telefono"));
                                usuario.setBiografia(doc.getString("biografia"));
                                usuario.setProfileImage(doc.getString("profile_img_path"));
                                usuario.setUserToken(doc.getString("token"));

                                // Manejo del campo vacaciones
                                Long vacaciones = doc.getLong("vacaciones");
                                if (vacaciones != null) {
                                    usuario.setVacaciones(vacaciones.intValue());
                                } else {
                                    usuario.setVacaciones(30); // Valor predeterminado
                                }

                                setDatosUsuario();
                            } else {
                                Log.d("Firestore", "No existe el documento para el usuario");
                            }
                        } else {
                            Log.e("Firestore", "Error obteniendo el documento", task.getException());
                            Toast.makeText(Login.this, "No se han podido obtener los datos de usuario", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error en la operación Firestore", e);
                        Toast.makeText(Login.this, "No se han podido obtener los datos de usuario", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("Auth", "No hay un usuario autenticado");
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDatosUsuario() {
        Gson gson = new Gson();
        String userJson = gson.toJson(usuario);
        sharedPreferences = getSharedPreferences("usuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userJson", userJson);
        editor.apply();

        // Sign in success, update UI with the signed-in user's information
        Intent intent = new Intent(Login.this, MainActivity2.class);
        startActivity(intent);
        finish();
    }
}
