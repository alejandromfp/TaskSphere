package com.example.tasksphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    final String API_KEY = "AAAAOwyZe_A:APA91bH2sWXmIU6uQJGwQ51bsu53CrZ1D7h7znkxf0jKFgYWBqzmwu5a0PoQmKcp9UxmWEjvSFBpVaf11hMp-y6auZvv3DB5Jb2tBkWQ7EgoUWok2bPUjV1A7tFtBnjkPXUq1HFPo8i-";
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
            // Crear usuario en Firebase
            String email = emailText.getText().toString().trim();
            String password = passText.getText().toString().trim();
            String nombre = nombreText.getText().toString().trim();
            String apellidos = apellidosText.getText().toString().trim();
            String direccion = direccionText.getText().toString().trim();
            String ciudad = ciudadText.getText().toString().trim();
            String telefono = telefonoText.getText().toString().trim();
            String fechaNacimiento = fechaNacimientoText.getText().toString().trim();
            String dni = dniText.getText().toString().trim();

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
                                    userData.put("email", email);
                                    userData.put("apellidos", apellidos);
                                    userData.put("direccion", direccion);
                                    userData.put("ciudad", ciudad);
                                    userData.put("telefono", telefono);
                                    userData.put("fechaNacimiento", fechaNacimiento);
                                    userData.put("dni", dni);
                                    userData.put("rol", "Sin asignar");
                                    userData.put("vacaciones", 30);
                                    obtenerTokensAdministradores(nombre);


                                    // Guardar estos datos en Firestore
                                    db.collection("users").document(user.getUid()).set(userData)
                                            .addOnSuccessListener(aVoid -> {

                                                Toast.makeText(MainActivity.this, "Usuario registrado con datos adicionales", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(MainActivity.this, Login.class);
                                                startActivity(intent);
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("Firestore", "Error al guardar datos adicionales", e);
                                                Toast.makeText(MainActivity.this, "Error al guardar datos adicionales", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void enviarNotificacion(String token, String title, String body, String administratorId) {
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", title);
            notificationBody.put("body", body);
            Log.d("TAG2", token);
            notification.put("to", token);
            notification.put("notification", notificationBody);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send",
                    notification,
                    response -> {
                        guardarNotificacionEnFirebase(title, body, administratorId);
                    },
                    error -> {
                        Log.e("TAG", "Error al enviar notificación: " + error.getMessage());
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + API_KEY);
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtenerTokensAdministradores(String username) {
        Log.d("TAG", "Error al obtener tokens de administradores: ");
        db.collection("users")
                .whereEqualTo("rol", "Administrador")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String token = document.getString("token");
                            String administratorId = document.getId();
                            if (token != null && !token.isEmpty()) {
                                enviarNotificacion(token,
                                        "Nuevo usuario registrado",
                                        "¡El usuario " + username + " se ha registrado en la aplicación, asignale un Rol cuanto antes!",
                                        administratorId);
                            }
                        }
                    } else {
                        Log.e("TAG", "Error al obtener tokens de administradores: ", task.getException());
                    }
                });
    }

    public void guardarNotificacionEnFirebase(String title, String body, String administratorId) {
        Map<String, Object> notificacion = new HashMap<>();
        notificacion.put("titulo", title);
        notificacion.put("descripcion", body);
        notificacion.put("fechaCreacion", Timestamp.now());
        notificacion.put("categoria", "Equipo");

        db.collection("users")
                .document(administratorId)
                .collection("notificaciones")
                .add(notificacion)
                .addOnSuccessListener(command -> {
                    Log.d("Firestore", "Se ha guardado con exito");
                })
                .addOnFailureListener(command -> {
                    Log.d("Firestore", "No se ha guardado con exito");
                });
    }
}
