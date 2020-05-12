package com.example.project.Fragments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project.ConnectionSQLiteHelper;
import com.example.project.MainActivity;
import com.example.project.R;
import com.example.project.Utilidades.Utilidades;

import static com.example.project.Utilidades.Utilidades.CAMPO_CADUCIDAD;
import static com.example.project.Utilidades.Utilidades.CAMPO_ID;
import static com.example.project.Utilidades.Utilidades.CAMPO_MARCA;
import static com.example.project.Utilidades.Utilidades.CAMPO_NOMBRE;
import static com.example.project.Utilidades.Utilidades.CAMPO_PESO_ACTUAL;
import static com.example.project.Utilidades.Utilidades.CAMPO_PESO_INICIAL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_Agregar_Producto.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_Agregar_Producto#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Agregar_Producto extends Fragment {

    //VARIABLES____________________________________
    View vista;
    EditText et_id, et_nombre, et_marca, et_peso_inicial, et_peso_actual, et_caducidad;
    Button boton_agregar;
    Fragment_Lista_productos fragment_lista_productos;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Fragment_Agregar_Producto() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Agregar_Producto.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Agregar_Producto newInstance(String param1, String param2) {
        Fragment_Agregar_Producto fragment = new Fragment_Agregar_Producto();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vista=inflater.inflate(R.layout.fragment_fragment__agregar__producto,container,false);
        et_id=(EditText)vista.findViewById(R.id.et_id);
        et_nombre=(EditText)vista.findViewById(R.id.et_nombre);
        et_marca=(EditText)vista.findViewById(R.id.et_marca);
        et_peso_inicial=(EditText)vista.findViewById(R.id.et_pesoinicial);
        et_peso_actual=(EditText)vista.findViewById(R.id.et_pesoactual);
        et_caducidad=(EditText)vista.findViewById(R.id.et_caducidad);
        boton_agregar=(Button)vista.findViewById(R.id.b_agregar);



        boton_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (/*!et_id.getText().equals("")||*/!et_nombre.getText().equals("")){
                    ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper(getContext(), Utilidades.TABLA_PRODUCTO,null,1);
                    SQLiteDatabase db = conn.getWritableDatabase();

                    String INSERTAR_DATOS_TABLA_MEDICINA = "INSERT INTO "+Utilidades.TABLA_PRODUCTO+
                            " ( "+CAMPO_ID+","
                            +CAMPO_NOMBRE+","
                            +CAMPO_MARCA+","
                            +CAMPO_PESO_INICIAL+","
                            +CAMPO_PESO_ACTUAL+","
                            +CAMPO_CADUCIDAD+") VALUES ("+et_id.getText().toString()+",'"
                            +et_nombre.getText().toString()+"','"
                            +et_marca.getText().toString()+"','"
                            +et_peso_inicial.getText().toString()+"','"
                            +et_peso_actual.getText().toString()+"','"
                            +et_caducidad+"')";

                    db.execSQL(INSERTAR_DATOS_TABLA_MEDICINA);
                    Toast.makeText(getContext(),"Producto Guardado Correctamente",Toast.LENGTH_LONG).show();
                    db.close();
                    //Regresar a la lista
                    ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_contenedor, new Fragment_Lista_productos()).commit();

                }else{
                    Toast.makeText(getView().getContext(), "FALTAN CAMPOS POR LLENAR",Toast.LENGTH_SHORT).show();
                }

            }
        });
        return vista;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
