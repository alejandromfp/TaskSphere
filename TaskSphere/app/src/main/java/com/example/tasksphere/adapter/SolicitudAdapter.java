package com.example.tasksphere.adapter;

import android.app.Activity;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

public class SolicitudAdapter extends FirestoreRecyclerAdapter<Solicitud, SolicitudAdapter.ViewHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Activity activity;

    public SolicitudAdapter(@NonNull FirestoreRecyclerOptions<Solicitud> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull Solicitud solicitud) {
        // Obtener la posición de vinculación
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getBindingAdapterPosition());
        final String solicitudId = documentSnapshot.getId();

        // Referencia a Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtener el ID de usuario de la solicitud
        String userId = solicitud.getUsuario();

        // Consultar la colección 'users' para obtener el nombre y apellidos del usuario por ID
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userDocument = task.getResult();
                    if (userDocument.exists()) {
                        // Establecer el nombre completo en el TextView de usuario
                        String nombre = userDocument.getString("nombre");
                        String apellidos = userDocument.getString("apellidos");  // Obtener apellidos
                        String nombreCompleto = nombre + " " + apellidos;  // Concatenar nombre y apellidos
                        viewHolder.usuario.setText(nombreCompleto);
                    } else {
                        viewHolder.usuario.setText("Nombre no encontrado");
                    }
                } else {
                    viewHolder.usuario.setText("Error al obtener los datos");
                    Log.e("Firebase", "Error al obtener el documento", task.getException());
                }
            }
        });

        viewHolder.fecha.setText(solicitud.getFecha());

        viewHolder.denegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSolicitud(solicitudId);
            }
        });

        viewHolder.aprobar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aprobarSolicitud(solicitudId, userId);
            }
        });
    }

    private void deleteSolicitud(String id) {
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

    private void aprobarSolicitud(String solicitudId, String userId) {
        final DocumentReference userRef = db.collection("users").document(userId);
        final DocumentReference solicitudRef = db.collection("solicitudes").document(solicitudId);

        db.runTransaction(transaction -> {
            DocumentSnapshot userSnapshot = transaction.get(userRef);
            DocumentSnapshot solicitudSnapshot = transaction.get(solicitudRef);

            // Obtener las vacaciones actuales y restar 1
            long vacaciones = userSnapshot.getLong("vacaciones");
            transaction.update(userRef, "vacaciones", vacaciones - 1);

            // Cambiar el estado de la solicitud a "aprobada"
            transaction.update(solicitudRef, "estado", "aprobada");

            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(activity, "Solicitud aprobada correctamente", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(activity, "Error al aprobar la solicitud", Toast.LENGTH_SHORT).show();
            Log.e("Firebase", "Error al aprobar la solicitud", e);
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
