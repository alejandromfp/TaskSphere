package com.example.tasksphere;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity implements Animation.AnimationListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView task = findViewById(R.id.task);
        Animation animacion_1 = AnimationUtils.loadAnimation(this, R.anim.splash_task);
        task.startAnimation(animacion_1);

        ImageView sphere = findViewById(R.id.sphere);
        Animation animacion_2 = AnimationUtils.loadAnimation(this, R.anim.splash_sphere);
        sphere.startAnimation(animacion_2);

        ImageView company = findViewById(R.id.company);
        Animation animacion_3 = AnimationUtils.loadAnimation(this, R.anim.splash_company);
        company.startAnimation(animacion_3);

        ImageView iconprincipalapp = findViewById(R.id.iconprincipalapp);
        Animation animacion_4 = AnimationUtils.loadAnimation(this, R.anim.splash_iconapp);
        iconprincipalapp.startAnimation(animacion_4);

        animacion_4.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Intent intent = new Intent(Splash.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}