package com.example.tasksphere.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasksphere.R;
import com.example.tasksphere.model.Solicitud;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SolicitudAdapter extends FirestoreRecyclerAdapter<Solicitud, SolicitudAdapter.ViewHolder>{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Activity activity;

    public SolicitudAdapter(@NonNull FirestoreRecyclerOptions<Solicitud> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Solicitud Solicitud) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        viewHolder.usuario.setText(Solicitud.getUsuario());
        viewHolder.fecha.setText(Solicitud.getFecha());

        viewHolder.denegar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteSolicitud(id);
            }
        });
    }

    private void deleteSolicitud(String id){
        db.collection("solicitudes").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Solicitud denegada correctamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al denegar la solicitud", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_solicitud_single, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView usuario, fecha;
        Button denegar, aprobar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            usuario = itemView.findViewById(R.id.user);
            fecha = itemView.findViewById(R.id.date);
            denegar = itemView.findViewById(R.id.denegar);
            aprobar = itemView.findViewById(R.id.aceptar);
        }
    }
}
