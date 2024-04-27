package com.example.tasksphere;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class Tasks extends AppCompatActivity {
    public Button pendientesButton, sinAsignarButton, finalizadasButton;
    public int[] buttonStatus  = new int[3];
    List<Button> listaBotones = new ArrayList<>();

    LinearLayout sinAsignarContainer, pendientesContainer, finalizadasContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        //Obteniendo los elementos
        getItems();



    }

    public void selectButton(View view){

        if(view.getId() == R.id.pendientesButton){
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
        } else if(view.getId() == R.id.sinAsignarButton){
            int estado = buttonStatus[1];
            setDefaultButtonStyles(listaBotones);
            if(estado == 0){
                buttonStatus[1] = 1;
                setSelectedButtonStyles(sinAsignarButton);
                //METER PARA QUE SE MUESTRE SOLO LAS PENDIENTES
                pendientesContainer.setVisibility(View.GONE);
                finalizadasContainer.setVisibility(View.VISIBLE);
                sinAsignarContainer.setVisibility(View.GONE);


            }else{
                mostrarTodos();
            }
        } else if(view.getId() == R.id.finalizadasButton){
            int estado = buttonStatus[2];
            setDefaultButtonStyles(listaBotones);
            if(estado == 0){
                buttonStatus[2] = 1;
                setSelectedButtonStyles(finalizadasButton);
                //METER PARA QUE SE MUESTRE SOLO LAS PENDIENTES
                pendientesContainer.setVisibility(View.GONE);
                finalizadasContainer.setVisibility(View.GONE);
                sinAsignarContainer.setVisibility(View.VISIBLE);

            }else{
                mostrarTodos();
            }
        }

    }

    public void setDefaultButtonStyles(List<Button> botones){
        for(Button button : botones){
            button.setTextColor(getColor(R.color.azulOscuro));
            button.setBackgroundColor(getColor(R.color.azulMasClaro));
        }
        buttonStatus[0] = 0;
        buttonStatus[1] = 0;
        buttonStatus[2] = 0;
        }


    public void setSelectedButtonStyles(Button button){
            button.setTextColor(getColor(R.color.white));
            button.setBackgroundColor(getColor(R.color.azulOscuro));

    }

    public void getItems(){
        pendientesButton = findViewById(R.id.pendientesButton);
        sinAsignarButton = findViewById(R.id.sinAsignarButton);
        finalizadasButton = findViewById(R.id.finalizadasButton);
        listaBotones.add(pendientesButton);
        listaBotones.add(sinAsignarButton);
        listaBotones.add(finalizadasButton);
        pendientesContainer = findViewById(R.id.tareas_pendientes_container);
        sinAsignarContainer = findViewById(R.id.tareas_sin_asignar_container);
        finalizadasContainer = findViewById(R.id.tareas_terminadas_container);
    }

    public void mostrarTodos(){
        pendientesContainer.setVisibility(View.VISIBLE);
        finalizadasContainer.setVisibility(View.VISIBLE);
        sinAsignarContainer.setVisibility(View.VISIBLE);
    }
}