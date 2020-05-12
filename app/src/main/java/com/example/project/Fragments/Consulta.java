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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ListView;
import android.widget.TextView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Consulta#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Consulta extends Fragment {
    View vista;

    TextView itM,  dosiM, vadM, peD, ndrM, hrM;

    //Conexión a firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Medicamento> listMedicamento = new ArrayList<Medicamento>();
    ArrayAdapter<Medicamento> arrayAdapterMedicamento;
     Spinner spinner;
    Medicamento medicamentoSelected;

    public Consulta() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Consulta newInstance(String param1, String param2) {
        Consulta fragment = new Consulta();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista=inflater.inflate(R.layout.fragment_consulta,container,false);
        spinner = (Spinner)vista.findViewById(R.id.spinner);

        itM = (TextView) vista.findViewById(R.id.txt_itM);
        dosiM =(TextView)vista. findViewById(R.id.txt_dosiM);
        vadM =(TextView)vista. findViewById(R.id.txt_vadM);
        peD =(TextView)vista. findViewById(R.id.txt_pedM);
        ndrM =(TextView)vista. findViewById(R.id.txt_nDrM);
        hrM =(TextView)vista. findViewById(R.id.txt_hrM);

        inicializarFirebase();
        listarDatos();

        // Inflate the layout for this fragment
        return vista;
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
                            itM.setText(medicamentoSelected.getIndicacionTerapeutica());
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
