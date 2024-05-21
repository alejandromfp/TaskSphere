package com.example.tasksphere.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tasksphere.NotificationActivity;
import com.example.tasksphere.ProfilePage;
import com.example.tasksphere.R;
import com.example.tasksphere.modelo.entidad.Notificacion;
import com.example.tasksphere.modelo.entidad.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {
    String userId;
    FirebaseFirestore db;
    private Context context;
    private List<Notificacion> notificacionsList;


    public NotificationsAdapter(Context context, List<Notificacion> notificacionsList, String userId) {
        this.context = context;
        this.notificacionsList = notificacionsList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
            return new NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
            Notificacion notificacion = notificacionsList.get(position);
            holder.bind(notificacion);
    }

    @Override
    public int getItemCount() {
            return notificacionsList.size();
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {
        private ImageView deleteButton;
        private TextView category, title , description;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteButton = itemView.findViewById(R.id.action_button);
            category = itemView.findViewById(R.id.category);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            db = FirebaseFirestore.getInstance();

        }

        public void bind(Notificacion notificacion) {

            category.setText(notificacion.getCategoria());
            title.setText(notificacion.getTitle());
            description.setText(notificacion.getBody());


            deleteButton.setOnClickListener(v -> {
                db.collection("users")
                        .document(userId)
                        .collection("notificaciones")
                        .document(notificacion.getNotificationId())
                        .delete();
            });
        }
    }
}

