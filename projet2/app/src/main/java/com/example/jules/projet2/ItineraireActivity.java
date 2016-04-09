package com.example.jules.projet2;

import android.content.res.XmlResourceParser;
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
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import graphe.Graphe;
import graphe.Noeud;
import parseur.ParseurGrapheXML;

public class ItineraireActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Graphe g;
    private ParseurGrapheXML p;
    private String[] poiStr;
    private int[] poiInt;

    public void recupererItineraire(View button) {
        final EditText dep = (EditText) findViewById(R.id.depart);
        String dep_str = dep.getText().toString();

        final EditText arr = (EditText) findViewById(R.id.arrivee);
        String arr_str = arr.getText().toString();

        //final CheckBox pmr = (CheckBox) findViewById(R.id.PMR);
        boolean bool_pmr = false;//pmr.isChecked();

        Intent i = new Intent(ItineraireActivity.this, MapsActivity.class);
        Bundle b = new Bundle();

        int depInt = -1;
        int arrInt = -1;

        for(int k = 0; k < poiStr.length; k++){
            if(poiStr[k].equals(dep_str))
                depInt = poiInt[k];
        }

        for(int k = 0; k < poiStr.length; k++){
            if(poiStr[k].equals(arr_str))
                arrInt = poiInt[k];
        }

        if(depInt != -1 && arrInt != -1) {
            b.putInt("depart", depInt);
            b.putInt("arrivee", arrInt);
            b.putBoolean("pmr", bool_pmr);
            i.putExtras(b);

            startActivity(i);

            this.finish();
        }else{
            TextView error = (TextView) findViewById(R.id.erreur);

            if(arrInt == -1)
                error.setText("'" + arr_str + "' n'est pas un point d'intérêt");

            if(depInt == -1)
                error.setText("'" + dep_str + "' n'est pas un point d'intérêt");
        }
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

        // Charger graphe
        try {
            getXMLfromResource();
        }
        catch (IOException | XmlPullParserException e)
        {
            e.printStackTrace();
        }
        chargerPoi();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, poiStr);

        AutoCompleteTextView deroule_dep = (AutoCompleteTextView) findViewById(R.id.depart);
        deroule_dep.setAdapter(adapter);

        AutoCompleteTextView deroule_arr = (AutoCompleteTextView) findViewById(R.id.arrivee);
        deroule_arr.setAdapter(adapter);
    }

    public void chargerPoi() {
        ArrayList<String> POIs = g.getPOIS();

        poiStr = new String[POIs.size()];
        poiInt = new int[POIs.size()];

        for(int i = 0; i < POIs.size(); i++) {
            poiStr[i] = POIs.get(i);
            poiInt[i] = g.cherchePOIExact(poiStr[i]);
        }

        for (int i = 1; i < poiStr.length; i++){
            for(int j = i; j > 0; j--){
                if(poiStr[j].compareTo(poiStr[j - 1]) < 0){
                    String tmpS = poiStr[j];
                    poiStr[j] = poiStr[j - 1];
                    poiStr[j - 1] = tmpS;

                    int tmpI = poiInt[j];
                    poiInt[j] = poiInt[j - 1];
                    poiInt[j - 1] = tmpI;
                }
            }
        }
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
            this.finish();
        } else if (id == R.id.nav_Calendrier) {
            Intent i = new Intent(ItineraireActivity.this, CalendrierActivity.class);
            startActivity(i);
            this.finish();
        } else if (id == R.id.nav_aide) {
            Intent i = new Intent(ItineraireActivity.this, AideActivity.class);
            startActivity(i);
            this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getXMLfromResource() throws IOException, XmlPullParserException {
        // Create ResourceParser for XML file
        XmlResourceParser xpp = getResources().getXml(R.xml.metare);

        g = new Graphe();
        Noeud n = null;
        float latitude = 0;
        float longitude = 0;
        char batiment ='-';
        String POI = "";
        int voisin = -1;
        int voisin_pmr = -1;

        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_TAG)
            {
                if(xpp.getName().equals("noeud")) { n = new Noeud(); }
                else if(xpp.getName().equals("latitude")) { eventType = xpp.next(); latitude = Float.parseFloat(xpp.getText()); }
                else if(xpp.getName().equals("longitude")) { eventType = xpp.next(); longitude = Float.parseFloat(xpp.getText()); }
                else if(xpp.getName().equals("batiment")) { eventType = xpp.next(); batiment = xpp.getText().charAt(0); }
                else if(xpp.getName().equals("POI")) { eventType = xpp.next(); POI = xpp.getText(); }
                else if(xpp.getName().equals("voisin")) { eventType = xpp.next(); voisin = Integer.parseInt(xpp.getText()); }
                else if(xpp.getName().equals("voisins_PMR")) { eventType = xpp.next(); Integer.parseInt(xpp.getText()); }
            }
            else if(eventType == XmlPullParser.END_TAG)
            {
                if(xpp.getName().equals("noeud")) { g.ajouterNoeud(n); }
                else if(xpp.getName().equals("latitude")) { n.setLatitude(latitude); }
                else if(xpp.getName().equals("longitude")) { n.setLongitude(longitude); }
                else if(xpp.getName().equals("batiment")) { n.setBatiment(batiment); }
                else if(xpp.getName().equals("POI")) { n.ajouterPOI(POI); }
                else if(xpp.getName().equals("voisin")) { n.ajouterVoisin(voisin); }
                else if(xpp.getName().equals("voisins_PMR")) { n.ajouterVoisinPMR(voisin_pmr); }
            }

            eventType = xpp.next();
        }
        // indicate app done reading the resource.
        xpp.close();
    }
}
