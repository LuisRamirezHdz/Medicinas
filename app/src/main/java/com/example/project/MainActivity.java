package com.example.project;

import android.net.Uri;
import android.os.Bundle;

import com.example.project.Fragments.Consulta;
import com.example.project.Fragments.Eliminar;
import com.example.project.Fragments.Listado;
import com.example.project.Fragments.Agregar;
import com.example.project.Fragments.Actualizar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.project.Fragments.Fragment_Agregar_Producto;
import com.example.project.Fragments.Fragment_Lista_productos;
import com.example.project.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class
MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        Listado.OnFragmentInteractionListener,
        Consulta.OnFragmentInteractionListener,
        Agregar.OnFragmentInteractionListener,
        Actualizar.OnFragmentInteractionListener,
        Eliminar.OnFragmentInteractionListener {

    Listado fragment_listado;
    Consulta fragment_consulta;
    Agregar fragment_agregar;
    Actualizar fragment_actualizar;
    Eliminar fragment_eliminar;

    //Conexión a firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //iniciando los fragments
        fragment_listado = new Listado();
        fragment_consulta = new Consulta();
        fragment_agregar = new Agregar();
        fragment_actualizar = new Actualizar();
        fragment_eliminar = new Eliminar();
        inicializarFirebase();
        //Iniciar en el listado
        getSupportFragmentManager().beginTransaction().add(R.id.frame_contenedor,fragment_listado).commit();

        //Poner icono en action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.medicine);
    }


    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment miFragment = null;
        boolean fragmentSeleccionado = false;

        if (id == R.id.nav_listap) {
            miFragment = new Listado();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_consulta) {
            miFragment = new Consulta();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_agregar) {
            miFragment = new Agregar();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_actualizar) {
            miFragment = new Actualizar();
            fragmentSeleccionado = true;
        }else if (id == R.id.nav_eliminar) {
            miFragment = new Eliminar();
            fragmentSeleccionado = true;
        }

        if (fragmentSeleccionado){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_contenedor,miFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
