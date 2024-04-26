package com.example.tasksphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText emailText, passText, nombreText, apellidosText, direccionText, ciudadText, telefonoText, fechaNacimientoText, dniText;
    TextView botonRegistro;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        emailText = findViewById(R.id.cajaEmail);
        passText = findViewById(R.id.cajaPassword);
        nombreText = findViewById(R.id.cajaNombre);
        apellidosText = findViewById(R.id.cajaApellidos);
        direccionText = findViewById(R.id.cajaDireccion);
        ciudadText = findViewById(R.id.cajaCiudad);
        telefonoText = findViewById(R.id.cajaTelefono);
        fechaNacimientoText = findViewById(R.id.cajaFechaNacimiento);
        dniText = findViewById(R.id.cajaDni);


        botonRegistro = findViewById(R.id.botonCrearCuenta);
        botonRegistro.setOnClickListener(v -> {
            //CREAR USUARIO EN FIREBASE

            String email = emailText.getText().toString();
            String password = passText.getText().toString();
            String nombre = nombreText.getText().toString();
            String apellidos = apellidosText.getText().toString();
            String direccion = direccionText.getText().toString();
            String ciudad = ciudadText.getText().toString();
            String telefono = telefonoText.getText().toString();
            String fechaNacimiento = fechaNacimientoText.getText().toString();
            String dni = dniText.getText().toString();

            // Validación de campos vacíos y formatos correctos
            if (nombre.isEmpty()) {
                nombreText.setError("Campo obligatorio");
            } else if (apellidos.isEmpty()) {
                apellidosText.setError("Campo obligatorio");
            } else if (direccion.isEmpty()) {
                direccionText.setError("Campo obligatorio");
            } else if (ciudad.isEmpty()) {
                ciudadText.setError("Campo obligatorio");
            } else if (telefono.isEmpty()) {
                telefonoText.setError("Campo obligatorio");
            } else if (!telefono.matches("\\d{9}")) {
                telefonoText.setError("El teléfono debe tener 9 dígitos");
            } else if (email.isEmpty()) {
                emailText.setError("Campo obligatorio");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailText.setError("Email incorrecto");
            } else if (password.isEmpty()) {
                passText.setError("Campo obligatorio");
            } else if (password.length() < 6) {
                passText.setError("La contraseña debe tener al menos 6 caracteres");
            } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$")) {
                passText.setError("La contraseña debe incluir números, letras mayúsculas y minúsculas, y caracteres especiales");
            } else if (fechaNacimiento.isEmpty() || !fechaNacimiento.matches("\\d{2}/\\d{2}/\\d{4}")) {
                fechaNacimientoText.setError("La fecha de nacimiento debe estar en formato DD/MM/AAAA");
            } else if (dni.isEmpty() || !dni.matches("\\d{8}[A-Z]")) {
                dniText.setError("El DNI debe tener 8 dígitos y una letra al final");
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Crear un mapa para almacenar los datos adicionales del usuario
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("nombre", nombre);
                                    userData.put("apellidos", apellidos);
                                    userData.put("direccion", direccion);
                                    userData.put("ciudad", ciudad);
                                    userData.put("telefono", telefono);
                                    userData.put("fechaNacimiento", fechaNacimiento);
                                    userData.put("dni", dni);

                                    // Guardar estos datos en Firestore
                                    db.collection("users").document(user.getUid()).set(userData)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(MainActivity.this, "Usuario registrado con datos adicionales", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(MainActivity.this, Login.class);
                                                startActivity(intent);
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error al guardar datos adicionales", Toast.LENGTH_SHORT).show());
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}