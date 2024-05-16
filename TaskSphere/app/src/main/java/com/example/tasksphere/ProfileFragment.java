package com.example.tasksphere;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.tasksphere.modelo.entidad.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {

    private Uri filePath;
    private final int CODE = 50;

    SharedPreferences sharedPreferences;
    FirebaseStorage storage;

    TextView name, apellidos, dni, fecha_nac, direccion, localidad, biografia, username, userEmail;

    Button editUser;

    Dialog dialog;

    ImageView profileImageView;

    private User usuario;
    FirebaseFirestore db;
    FirebaseUser user;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    CardView profileCard;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        getItems(rootView);
        obtenerDatosDeUsuario();
        setDatosDeUsuario();
        actualizarDatos();

        return rootView;
    }

    private void getItems(View rootView){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        profileCard = rootView.findViewById(R.id.profile_card);
        profileCard.setOnClickListener(v -> {
            uploadImage();
        });
        profileImageView = rootView.findViewById(R.id.profile_image);
        username = rootView.findViewById(R.id.username);
        name = rootView.findViewById(R.id.name_tag);
        apellidos = rootView.findViewById(R.id.apellidos);
        dni = rootView.findViewById(R.id.dni);
        fecha_nac = rootView.findViewById(R.id.fecha_nacimiento);
        direccion = rootView.findViewById(R.id.direccion);
        localidad = rootView.findViewById(R.id.localidad);
        biografia = rootView.findViewById(R.id.biografia);
        userEmail = rootView.findViewById(R.id.useremail);
        editUser = rootView.findViewById(R.id.edit);
        editUser.setOnClickListener(v -> {
            dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.edit_profile);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogSetDatos();

        });


    }

    private void uploadImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent ,"Selecione una foto "), CODE);

    }

    //Esta funcion se llama como resultante de haber escogido una foto de la galeria.
    @Override

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE && resultCode == Activity.RESULT_OK && data !=null && data.getData() != null){
            //OBTENEMOS EL PATH Y MANDAMOS A RECORTAR
            filePath = data.getData();
            Log.d("1234", filePath.toString());
            iniciarUCrop(filePath);



        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK && data != null) {
            Uri croppedUri = UCrop.getOutput(data);
            progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Subiendo foto...");
            progressDialog.setCancelable(false);

            //obtengo la referencia al storage y la carpeta en la que se guardara que sera la de images dentro de la de uid
            StorageReference ref = storageReference.child(user.getUid()+ "/images/" + "profileImage");
            //intentamos subir el archivo ya con la imagen croppeada
            progressDialog.show();
            ref.putFile(croppedUri)
                    //Si se sube correctamente, mostramos un toast y actualizamos base de datos y  el usuario con la url
                    .addOnSuccessListener(taskSnapshot -> {
                        ref.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String url = uri.toString();
                                    String storedUserJson = sharedPreferences.getString("userJson", "");
                                    JSONObject userJsonObj = null;
                                    try {
                                        userJsonObj = new JSONObject(storedUserJson);
                                        userJsonObj.put("profileImage", url);
                                        String modifiedUserJson = userJsonObj.toString();
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("userJson", modifiedUserJson);
                                        editor.apply();

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                    db.collection("users")
                                            .document(user.getUid())
                                            .update("profile_img_path",url)
                                            .addOnSuccessListener(aVoid -> {

                                                Glide.with(requireContext())
                                                        .load(url)
                                                        .into(profileImageView);
                                                Log.d("REFDBPROFILEIMG", "Actualizada la ruta a la imagen");
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("REFERROR", "No se ha podido obtener referencia a la Storage");
                                });
                        Toast.makeText(requireContext(), "Archivo subido correctamente",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(requireContext(), "No se ha podido subir la imagen",Toast.LENGTH_SHORT).show();

                    });
        }
        else
            Toast.makeText(requireContext(), "Archivo no seleccionado",Toast.LENGTH_SHORT).show();
    }

    private void obtenerDatosDeUsuario(){

        sharedPreferences = getActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("userJson", null);
        if(userJson != null){
            Gson gson = new Gson();
            usuario = gson.fromJson(userJson, User.class);

        }


    }

    private void setDatosDeUsuario(){
        username.setText(usuario.getNombre());
        userEmail.setText(usuario.getEmail());
        name.setText(usuario.getNombre());
        apellidos.setText(usuario.getApellidos());
        dni.setText(usuario.getDni());
        direccion.setText(usuario.getDireccion());
        localidad.setText(usuario.getLocalidad());
        fecha_nac.setText(usuario.getFechaNac());
        biografia.setText(usuario.getBiografia());

        if (isAdded() && getContext() != null) {
            Glide.with(requireContext())
                    .load(usuario.getProfileImage())
                    .into(profileImageView);
        }
    }
    private void actualizarDatos(){
        db.collection("users")
                .document(user.getUid())
                .addSnapshotListener((value, error) -> {
                    //actualizo la imagen
                    if(error != null){
                        return;
                    }
                    obtenerDatosFromDatabase();
                });
    }


    //Funcion que inicia una activity para que al subir la imagen, se tenga que recortar en cuadrado.
    private void iniciarUCrop(Uri sourceUri) {
        String destinationFileName = "cropped_image"; // Nombre del archivo de salida
        UCrop.Options options = new UCrop.Options();

        options.setCompressionQuality(90); // Calidad de compresión
        options.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.azulMasClaro)); // Color de la barra de herramientas
        options.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.azulMasClaro)); // Color de la barra de estado
        options.withAspectRatio(1, 1);
        options.withMaxResultSize(250, 250);
        UCrop uCrop = UCrop.of(sourceUri, Uri.fromFile(new File(requireContext().getCacheDir(), destinationFileName)))
                .withOptions(options);


        uCrop.start(requireContext(), this);
    }

    public void obtenerDatosFromDatabase(){
        db.collection("users")
                .document(usuario.getUserId())
                .get()
                .addOnSuccessListener(doc -> {
                    usuario.setUserId(doc.getId());
                    usuario.setNombre(doc.getString("nombre"));
                    usuario.setApellidos(doc.getString("apellidos"));
                    usuario.setDireccion(doc.getString("direccion"));
                    usuario.setDni(doc.getString("dni"));
                    usuario.setLocalidad(doc.getString("ciudad"));
                    usuario.setEmail(user.getEmail());
                    usuario.setRol(doc.getString("rol"));
                    usuario.setFechaNac(doc.getString("fechaNacimiento"));
                    usuario.setTelefono(doc.getString("telefono"));
                    usuario.setBiografia(doc.getString("biografia"));
                    usuario.setProfileImage(doc.getString("profile_img_path"));
                    setDatosDeUsuario();
                });
    }

    public void dialogSetDatos(){
        EditText name, apellidos, fechaNac, direccion, localidad, biografia;
        Button saveChanges;
        name = dialog.findViewById(R.id.name);
        apellidos = dialog.findViewById(R.id.apellidos);
        fechaNac = dialog.findViewById(R.id.fecha_nacimiento);
        direccion = dialog.findViewById(R.id.direccion);
        localidad = dialog.findViewById(R.id.localidad);
        biografia = dialog.findViewById(R.id.biografia);

        name.setText(usuario.getNombre());
        apellidos.setText(usuario.getApellidos());
        fechaNac.setText(usuario.getFechaNac());
        direccion.setText(usuario.getDireccion());
        localidad.setText(usuario.getLocalidad());
        biografia.setText(usuario.getBiografia());
        saveChanges = dialog.findViewById(R.id.saveChanges);
        saveChanges.setOnClickListener(v -> {
            if(validarCampos(name,apellidos,fechaNac,direccion,localidad,biografia))
                guardarCambios(name, apellidos, fechaNac, direccion,localidad,biografia);
        });

        dialog.show();
    }

    public void guardarCambios(
            EditText name, EditText apellidos,
            EditText fechaNac, EditText direccion,
            EditText localidad, EditText biografia){
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", name.getText().toString());
        userData.put("apellidos", apellidos.getText().toString());
        userData.put("direccion", direccion.getText().toString());
        userData.put("ciudad", localidad.getText().toString());
        userData.put("fechaNacimiento", fechaNac.getText().toString());
        userData.put("biografia", biografia.getText().toString());



        db.collection("users")
                .document(usuario.getUserId())
                .update(userData)
                .addOnSuccessListener(command -> {
                    Toast.makeText(requireContext(), "Datos actualizados correctamente",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "No se han podido actualizar los datos",Toast.LENGTH_SHORT).show();
                });

    }

    public boolean validarCampos(
            EditText name, EditText apellidos,
            EditText fechaNac, EditText direccion,
            EditText localidad, EditText biografia) {
        boolean result = false;
        if (name.getText().toString().isEmpty()) {
            name.setError("Campo obligatorio");
        } else if (apellidos.getText().toString().isEmpty()) {
            apellidos.setError("Campo obligatorio");
        } else if (direccion.getText().toString().isEmpty()) {
            direccion.setError("Campo obligatorio");
        } else if (localidad.getText().toString().isEmpty()) {
            localidad.setError("Campo obligatorio");
        } else if (fechaNac.toString().isEmpty() || !fechaNac.getText().toString().matches("\\d{2}/\\d{2}/\\d{4}")) {
            fechaNac.setError("La fecha de nacimiento debe estar en formato DD/MM/AAAA");
        } else
            result = true;

        return result;
    }
}

