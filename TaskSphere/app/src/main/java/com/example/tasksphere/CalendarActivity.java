package com.example.tasksphere;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormatSymbols;

public class  CalendarActivity extends AppCompatActivity {

    TextView fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cambiar la localización de la app a español
        Locale locale = new Locale("es", "ES");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        setContentView(R.layout.activity_calendar);

        fecha = findViewById(R.id.fecha);
        actualizarFechaTexto(new Date()); // Actualizar la fecha al iniciar
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
                Calendar fechaSeleccionada = Calendar.getInstance();
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

