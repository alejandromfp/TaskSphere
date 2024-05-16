package com.example.tasksphere;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tasksphere.modelo.entidad.Task;
import com.example.tasksphere.modelo.entidad.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TaskDetaills extends AppCompatActivity {

    TextView taskName, taskDescription, taskFechaInicio, taskFechafinal, userName, contador;
    ImageView profileUserImg;
    CardView imgCardView;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    Timer timer;

    Date fechaInicio, fechaFinal;

    //Se usa el format para luego parsear la fecha que me viene de la tarea a tipo Date para poder manejar los datos.
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());;

    Button empezarButton , pararButton, backbutton;

    User userAsignado = new User();
    private Task tarea;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detaills);
        tarea = (Task) getIntent().getSerializableExtra("task");
        Log.d("TAREADETALLE", tarea.getTaskName());
        getItems();
        setItems();
        actualizarTarea();

    }


    public void getItems(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        taskName = findViewById(R.id.task_name);
        taskDescription = findViewById(R.id.task_description);
        taskFechaInicio = findViewById(R.id.task_fechainicio);
        taskFechafinal  = findViewById(R.id.task_fechafinal);
        userName = findViewById(R.id.task_asignada);
        profileUserImg = findViewById(R.id.profileUserImg);
        empezarButton = findViewById(R.id.button_empezar);
        empezarButton.setOnClickListener(v -> {
            empezarTarea();
        });

        pararButton = findViewById(R.id.button_parar);
        pararButton.setOnClickListener(v -> {
            finalizarTarea();
        });
        contador = findViewById(R.id.contador);
        imgCardView = findViewById(R.id.cardView);
        backbutton = findViewById(R.id.backbutton);
        backbutton.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    public void setItems(){
        taskName.setText(tarea.getTaskName());
        taskDescription.setText(tarea.getTaskDescription());

        if(tarea.getFechaInicio()!= null ){
            taskFechaInicio.setText(tarea.getFechaInicio());

        }
        if(tarea.getFechaFinal()!= null)
            taskFechafinal.setText(tarea.getFechaFinal());


        if(tarea.getFechaInicio()== null && tarea.getFechaFinal()== null){
            empezarButton.setVisibility(View.VISIBLE);
            pararButton.setVisibility(View.GONE);
        }else if (tarea.getFechaInicio()!= null && tarea.getFechaFinal()== null){
            empezarButton.setVisibility(View.GONE);
            pararButton.setVisibility(View.VISIBLE);
            if(timer ==null)
                iniciarContador();

        }else if (tarea.getFechaFinal()!= null){
            empezarButton.setVisibility(View.GONE);
            pararButton.setVisibility(View.VISIBLE);
            pararButton.setText("Terminada");
            pararButton.setEnabled(false);
            try{
                fechaInicio = sdf.parse(tarea.getFechaInicio());
                fechaFinal = sdf.parse(tarea.getFechaFinal());
                long tiempoTranscurrido = fechaFinal.getTime() - fechaInicio.getTime();
                contador.setText(milisegundosAContador(tiempoTranscurrido));

            }catch (ParseException e){
                e.printStackTrace();
            }



        }

        if(tarea.getAsignadaA() != null){
            userName.setVisibility(View.VISIBLE);
            imgCardView.setVisibility(View.VISIBLE);
            getUserAsignado();        }
        else{
            userName.setVisibility(View.GONE);
            imgCardView.setVisibility(View.GONE);
        }




    }

    public void getUserAsignado(){
        db.collection("users")
                .document(tarea.getAsignadaA())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if(doc.exists()){
                            userAsignado.setNombre(doc.getString("nombre"));
                            userAsignado.setProfileImage(doc.getString("profile_img_path"));
                            userName.setText(userAsignado.getNombre());
                            Glide.with(getApplicationContext())
                                    .load(userAsignado.getProfileImage())
                                    .into(profileUserImg);
                        }

                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "No se han podido obtener los datos de usuario asignado", Toast.LENGTH_SHORT).show();
                });
    }

    public void empezarTarea(){
        Date fechaActual = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String fechaActualString = sdf.format(fechaActual);

        if(tarea.getAsignadaA() != null){
            db.collection("tareas")
                    .document(tarea.getTaskId())
                    .update("fechaInicio", fechaActualString)
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "No se ha podido empezar la tarea", Toast.LENGTH_SHORT).show();
                    });
        }else{
            Map<String, Object> actualizacion = new HashMap<>();
            actualizacion.put("asignadaA", mAuth.getCurrentUser().getUid());
            actualizacion.put("fechaInicio", fechaActualString);

            db.collection("tareas")
                    .document(tarea.getTaskId())
                    .update(actualizacion)
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "No se ha podido empezar la tarea", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void iniciarContador(){
        try{
            fechaInicio = sdf.parse(tarea.getFechaInicio());
            //Una vez obtenida la fechadeinicio en el formato adecuado se inicia el temporizador
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Obtener la fecha y hora actual
                            long tiempoActual = System.currentTimeMillis();
                            long tiempoTranscurrido = tiempoActual - fechaInicio.getTime();
                            String contadorActualizado = milisegundosAContador(tiempoTranscurrido);
                            contador.setText(contadorActualizado);

                        }
                    });

                }
            },1000,1000);


        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    private String milisegundosAContador(long milisegundos) {
        // Convierte los milisegundos transcurridos a formato días:horas:minutos:segundos
        long segundos = milisegundos / 1000;
        long dias = segundos / (24 * 3600);
        segundos %= (24 * 3600);
        long horas = segundos / 3600;
        segundos %= 3600;
        long minutos = segundos / 60;
        segundos %= 60;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", dias, horas, minutos, segundos);
    }

    public void actualizarTarea(){
        db.collection("tareas").document(tarea.getTaskId())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    obtenerTarea();
                });
    }

    public void obtenerTarea(){
        db.collection("tareas").document(tarea.getTaskId()).get()
                .addOnSuccessListener(doc -> {
                    if(doc.exists()){
                        Task task = new Task();
                        task.setTaskId(doc.getId());
                        task.setTaskName(doc.getString("nombreTarea"));
                        task.setTaskDescription(doc.getString("descripcionTarea"));
                        task.setAsignadaA(doc.getString("asignadaA"));
                        task.setFechaInicio(doc.getString("fechaInicio"));
                        task.setFechaFinal(doc.getString("fechaFinalizacion"));
                        tarea = task;
                        setItems();
                    }
                });
    }

    public void finalizarTarea(){

        //obtener la fecha actual y parsearla
        Date fechaActual = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String fechaActualString = sdf.format(fechaActual);
        //PARAR EL CONTADOR
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
            }
        });

        timer = null;

        //Insertar la fecha de finalizacion de la tarea en base de datos
        db.collection("tareas")
                .document(tarea.getTaskId())
                .update("fechaFinalizacion", fechaActualString)
                .addOnSuccessListener(command -> {
                    Toast.makeText(this, "La tarea ha concluido",Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,"No se ha podido finalizar la tarea", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Detener el temporizador si está activo
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}