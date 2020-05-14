package com.example.project.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project.MainActivity;
import com.example.project.R;
import com.example.project.modelo.Medicamento;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Eliminar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Eliminar extends Fragment {
    View vista;

    TextView itM,  dosiM, vadM, peD, ndrM, hrM;
    ImageView imgM, img2;
    Button btn;
    //Conexión a firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Medicamento> listMedicamento = new ArrayList<Medicamento>();
    ArrayAdapter<Medicamento> arrayAdapterMedicamento;
    Spinner spinner;
    Medicamento medicamentoSelected;

    public Eliminar() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Eliminar newInstance(String param1, String param2) {
        Eliminar fragment = new Eliminar();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista=inflater.inflate(R.layout.fragment_eliminar,container,false);
        spinner = (Spinner)vista.findViewById(R.id.spinner);

        itM = (TextView) vista.findViewById(R.id.txt_itM);
        dosiM =(TextView)vista. findViewById(R.id.txt_dosiM);
        vadM =(TextView)vista. findViewById(R.id.txt_vadM);
        peD =(TextView)vista. findViewById(R.id.txt_pedM);
        ndrM =(TextView)vista. findViewById(R.id.txt_nDrM);
        hrM =(TextView)vista. findViewById(R.id.txt_hrM);
        imgM=(ImageView)vista.findViewById(R.id.img_foto);
        img2=(ImageView)vista.findViewById(R.id.img_foto2);


        btn=(Button)vista.findViewById(R.id.b_eliminar);
        inicializarFirebase();
        listarDatos();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Medicamento m =new Medicamento();
                m.setID(medicamentoSelected.getID());
                databaseReference.child("Medicamento").child(m.getID()).removeValue();
                Toast.makeText(getContext(), "Medicamento eliminado", Toast.LENGTH_LONG).show();
                //Regresar a la lista
                ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_contenedor, new Listado()).commit();



            }
        });

        // Inflate the layout for this fragment
        return vista;
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(vista.getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
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
                            hrM.setText(medicamentoSelected.getHora());
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
                            hrM.setText("");
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
