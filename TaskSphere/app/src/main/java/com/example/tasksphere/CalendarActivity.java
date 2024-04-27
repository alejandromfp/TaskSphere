package com.example.tasksphere;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.text.DateFormatSymbols;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

    TextView fecha;
    private FirebaseAuth mAuth;

    private Calendar fechaSeleccionada = Calendar.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa Firebase
        FirebaseApp.initializeApp(this);
        // Configura Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        // Cambiar la localización de la app a español
        Locale locale = new Locale("es", "ES");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        setContentView(R.layout.activity_calendar);

        fecha = findViewById(R.id.fecha);
        actualizarFechaTexto(new Date()); // Actualizar la fecha al iniciar

        Button botonVacaciones = findViewById(R.id.botonVacaciones);
        botonVacaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarSolicitudVacaciones();
            }
        });



    }

    private void enviarSolicitudVacaciones() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No autenticado, no se puede enviar la solicitud", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaVacaciones = sdf.format(fechaSeleccionada.getTime());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Comprobar si existe una solicitud similar
        db.collection("solicitudes")
                .whereEqualTo("usuario", user.getUid())
                .whereEqualTo("fecha", fechaVacaciones)
                .whereEqualTo("estado", "pendiente")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Toast.makeText(CalendarActivity.this, "Ya existe una solicitud para esta fecha.", Toast.LENGTH_SHORT).show();
                    } else {
                        enviarNuevaSolicitud(db, user.getUid(), fechaVacaciones);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CalendarActivity.this, "Error al verificar solicitudes existentes", Toast.LENGTH_SHORT).show();
                });
    }


    private void enviarNuevaSolicitud(FirebaseFirestore db, String userId, String fechaVacaciones) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("usuario", userId);
        userData.put("fecha", fechaVacaciones);
        userData.put("estado", "pendiente");

        db.collection("solicitudes")
                .add(userData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CalendarActivity.this, "Solicitud enviada con éxito", Toast.LENGTH_LONG).show();
                    Log.d("Firestore", "Documento añadido con ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CalendarActivity.this, "Error al enviar la solicitud", Toast.LENGTH_LONG).show();
                    Log.w("Firestore", "Error añadiendo documento", e);
                });
    }


    public void abrirCalendario(View view){
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(CalendarActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fechaSeleccionada.set(year, month, dayOfMonth);
                actualizarFechaTexto(fechaSeleccionada.getTime());
            }
        }, anio, mes, dia);

        dpd.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
        dpd.show();
    }

    private void actualizarFechaTexto(Date date) {
        Calendar hoy = Calendar.getInstance();
        Calendar manana = Calendar.getInstance();
        manana.add(Calendar.DAY_OF_YEAR, 1);
        Calendar ayer = Calendar.getInstance();
        ayer.add(Calendar.DAY_OF_YEAR, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        Calendar fechaCal = Calendar.getInstance();
        fechaCal.setTime(date);

        if (fechaCal.get(Calendar.YEAR) == hoy.get(Calendar.YEAR) &&
                fechaCal.get(Calendar.DAY_OF_YEAR) == hoy.get(Calendar.DAY_OF_YEAR)) {
            fecha.setText("Hoy");
        } else if (fechaCal.get(Calendar.YEAR) == manana.get(Calendar.YEAR) &&
                fechaCal.get(Calendar.DAY_OF_YEAR) == manana.get(Calendar.DAY_OF_YEAR)) {
            fecha.setText("Mañana");
        } else if (fechaCal.get(Calendar.YEAR) == ayer.get(Calendar.YEAR) &&
                fechaCal.get(Calendar.DAY_OF_YEAR) == ayer.get(Calendar.DAY_OF_YEAR)) {
            fecha.setText("Ayer");
        } else {
            fecha.setText(sdf.format(date));
        }
    }


}

