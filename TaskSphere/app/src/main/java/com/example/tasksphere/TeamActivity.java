package com.example.tasksphere;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.Toast;

import com.example.tasksphere.adapter.UserCardAdapter;
import com.example.tasksphere.modelo.entidad.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends AppCompatActivity {

    List<User> equipo = new ArrayList<>();
    RecyclerView recyclerView;
    UserCardAdapter adapter;

    Button backButton;

    FirebaseFirestore db;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        getItems();
        obtenerListaDeUsuarios();

    }


    public void getItems(){
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerContainer);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        backButton = findViewById(R.id.backbutton);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    public void obtenerListaDeUsuarios(){
            db.collection("users")
                    .orderBy("nombre")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        equipo.clear();
                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                            User usuario = new User();
                            usuario.setUserId(doc.getId());
                            usuario.setNombre(doc.getString("nombre"));
                            usuario.setApellidos(doc.getString("apellidos"));
                            usuario.setDireccion(doc.getString("direccion"));
                            usuario.setDni(doc.getString("dni"));
                            usuario.setLocalidad(doc.getString("ciudad"));
                            usuario.setEmail(doc.getString("email"));
                            usuario.setRol(doc.getString("rol"));
                            usuario.setFechaNac(doc.getString("fechaNacimiento"));
                            usuario.setTelefono(doc.getString("telefono"));
                            usuario.setBiografia(doc.getString("biografia"));
                            usuario.setProfileImage(doc.getString("profile_img_path"));
                            equipo.add(usuario);
                        }
                        adapter = new UserCardAdapter(this, equipo);
                        recyclerView.setAdapter(adapter);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this,"No se han podido obtener los usuarios",Toast.LENGTH_SHORT).show();
                    });
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerListaDeUsuarios();
    }
}