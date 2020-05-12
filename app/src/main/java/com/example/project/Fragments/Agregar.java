package com.example.project.Fragments;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.project.ConnectionSQLiteHelper;
import com.example.project.MainActivity;
import com.example.project.R;
import com.example.project.Utilidades.Utilidades;
import com.example.project.modelo.Medicamento;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import static com.example.project.Utilidades.Utilidades.CAMPO_CADUCIDAD;
import static com.example.project.Utilidades.Utilidades.CAMPO_ID;
import static com.example.project.Utilidades.Utilidades.CAMPO_MARCA;
import static com.example.project.Utilidades.Utilidades.CAMPO_NOMBRE;
import static com.example.project.Utilidades.Utilidades.CAMPO_PESO_ACTUAL;
import static com.example.project.Utilidades.Utilidades.CAMPO_PESO_INICIAL;

public class Agregar extends Fragment {
    View vista;

    EditText nomM, itM, dosiM, vadM, peD, hrM, ndrM;
    Button btn;
     Spinner spinner;
     static final String[] paths = {"dia(s)", "semana(s)", "mese(s)", "año(s)"};
    //Conexión a firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String periodo;

    public Agregar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Agregar.
     */
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
                    m.setID(UUID.randomUUID().toString());
                    m.setNombre(n);
                    m.setIndicacionTerapeutica(i);
                    m.setDosis(d);
                    m.setVecesAlDia(v);
                    m.setPeriodoEnDias(p +periodo);
                    m.setHora(hr);
                    m.setNombreDr(nd);
                    databaseReference.child("Medicamento").child(m.getID()).setValue(m);
                    Toast.makeText(getContext(), "Agregado", Toast.LENGTH_LONG).show();
                    //limpiarCajas();
                    //Regresar a la lista
                    ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_contenedor, new Listado()).commit();

                }

            }
        });
        return vista;
    }
    private void limpiarCajas() {
        nomM.setText("");
        peD.setText("");
        dosiM.setText("");
        vadM.setText("");
        ndrM.setText("");
        itM.setText("");
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
        /*Implementar persistencia(solo funciona con con un activity)
        firebaseDatabase.setPersistenceEnabled(true);*/
        databaseReference = firebaseDatabase.getReference();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
