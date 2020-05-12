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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
 * Use the {@link Actualizar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Actualizar extends Fragment {
    View vista;

    EditText nomM, itM, dosiM, vadM, peD,hrM, ndrM;
    //Conexión a firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Medicamento> listMedicamento = new ArrayList<Medicamento>();
    ArrayAdapter<Medicamento> arrayAdapterMedicamento;
    Spinner spinner;
    Medicamento medicamentoSelected;
    Button btn;
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
        hrM =(EditText)vista.findViewById(R.id.txt_hrM);

        btn=(Button)vista.findViewById(R.id.b_actualizar);
        inicializarFirebase();
        listarDatos();


        inicializarFirebase();

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
                    m.setID(medicamentoSelected.getID());
                    m.setNombre(nomM.getText().toString().trim());
                    m.setIndicacionTerapeutica(itM.getText().toString().trim());
                    m.setDosis(dosiM.getText().toString().trim());
                    m.setVecesAlDia(vadM.getText().toString().trim());
                    m.setPeriodoEnDias(peD.getText().toString().trim());
                    m.setHora(hrM.getText().toString().trim());
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
        }else if(hr.equals("")){
            hrM.setError("Required");
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
