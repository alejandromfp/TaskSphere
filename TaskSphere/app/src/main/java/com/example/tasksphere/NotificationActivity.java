package com.example.tasksphere;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.tasksphere.adapter.NotificationsAdapter;
import com.example.tasksphere.adapter.TareasAdapter;
import com.example.tasksphere.adapter.UserCardAdapter;
import com.example.tasksphere.modelo.entidad.Notificacion;
import com.example.tasksphere.modelo.entidad.User;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    List<Notificacion> notificaciones = new ArrayList<>();
    RecyclerView recyclerView;

    Button backbutton;

    String userId;
    NotificationsAdapter adapter;

    FirebaseFirestore db;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getItems();
        obtenerListaDeNotificaciones();
        actualizarListaDeNotificaciones();

    }


    public void getItems(){
        db = FirebaseFirestore.getInstance();
        userId = getIntent().getStringExtra("userId");
        recyclerView = findViewById(R.id.recyclerContainer);
        adapter = new NotificationsAdapter(this, notificaciones, userId);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        backbutton = findViewById(R.id.backbutton);
        backbutton.setOnClickListener(v -> {
            onBackPressed();
        });
    }


    

    public void obtenerListaDeNotificaciones(){
        db.collection("users")
                .document(userId)
                .collection("notificaciones")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notificaciones.clear();
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        Notificacion notificacion = new Notificacion();
                        notificacion.setNotificationId(doc.getId());
                        notificacion.setTitle(doc.getString("titulo"));
                        notificacion.setBody(doc.getString("descripcion"));
                        notificacion.setFechaCreacion(doc.getTimestamp("fechaCreacion").toDate());
                        notificacion.setCategoria(doc.getString("categoria"));
                        notificaciones.add(notificacion);
                    }
                    Collections.sort(notificaciones, (t1, t2) -> t2.getFechaCreacion().compareTo(t1.getFechaCreacion()));
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,"No se han podido obtener las notificaciones",Toast.LENGTH_SHORT).show();
                });
    }




    public void actualizarListaDeNotificaciones(){
        db.collection("users")
                .document(userId)
                .collection("notificaciones")
                .addSnapshotListener((value, error) -> {
                    if(error != null)
                        return;
                    obtenerListaDeNotificaciones();
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}