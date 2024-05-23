package com.example.tasksphere;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tasksphere.adapter.FichajesAdapter;
import com.example.tasksphere.adapter.TareasAdapter;
import com.example.tasksphere.modelo.entidad.Fichaje;
import com.example.tasksphere.modelo.entidad.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FichajesActivity extends AppCompatActivity {

    TextView empleado, diaSeleccionado, horasTotales , empleadoTag;
    Spinner spinner;
    FirebaseFirestore db;

    Button backbutton;
    long sumaHoras;

    FichajesAdapter adapter;

    List<Fichaje>fichajesDia = new ArrayList<>();

    SharedPreferences sharedPreferences;
    User usuario;
    FirebaseAuth mAuth;
    Calendar calendar;

    SimpleDateFormat sdf;

    String idUsuarioSeleccionado;
    String fechaSeleccionada;

    CalendarView calendarView;

    RecyclerView fichajesRecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fichajes);
        obtenerDatosDeUsuario();
        getItems();
        setItems();
    }

    private void obtenerDatosDeUsuario(){
        sharedPreferences = this.getSharedPreferences("usuario", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("userJson", "uwu");
        Log.d("JSON", userJson);
        if(userJson != null){
            Gson gson = new Gson();
            usuario = gson.fromJson(userJson, User.class);
        }

    }

    public void getItems(){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        empleado = findViewById(R.id.empleado);

        diaSeleccionado = findViewById(R.id.selectday);
        horasTotales = findViewById(R.id.horas_totales_dia);
        spinner = findViewById(R.id.spinner);
        fichajesRecycler = findViewById(R.id.recyclerFichajes);
        calendarView = findViewById(R.id.calendar);
        empleadoTag = findViewById(R.id.empleado_text);
        calendar = Calendar.getInstance();
        adapter = new FichajesAdapter(this, fichajesDia);
        fichajesRecycler.setAdapter(adapter);
        fichajesRecycler.setLayoutManager(new LinearLayoutManager(this));
        backbutton = findViewById(R.id.backbutton);
        backbutton.setOnClickListener(v -> onBackPressed());

    }

    public void setItems(){
        //SETEAMOS CALENDARIO CON LA FECHA POR DEFAULT CUANDO SE ENTRE A LA ACTIVITY
        Calendar calendar = Calendar.getInstance();
        long today = calendar.getTimeInMillis();
        calendarView.setDate(today, true, true);
        diaSeleccionado.setText(sdf.format(calendar.getTime()));
        fechaSeleccionada = sdf.format(calendar.getTime());


        if(usuario.getRol().equals("Administrador")  || usuario.getRol().equals("Gerente")){
            getEmpleados(spinner);
            spinner.setVisibility(View.VISIBLE);
            empleadoTag.setVisibility(View.VISIBLE);
        }
        else{
            getFichajes(usuario.getUserId());

        }


        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            calendar.set(year,month,dayOfMonth);
            fechaSeleccionada = sdf.format(calendar.getTime());
            diaSeleccionado.setText(fechaSeleccionada);
            if(usuario.getRol().equals("Administrador")){
                //TODO VERFICHAJES PARA EL DIA Y EMPLEADO
                fichajesDia.clear();
                adapter.notifyDataSetChanged();
                spinner.setSelection(0);
            }
            else{
                fichajesDia.clear();
                adapter.notifyDataSetChanged();
                getFichajes(usuario.getUserId());

            }

        });
    }

    private void getEmpleados(Spinner spinner){
        List<User> empleados = new ArrayList<>();
        User opcionPorDefecto = new User();
        opcionPorDefecto.setUserId("");
        opcionPorDefecto.setNombre("Sin seleccionar");
        opcionPorDefecto.setApellidos("");
        empleados.add(opcionPorDefecto);

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot doc: task.getResult()){
                            User usuario = new User();

                            usuario.setUserId(doc.getId());
                            usuario.setNombre(doc.getString("nombre"));
                            usuario.setApellidos(doc.getString("apellidos"));
                            empleados.add(usuario);
                        }



                        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, android.R.layout.simple_spinner_item, empleados) {
                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                TextView textView = (TextView) super.getView(position, convertView, parent);
                                textView.setText(empleados.get(position).getNombre() + " " + empleados.get(position).getApellidos());
                                return textView;
                            }

                            @Override
                            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                                textView.setText(empleados.get(position).getNombre() + " " + empleados.get(position).getApellidos());
                                return textView;
                            }
                        };
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        spinner.setSelection(0);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                User usuarioSeleccionado = (User) parent.getItemAtPosition(position);
                                if (!usuarioSeleccionado.getUserId().isEmpty()) {
                                    idUsuarioSeleccionado = usuarioSeleccionado.getUserId();
                                    getFichajes(idUsuarioSeleccionado);
                                }else
                                    idUsuarioSeleccionado = null;

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // No se ha seleccionado ningún usuario
                            }
                        });
                    }
                });
    }

    public void getFichajes(String userId){
        sumaHoras = 0;
        fichajesDia.clear();
        db.collection("users")
                .document(userId)
                .collection("fichajes")
                .document(fechaSeleccionada)
                .collection("listafichajedia")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        Fichaje fichaje = new Fichaje();
                        fichaje.setFechaEmpiezo(doc.getTimestamp("fechaEmpiezo").toDate());
                        fichaje.setDocumentFechaId(doc.getId());
                        try{
                            fichaje.setFechaFin(doc.getTimestamp("fechaFin").toDate());
                        }catch (Exception e){
                            fichaje.setFechaFin(null);
                        }
                        // Formatear la hora en formato HH:mm
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

                        if(fichaje.getFechaFin() != null){
                            String fechaFin = sdf.format(fichaje.getFechaFin());

                            //Se acumula la diferencia en milisegundos, de las horas para ir calculando las horas totales
                            long differenceInMillis = fichaje.getFechaFin().getTime() - fichaje.getFechaEmpiezo().getTime();
                            sumaHoras += differenceInMillis;
                        fichajesDia.add(fichaje);
                        }
                    }
                    Collections.sort(fichajesDia, (t2, t1) -> t2.getFechaEmpiezo().compareTo(t1.getFechaEmpiezo()));

                    //CONVERSION AL FORMATO HH:MM DE LAS HORAS TOTALES
                    long differenceInMinutes = sumaHoras / (60 * 1000);
                    long hours = differenceInMinutes / 60;
                    long minutes = differenceInMinutes % 60;
                    String horasTotalString = String.format("%02d:%02d", hours, minutes);
                    horasTotales.setText(horasTotalString);
                    adapter.notifyDataSetChanged();

                });
    }
}