package com.example.tasksphere.adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasksphere.R;
import com.example.tasksphere.modelo.entidad.Message;
import com.example.tasksphere.modelo.entidad.Notificacion;
import com.example.tasksphere.modelo.entidad.User;
import com.google.api.Distribution;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.MensajesViewHolder> {
    String userId;
    FirebaseFirestore db;


    private Context context;
    private List<Message> mensajesList;


    public MensajesAdapter(Context context, List<Message> mensajesList, String userId) {
        this.context = context;
        this.mensajesList = mensajesList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public MensajesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);

        return new MensajesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajesViewHolder holder, int position) {
        Message message = mensajesList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return mensajesList.size();
    }

    public class MensajesViewHolder extends RecyclerView.ViewHolder {
        private TextView contenidoMensaje , contenidoMensajeSent, fecha, fechaSent, userSent;
        LinearLayout noSent, sent;


        public MensajesViewHolder(@NonNull View itemView) {
            super(itemView);
            contenidoMensaje = itemView.findViewById(R.id.message_text);
            contenidoMensajeSent = itemView.findViewById(R.id.message_text_sent);
            fecha = itemView.findViewById(R.id.hora_mensaje);
            fechaSent = itemView.findViewById(R.id.hora_mensaje_sent);
            userSent = itemView.findViewById(R.id.nombre_sent);
            noSent = itemView.findViewById(R.id.item_message);
            sent = itemView.findViewById(R.id.item_message_sent);
            db = FirebaseFirestore.getInstance();

        }

        public void bind(Message message) {
            if(message.getUserId().equals(userId)){
                noSent.setVisibility(View.VISIBLE);
                sent.setVisibility(View.GONE);
                fecha.setText(transformDateToHours(message.getTimestamp()));
                contenidoMensaje.setText(message.getContent());
            }else {
                noSent.setVisibility(View.GONE);
                sent.setVisibility(View.VISIBLE);
                fechaSent.setText(transformDateToHours(message.getTimestamp()));
                contenidoMensajeSent.setText(message.getContent());
                userSent.setText(message.getUsername());

            }


        }

        private String transformDateToHours(String dateTimeString){

            String result = null;

            if(dateTimeString != null){
                DateTimeFormatter formatter;
                LocalDateTime localDateTime;
                // Formateador para analizar el String en LocalDateTime
                try{
                     formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    localDateTime= LocalDateTime.parse(dateTimeString, formatter);
                } catch (Exception e){

                     try{
                         formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S");
                         localDateTime = LocalDateTime.parse(dateTimeString, formatter);

                     } catch (Exception exception){
                         formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS");
                         localDateTime = LocalDateTime.parse(dateTimeString, formatter);
                         
                     }
                }


                // Convertir el String en LocalDateTime



                int horas = localDateTime.getHour();
                int minutos = localDateTime.getMinute();

                String horasStr = (horas < 10) ? "0" + horas : String.valueOf(horas);
                String minutosStr = (minutos < 10) ? "0" + minutos : String.valueOf(minutos);

                result = horasStr + ":" + minutosStr;
            }else
                result = "00:00";

            return  result;
        }
    }


}


