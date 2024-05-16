package com.example.tasksphere;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tasksphere.modelo.entidad.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CalendarFragment extends Fragment {

    TextView fecha;
    Button seleccionarFecha, botonVacaciones;

    private Calendar fechaSeleccionada = Calendar.getInstance();


    FirebaseAuth mAuth;

    SharedPreferences sharedPreferences;
    User usuario;


    FirebaseFirestore db;
    ImageView profileImg;
    TextView username;

    public CalendarFragment() {

    }

    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView  = inflater.inflate(R.layout.fragment_calendar, container, false);
        // Cambiar la localización de la app a español
        Locale locale = new Locale("es", "ES");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        //Usar el rootview en los fragment, para hacer el findviewbyid!!!!!!
        getItems(rootView);
        obtenerDatosDeUsuario();
        setDatosDeUsuario();

        actualizarFechaTexto(new Date()); // Actualizar la fecha al iniciar

        return rootView;
    }

    public void abrirCalendario(View view){
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar fechaSeleccionada = Calendar.getInstance();
                fechaSeleccionada.set(year, month, dayOfMonth);
                actualizarFechaTexto(fechaSeleccionada.getTime());
            }
        }, anio, mes, dia);

        dpd.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);

        //arreglar calendar background ... dpd.getWindow()... TODO
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

    private void enviarSolicitudVacaciones() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "No autenticado, no se puede enviar la solicitud", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(requireContext(), "Ya existe una solicitud para esta fecha.", Toast.LENGTH_SHORT).show();
                    } else {
                        enviarNuevaSolicitud(db, user.getUid(), fechaVacaciones);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al verificar solicitudes existentes", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(requireContext(), "Solicitud enviada con éxito", Toast.LENGTH_LONG).show();
                    Log.d("Firestore", "Documento añadido con ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al enviar la solicitud", Toast.LENGTH_LONG).show();
                    Log.w("Firestore", "Error añadiendo documento", e);
                });
    }

    private void obtenerDatosDeUsuario(){
        sharedPreferences = requireContext().getSharedPreferences("usuario", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("userJson", "uwu");
        Log.d("JSON", userJson);
        if(userJson != null){
            Gson gson = new Gson();
            usuario = gson.fromJson(userJson, User.class);
        }

    }
    private void setDatosDeUsuario(){
        username.setText(usuario.getNombre());
        Glide.with(requireContext())
                .load(usuario.getProfileImage())
                .into(profileImg);
    }


    public void getItems(View rootView){
        fecha = rootView.findViewById(R.id.fecha);
        botonVacaciones = rootView.findViewById(R.id.botonVacaciones);
        botonVacaciones.setOnClickListener(v -> {
            enviarSolicitudVacaciones();
        });
        seleccionarFecha = rootView.findViewById(R.id.botonCambiarFecha);
        seleccionarFecha.setOnClickListener(v -> {
            abrirCalendario(seleccionarFecha);
        });

        username = rootView.findViewById(R.id.username);
        profileImg = rootView.findViewById(R.id.profileImg);
        profileImg.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
            navController.navigate(R.id.profile_page);
        });


    }

}