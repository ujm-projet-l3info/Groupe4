package com.example.jules.univLocation;

import android.app.AlertDialog;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.view.KeyEvent;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ItineraireActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String[] poiStr;

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

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recupererItineraire();
            }
        });

        final Switch pmr = (Switch) findViewById(R.id.PMR);
        pmr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pmr.isChecked())
                    fab.setImageResource(R.drawable.ic_accessible_white_48dp);
                else
                    fab.setImageResource(R.drawable.ic_directions_walk_white_48dp);
            }
        });

        poiStr = chargerPoi();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, poiStr);

        AutoCompleteTextView deroule_dep = (AutoCompleteTextView) findViewById(R.id.depart);
        deroule_dep.setAdapter(adapter);

        AutoCompleteTextView deroule_etape = (AutoCompleteTextView) findViewById(R.id.etape);
        deroule_etape.setAdapter(adapter);

        final AutoCompleteTextView deroule_arr = (AutoCompleteTextView) findViewById(R.id.arrivee);
        deroule_arr.setAdapter(adapter);

        deroule_arr.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager inputMethodManager = (InputMethodManager) ItineraireActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(deroule_arr.getWindowToken(), 0);

                    recupererItineraire();

                    return true;
                }
                return false;
            }
        });

        Bundle b = getIntent().getExtras();
        if(b != null)
            deroule_arr.setText(b.getString("arrivee"));
    }

    public void recupererItineraire() {
        final EditText dep = (EditText) findViewById(R.id.depart);
        final EditText arr = (EditText) findViewById(R.id.arrivee);
        final EditText etape = (EditText) findViewById(R.id.etape);
        final Switch pmr = (Switch) findViewById(R.id.PMR);

        boolean bool_pmr = pmr.isChecked();

        LocationManager mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if(!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && (dep.getText().toString().equals("Ma position") || etape.getText().toString().equals("Ma position") || arr.getText().toString().equals("Ma position"))){
            showDialogError(-2, "Veuillez activer votre localisation pour utiliser votre position");
            //error.setText("Activez votre localisation");
        }else if(!dep.getText().toString().equals(arr.getText().toString())) {
            boolean presenceEtape;
            if (etape.getText().toString().equals(""))
                presenceEtape = false;
            else
                presenceEtape = true;

            ArrayList<Integer> listeNoeudsDep = null;
            ArrayList<Integer> listeNoeudsArr = null;
            ArrayList<Integer> listeNoeudsEtape = null;

            listeNoeudsDep = MapsActivity.g.cherchePOI(dep.getText().toString());
            listeNoeudsArr = MapsActivity.g.cherchePOI(arr.getText().toString());

            if (presenceEtape)
                listeNoeudsEtape = MapsActivity.g.cherchePOI(etape.getText().toString());
            else
                listeNoeudsEtape = new ArrayList<Integer>();

            if ((!listeNoeudsArr.isEmpty()) && (!listeNoeudsDep.isEmpty()) && (!listeNoeudsEtape.isEmpty() || !presenceEtape)) {
                MapsActivity.b = new Bundle();
                MapsActivity.b.putInt("depart", listeNoeudsDep.get(0));
                MapsActivity.b.putInt("arrivee", listeNoeudsArr.get(0));

                if (presenceEtape)
                    MapsActivity.b.putInt("etape", listeNoeudsEtape.get(0));
                else
                    MapsActivity.b.putInt("etape", -1);

                MapsActivity.b.putBoolean("pmr", bool_pmr);

                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                TextView error = (TextView) findViewById(R.id.erreur);

                if (listeNoeudsArr.isEmpty())
                    showDialogError(2, arr.getText().toString());
                //error.setText("'" + arr.getText().toString() + "' introuvable");

                if (presenceEtape && listeNoeudsEtape.isEmpty())
                    showDialogError(1, etape.getText().toString());
                //error.setText("'" + etape.getText().toString() + "' introuvable");

                if (listeNoeudsDep.isEmpty())
                    showDialogError(0, dep.getText().toString());
                //error.setText("'" + dep.getText().toString() + "' introuvable");
            }
        }else{
            showDialogError(-1, "Les points de départ et d'arrivée doivent être différents");
            //error.setText("Départ et arrivée doivent être différents");
        }
    }

    public void onBackPressed() {
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
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.itineraire, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_carte)
        {
            Intent i = new Intent();
            setResult(ItineraireActivity.this.RESULT_CANCELED, i);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    public static String[] chargerPoi() {
        ArrayList<String> listePOIs = MapsActivity.g.getPOIS();
        listePOIs.add("Ma position");

        String[] poiStr = new String[listePOIs.size()];

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

        return poiStr;
    }

    private void showDialogError(int i, String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setCancelable(true);

        if(s.equals("") && i != 1) {
            builder.setTitle("Erreur d'itinéraire");
            if(i == 0)
                builder.setMessage("Veuillez entrer un lieu de départ");
            if(i == 2)
                builder.setMessage("Veuillez entrer un lieu d'arrivée");
        }else if(i == -1) {
            builder.setTitle("Erreur d'itinéraire");
            builder.setMessage(s);
        }else if(i == -2){
            builder.setTitle("Localisation désactivée");
            builder.setMessage(s);

            builder.setPositiveButton("Activer", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });

            builder.setNegativeButton("Ignorer", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else{
            builder.setTitle("Erreur d'itinéraire");
            builder.setMessage("'" + s + "' n'a pas été trouvé");
        }

        if(i != -2) {
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        AlertDialog alert = builder.create();

        alert.setCanceledOnTouchOutside(true);

        alert.show();
    }
}
