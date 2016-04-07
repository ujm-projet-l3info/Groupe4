package com.example.jules.projet2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

public class ItineraireActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String[] poi = new String[] {
            "Ma position",
            "A2",
            "Intersection Parking Sud descente Resto U",
            "A4",
            "A3",
            "A6",
            "A5",
            "A8",
            "A7",
            "Scolarité",
            "Place devant l'entrée principale",
            "Portail Nord",
            "ASTRE",
            "DSI",
            "Intersection parking Est et Nord et BU",
            "Salle de réunion",
            "Chemin vers la BU",
            "A24",
            "Salle de vie étudiante",
            "Distributeurs",
            "Sortie derrière bâtiment H",
            "Portail Sud",
            "Toilettes",
            "F101",
            "Intersection Resto U montée vers Parking Sud",
            "Intersection derrière bâtiment D",
            "Dehors sortie après salle de vie étudiante",
            "Bibliothèque universitaire",
            "Entrée principale",
            "En bas des escaliers après bâtiment A",
            "Bureau professeurs d'Anglais",
            "Entrée bâtiment J",
            "Petite place devant bâtiments H et J",
            "Entrée bâtiment K",
            "Petite place en sortant du bâtiment A vers H J K L",
            "Intersection bâtiment D et salle de vie étudiante",
            "Passage à côté du bâtiment H",
            "Parking Est",
            "Resto U",
            "Portail principal",
            "Parking Sud",
            "Cafet'U",
            "Snack",
            "Couloir vers bâtiment C",
            "En allant vers la cafet'U",
            "F001",
            "H204",
            "H205",
            "H202",
            "H203",
            "Fin des salles A",
            "Intersection passerelle vers bâtiments H F et BU",
            "Couloir vers salles A",
            "Intersection hall d'entrée bâtiment C",
            "Bureau C2I",
            "D203",
            "D202",
            "Salle de repas",
            "D205",
            "D204",
            "D201",
            "Tables à pique-nique",
            "Intersection salles H200 et passerelle vers F",
            "Parking Nord",
            "A13",
            "A15",
            "A14",
            "H206",
            "A16"
    };

    public void recupererItineraire(View button) {
        final EditText dep = (EditText) findViewById(R.id.depart);
        String dep_str = dep.getText().toString();

        final EditText arr = (EditText) findViewById(R.id.arrivee);
        String arr_str = arr.getText().toString();

        //final CheckBox pmr = (CheckBox) findViewById(R.id.PMR);
        boolean bool_pmr = false;//pmr.isChecked();

        Snackbar.make(button, dep_str + " " + arr_str + " " + bool_pmr, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itineraire);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //poi = charger_poi();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, poi);

        AutoCompleteTextView deroule_dep = (AutoCompleteTextView) findViewById(R.id.depart);
        deroule_dep.setAdapter(adapter);

        AutoCompleteTextView deroule_arr = (AutoCompleteTextView) findViewById(R.id.arrivee);
        deroule_arr.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.itineraire, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_carte) {
            Intent i = new Intent(ItineraireActivity.this, MapsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_Calendrier) {
            Intent i = new Intent(ItineraireActivity.this, CalendrierActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_aide) {
            Intent i = new Intent(ItineraireActivity.this, AideActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
