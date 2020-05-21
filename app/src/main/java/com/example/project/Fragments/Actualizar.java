package com.example.project.Fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project.Alarm.AlarmReceiver;
import com.example.project.MainActivity;
import com.example.project.R;
import com.example.project.modelo.Medicamento;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static android.content.Context.ALARM_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Actualizar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Actualizar extends Fragment {
    View vista;

    EditText nomM, itM, dosiM, vadM, peD, ndrM;
    ImageView imgM, img2;
    //Conexión a firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Medicamento> listMedicamento = new ArrayList<Medicamento>();
    ArrayAdapter<Medicamento> arrayAdapterMedicamento;
    Spinner spinner;
    Medicamento medicamentoSelected;
    Button btn;

    //Lo necesario para subir fotos
    Button seleccionar, seleccionar2;
    StorageReference storageReference;
    StorageReference storageReference2;
    ProgressDialog cargando;
    Bitmap thumb_bitmap = null;
    Bitmap thumb_bitmap2 = null;
    boolean botonimagen=true;
    Uri downloaduri1, downloaduri2;


    //Time picker

    String finalHour, finalMinute;
    Button btnCambiarHora;
    TextView notificationsTime;
    Calendar today = Calendar.getInstance();

    //foto
    int nofoto1=0, nofoto2=0;

    public Actualizar() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Actualizar newInstance(String param1, String param2) {
        Actualizar fragment = new Actualizar();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista=inflater.inflate(R.layout.fragment_actualizar,container,false);
        spinner = (Spinner)vista.findViewById(R.id.spinner);

        nomM = (EditText)vista.findViewById(R.id.txt_nomM);
        itM = (EditText)vista.findViewById(R.id.txt_itM);
        dosiM =(EditText)vista. findViewById(R.id.txt_dosiM);
        vadM =(EditText)vista. findViewById(R.id.txt_vadM);
        peD =(EditText)vista. findViewById(R.id.txt_pedM);
        ndrM =(EditText)vista. findViewById(R.id.txt_nDrM);
        notificationsTime = (TextView) vista.findViewById(R.id.notifications_time);
        btnCambiarHora = (Button)vista.findViewById(R.id.change_notification);
        imgM=(ImageView)vista.findViewById(R.id.img_foto);
        img2=(ImageView)vista.findViewById(R.id.img_foto2);

        //Imagen
        seleccionar = (Button) vista.findViewById(R.id.btn_selefoto);
        storageReference = FirebaseStorage.getInstance().getReference().child("img_envase");
        cargando = new ProgressDialog(getContext());

        //Imagen 2+
        seleccionar2 = (Button) vista.findViewById(R.id.btn_selefotoP);
        storageReference2 = FirebaseStorage.getInstance().getReference().child("img_presentacion");


        btn=(Button)vista.findViewById(R.id.b_actualizar);
        inicializarFirebase();
        listarDatos();
        //*********************************   BOTONES ************************************************
        //Para seleccionar la primera imagen
        seleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CropImage.activity()
                        .setAspectRatio(16,9)
                        .getIntent(getContext());
                botonimagen = true;
                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        //Para seleccionar la segunda imagen
        seleccionar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = CropImage.activity()
                        .setAspectRatio(16,9)
                        .getIntent(getContext());
                botonimagen = false;
                startActivityForResult(intent2, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        //Time picker
        btnCambiarHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(vista.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        finalHour = "" + selectedHour;
                        finalMinute = "" + selectedMinute;
                        if (selectedHour < 10) finalHour = "0" + selectedHour;
                        if (selectedMinute < 10) finalMinute = "0" + selectedMinute;
                        notificationsTime.setText(finalHour + ":" + finalMinute);
                        today.set(Calendar.HOUR_OF_DAY, selectedHour);
                        today.set(Calendar.MINUTE, selectedMinute);
                        today.set(Calendar.SECOND, 0);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.select_time));
                mTimePicker.show();
            }
        });
        //Actualizar el medicamento
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = nomM.getText().toString();
                String p = peD.getText().toString();
                String d = dosiM.getText().toString();
                String v = vadM.getText().toString();
                String nd = ndrM.getText().toString();
                String hr = notificationsTime.getText().toString();
                String i = itM.getText().toString();
                if(n.equals("") || i.equals("")|| d.equals("")||v.equals("")||p.equals("")||nd.equals("")||hr.equals("")){
                    validacion();
                }else {
                    Medicamento m =new Medicamento();
                    m.setID(medicamentoSelected.getID());
                    m.setNombre(nomM.getText().toString().trim());
                    m.setIndicacionTerapeutica(itM.getText().toString().trim());
                    if(nofoto1==0){
                        m.setUrlEnvase(medicamentoSelected.getUrlEnvase());//Foto
                    }else {
                        m.setUrlEnvase(downloaduri1.toString());//Foto
                    }
                    if(nofoto2==0){
                        m.setUrlPresentacion(medicamentoSelected.getUrlPresentacion());//Foto
                    }else {
                        m.setUrlPresentacion(downloaduri2.toString());//Foto
                    }
                    m.setDosis(dosiM.getText().toString().trim());
                    m.setVecesAlDia(vadM.getText().toString().trim());
                    m.setPeriodoEnDias(peD.getText().toString().trim());
                    m.setHora(notificationsTime.getText().toString().trim());
                    m.setNombreDr(ndrM.getText().toString().trim());
                    databaseReference.child("Medicamento").child(m.getID()).setValue(m);
                    Toast.makeText(getContext(), "Actualizado", Toast.LENGTH_LONG).show();

                    //limpiarCajas();
                    //Regresar a la lista
                    ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_contenedor, new Listado()).commit();

                }

            }
        });

        // Inflate the layout for this fragment
        return vista;
    }

    private void validacion() {
        String n = nomM.getText().toString();
        String p = peD.getText().toString();
        String d = dosiM.getText().toString();
        String v = vadM.getText().toString();
        String nd = ndrM.getText().toString();
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
        /*Implementar persistencia(solo funciona con con un activity)
        firebaseDatabase.setPersistenceEnabled(true);*/
        databaseReference = firebaseDatabase.getReference();
    }
    private void listarDatos() {
        databaseReference.child("Medicamento").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listMedicamento.clear();
                for(DataSnapshot objSnaptshop : dataSnapshot.getChildren()){
                    //Regresa el toString, por el momento solo es el nombre
                    Medicamento p = objSnaptshop.getValue(Medicamento.class);
                    listMedicamento.add(p);

                    arrayAdapterMedicamento = new ArrayAdapter<Medicamento>(vista.getContext(), android.R.layout.simple_list_item_1, listMedicamento);
                    spinner.setAdapter(arrayAdapterMedicamento);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            medicamentoSelected = (Medicamento) parent.getItemAtPosition(position);
                            nomM.setText(medicamentoSelected.getNombre());
                            itM.setText(medicamentoSelected.getIndicacionTerapeutica());
                            Uri descargarFoto = Uri.parse(medicamentoSelected.getUrlEnvase());
                            Glide.with(getActivity())
                                    .load(descargarFoto)
                                    .fitCenter()
                                    .centerCrop()
                                    .into(imgM);
                            Uri descargarFoto2 = Uri.parse(medicamentoSelected.getUrlPresentacion());
                            Glide.with(getActivity())
                                    .load(descargarFoto2)
                                    .fitCenter()
                                    .centerCrop()
                                    .into(img2);
                            dosiM.setText(medicamentoSelected.getDosis());
                            vadM.setText(medicamentoSelected.getVecesAlDia());
                            notificationsTime.setText(medicamentoSelected.getHora());
                            peD.setText(medicamentoSelected.getPeriodoEnDias());
                            ndrM.setText(medicamentoSelected.getNombreDr());

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            itM.setText("-");
                            dosiM.setText("-");
                            vadM.setText("-");
                            peD.setText("-");
                            ndrM.setText("");
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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


                if(botonimagen==true){
                    Uri resultUri = result.getUri();
                    nofoto1=1;
                    File url = new File(resultUri.getPath());
                    Picasso.with(getContext()).load(url).into(imgM);
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

                }else if(botonimagen == false){
                    Uri resultUri = result.getUri();
                    nofoto2=1;
                    File url = new File(resultUri.getPath());
                    Picasso.with(getContext()).load(url).into(img2);
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
