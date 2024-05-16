package com.example.tasksphere;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComunicadosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComunicadosFragment extends Fragment {

    SharedPreferences sharedPreferences;
    User usuario;

    FirebaseAuth mAuth;
    Dialog dialog;

    FloatingActionButton addNews;

    Button saveNew;
    FirebaseFirestore db;
    ImageView profileImg;
    TextView username;

    public ComunicadosFragment() {
        // Required empty public constructor
    }


    public static ComunicadosFragment newInstance(String param1, String param2) {
        ComunicadosFragment fragment = new ComunicadosFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_comunicados, container, false);
        getItems(rootView);
        obtenerDatosDeUsuario();
        setDatosDeUsuario();
        return rootView;
    }

    private void getItems(View rootView){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        profileImg = rootView.findViewById(R.id.profileImg);
        profileImg.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
            navController.navigate(R.id.profile_page);
        });
        username = rootView.findViewById(R.id.username);

        //New Task

        addNews = rootView.findViewById(R.id.add_news_button);
        addNews.setOnClickListener(v -> {
            dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.add_news);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            saveNew = dialog.findViewById(R.id.saveButton);
            saveNew.setOnClickListener(v1 -> {
                //AGREGAR TAREA A BASE DE DATOS TODO
                TextInputEditText titleInput = dialog.findViewById(R.id.titleinput);
                TextInputEditText descriptionInput = dialog.findViewById(R.id.descripcioninput);
                guardarComunicado(titleInput.getText().toString(), descriptionInput.getText().toString());
                dialog.dismiss();



            });
            dialog.show();
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


    public void guardarComunicado(String titulo, String descripcion) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference comunicadosRef = db.collection("Comunicados");

        // Crear un nuevo comunicado con los datos proporcionados
        Map<String, Object> comunicado = new HashMap<>();
        comunicado.put("titulo", titulo);
        comunicado.put("descripcion", descripcion);
        comunicado.put("fecha_creacion", FieldValue.serverTimestamp());
        comunicado.put("id_usuario", FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Agregar el comunicado a la colecciÃ³n "comunicados"
        comunicadosRef.add(comunicado)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(requireContext(), "Comunicado guardado correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Error al guardar el comunicado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}