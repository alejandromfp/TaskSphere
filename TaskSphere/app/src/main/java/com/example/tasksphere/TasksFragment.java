package com.example.tasksphere;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.tasksphere.adapter.TareasAdapter;
import com.example.tasksphere.modelo.entidad.Task;
import com.example.tasksphere.modelo.entidad.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class TasksFragment extends Fragment  {

    public Button pendientesButton, sinAsignarButton, finalizadasButton, saveNewTask;
    public FloatingActionButton addTask;
    public int[] buttonStatus  = new int[3];
    List<Button> listaBotones = new ArrayList<>();
    List<Task> tareasSinAsignar = new ArrayList<>();
    List<Task> tareasPendientes = new ArrayList<>();
    List<Task> tareasTerminadas = new ArrayList<>();
    LinearLayout sinAsignarContainer, pendientesContainer, finalizadasContainer;

    ArrayAdapter adapter;
    Dialog dialog;

    RecyclerView recTareasSinAsignar, recTareasPendientes, recTareasTerminadas;
    TareasAdapter adapterSinAsignar , adapterPendientes, adapterTerminadas;

    String idUsuarioSeleccionado = null;
    SharedPreferences sharedPreferences;
    User usuario;

    FirebaseAuth mAuth;

    FirebaseFirestore db;
    ImageView profileImg;
    TextView username;


    public TasksFragment() {
        // Required empty public constructor
    }


    public static TasksFragment newInstance(String param1, String param2) {
        TasksFragment fragment = new TasksFragment();
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
        // Inflate the layout for this fragment
        View rootView  = inflater.inflate(R.layout.fragment_tasks, container, false);
        getItems(rootView);
        obtenerDatosDeUsuario();
        setDatosDeUsuario();
        actualizarTareas();

        return rootView;

    }

    public void selectButton(Button button){
        if(button.getId() == R.id.pendientesButton){
            int estado = buttonStatus[0];
            setDefaultButtonStyles(listaBotones);
            if(estado == 0){
                buttonStatus[0] = 1;
                setSelectedButtonStyles(pendientesButton);

                //METER PARA QUE SE MUESTRE SOLO LAS PENDIENTES
                pendientesContainer.setVisibility(View.VISIBLE);
                finalizadasContainer.setVisibility(View.GONE);
                sinAsignarContainer.setVisibility(View.GONE);
            }else{
                mostrarTodos();
            }
        } else if(button.getId() == R.id.sinAsignarButton){
            int estado = buttonStatus[1];
            setDefaultButtonStyles(listaBotones);
            if(estado == 0){
                buttonStatus[1] = 1;
                setSelectedButtonStyles(sinAsignarButton);
                //METER PARA QUE SE MUESTRE SOLO LAS PENDIENTES
                pendientesContainer.setVisibility(View.GONE);
                finalizadasContainer.setVisibility(View.GONE);
                sinAsignarContainer.setVisibility(View.VISIBLE);


            }else{
                mostrarTodos();
            }
        } else if(button.getId() == R.id.finalizadasButton){
            int estado = buttonStatus[2];
            setDefaultButtonStyles(listaBotones);
            if(estado == 0){
                buttonStatus[2] = 1;
                setSelectedButtonStyles(finalizadasButton);
                //METER PARA QUE SE MUESTRE SOLO LAS PENDIENTES
                pendientesContainer.setVisibility(View.GONE);
                finalizadasContainer.setVisibility(View.VISIBLE);
                sinAsignarContainer.setVisibility(View.GONE);

            }else{
                mostrarTodos();
            }
        }

    }

    public void setDefaultButtonStyles(List<Button> botones){
        for(Button button : botones){
            button.setTextColor(ContextCompat.getColor(requireContext(),R.color.azulOscuro));
            button.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.azulMasClaro));
        }
        buttonStatus[0] = 0;
        buttonStatus[1] = 0;
        buttonStatus[2] = 0;


    }


    public void setSelectedButtonStyles(Button button){
        button.setTextColor(ContextCompat.getColor(requireContext(),R.color.white));
        button.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.azulOscuro));

    }

    public void getItems(View rootView){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        pendientesButton = rootView.findViewById(R.id.pendientesButton);
        sinAsignarButton = rootView.findViewById(R.id.sinAsignarButton);
        finalizadasButton = rootView.findViewById(R.id.finalizadasButton);
        listaBotones.add(pendientesButton);
        listaBotones.add(sinAsignarButton);
        listaBotones.add(finalizadasButton);

        for (Button button : listaBotones){
            button.setOnClickListener(v -> {
                selectButton(button);
            });
        }
        pendientesContainer = rootView.findViewById(R.id.tareas_pendientes_container);
        sinAsignarContainer = rootView.findViewById(R.id.tareas_sin_asignar_container);
        finalizadasContainer = rootView.findViewById(R.id.tareas_terminadas_container);

        //TAREAS SIN ASIGNAR VIEW
        recTareasSinAsignar = rootView.findViewById(R.id.recyclerViewSinAsignar);
        adapterSinAsignar = new TareasAdapter(requireContext(), tareasSinAsignar);
        recTareasSinAsignar.setAdapter(adapterSinAsignar);
        recTareasSinAsignar.setLayoutManager(new LinearLayoutManager(requireContext()));
        obtenerTareasSinAsignar();


        //TAREAS PENDIENTES VIEW
        recTareasPendientes = rootView.findViewById(R.id.recyclerViewPendientes);
        adapterPendientes = new TareasAdapter(requireContext(), tareasPendientes);
        recTareasPendientes.setAdapter(adapterPendientes);
        recTareasPendientes.setLayoutManager(new LinearLayoutManager(requireContext()));
        obtenerTareasPendientes();


        //TAREAS TERMINADAS VIEW
        recTareasTerminadas = rootView.findViewById(R.id.recyclerViewTerminadas);
        adapterTerminadas = new TareasAdapter(requireContext(), tareasTerminadas);
        recTareasTerminadas.setAdapter(adapterTerminadas);
        recTareasTerminadas.setLayoutManager(new LinearLayoutManager(requireContext()));
        obtenerTareasTerminadas();


        //Añadir nueva tarea

        addTask = rootView.findViewById(R.id.add_task_button);
        addTask.setOnClickListener(v -> {
            dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.add_task_layout);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextInputEditText taskname, taskdescription;

            taskname = dialog.findViewById(R.id.task_name);
            taskdescription = dialog.findViewById(R.id.task_description);

            //Set combobox con los empleados
            Spinner spinner = dialog.findViewById(R.id.spinner);
            getEmpleados(spinner);


            saveNewTask = dialog.findViewById(R.id.savetask);
            saveNewTask.setOnClickListener(v1 -> {
                if(validarCampos(taskname, taskdescription)){
                    guardarTareaEnFirestore(taskname.getText().toString(), taskdescription.getText().toString());
                    dialog.dismiss();
                }



            });

        });

        username = rootView.findViewById(R.id.username);
        profileImg = rootView.findViewById(R.id.profileImg);
        profileImg.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
            navController.navigate(R.id.profile_page);
        });

    }

    public void mostrarTodos(){
        if(tareasSinAsignar.size()<1)
            sinAsignarContainer.setVisibility(View.GONE);
        else
            sinAsignarContainer.setVisibility(View.VISIBLE);

        if(tareasPendientes.size()<1)
            pendientesContainer.setVisibility(View.GONE);
        else
            pendientesContainer.setVisibility(View.VISIBLE);

        if(tareasTerminadas.size()<1)
            finalizadasContainer.setVisibility(View.GONE);
        else
            finalizadasContainer.setVisibility(View.VISIBLE);
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
        Log.d("123456", usuario.getNombre());
        Glide.with(requireContext())
                .load(usuario.getProfileImage())
                .into(profileImg);
    }

    private void getEmpleados(Spinner spinner){
        List<User> empleados = new ArrayList<>();
        User opcionPorDefecto = new User();
        opcionPorDefecto.setUserId("");
        opcionPorDefecto.setNombre("Sin seleccionar");
        opcionPorDefecto.setApellidos("");
        empleados.add(opcionPorDefecto);

        db.collection("users")
                .whereNotEqualTo("rol","administrador")
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



                        ArrayAdapter<User> adapter = new ArrayAdapter<User>(requireContext(), android.R.layout.simple_spinner_item, empleados) {
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
                                    Log.d("123456", "ID del usuario seleccionado: " + idUsuarioSeleccionado);
                                }else
                                    idUsuarioSeleccionado = null;

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // No se ha seleccionado ningún usuario
                            }
                        });
                        dialog.show();
                    }
                });
    }


    private void guardarTareaEnFirestore(String nombreTarea, String descripcionTarea) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> tarea = new HashMap<>();
        tarea.put("nombreTarea", nombreTarea);
        tarea.put("descripcionTarea", descripcionTarea);
        tarea.put("asignadaA", idUsuarioSeleccionado);
        tarea.put("fechaCreacion", Timestamp.now());
        tarea.put("fechaInicio",null);
        tarea.put("fechaFinalizacion",null);

        // Agregar la tarea a la colección "tareas" en Firestore
        db.collection("tareas")
                .add(tarea)
                .addOnSuccessListener(documentReference -> {
                    if(idUsuarioSeleccionado !=null){
                        obtenerToken(idUsuarioSeleccionado);
                    }
                    Toast.makeText(requireContext() ,"La tarea se añadió correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext() ,"Hubo un error al añadir la tarea", Toast.LENGTH_SHORT).show();
                });
    }

    private void obtenerTareasSinAsignar(){
        db.collection("tareas")
                .whereEqualTo("asignadaA",null)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tareasSinAsignar.clear();
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        Task tarea = new Task();
                        tarea.setTaskId(doc.getId());
                        tarea.setTaskName(doc.getString("nombreTarea"));
                        Log.d("EYYYY", doc.getString("nombreTarea"));
                        tarea.setFechaCreacion(doc.getTimestamp("fechaCreacion").toDate());
                        tarea.setTaskDescription(doc.getString("descripcionTarea"));
                        tarea.setAsignadaA(doc.getString("asignadaA"));
                        tarea.setFechaInicio(doc.getString("fechaInicio"));
                        tarea.setFechaFinal(doc.getString("fechaFinalizacion"));
                        Log.d("TAREASINASIGNAR", tarea.getTaskName());
                        tareasSinAsignar.add(tarea);
                    }
                    Collections.sort(tareasSinAsignar, (t1, t2) -> t2.getFechaCreacion().compareTo(t1.getFechaCreacion()));
                    adapterSinAsignar.notifyDataSetChanged();
                });
    }

    private void obtenerTareasPendientes(){
        Log.d("MYID", mAuth.getCurrentUser().getUid());
        db.collection("tareas")
                .whereEqualTo("fechaFinalizacion",null)
                .whereEqualTo("asignadaA", mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tareasPendientes.clear();
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        Task tarea = new Task();
                        tarea.setTaskId(doc.getId());
                        tarea.setTaskName(doc.getString("nombreTarea"));
                        tarea.setTaskDescription(doc.getString("descripcionTarea"));
                        tarea.setFechaCreacion(doc.getTimestamp("fechaCreacion").toDate());
                        tarea.setAsignadaA(doc.getString("asignadaA"));
                        tarea.setFechaInicio(doc.getString("fechaInicio"));
                        tarea.setFechaFinal(doc.getString("fechaFinalizacion"));
                        Log.d("TAREAPENDIENTE", tarea.getTaskName());
                        tareasPendientes.add(tarea);
                    }
                    Collections.sort(tareasPendientes, (t1, t2) -> t2.getFechaCreacion().compareTo(t1.getFechaCreacion()));
                    adapterPendientes.notifyDataSetChanged();
                });
    }
    private void obtenerTareasTerminadas(){
        Log.d("MYID", mAuth.getCurrentUser().getUid());
        db.collection("tareas")
                .whereNotEqualTo("fechaFinalizacion",null)
                .whereEqualTo("asignadaA", mAuth.getCurrentUser().getUid())
                .orderBy("fechaCreacion", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tareasTerminadas.clear();
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        Task tarea = new Task();
                        tarea.setTaskId(doc.getId());
                        tarea.setTaskName(doc.getString("nombreTarea"));
                        tarea.setTaskDescription(doc.getString("descripcionTarea"));
                        tarea.setAsignadaA(doc.getString("asignadaA"));
                        tarea.setFechaCreacion(doc.getTimestamp("fechaCreacion").toDate());
                        tarea.setFechaInicio(doc.getString("fechaInicio"));
                        tarea.setFechaFinal(doc.getString("fechaFinalizacion"));
                        Log.d("TAREASTERMINADA", tarea.getTaskName());
                        tareasTerminadas.add(tarea);

                    }
                    Collections.sort(tareasTerminadas, (t1, t2) -> t2.getFechaCreacion().compareTo(t1.getFechaCreacion()));
                    adapterTerminadas.notifyDataSetChanged();
                    mostrarTodos();
                });
    }

    private void actualizarTareas(){
        db.collection("tareas")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    obtenerTareasPendientes();
                    obtenerTareasSinAsignar();
                    obtenerTareasTerminadas();

                    adapterTerminadas.notifyDataSetChanged();
                    adapterPendientes.notifyDataSetChanged();
                    adapterSinAsignar.notifyDataSetChanged();
                });
    }

    public boolean validarCampos(TextInputEditText taskname, TextInputEditText taskdescription) {
        if (taskname.getText().toString().isEmpty()) {
            taskname.setError("El campo no puede estar vacío");
            taskname.requestFocus();
            return false;
        } else if (taskdescription.getText().toString().isEmpty()) {
            taskdescription.setError("El campo no puede estar vacío");
            taskdescription.requestFocus();
            return false;
        } else {
            return true;
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        obtenerTareasTerminadas();
        obtenerTareasPendientes();
        obtenerTareasSinAsignar();
    }

    private void enviarNotificacion(String token, String title, String body, String userId) {
        final String API_KEY = "AAAAOwyZe_A:APA91bH2sWXmIU6uQJGwQ51bsu53CrZ1D7h7znkxf0jKFgYWBqzmwu5a0PoQmKcp9UxmWEjvSFBpVaf11hMp-y6auZvv3DB5Jb2tBkWQ7EgoUWok2bPUjV1A7tFtBnjkPXUq1HFPo8i-";

        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", title);
            notificationBody.put("body", body);
            Log.d("TAG2", token);
            notification.put("to", token);
            notification.put("notification", notificationBody);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send",
                    notification,
                    response -> {
                        guardarNotificacionEnFirebase(title, body, userId);
                    },
                    error -> {
                        Log.e("TAG", "Error al enviar notificación: " + error.getMessage());
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + API_KEY);
                    return headers;
                }
            };

            Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtenerToken(String userId) {

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String token = documentSnapshot.getString("token");
                    if (token != null && !token.isEmpty()) {

                        enviarNotificacion(token,
                                "¡Nueva tarea!",
                                "¡Un administrador te ha asignado una tarea, empiezala cuando puedas!",
                                userId);
                    }
                });
    }

    public void guardarNotificacionEnFirebase(String title, String body, String administratorId){

        Map<String, Object> notificacion = new HashMap<>();
        notificacion.put("titulo", title);
        notificacion.put("descripcion", body);
        notificacion.put("fechaCreacion", Timestamp.now());
        notificacion.put("categoria", "Tareas");

        db.collection("users")
                .document(administratorId)
                .collection("notificaciones")
                .add(notificacion)
                .addOnSuccessListener(command -> {
                    Log.d("Firestore", "Se ha guardado con exito");
                })
                .addOnFailureListener(command -> {
                    Log.d("Firestore", "No se ha guardado con exito");
                });

    }
}