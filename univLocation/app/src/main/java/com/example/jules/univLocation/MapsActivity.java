package com.example.jules.univLocation;

import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import graphe.*;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {


    private GoogleMap mMap;
    public static Graphe g;
    private GroundOverlayOptions carteFac;
    private ArrayList<PolylineOptions> lignes = new ArrayList<PolylineOptions>();
    private int depart = -1;
    private int arrivee = -1;
    private int etape = -1;
    private int niveau = 0;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Charger graphe
        try {
            parseGraphe();
        }
        catch (IOException | XmlPullParserException e)
        {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode , int resultCode , Intent data)
    {
        if (requestCode == 1)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                Bundle b = data.getExtras();
                tracerItineraire(b);
            }
            if (resultCode == Activity.RESULT_CANCELED)
            {
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }

    public void tracerItineraire(Bundle b)
    {
        if(b != null)
        {
            mMap.clear();

            Button changer_type = (Button) findViewById(R.id.type_carte);
            if(changer_type.getText().equals("Satellite"))
                placerCalque();

            depart = b.getInt("depart");
            arrivee = b.getInt("arrivee");
            etape = b.getInt("etape");
            boolean pmr = b.getBoolean("pmr");

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(g.noeuds.get(depart).getLat(), g.noeuds.get(depart).getLon()),18));

            ArrayList<Integer> l = new ArrayList<>(); // Ajout de checkpoint pour itineraire

            l.add(depart);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(g.noeuds.get(depart).getLat(), g.noeuds.get(depart).getLon()))
                    .title(g.noeuds.get(depart).POIs.get(0)));

            if(etape != -1) {
                l.add(etape);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(g.noeuds.get(etape).getLat(), g.noeuds.get(etape).getLon()))
                        .title(g.noeuds.get(etape).POIs.get(0)));
            }

            l.add(arrivee);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(g.noeuds.get(arrivee).getLat(), g.noeuds.get(arrivee).getLon()))
                    .title(g.noeuds.get(arrivee).POIs.get(0)));

            Chemin chemin = g.itineraireMultiple(l, pmr); // Calcul itineraire le plus court

            PolylineOptions ligne = new PolylineOptions();
            int n = g.noeuds.get(chemin.noeuds.get(0)).getNiveau();
            for (int i = 0; i < chemin.noeuds.size(); i++)
            {
                int j = chemin.noeuds.get(i);
                if(g.noeuds.get(j).getNiveau() != n)
                {
                    switch(n) {
                        case -2:
                            ligne.color(Color.rgb(0 , 0 , 0));
                            break;
                        case -1:
                            ligne.color(Color.rgb(64 , 64 , 64));
                            break;
                        case 0:
                            ligne.color(Color.rgb(128 , 128 , 128));
                            break;
                        case 1:
                            ligne.color(Color.rgb(192 , 192 , 192));
                            break;
                    }

                    ligne.width(10);
                    lignes.add(ligne);
                    mMap.addPolyline(ligne);
                    n = g.noeuds.get(j).getNiveau();
                    ligne = new PolylineOptions();
                    ligne.add(new LatLng(g.noeuds.get(j).getLat(), g.noeuds.get(j).getLon()));
                }
                else
                {
                    ligne.add(new LatLng(g.noeuds.get(j).getLat(), g.noeuds.get(j).getLon()));
                }
            }

            switch(n) {
                case -2:
                    ligne.color(Color.rgb(0 , 0 , 0));
                    break;
                case -1:
                    ligne.color(Color.rgb(64 , 64 , 64));
                    break;
                case 0:
                    ligne.color(Color.rgb(128 , 128 , 128));
                    break;
                case 1:
                    ligne.color(Color.rgb(192 , 192 , 192));
                    break;
            }

            ligne.width(10);
            lignes.add(ligne);
            mMap.addPolyline(ligne);
        }
    }

    public void onClick(View view)
    {
        Intent i = new Intent(MapsActivity.this, ItineraireActivity.class);
        startActivityForResult(i, 1);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void placerCalque()
    {
        mMap.addGroundOverlay(carteFac);
    }

    public void onMapReady(GoogleMap googleMap) throws SecurityException {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.422949, 4.425735), 18));

        mMap.setBuildingsEnabled(true);

        // Calque
        carteFac = new GroundOverlayOptions();
        carteFac.image(BitmapDescriptorFactory.fromResource(R.drawable.calque0));
        carteFac.position(new LatLng(45.4231698,4.4252605), 428.435f, 428.435f);
        placerCalque();

        //Type de carte
        final Button changer_type = (Button) findViewById(R.id.type_carte);
        changer_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changer_type.getText().equals("Satellite")){
                    mMap.clear();
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    if(!lignes.isEmpty())
                    {
                        for(int i = 0 ; i < lignes.size() ; i++)
                        {
                            mMap.addPolyline(lignes.get(i));
                        }
                    }
                    if(depart != -1){
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(g.noeuds.get(depart).getLat(), g.noeuds.get(depart).getLon()))
                                .title(g.noeuds.get(depart).POIs.get(0)));
                    }
                    if(arrivee != -1){
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(g.noeuds.get(arrivee).getLat(), g.noeuds.get(arrivee).getLon()))
                                .title(g.noeuds.get(arrivee).POIs.get(0)));
                    }
                    if(etape != -1){
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(g.noeuds.get(etape).getLat(), g.noeuds.get(etape).getLon()))
                                .title(g.noeuds.get(etape).POIs.get(0)));
                    }
                    changer_type.setText(R.string.plan);
                }else{
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    placerCalque();
                    changer_type.setText(R.string.satellite);
                }
            }
        });

        Button monter = (Button) findViewById(R.id.monter);
        monter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(niveau < 1 && mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
                    niveau++;
                    switch (niveau){
                        case -1:
                            carteFac.image(BitmapDescriptorFactory.fromResource(R.drawable.calque_1));
                            break;
                        case 0:
                            carteFac.image(BitmapDescriptorFactory.fromResource(R.drawable.calque0));
                            break;
                        case 1:
                            carteFac.image(BitmapDescriptorFactory.fromResource(R.drawable.calque1));
                            break;
                    }
                    placerCalque();
                }
            }
        });

        Button descendre = (Button) findViewById(R.id.descendre);
        descendre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(niveau > -2 && mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
                    niveau--;
                    switch (niveau){
                        case -2:
                            carteFac.image(BitmapDescriptorFactory.fromResource(R.drawable.calque_2));
                            break;
                        case -1:
                            carteFac.image(BitmapDescriptorFactory.fromResource(R.drawable.calque_1));
                            break;
                        case 0:
                            carteFac.image(BitmapDescriptorFactory.fromResource(R.drawable.calque0));
                            break;
                    }
                    placerCalque();
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("Lat :" +latLng.latitude + " Lon :"+latLng.longitude));
            }
        });

        /*for(int i = 0; i < g.noeuds.size(); i++){
            LatLng latLong = new LatLng(g.noeuds.get(i).getLat(), g.noeuds.get(i).getLon());
            mMap.addMarker(new MarkerOptions().position(latLong).title("" + i));
        }*/
    }

    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_Calendrier)
        {
            Intent i = new Intent(MapsActivity.this, CalendrierActivity.class);
            startActivityForResult(i, 1);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else if (id == R.id.nav_aide)
        {
            Intent i = new Intent(MapsActivity.this, AideActivity.class);
            startActivityForResult(i, 1);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else if (id == R.id.nav_itineraire)
        {
            Intent i = new Intent(MapsActivity.this, ItineraireActivity.class);
            startActivityForResult(i, 1);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        return true;
    }

    private void parseGraphe() throws IOException, XmlPullParserException
    {
        XmlResourceParser xpp = getResources().getXml(R.xml.metare);

        g = new Graphe();
        Noeud n = null;

        float latitude = 0;
        float longitude = 0;
        char batiment ='-';
        String POI = "";
        int voisin = -1;
        int voisin_pmr = -1;
        int niveau = 99;

        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_TAG)
            {
                if(xpp.getName().equals("noeud")) { n = new Noeud(); }
                else if(xpp.getName().equals("latitude")) { xpp.next(); latitude = Float.parseFloat(xpp.getText()); }
                else if(xpp.getName().equals("longitude")) { xpp.next(); longitude = Float.parseFloat(xpp.getText()); }
                else if(xpp.getName().equals("batiment")) { xpp.next(); batiment = xpp.getText().charAt(0); }
                else if(xpp.getName().equals("POI")) { xpp.next(); POI = xpp.getText(); }
                else if(xpp.getName().equals("voisin")) { xpp.next(); voisin = Integer.parseInt(xpp.getText()); }
                else if(xpp.getName().equals("voisin_PMR")) { xpp.next(); voisin_pmr = Integer.parseInt(xpp.getText()); }
                else if(xpp.getName().equals("niveau")) { xpp.next(); niveau = Integer.parseInt(xpp.getText()); }
            }
            else if(eventType == XmlPullParser.END_TAG)
            {
                if(xpp.getName().equals("noeud")) { g.ajouterNoeud(n); }
                else if(xpp.getName().equals("latitude")) { n.setLatitude(latitude); }
                else if(xpp.getName().equals("longitude")) { n.setLongitude(longitude); }
                else if(xpp.getName().equals("batiment")) { n.setBatiment(batiment); }
                else if(xpp.getName().equals("POI")) { n.ajouterPOI(POI); }
                else if(xpp.getName().equals("voisin")) { n.ajouterVoisin(voisin); }
                else if(xpp.getName().equals("voisin_PMR")) { n.ajouterVoisinPMR(voisin_pmr); }
                else if(xpp.getName().equals("niveau")) { n.setNiveau(niveau); }
            }

            eventType = xpp.next();
        }

        xpp.close();
    }
}