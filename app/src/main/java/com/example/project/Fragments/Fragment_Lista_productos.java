package com.example.project.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.project.ConnectionSQLiteHelper;
import com.example.project.Producto;
import com.example.project.R;
import com.example.project.Utilidades.Utilidades;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_Lista_productos.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_Lista_productos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Lista_productos extends Fragment {

    //editext y botones
    View vista;
    ConnectionSQLiteHelper conn;
    ArrayList<String> lista_Informacion;
    ArrayList <Producto> lista_productos;
    ListView lv;
    Button boton_agregar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Fragment_Lista_productos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Lista_productos.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Lista_productos newInstance(String param1, String param2) {
        Fragment_Lista_productos fragment = new Fragment_Lista_productos();
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
        // Inflate the layout for this fragment

        vista=inflater.inflate(R.layout.fragment_lista_productos,container,false);
        lv=(ListView)vista.findViewById(R.id.lv_productos);
        boton_agregar=(Button)vista.findViewById(R.id.b_agregar);

        conn = new ConnectionSQLiteHelper(vista.getContext(), Utilidades.TABLA_PRODUCTO,null,1);
        LLenar_informacion();

        ArrayAdapter adapter = new ArrayAdapter(vista.getContext(),android.R.layout.simple_list_item_1, lista_Informacion);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String informacion = "Nombre: " + lista_productos.get(position).getNombre() + "\n";
                informacion += "Marca: " + lista_productos.get(position).getMarca() + "\n";
                informacion += "Peso inicial: " + lista_productos.get(position).getPeso_inicial() + "Peso actual: " + lista_productos.get(position).getPeso_actual() + "\n";

                Toast.makeText(vista.getContext(), informacion, Toast.LENGTH_LONG).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id)
            {
                ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper(getContext(), Utilidades.TABLA_PRODUCTO,null,1);
                SQLiteDatabase db = conn.getWritableDatabase();
                //Esto esta en comentarios porque esta en intents y necesitamos pasarlo usando fragments

                /*IntentEditarMedicina.putExtra(Utilidades.CAMPO_ID,lista_medicinas.get(pos).getID());
                IntentEditarMedicina.putExtra(Utilidades.CAMPO_NOMBRE,lista_medicinas.get(pos).getNombre());
                IntentEditarMedicina.putExtra(Utilidades.CAMPO_COMPANIA,lista_medicinas.get(pos).getCompania());
                IntentEditarMedicina.putExtra(Utilidades.CAMPO_HORARIO,lista_medicinas.get(pos).getHorario());
                IntentEditarMedicina.putExtra(Utilidades.CAMPO_DETALLES,lista_medicinas.get(pos).getDetalles());
                startActivity(IntentEditarMedicina);*/
                db.delete(Utilidades.TABLA_PRODUCTO,Utilidades.CAMPO_ID+" = "+lista_productos.get(pos).getID().toString(),null);
                db.close();
                //getFragmentManager().beginTransaction(R.id.frame_contenedor,).commit();

                Toast.makeText(vista.getContext(), "Producto Eliminado", Toast.LENGTH_LONG).show();
                return false;
            }
        });


        return vista;
    }

    private void LLenar_informacion() {
        SQLiteDatabase db = conn.getReadableDatabase();

        Producto miproducto = null;

        lista_productos = new ArrayList<Producto>();

        Cursor cursor = db.rawQuery("SELECT * FROM "+Utilidades.TABLA_PRODUCTO,null);

        while (cursor.moveToNext()){
            miproducto = new Producto("","","","","","");
            miproducto.setID(cursor.getString(0));
            miproducto.setNombre(cursor.getString(1));
            miproducto.setMarca(cursor.getString(2));
            miproducto.setPeso_inicial(cursor.getString(3));
            miproducto.setPeso_actual(cursor.getString(4));
            miproducto.setCaducidad(cursor.getString(5));

            lista_productos.add(miproducto);
        }
        Llenar_Lista_Informacion();
    }

    private void Llenar_Lista_Informacion() {
        lista_Informacion = new ArrayList<String>();

        for (int i=0;i<lista_productos.size();i++){
            lista_Informacion.add("ID: "+lista_productos.get(i).getID()+
                    "\nNombre: "+lista_productos.get(i).getNombre()+
                    "\nMarca: "+lista_productos.get(i).getMarca());
        }
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
