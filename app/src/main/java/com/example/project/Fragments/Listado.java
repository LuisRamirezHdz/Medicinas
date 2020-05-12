package com.example.project.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.project.modelo.Medicamento;
import com.example.project.R;
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
 * Use the {@link Listado#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Listado extends Fragment {
    View vista;
    //Listar datos
    private List<Medicamento> listMedicamento = new ArrayList<Medicamento>();
    ArrayAdapter<Medicamento> arrayAdapterMedicamento;

    ListView listV_medicamentos;

    //Conexión a firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public Listado() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Listado newInstance() {
        Listado fragment = new Listado();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista=inflater.inflate(R.layout.fragment_listado,container,false);
        listV_medicamentos = (ListView)vista.findViewById(R.id.lv_datosMedicamentos);

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
                    listV_medicamentos.setAdapter(arrayAdapterMedicamento);
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
