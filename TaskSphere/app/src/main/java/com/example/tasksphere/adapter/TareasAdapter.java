package com.example.tasksphere.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tasksphere.R;
import com.example.tasksphere.TaskDetaills;
import com.example.tasksphere.modelo.entidad.Task;
import com.example.tasksphere.modelo.entidad.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TareasAdapter extends RecyclerView.Adapter<TareasAdapter.TareaViewHolder> {
    private List<Task> tasksList;
    private Context context;

    private FirebaseFirestore db;



    private User usuario;

    public TareasAdapter(Context context, List<Task> tasksList, User usuario) {
        this.context = context;
        this.tasksList = tasksList;
        this.usuario = usuario;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tasks, parent, false);
        return new TareaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Task tarea = tasksList.get(position);
        holder.nombreTextView.setText(tarea.getTaskName());

        if((usuario.getRol().equals("Administrador") || usuario.getRol().equals("Gerente")) && tarea.getAsignadaA() !=null){
            holder.cardView.setVisibility(View.VISIBLE);
            db.collection("users")
                    .document(tarea.getAsignadaA())
                            .get()
                            .addOnSuccessListener(doc -> {
                                Glide.with(context)
                                        .load(doc.getString("profile_img_path"))
                                        .placeholder(R.drawable.defaultavatar)
                                        .into(holder.profileImg);
                            });

        }else{
            holder.cardView.setVisibility(View.GONE);
        }

        if(tarea.getAsignadaA() == null)
            holder.category.setText("Tarea sin asignar");
        else if (tarea.getAsignadaA() != null && tarea.getFechaInicio() == null) {
            holder.category.setText("Tarea pendiente");
        }else if (tarea.getAsignadaA() != null && tarea.getFechaInicio() != null && tarea.getFechaFinal() == null) {
            holder.category.setText("Tarea pendiente");
        }else if (tarea.getAsignadaA() != null && tarea.getFechaInicio() != null && tarea.getFechaFinal() != null) {
            holder.category.setText("Tarea finalizada");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaskDetaills.class);
                intent.putExtra("task", tarea);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        TextView category;
        private CardView cardView;
        private ImageView profileImg;
        private Task tarea;
        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.fichajetitle);
            category = itemView.findViewById(R.id.category);
            cardView = itemView.findViewById(R.id.cardView);
            profileImg = itemView.findViewById(R.id.profileImg);
        }
    }




}
