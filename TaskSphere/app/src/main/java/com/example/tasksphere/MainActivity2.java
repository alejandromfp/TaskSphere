package com.example.tasksphere;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tasksphere.modelo.entidad.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class MainActivity2 extends AppCompatActivity {

    User usuario;
    FirebaseAuth mAuth;

    SharedPreferences sharedPreferences;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setupNavigation();
        obtenerDatosDeUsuario();
        comprobarRol();
    }


    private void setupNavigation(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_hostContainer);
        NavigationUI.setupWithNavController(
                bottomNavigationView,
                navHostFragment.getNavController()
        );
    }

    private void obtenerDatosDeUsuario(){
        sharedPreferences = this.getSharedPreferences("usuario", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("userJson", "uwu");
        Log.d("JSON", userJson);
        if(userJson != null) {
            Gson gson = new Gson();
            usuario = gson.fromJson(userJson, User.class);

        }
    }

    private void comprobarRol(){

        if(usuario.getRol().isEmpty() || usuario.getRol() == null || usuario.getRol().equals("Sin asignar")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Lo sentimos, no tienes permisos para acceder a la aplicación, Un administrador queda a la espera de asignarte un rol");
            builder.setCancelable(false);
            builder.show();
        }

    }


}