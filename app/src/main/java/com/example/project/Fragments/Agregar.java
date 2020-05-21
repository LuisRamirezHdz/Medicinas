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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.project.Alarm.AlarmReceiver;
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
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

import static android.content.Context.ALARM_SERVICE;


public class Agregar extends Fragment {
    //Manejar el fragment
    View vista;
    //Elementos del formulario
     EditText nomM, itM, dosiM, vadM, peD, ndrM;
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

    //Numero de días
    int periodoendias;
    String periodo;

    //Time picker

    String finalHour, finalMinute;
    Button btnCambiarHora;
    TextView notificationsTime;
    Calendar today = Calendar.getInstance();


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
        //Se muestra las opciones del periodo
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
                        periodoendias = 1;
                        break;
                    case 1:
                        periodo = " semana(s)";
                        periodoendias=7;
                        break;
                    case 2:
                        periodo = " mese(s)";
                        periodoendias=30;
                        break;
                    case 3:
                        periodo = " año(s)";
                        periodoendias=365;
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
        notificationsTime = (TextView) vista.findViewById(R.id.notifications_time);
        btnCambiarHora = (Button)vista.findViewById(R.id.change_notification);
        btn=(Button)vista.findViewById(R.id.b_agregar);

        //Imagenes
        foto = (ImageView) vista.findViewById(R.id.img_foto);
        foto2 = (ImageView) vista.findViewById(R.id.img_fotoP);
        seleccionar = (Button) vista.findViewById(R.id.btn_selefoto);
        seleccionar2 = (Button) vista.findViewById(R.id.btn_selefotoP);
        storageReference = FirebaseStorage.getInstance().getReference().child("img_envase");
        storageReference2 = FirebaseStorage.getInstance().getReference().child("img_presentacion");
        cargando = new ProgressDialog(getContext());

        inicializarFirebase();
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
        //Agregar el nuevo medicamento
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
                   //Guarda los datos para el mensaje de la notificación
                    SharedPreferences preferences=getActivity().getSharedPreferences("AlarmaMedicamento",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("nombre", n);
                    editor.putString("dosis", d);
                    //Guardo el id con el nombre del medicamento
                    int alarmID = (10000*Integer.parseInt(v)) + (100* Integer.parseInt(finalHour))+Integer.parseInt(finalMinute);
                    editor.putInt(n, alarmID);
                    editor.commit();
                    programarAlarmas(alarmID, v, p);
                    databaseReference.child("Medicamento").child(m.getID()).setValue(m);
                    Toast.makeText(getContext(), "Agregado", Toast.LENGTH_LONG).show();
                    //Regresar a la lista
                    ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_contenedor, new Listado()).commit();
                }
            }
        });
        return vista;
    }

    private void programarAlarmas(int ID, String v, String p) {
        Calendar aux = Calendar.getInstance();
        aux.set(Calendar.MINUTE,  today.get(Calendar.MINUTE));
        aux.set(Calendar.SECOND, 0);
        //Programar las alarmas de acuerdo al periodo de días
         periodoendias= periodoendias * Integer.parseInt(p);

        //Programar las alarmas que habra en un día
        int vecesAlDia=Integer.parseInt(v);
        int incrementoHoras=0, horas=0;
        switch (vecesAlDia){
            case 2:
                horas = 12;
                break;
            case 3:
                horas = 8;
                break;
            case 4:
                horas = 6;
                break;
            case 6:
                horas = 4;
                break;
            case 8:
                horas = 3;
                break;
            case 12:
                horas = 2;
                break;
            default:
                break;
        }
        for (int j=0; j<periodoendias; j++){
            for(int i=0; i<vecesAlDia; i++){
                //Programara la hora en la que sonara de acuerdo al número de veces
                aux.set(Calendar.HOUR_OF_DAY, today.get(Calendar.HOUR_OF_DAY) + incrementoHoras);
                //Programa la alarma
                setAlarm(ID, aux.getTimeInMillis(), vista.getContext());
                //Cambia la hora de la siguiente de ese día
                incrementoHoras+=horas;
                //Cambiar ID
                ID+=1;
            }
            //Programara el día en la que sonara de acuerdo al número de días
            aux.add(Calendar.DAY_OF_YEAR, 1);//Vamos cambiando de día
            //Restablecer hora
            aux.set(Calendar.HOUR_OF_DAY, today.get(Calendar.HOUR_OF_DAY));
        }
    }

    //Confirmar que los campos están vacios
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

    private static  void setAlarm(int i, long timestamp, Context ctx){
        AlarmManager alarmManager = (AlarmManager)
                ctx.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getBroadcast(ctx, i, alarmIntent,
                PendingIntent.FLAG_ONE_SHOT);
        alarmIntent.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
