package com.example.tasksphere;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tasksphere.model.Comunicado;
import com.example.tasksphere.modelo.entidad.Fichaje;
import com.example.tasksphere.modelo.entidad.Task;
import com.example.tasksphere.modelo.entidad.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


public class HomeFragment extends Fragment {

    SharedPreferences sharedPreferences;
    User usuario;

    Comunicado lastComunicado;
    List<Comunicado> comunicadoList = new ArrayList<>();
    String token;

    Button notificationsButton, buttonPlay, buttonPause;
    ConstraintLayout teamItem, fichajesItem, chatItem;

    Fichaje fichajeActual;

    Timer contador;
    FirebaseAuth mAuth;
    int activos;
    FirebaseFirestore db;
    ImageView profileImg;


    Handler handler;
    Runnable timerRunnable ;
    TextView username,userRole, notificationCount, timer, usernameFichar,ficharTag ;

    View fichar, lastComunicadoView;

    Button logOut;



    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        getItems(rootView);
        guardarTokenUsuario();
        obtenerDatosDeUsuario();
        setDatosDeUsuario();
        updateNotificationCount();

        return rootView;
    }
    private void getItems(View rootView){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        profileImg = rootView.findViewById(R.id.profileimg);
        profileImg.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
            navController.navigate(R.id.profile_page);
        });

        username = rootView.findViewById(R.id.username);
        userRole = rootView.findViewById(R.id.userRole);
        teamItem = rootView.findViewById(R.id.team_item);
        teamItem.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TeamActivity.class);
            requireActivity().startActivity(intent);
        });
        lastComunicadoView = rootView.findViewById(R.id.last_comunicado);

        fichajesItem = rootView.findViewById(R.id.item_fichajes_horarios);
        fichajesItem.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FichajesActivity.class);
            requireActivity().startActivity(intent);
        });

        chatItem = rootView.findViewById(R.id.item_chats);
        chatItem.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            requireActivity().startActivity(intent);
        });


        notificationsButton = rootView.findViewById(R.id.notificationbutton);
        notificationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotificationActivity.class);
            intent.putExtra("userId", usuario.getUserId() );
            requireContext().startActivity(intent);
        });

        notificationCount = rootView.findViewById(R.id.notificationCount);
        notificationCount.setVisibility(View.GONE);

        fichar = rootView.findViewById(R.id.fichar);
        buttonPlay = fichar.findViewById(R.id.botonplay);
        buttonPause = fichar.findViewById(R.id.botonstop);
        usernameFichar = fichar.findViewById(R.id.username_fichar);
        timer = fichar.findViewById(R.id.timer);
        ficharTag = fichar.findViewById(R.id.fichar_tag);
        handler = new Handler();
        obtainLastComunicado();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    long tiempoActual = System.currentTimeMillis();
                    long tiempoTranscurrido = tiempoActual - fichajeActual.getFechaEmpiezo().getTime();
                    String contadorActualizado = milisegundosAContador(tiempoTranscurrido);
                    timer.setText(contadorActualizado);
                    handler.postDelayed(this, 1000);
                }
            }
        };

        Button logOutButton = rootView.findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(v -> {
            logOut();
        });
    }
    private void logOut() {
        mAuth.signOut();
        clearUserData();

        // Redirigir a la pantalla de inicio de sesión
        Intent intent = new Intent(requireContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Evitar que el usuario vuelva a la pantalla anterior utilizando el botno de retroceso
        startActivity(intent);
        requireActivity().finish();
    }

    private void clearUserData() {
        // Limpiar los datos de SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
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
                .placeholder(R.drawable.defaultavatar)
                .into(profileImg);
        userRole.setText(usuario.getRol());
        usernameFichar.setText(usuario.getNombre());
        updateItemFichar();
        setFicharItem();
    }

    private void guardarTokenUsuario() {
        FirebaseMessaging
                .getInstance()
                .getToken()
                .addOnCompleteListener(task -> {
                    token  = task.getResult();
                    Log.d("TOKEN" ,token);

                    if (mAuth.getUid() != null) {
                        db.collection("users")
                                .document(mAuth.getUid())
                                .update("token", token)
                                .addOnSuccessListener(aVoid -> {

                                })
                                .addOnFailureListener(e -> {

                                });
                    }
                });
    }

    public void obtenerNotificacionesCount(){

        db.collection("users")
                .document(usuario.getUserId())
                .collection("notificaciones")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots ->{
                    int count = queryDocumentSnapshots.getDocuments().size();
                    if(count > 0){
                        notificationCount.setVisibility(View.VISIBLE);
                        notificationCount.setText(String.valueOf(count));
                    }else
                        notificationCount.setVisibility(View.GONE);
                });

    }


    private void setFicharItem(){
        activos = 0;
        ficharTag.setVisibility(View.VISIBLE);
        buttonPlay.setVisibility(View.VISIBLE);
        buttonPause.setVisibility(View.GONE);
        timer.setVisibility(View.GONE);
        CollectionReference fichajesRef = db.collection("users")
                .document(usuario.getUserId())
                .collection("fichajes");

        fichajesRef.get()
                .addOnSuccessListener(querySnapshot ->{
                        if(!querySnapshot.isEmpty()){
                            AtomicInteger completedQueries = new AtomicInteger(0);
                            for (QueryDocumentSnapshot doc: querySnapshot){
                                fichajesRef.document(doc.getId())
                                        .collection("listafichajedia")
                                        .whereNotEqualTo("fechaEmpiezo",null)
                                        .whereEqualTo("fechaFin", null)
                                        .get()
                                        .addOnSuccessListener(subQuerySnapshot -> {
                                            if(subQuerySnapshot != null){
                                                Log.d("HOLA123", "eyyy");
                                                for (QueryDocumentSnapshot subDoc : subQuerySnapshot) {
                                                    activos++;
                                                    String subDocId = subDoc.getId();
                                                    fichajeActual = new Fichaje();
                                                    fichajeActual.setDocumentFechaId(doc.getId());
                                                    fichajeActual.setListaFichajeId(subDocId);
                                                    Log.d("HOLA123", subDocId);

                                                    fichajeActual.setFechaEmpiezo(subDoc.getTimestamp("fechaEmpiezo").toDate());
                                                    iniciarContador();
                                                }

                                                if (completedQueries.incrementAndGet() == querySnapshot.size()) {
                                                    // Todas las consultas han terminado
                                                    if (activos == 0) {


                                                        fichar.setVisibility(View.VISIBLE);
                                                        ficharTag.setVisibility(View.VISIBLE);
                                                        buttonPlay.setVisibility(View.VISIBLE);
                                                        buttonPause.setVisibility(View.GONE);
                                                        timer.setVisibility(View.GONE);
                                                        buttonPlay.setOnClickListener(v -> {
                                                            empezarFichaje();
                                                            setFicharItem();

                                                        });
                                                    }else{
                                                        Log.d("121212", "PUES ESTOY PLAYED :D");
                                                        fichar.setVisibility(View.VISIBLE);
                                                        ficharTag.setVisibility(View.GONE);
                                                        buttonPlay.setVisibility(View.GONE);
                                                        buttonPause.setVisibility(View.VISIBLE);
                                                        timer.setVisibility(View.VISIBLE);
                                                        buttonPause.setOnClickListener(v -> {
                                                            terminarFichaje();
                                                            setFicharItem();

                                                        });


                                                    }
                                                }
                                            }
                                        });
                            }
                        }else{
                            fichar.setVisibility(View.VISIBLE);
                            buttonPause.setVisibility(View.GONE);
                            timer.setVisibility(View.GONE);
                            buttonPlay.setOnClickListener(v -> {
                                empezarFichaje();
                                setFicharItem();
                            });
                        }
                });
    }


    public void empezarFichaje(){
        //NOMBRE DOCUMENTO SERA LA FECHA
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaActual = sdf.format(new Date());
        //NEW FICHAJE
        Map<String, Object> fichaje = new HashMap<>();
        fichaje.put("fechaEmpiezo", Timestamp.now());
        fichaje.put("fechaFin", null);

        //CADA FECHA PUEDE TENER VARIOS FICHAJES
        DocumentReference parentDocRef = db.collection("users")
                .document(usuario.getUserId())
                .collection("fichajes")
                .document(fechaActual);

        Map<String, Object> parentData = new HashMap<>();
        parentData.put("exists", true);

        parentDocRef.set(parentData, SetOptions.merge())
                .addOnCompleteListener(parentTask -> {
                    if (parentTask.isSuccessful()) {
                        parentDocRef.collection("listafichajedia")
                                .add(fichaje)
                                .addOnCompleteListener(subcollectionTask -> {

                                });
                    } else {
                    }
                });
    }

    public void terminarFichaje(){

        Map<String, Object> fichajefin = new HashMap<>();
        fichajefin.put("fechaFin", Timestamp.now());
        detenerContador();
        timer.setText("00:00:00");

        Log.d("121212",fichajeActual.getDocumentFechaId());
        db.collection("users")
                .document(usuario.getUserId())
                .collection("fichajes")
                .document(fichajeActual.getDocumentFechaId())
                .collection("listafichajedia")
                .document(fichajeActual.getListaFichajeId())
                .update(fichajefin);
    }
    public void updateNotificationCount(){
        db.collection("users")
                .document(usuario.getUserId())
                .collection("notificaciones")
                .addSnapshotListener((value, error) -> {
                    if(error !=null)
                        return;
                    obtenerNotificacionesCount();
                });
    }

    public void onResume() {
        super.onResume();
        obtenerNotificacionesCount();
    }

    public void updateItemFichar(){
        db.collection("users")
                .document(usuario.getUserId())
                .collection("fichajes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        doc.getReference().collection("listafichajedia")
                                .addSnapshotListener((value, error) -> {
                                    if(error != null)
                                        return;
                                    setFicharItem();
                                });
                    }
                });

    }

    public void iniciarContador(){
        handler.post(timerRunnable);
    }

    public void detenerContador() {
        handler.removeCallbacks(timerRunnable);
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

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", horas, minutos, segundos);
    }

    public void obtainLastComunicado(){
        db.collection("Comunicados")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    comunicadoList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Comunicado comunicado = new Comunicado(
                                document.getId(),
                                document.getString("id_usuario"),
                                document.getString("titulo"),
                                document.getString("descripcion"),
                                document.getTimestamp("fecha_creacion").toDate()
                        );
                        comunicadoList.add(comunicado);
                    }
                    Collections.sort(comunicadoList, (t1, t2) -> t2.getDateCreation().compareTo(t1.getDateCreation()));
                    lastComunicado = comunicadoList.get(0);
                    setComunicado(lastComunicado);
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error al obtener los comunicados", Toast.LENGTH_SHORT).show());
    }

    public void setComunicado(Comunicado comunicado){

        TextView title, description, fechaCreacion, username;

        ImageView profileImg;

        title = lastComunicadoView.findViewById(R.id.title_comunicado);
        username = lastComunicadoView.findViewById(R.id.username_comunicado);
        description = lastComunicadoView.findViewById(R.id.descripcion_comunicado);
        profileImg = lastComunicadoView.findViewById(R.id.profileImg);
        fechaCreacion = lastComunicadoView.findViewById(R.id.fechacreacion_comunicado);

        title.setText(comunicado.getTitle());
        description.setText(comunicado.getDescription());
        fechaCreacion.setText(obtenerFechaEnString(comunicado.getDateCreation()));
        obtenerDatosUserComunicado(profileImg, username, comunicado.getUser());

    }


    public void obtenerDatosUserComunicado(ImageView profileImg, TextView username, String userId){
        User usuario  = new User();
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    usuario.setUserId(doc.getId());
                    usuario.setNombre(doc.getString("nombre"));
                    usuario.setProfileImage(doc.getString("profile_img_path"));
                    Glide.with(requireContext())
                            .load(usuario.getProfileImage())
                            .centerCrop()
                            .placeholder(R.drawable.defaultavatar)
                            .into(profileImg);

                    username.setText(usuario.getNombre());
                });

    }
    private String obtenerFechaEnString(Date date){
        String fecha = null;
        SimpleDateFormat formatter = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        fecha = formatter.format(date);
        return fecha;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (contador != null) {
            contador.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detenerContador();
    }



}