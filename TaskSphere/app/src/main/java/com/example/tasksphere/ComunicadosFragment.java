package com.example.tasksphere;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tasksphere.modelo.entidad.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComunicadosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComunicadosFragment extends Fragment {

    SharedPreferences sharedPreferences;
    User usuario;

    FirebaseAuth mAuth;

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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        getItems(rootView);
        return rootView;
    }

    private void getItems(View rootView){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        profileImg = rootView.findViewById(R.id.profileimg);
        username = rootView.findViewById(R.id.username);
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


    @Override
    public void onResume() {
        super.onResume();
        obtenerDatosDeUsuario();
        setDatosDeUsuario();
    }
}