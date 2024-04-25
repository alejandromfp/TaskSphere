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

public class Registry extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText NombreText, ApellidosText, DireccionText, CiudadText, TelefonoText, EmailText, ContraseñaText;

    Button botonRegistro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

        // Inicializar vistas
        mAuth = FirebaseAuth.getInstance();
        NombreText = findViewById(R.id.CajaNombre);
        ApellidosText = findViewById(R.id.CajaApellidos);
        DireccionText = findViewById(R.id.CajaDireccion);
        CiudadText = findViewById(R.id.CajaCiudad);
        TelefonoText = findViewById(R.id.CajaTelefono);
        EmailText = findViewById(R.id.CajaEmail);
        ContraseñaText = findViewById(R.id.CajaContraseña);
        botonRegistro = findViewById(R.id.botonRegistro);

        // Configurar el listener del botón Registrar
        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores de los campos
                String nombre = NombreText.getText().toString().trim();
                String apellidos = ApellidosText.getText().toString().trim();
                String direccion = DireccionText.getText().toString().trim();
                String ciudad = CiudadText.getText().toString().trim();
                String telefono = TelefonoText.getText().toString().trim();
                String email = EmailText.getText().toString().trim();
                String contraseña = ContraseñaText.getText().toString().trim();

                // Validar los campos
                if (nombre.isEmpty() || apellidos.isEmpty() || direccion.isEmpty() || ciudad.isEmpty() ||
                        telefono.isEmpty() || email.isEmpty() || contraseña.isEmpty()) {
                    Toast.makeText(Registry.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();

                } else if (!nombre.matches("[a-zA-Z\\s]+")) {
                    NombreText.setError("Nombre incorrecto");

                } else if (!apellidos.matches("[a-zA-Z\\s]+")) {
                    ApellidosText.setError("Apellidos incorrecto");

                } else if (!direccion.matches(".*\\d+.*") || !direccion.matches(".*[a-zA-Z]+.*")) {
                    DireccionText.setError("Direccion incorrecta");

                } else if (!telefono.matches("\\d{1,9}")) {
                    TelefonoText.setError("Teléfono no valido");

                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    EmailText.setError("Correo incorrecto");

                } else if (contraseña.length() < 6) {
                    ContraseñaText.setError("Minimo 6 caracteres");

                }else{
                    mAuth.createUserWithEmailAndPassword(email, contraseña)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(Registry.this, "Usuario Registrado", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Registry.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Toast.makeText(Registry.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                    Toast.makeText(Registry.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

                    // Redirigir al usuario a otra actividad después del registro (en este caso al inicio de sesión)
                    Intent intent = new Intent(Registry.this, Login.class);
                    startActivity(intent);

                    // Finalizar esta actividad
                    finish();
                }

            }

        });

    }
}