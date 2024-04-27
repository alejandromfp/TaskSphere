package com.example.tasksphere;

import android.app.Dialog;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class TasksFragment extends Fragment  {

    public Button pendientesButton, sinAsignarButton, finalizadasButton, saveNewTask;
    public FloatingActionButton addTask;
    public int[] buttonStatus  = new int[3];
    List<Button> listaBotones = new ArrayList<>();

    LinearLayout sinAsignarContainer, pendientesContainer, finalizadasContainer;

    Dialog dialog;


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
                finalizadasContainer.setVisibility(View.VISIBLE);
                sinAsignarContainer.setVisibility(View.GONE);


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
                finalizadasContainer.setVisibility(View.GONE);
                sinAsignarContainer.setVisibility(View.VISIBLE);

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

        //New Task

        addTask = rootView.findViewById(R.id.add_task_button);
        addTask.setOnClickListener(v -> {
            dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.add_task_layout);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.));

            saveNewTask = dialog.findViewById(R.id.savetask);
            saveNewTask.setOnClickListener(v1 -> {
                //AGREGAR TAREA A BASE DE DATOS TODO

            });
            dialog.show();
        });


    }

    public void mostrarTodos(){
        pendientesContainer.setVisibility(View.VISIBLE);
        finalizadasContainer.setVisibility(View.VISIBLE);
        sinAsignarContainer.setVisibility(View.VISIBLE);
    }
}