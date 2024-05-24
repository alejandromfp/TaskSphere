package com.example.tasksphere.adapter;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tasksphere.R;
import com.example.tasksphere.model.Comunicado;
import com.example.tasksphere.modelo.entidad.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComunicadosAdapter extends RecyclerView.Adapter<ComunicadosAdapter.ComunicadoViewHolder> {
    private List<Comunicado> comunicadosList;
    private User user;
    private Context context;
    private FirebaseFirestore db;

    public ComunicadosAdapter(Context context, List<Comunicado> comunicadosList, User user) {
        this.context = context;
        this.user = user;
        this.comunicadosList = comunicadosList;
        this.db = FirebaseFirestore.getInstance();
    }
    @NonNull
    @Override
    public ComunicadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comunicados, parent, false);
        return new ComunicadoViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ComunicadoViewHolder holder, int position) {
        Comunicado comunicado = comunicadosList.get(position);
        holder.title.setText(comunicado.getTitle());
        holder.description.setText(comunicado.getDescription());
        holder.fechaCreacion.setText(obtenerFechaEnString(comunicado.getDateCreation()));
        obtenerDatosUserComunicado(holder.profileImg, holder.username, comunicado.getUser());

        if(this.user.getUserId().equals(comunicado.getUser()) && !comunicado.getUser().isEmpty() || user.getRol().equals("Administrador")){
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        }else{
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }

        holder.deleteButton.setOnClickListener(v -> {
            borrarComunicado(comunicado);
        });
        holder.editButton.setOnClickListener(v -> {
            showEditDialog(comunicado);
        });
    }

    private void showEditDialog(Comunicado comunicado) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.edit_news);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextInputEditText editTitulo = dialog.findViewById(R.id.titleinput);
        TextInputEditText editDescripcion = dialog.findViewById(R.id.descripcioninput);
        Button editButton = dialog.findViewById(R.id.saveButton);

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
                .update("titulo", newTitulo, "descripcion", newDescripcion)
                .addOnSuccessListener(aVoid -> {
                    comunicado.setTitle(newTitulo);
                    comunicado.setDescription(newDescripcion);
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
                            comunicadosList.add(comunicado);
                        }
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "No se ha encontrado ningún comunicado con esa fecha", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class ComunicadoViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, fechaCreacion, username;

        ImageView profileImg;
        ImageButton editButton, deleteButton;

        public ComunicadoViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_comunicado);
            username = itemView.findViewById(R.id.username_comunicado);
            description = itemView.findViewById(R.id.descripcion_comunicado);
            profileImg = itemView.findViewById(R.id.profileImg);
            editButton = itemView.findViewById(R.id.editButton);
            fechaCreacion = itemView.findViewById(R.id.fechacreacion_comunicado);
            deleteButton = itemView.findViewById(R.id.deleteButton);


        }
    }

    private String obtenerFechaEnString(Date date){
        String fecha = null;
        SimpleDateFormat formatter = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        fecha = formatter.format(date);
        return fecha;
    }

    public void obtenerDatosUserComunicado(ImageView profileImg, TextView username, String userId){
        User usuario  = new User();
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    usuario.setUserId(doc.getId());
                    usuario.setNombre(doc.getString("nombre"));
                    usuario.setProfileImage(doc.getString("profile_img_path"));
                    Glide.with(context)
                            .load(usuario.getProfileImage())
                            .centerCrop()
                            .placeholder(R.drawable.defaultavatar)
                            .into(profileImg);

                    username.setText(usuario.getNombre());
                });

    }

}