package com.example.tasksphere.adapter;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tasksphere.R;
import com.example.tasksphere.model.Comunicado;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ComunicadosAdapter extends RecyclerView.Adapter<ComunicadosAdapter.ComunicadoViewHolder> {
    private List<Comunicado> comunicadosList;
    private Context context;
    private FirebaseFirestore db;

    public ComunicadosAdapter(Context context) {
        this.context = context;
        this.comunicadosList = new ArrayList<>();
        this.db = FirebaseFirestore.getInstance();
    }
    @NonNull
    @Override
    public ComunicadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new ComunicadoViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ComunicadoViewHolder holder, int position) {
        Comunicado comunicado = comunicadosList.get(position);
        holder.title.setText(comunicado.getTitle());
        holder.description.setText(comunicado.getDescription());

        holder.itemView.setOnClickListener(v -> {
            showEditDialog(comunicado);
        });

        holder.itemView.setOnLongClickListener(v -> {
            borrarComunicado(comunicado);
            return true;
        });
    }

    private void showEditDialog(Comunicado comunicado) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.item_news);

        EditText editTitulo = dialog.findViewById(R.id.textViewTitle);
        EditText editDescripcion = dialog.findViewById(R.id.textViewDescription);
        Button editButton = dialog.findViewById(R.id.editButton);

        editTitulo.setText(comunicado.getTitle());
        editDescripcion.setText(comunicado.getDescription());

        editButton.setOnClickListener(v -> {
            String newTitulo = editTitulo.getText().toString().trim();
            String newDescripcion = editDescripcion.getText().toString().trim();

            if (!newTitulo.isEmpty() && !newDescripcion.isEmpty()) {
                actualizarComunicado(comunicado, newTitulo, newDescripcion);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void actualizarComunicado(Comunicado comunicado, String newTitulo, String newDescripcion) {
        db.collection("Comunicados").document(comunicado.getId())
                .update("title", newTitulo, "description", newDescripcion)
                .addOnSuccessListener(aVoid -> {
                    comunicado.setTitle(newTitulo);
                    comunicado.setDescription(newDescripcion);
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al actualizar el comunicado", Toast.LENGTH_SHORT).show();
                });
    }

    private void borrarComunicado(Comunicado comunicado) {
        db.collection("Comunicados").document(comunicado.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    comunicadosList.remove(comunicado);
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar el comunicado", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return comunicadosList.size();
    }

    public void cargarComunicadoPorUsuario (String userId) {
        db.collection("Comunicados")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        comunicadosList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comunicado comunicado = document.toObject(Comunicado.class);
                            comunicado.setUser(document.getId());
                            comunicadosList.add(comunicado);
                        }
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Error al cargar el comunicado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void cargarComunicadoPorFecha(long timestamp) {
        db.collection("Comunicados")
                .whereGreaterThanOrEqualTo("timestamp", timestamp)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        comunicadosList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comunicado comunicado = document.toObject(Comunicado.class);
                            comunicado.setDateCreation(document.getId());
                            comunicadosList.add(comunicado);
                        }
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "No se ha encontrado ningún comunicado con esa fecha", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class ComunicadoViewHolder extends RecyclerView.ViewHolder {

        TextView title, description;
        ImageButton editButton, deleteButton;

        public ComunicadoViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewTitle);
            description = itemView.findViewById(R.id.textViewDescription);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}