package com.example.jules.univLocation;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class ItineraireActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private String[] poiStr;

    public void recupererItineraire()
    {
        final EditText dep = (EditText) findViewById(R.id.depart);
        final EditText arr = (EditText) findViewById(R.id.arrivee);
        final Switch pmr = (Switch) findViewById(R.id.PMR);

        boolean bool_pmr = pmr.isChecked();

        ArrayList<Integer> listeNoeudsDep = MapsActivity.g.cherchePOI(dep.getText().toString());
        ArrayList<Integer> listeNoeudsArr = MapsActivity.g.cherchePOI(arr.getText().toString());

        if((!listeNoeudsArr.isEmpty()) && (!listeNoeudsDep.isEmpty()))
        {
            Intent i = new Intent();
            Bundle b = new Bundle();

            b.putInt("depart", listeNoeudsDep.get(0));
            b.putInt("arrivee", listeNoeudsArr.get(0));
            b.putBoolean("pmr", bool_pmr);

            i.putExtras(b);
            setResult(Activity.RESULT_OK, i);
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);

            finish();
        }
        else
        {
            TextView error = (TextView) findViewById(R.id.erreur);

            if(listeNoeudsDep.isEmpty())
                error.setText(dep.getText().toString() + "' introuvable");

            if(listeNoeudsArr.isEmpty())
                error.setText(arr.getText().toString() + "' introuvable");
        }
    }

    protected void onCreate(Bundle savedInstanceState)
    {
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);


        chargerPoi();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, poiStr);

        AutoCompleteTextView deroule_dep = (AutoCompleteTextView) findViewById(R.id.depart);
        deroule_dep.setAdapter(adapter);

        AutoCompleteTextView deroule_arr = (AutoCompleteTextView) findViewById(R.id.arrivee);
        deroule_arr.setAdapter(adapter);
    }

    public void onClick(View view)
    {
        recupererItineraire();
    }

    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            Intent i = new Intent();
            setResult(ItineraireActivity.this.RESULT_CANCELED, i);
            finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.itineraire, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_carte)
        {
            Intent i = new Intent();
            setResult(ItineraireActivity.this.RESULT_CANCELED, i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
        else if (id == R.id.nav_Calendrier)
        {
            Intent i = new Intent(ItineraireActivity.this, CalendrierActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        else if (id == R.id.nav_aide)
        {
            Intent i = new Intent(ItineraireActivity.this, AideActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }

        return true;
    }

    public void chargerPoi()
    {
        ArrayList<String> listePOIs = MapsActivity.g.getPOIS();

        poiStr = new String[listePOIs.size()];

        for(int i = 0; i < listePOIs.size(); i++) // Recuperation dans tableau
        {
            poiStr[i] = listePOIs.get(i);
        }

        for (int i = 1 ; i < poiStr.length; i++) // Tri par ordre alphabetique
        {
            for(int j = i; j > 0; j--)
            {
                if(poiStr[j].compareTo(poiStr[j - 1]) < 0) // Echange
                {
                    String tmpS = poiStr[j];
                    poiStr[j] = poiStr[j - 1];
                    poiStr[j - 1] = tmpS;
                }
            }
        }
    }
}
