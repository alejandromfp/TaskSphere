package com.example.tasksphere.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tasksphere.R;
import com.example.tasksphere.TaskDetaills;
import com.example.tasksphere.modelo.entidad.Fichaje;
import com.example.tasksphere.modelo.entidad.Task;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FichajesAdapter extends RecyclerView.Adapter<FichajesAdapter.FichajeViewHolder> {
    private List<Fichaje> fichajes;
    private Context context;

    public FichajesAdapter(Context context, List<Fichaje> fichajes) {
        this.context = context;
        this.fichajes = fichajes;
    }

    @NonNull
    @Override
    public FichajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fichaje, parent, false);
        return new FichajeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FichajeViewHolder holder, int position) {
        Fichaje fichaje = fichajes.get(position);

        // Formatear la hora en formato HH:mm
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String fechaEmpiezo = sdf.format(fichaje.getFechaEmpiezo());
        holder.horaEmpiezo.setText(fechaEmpiezo);

        if(fichaje.getFechaFin() != null){
            String fechaFin = sdf.format(fichaje.getFechaFin());

            //CALCULAR HORAS TOTALES
            long differenceInMillis = fichaje.getFechaFin().getTime() - fichaje.getFechaEmpiezo().getTime();
            // Convertir la diferencia de milisegundos a minutos
            long differenceInMinutes = differenceInMillis / (60 * 1000);
            // Calculamos las horas y minutos
            long hours = differenceInMinutes / 60;
            long minutes = differenceInMinutes % 60;
            String horasTotales = String.format("%02d:%02d", hours, minutes);

            holder.horaSalida.setText(fechaFin);
            holder.horasTotales.setText(horasTotales);
        }else{
            holder.horaSalida.setText("00:00");
            holder.horasTotales.setText("En curso...");
        }




    }

    @Override
    public int getItemCount() {
        return fichajes.size();
    }

    public class FichajeViewHolder extends RecyclerView.ViewHolder {
        TextView horaEmpiezo, horaSalida , horasTotales;
        private Fichaje fichaje;
        public FichajeViewHolder(@NonNull View itemView) {
            super(itemView);
            horaEmpiezo = itemView.findViewById(R.id.hora_entrada);
            horaSalida = itemView.findViewById(R.id.hora_salida);
            horasTotales = itemView.findViewById(R.id.total_horas);
        }
    }




}
