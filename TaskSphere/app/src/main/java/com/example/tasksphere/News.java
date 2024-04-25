package com.example.tasksphere;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class News extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        LinearLayout novedadesContainer = findViewById(R.id.novedades_container);

        List<Noticia> noticias = obtenerListaDeNoticias();

        for (Noticia noticia : noticias) {
            LinearLayout itemNoticia = (LinearLayout) getLayoutInflater().inflate(R.layout.item_news, null);

            TextView textViewTitle = itemNoticia.findViewById(R.id.textViewTitle);
            TextView textViewDescription = itemNoticia.findViewById(R.id.textViewDescription);

            textViewTitle.setText(noticia.getTitulo());
            textViewDescription.setText(noticia.getDescripcion());

            novedadesContainer.addView(itemNoticia);
        }
    }

    private List<Noticia> obtenerListaDeNoticias() {
        List<Noticia> noticias = new ArrayList<>();
        noticias.add(new Noticia("Título de la Noticia 1", "Descripción de la noticia 1"));
        noticias.add(new Noticia("Título de la Noticia 2", "Descripción de la noticia 2"));
        noticias.add(new Noticia("Título de la Noticia 3", "Descripción de la noticia 3"));
        noticias.add(new Noticia("Título de la Noticia 4", "Descripción de la noticia 4"));
        return noticias;
    }
}
