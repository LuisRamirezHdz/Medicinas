package com.example.project.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import id.zelory.compressor.Compressor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.project.MainActivity;
import com.example.project.R;
import com.example.project.modelo.Medicamento;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;


public class Agregar extends Fragment {
    //Manejar el fragment
    View vista;
    //Elementos del formulario
    EditText nomM, itM, dosiM, vadM, peD, hrM, ndrM;
    Button btn;
     Spinner spinner;
     static final String[] paths = {"dia(s)", "semana(s)", "mese(s)", "año(s)"};

     //Lo necesario para subir fotos
     Button seleccionar, seleccionar2;
     ImageView foto, foto2;
     StorageReference storageReference;
    StorageReference storageReference2;
     ProgressDialog cargando;
     Bitmap thumb_bitmap = null;
    Bitmap thumb_bitmap2 = null;

     boolean botonimagen=true;

     Uri downloaduri1, downloaduri2;

    //Conexión a firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String periodo;

    public Agregar() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Agregar newInstance(String param1, String param2) {
        Agregar fragment = new Agregar();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista=inflater.inflate(R.layout.fragment_agregar,container,false);

        spinner = (Spinner)vista.findViewById(R.id.spinner);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(vista.getContext(),android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        periodo = " día(s)";
                        break;
                    case 1:
                        periodo = " semana(s)";
                        break;
                    case 2:
                        periodo = " mese(s)";
                        break;
                    case 3:
                        periodo = " año(s)";
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nomM = (EditText)vista.findViewById(R.id.txt_nomM);
        itM = (EditText)vista.findViewById(R.id.txt_itM);
        dosiM =(EditText)vista. findViewById(R.id.txt_dosiM);
        vadM =(EditText)vista. findViewById(R.id.txt_vadM);
        peD =(EditText)vista. findViewById(R.id.txt_pedM);
        ndrM =(EditText)vista. findViewById(R.id.txt_nDrM);
        hrM =(EditText)vista.findViewById(R.id.txt_hrM);
        btn=(Button)vista.findViewById(R.id.b_agregar);

        //Imagen
        foto = (ImageView) vista.findViewById(R.id.img_foto);
        seleccionar = (Button) vista.findViewById(R.id.btn_selefoto);
        //imgref = FirebaseDatabase.getInstance().getReference().child("Fotos subidas");
        storageReference = FirebaseStorage.getInstance().getReference().child("img_envase");
        cargando = new ProgressDialog(getContext());

        //Imagen 2
        foto2 = (ImageView) vista.findViewById(R.id.img_fotoP);
        seleccionar2 = (Button) vista.findViewById(R.id.btn_selefotoP);
        //imgref = FirebaseDatabase.getInstance().getReference().child("Fotos subidas");
        storageReference2 = FirebaseStorage.getInstance().getReference().child("img_presentacion");
        //cargando = new ProgressDialog(getContext());


        inicializarFirebase();
        seleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // CropImage.startPickImageActivity(getActivity());
                Intent intent = CropImage.activity()
                        .setAspectRatio(16,9)
                        .getIntent(getContext());
                botonimagen = true;
                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);



            }
        });



        seleccionar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CropImage.startPickImageActivity(getActivity());
                Intent intent2 = CropImage.activity()
                        .setAspectRatio(16,9)
                        .getIntent(getContext());
                botonimagen = false;
                startActivityForResult(intent2, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);



            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = nomM.getText().toString();
                String p = peD.getText().toString();
                String d = dosiM.getText().toString();
                String v = vadM.getText().toString();
                String nd = ndrM.getText().toString();
                String hr = hrM.getText().toString();
                String i = itM.getText().toString();



                if(n.equals("") || i.equals("")|| d.equals("")||v.equals("")||p.equals("")||nd.equals("")||hr.equals("")){
                    validacion();
                }else {

                    Medicamento m =new Medicamento();
                    m.setID(UUID.randomUUID().toString());
                    m.setNombre(n);
                    m.setIndicacionTerapeutica(i);
                    m.setUrlEnvase(downloaduri1.toString());//Foto
                    m.setUrlPresentacion(downloaduri2.toString());//Foto
                    m.setDosis(d);
                    m.setVecesAlDia(v);
                    m.setPeriodoEnDias(p +periodo);
                    m.setHora(hr);
                    m.setNombreDr(nd);
                    databaseReference.child("Medicamento").child(m.getID()).setValue(m);
                    Toast.makeText(getContext(), "Agregado", Toast.LENGTH_LONG).show();
                    //Regresar a la lista
                    ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_contenedor, new Listado()).commit();




                }

            }
        });




        return vista;
    }

    private void validacion() {
        String n = nomM.getText().toString();
        String p = peD.getText().toString();
        String d = dosiM.getText().toString();
        String v = vadM.getText().toString();
        String nd = ndrM.getText().toString();
        String hr = hrM.getText().toString();
        String i = itM.getText().toString();

        if(n.equals("")){
            nomM.setError("Required");
        }else if(i.equals("")){
            itM.setError("Required");
        }else if(d.equals("")){
            dosiM.setError("Required");
        }else if(v.equals("")){
            vadM.setError("Required");
        }else if(p.equals("")){
            peD.setError("Required");
        }else if(nd.equals("")){
            ndrM.setError("Required");
        }

    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(vista.getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
    //Cuando se seleccione un boton de imagen
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && requestCode== Activity.RESULT_OK){
            Uri imageuri = CropImage.getPickImageResultUri(getContext(), data);
            //Recortar imagen
            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(640,480)
                    .setAspectRatio(2,1)
                    .start(getActivity());
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
                            //originalmente no va el activity
            if(resultCode == Activity.RESULT_OK){
                Uri resultUri = result.getUri();

                File url = new File(resultUri.getPath());

                if(botonimagen==true){
                    Picasso.with(getContext()).load(url).into(foto);
                    //comprimiendo imagen
                    try{
                        thumb_bitmap =new Compressor(vista.getContext())
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(90)
                                .compressToBitmap(url);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,90, byteArrayOutputStream);
                    final byte [] thumb_byte=byteArrayOutputStream.toByteArray();
                    //fin del compresor....
                    int p = (int) (Math.random() *25 +1 ); int s = (int) (Math.random() *25 +1 );
                    int t = (int) (Math.random() *25 +1 ); int c = (int) (Math.random() *25 +1 );
                    int numero1 = (int) (Math.random() *1012 +2111 );
                    int numero2 = (int) (Math.random() *1012 +2111 );

                    String [] elementos = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

                    cargando.setTitle("Subiendo foto...");
                    cargando.setMessage("Espere por favor...");
                    cargando.show();
                    final String nombreAl1 = elementos[p]+elementos[s]+numero1+elementos[t] +elementos[c] + numero2 + "comprimido.jpg";
                    final StorageReference ref = storageReference.child(nombreAl1);
                    UploadTask uploadTask = ref.putBytes(thumb_byte);
                    //Subir imagen en storage...
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw Objects.requireNonNull(task.getException());
                            }
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            downloaduri1 = task.getResult();

                            cargando.dismiss();
                        }
                    });

                }else{
                    Picasso.with(getContext()).load(url).into(foto2);
                    //comprimiendo imagen
                    try{
                        thumb_bitmap2 =new Compressor(vista.getContext())
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(90)
                                .compressToBitmap(url);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumb_bitmap2.compress(Bitmap.CompressFormat.JPEG,90, byteArrayOutputStream);
                    final byte [] thumb_byte=byteArrayOutputStream.toByteArray();
                    //fin del compresor....
                    int p = (int) (Math.random() *25 +1 ); int s = (int) (Math.random() *25 +1 );
                    int t = (int) (Math.random() *25 +1 ); int c = (int) (Math.random() *25 +1 );
                    int numero1 = (int) (Math.random() *1012 +2111 );
                    int numero2 = (int) (Math.random() *1012 +2111 );

                    String [] elementos = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

                    cargando.setTitle("Subiendo foto...");
                    cargando.setMessage("Espere por favor...");
                    cargando.show();
                    final String nombreAl2 = elementos[s]+elementos[p]+numero1+elementos[c] +elementos[t] + numero2 + "comprimido.jpg";
                    final StorageReference ref = storageReference2.child(nombreAl2);
                    UploadTask uploadTask = ref.putBytes(thumb_byte);
                    //Subir imagen en storage...
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw Objects.requireNonNull(task.getException());
                            }
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            downloaduri2 = task.getResult();

                            cargando.dismiss();
                        }
                    });
                }



            }
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
