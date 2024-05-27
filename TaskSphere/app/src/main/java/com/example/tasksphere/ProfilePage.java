package com.example.tasksphere;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.tasksphere.modelo.entidad.Rol;
import com.example.tasksphere.modelo.entidad.User;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfilePage extends AppCompatActivity {


    FirebaseStorage storage;

    SharedPreferences sharedPreferences;

    TextView name, apellidos, dni, fecha_nac, direccion, localidad, biografia, username, userEmail, rolTag;

    Button editUser, editRol;

    Dialog dialog;


    Spinner spinner;
    ImageView profileImageView;
    private User usuario, loginUser;
    FirebaseFirestore db;
    FirebaseUser user;

    private FirebaseAuth mAuth;

    CardView profileCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        obtenerDatosDeUsuario();
        getItems();
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        obtenerDatosFromDatabase(userId);

        actualizarDatos(userId);
    }

    private void getItems(){
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        profileCard = findViewById(R.id.profile_card);
        profileImageView =findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        name = findViewById(R.id.name_tag);
        apellidos = findViewById(R.id.apellidos);
        dni = findViewById(R.id.dni);
        fecha_nac = findViewById(R.id.fecha_nacimiento);
        direccion = findViewById(R.id.direccion);
        localidad = findViewById(R.id.localidad);
        biografia =findViewById(R.id.biografia);
        userEmail = findViewById(R.id.useremail);
        rolTag = findViewById(R.id.rol_tag);
        editRol = findViewById(R.id.edit_laborales);
        spinner = findViewById(R.id.rol);


        editUser = findViewById(R.id.edit);
        editUser.setOnClickListener(v -> {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.edit_profile);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogSetDatos();

        });


    }


    private void setDatosDeUsuario(){
        username.setText(usuario.getNombre());
        userEmail.setText(usuario.getEmail());
        name.setText(usuario.getNombre());
        apellidos.setText(usuario.getApellidos());
        dni.setText(usuario.getDni());
        direccion.setText(usuario.getDireccion());
        localidad.setText(usuario.getLocalidad());
        fecha_nac.setText(usuario.getFechaNac());
        biografia.setText(usuario.getBiografia());
        rolTag.setText(usuario.getRol());

        if (!isFinishing() && !isDestroyed()) {
            Glide.with(this)
                    .load(usuario.getProfileImage())
                    .placeholder(R.drawable.defaultavatar)
                    .into(profileImageView);
        }

        if(loginUser.getRol().equals("Administrador")){

            spinner.setVisibility(View.VISIBLE);
            rolTag.setVisibility(View.GONE);
            setRolSpinner(spinner);
        }else{
            editRol.setVisibility(View.GONE);
            editUser.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            rolTag.setVisibility(View.VISIBLE);
        }

    }
    private void actualizarDatos(String userId){
        db.collection("users")
                .document(userId)
                .addSnapshotListener((value, error) -> {
                    //actualizo la imagen
                    if(error != null){
                        return;
                    }
                    obtenerDatosFromDatabase(userId);
                });
    }


    private void obtenerDatosDeUsuario(){

        sharedPreferences = getSharedPreferences("usuario", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("userJson", null);
        if(userJson != null){
            Gson gson = new Gson();
             loginUser = gson.fromJson(userJson, User.class);
        }


    }

    public void obtenerDatosFromDatabase(String userId){
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    usuario = new User();
                    usuario.setUserId(doc.getId());
                    usuario.setNombre(doc.getString("nombre"));
                    usuario.setApellidos(doc.getString("apellidos"));
                    usuario.setDireccion(doc.getString("direccion"));
                    usuario.setDni(doc.getString("dni"));
                    usuario.setLocalidad(doc.getString("ciudad"));
                    usuario.setEmail(doc.getString("email"));
                    usuario.setRol(doc.getString("rol"));
                    usuario.setRolId(doc.getString("rolId"));
                    usuario.setFechaNac(doc.getString("fechaNacimiento"));
                    usuario.setTelefono(doc.getString("telefono"));
                    usuario.setBiografia(doc.getString("biografia"));
                    usuario.setProfileImage(doc.getString("profile_img_path"));
                    usuario.setUserToken(doc.getString("token"));
                    setDatosDeUsuario();
                });
    }

    public void dialogSetDatos(){
        EditText name, apellidos, fechaNac, direccion, localidad, biografia;
        Button saveChanges;
        name = dialog.findViewById(R.id.name);
        apellidos = dialog.findViewById(R.id.apellidos);
        fechaNac = dialog.findViewById(R.id.fecha_nacimiento);
        direccion = dialog.findViewById(R.id.direccion);
        localidad = dialog.findViewById(R.id.localidad);
        biografia = dialog.findViewById(R.id.biografia);

        name.setText(usuario.getNombre());
        apellidos.setText(usuario.getApellidos());
        fechaNac.setText(usuario.getFechaNac());
        direccion.setText(usuario.getDireccion());
        localidad.setText(usuario.getLocalidad());
        biografia.setText(usuario.getBiografia());
        saveChanges = dialog.findViewById(R.id.saveChanges);
        saveChanges.setOnClickListener(v -> {
            if(validarCampos(name,apellidos,fechaNac,direccion,localidad,biografia))
                guardarCambios(name, apellidos, fechaNac, direccion,localidad,biografia);
        });

        dialog.show();
    }

    public void guardarCambios(
            EditText name, EditText apellidos,
            EditText fechaNac, EditText direccion,
            EditText localidad, EditText biografia){
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", name.getText().toString().trim());
        userData.put("apellidos", apellidos.getText().toString().trim());
        userData.put("direccion", direccion.getText().toString().trim());
        userData.put("ciudad", localidad.getText().toString().trim());
        userData.put("fechaNacimiento", fechaNac.getText().toString().trim());
        userData.put("biografia", biografia.getText().toString().trim());



        db.collection("users")
                .document(usuario.getUserId())
                .update(userData)
                .addOnSuccessListener(command -> {
                    Toast.makeText(this, "Datos actualizados correctamente",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "No se han podido actualizar los datos",Toast.LENGTH_SHORT).show();
                });

    }

    public boolean validarCampos(
            EditText name, EditText apellidos,
            EditText fechaNac, EditText direccion,
            EditText localidad, EditText biografia) {
        boolean result = false;
        if (name.getText().toString().isEmpty()) {
            name.setError("Campo obligatorio");
        } else if (apellidos.getText().toString().isEmpty()) {
            apellidos.setError("Campo obligatorio");
        } else if (direccion.getText().toString().isEmpty()) {
            direccion.setError("Campo obligatorio");
        } else if (localidad.getText().toString().isEmpty()) {
            localidad.setError("Campo obligatorio");
        } else if (fechaNac.toString().isEmpty() || !fechaNac.getText().toString().matches("\\d{2}/\\d{2}/\\d{4}")) {
            fechaNac.setError("La fecha de nacimiento debe estar en formato DD/MM/AAAA");
        } else
            result = true;

        return result;
    }

    public void setRolSpinner(Spinner spinner){
            List<Rol> roles = new ArrayList<>();
            Rol opcionPorDefecto = new Rol();
            opcionPorDefecto.setRolName("Sin seleccionar");
            roles.add(opcionPorDefecto);

            db.collection("roles")
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc: task.getResult()){
                                Rol rol = new Rol();
                                rol.setRolId(doc.getId());
                                rol.setRolName(doc.getString("rolName"));
                                roles.add(rol);
                            }

                            ArrayAdapter<Rol> adapter = new ArrayAdapter<Rol>(this, android.R.layout.simple_spinner_item, roles) {
                                @NonNull
                                @Override
                                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    TextView textView = (TextView) super.getView(position, convertView, parent);
                                    if(usuario.getRolId() != null)
                                        textView.setText(usuario.getRol());
                                    else
                                        textView.setText(roles.get(position).getRolName());
                                    return textView;
                                }

                                @Override
                                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                                    textView.setText(roles.get(position).getRolName());
                                    return textView;
                                }
                            };
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                            spinner.setSelection(0);

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Rol rolSeleccionado = (Rol) parent.getItemAtPosition(position);
                                    if (rolSeleccionado.getRolId() != null) {
                                        Map<String, Object> userRol = new HashMap<>();
                                        userRol.put("rol", rolSeleccionado.getRolName());
                                        userRol.put("rolId", rolSeleccionado.getRolId());
                                        db.collection("users")
                                                .document(usuario.getUserId())
                                                .update(userRol)
                                                .addOnSuccessListener(command -> {
                                                    usuario.setRolId(rolSeleccionado.getRolId());
                                                    usuario.setRol(rolSeleccionado.getRolName());

                                                    //NOTIFICAMOS AL USUARIO
                                                    enviarNotificacion(
                                                            usuario.getUserToken(),
                                                            "Nuevo Rol",
                                                            "Un administrador te ha asignado el Rol de "+ usuario.getRol(),
                                                            usuario.getUserId()
                                                    );
                                                    Log.d("ROLCHANGE", "Se ha cambiado el rol");
                                                });

                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    // No se ha seleccionado ningún usuario
                                }

                            });


                        }
                    }
            );
    }

    private void enviarNotificacion(String token, String title, String body, String userId) {
        final String API_KEY = "AAAAOwyZe_A:APA91bH2sWXmIU6uQJGwQ51bsu53CrZ1D7h7znkxf0jKFgYWBqzmwu5a0PoQmKcp9UxmWEjvSFBpVaf11hMp-y6auZvv3DB5Jb2tBkWQ7EgoUWok2bPUjV1A7tFtBnjkPXUq1HFPo8i-";

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
                        guardarNotificacionEnFirebase(title, body, userId);
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

    public void guardarNotificacionEnFirebase(String title, String body, String administratorId){

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