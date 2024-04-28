package com.example.tasksphere;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.Query;


import com.example.tasksphere.adapter.SolicitudAdapter;
import com.example.tasksphere.model.Solicitud;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class ApprovalsActivity extends AppCompatActivity {

    RecyclerView mRecycler;
    SolicitudAdapter mAdapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_approvals);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.listadoApprovalsPendientes);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        Query query = db.collection("solicitudes");
        FirestoreRecyclerOptions<Solicitud> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Solicitud>().setQuery(query, Solicitud.class).build();

        mAdapter = new SolicitudAdapter(firestoreRecyclerOptions, this);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}