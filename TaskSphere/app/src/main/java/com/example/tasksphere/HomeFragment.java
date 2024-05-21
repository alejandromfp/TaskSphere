package com.example.tasksphere;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tasksphere.modelo.entidad.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;


public class HomeFragment extends Fragment {

    SharedPreferences sharedPreferences;
    User usuario;

    String token;

    ConstraintLayout teamItem;
    FirebaseAuth mAuth;

    FirebaseFirestore db;
    ImageView profileImg;
    Button logOut;
    TextView username;

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
        teamItem = rootView.findViewById(R.id.team_item);
        teamItem.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TeamActivity.class);
            requireActivity().startActivity(intent);
        });

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
                .into(profileImg);
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
}