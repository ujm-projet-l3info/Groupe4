package com.example.jules.projet2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.internal.IPolylineDelegate;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import parseur.*;
import graphe.*;




public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, LocationListener {


    private GoogleMap mMap;
    private Marker marker;
    private LocationManager lManager;
    private ParseurGrapheXML p;
    private Graphe g;
    private LatLng posUser;

    @Override
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

        lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Charger graphe
        try {
            getXMLfromResource();
        }
        catch (IOException | XmlPullParserException e)
        {
            e.printStackTrace();
        }


    }
<<<<<<< HEAD
/*
    private void obtenirPosition()
    {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.getLastKnownLocation().getLatitude();
        locationManager.getLastKnownLocation().getLongitude();

    }*/


=======

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                Bundle b = data.getExtras();
                tracerItineraire(b);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }

    public void tracerItineraire(Bundle b) {
        if(b != null) {
            mMap.clear();
            placerCalque();

            int depart = b.getInt("depart");
            int arrivee = b.getInt("arrivee");
            boolean pmr = b.getBoolean("pmr");

            ArrayList<Integer> l = new ArrayList<Integer>(); // Ajout de checkpoint pour itineraire
            l.add(depart);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(g.noeuds.get(depart).getLat(), g.noeuds.get(depart).getLon()))
                    .title(g.noeuds.get(depart).POIs.get(0)));


            l.add(arrivee);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(g.noeuds.get(arrivee).getLat(), g.noeuds.get(arrivee).getLon()))
                    .title(g.noeuds.get(arrivee).POIs.get(0)));

            Chemin chemin = g.itineraireMultiple(l, pmr); // Calcul itineraire le plus court pour non PMR

            PolylineOptions lineOptions = new PolylineOptions();
            int j;
            for (int i = 0; i < chemin.noeuds.size(); i++) {
                j = chemin.noeuds.get(i);
                lineOptions.add(new LatLng(g.noeuds.get(j).getLat(), g.noeuds.get(j).getLon()));
            }

            lineOptions.width(20);
            lineOptions.color(Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), ((int) Math.random() * 255)));

            mMap.addPolyline(lineOptions);
        }
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(MapsActivity.this, ItineraireActivity.class);
        startActivityForResult(i, 1);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /*private void obtenirPosition() {
        //on démarre le cercle de chargement
        setProgressBarIndeterminateVisibility(true);

        //On demande au service de localisation de nous notifier tout changement de position
        //sur la source (le provider) choisie, toute les minutes (60000millisecondes).
        //Le paramètre this spécifie que notre classe implémente LocationListener et recevra
        //les notifications.
        lManager.requestLocationUpdates(, 60000, 0, this);
    }*/
>>>>>>> 2c14f0ca584628e2420a30c24dea109b84a431de

    public void onLocationChanged(Location location)
    {
        posUser = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(posUser).title("USER"));
    }

    public void placerCalque() {
        LatLng fac = new LatLng(45.42291, 4.42566);

<<<<<<< HEAD
=======
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fac,18));

        fac = new LatLng(45.4235668,4.4254605);
        GroundOverlayOptions carteFac= new GroundOverlayOptions();
        carteFac.image(BitmapDescriptorFactory.fromResource(R.drawable.calque0704));
        carteFac.position(fac, 428.435f, 428.435f);

        mMap.addGroundOverlay(carteFac);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
>>>>>>> 2c14f0ca584628e2420a30c24dea109b84a431de
    @Override
    public void onMapReady(GoogleMap googleMap) throws SecurityException{
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        placerCalque();

        /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Lat : " + latLng.latitude + " Lon : " + latLng.longitude));
            }
        });*/

        /* Test TAD graphe */

<<<<<<< HEAD
    }
=======
        /*for(int i = 0 ; i < g.noeuds.size() ; i++)
        {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(g.noeuds.get(i).getLat(), g.noeuds.get(i).getLon()))
                    .title("n°" + i));

            for(int j = 0 ; j < g.noeuds.get(i).voisins.size() ; j++)
            {
                int k = g.noeuds.get(i).voisins.get(j);

                if(g.noeuds.get(k).voisins.contains(i)) {
                    PolylineOptions lineOptions = new PolylineOptions();
                    lineOptions.add(new LatLng(g.noeuds.get(i).getLat(), g.noeuds.get(i).getLon()));
                    lineOptions.add(new LatLng(g.noeuds.get(k).getLat(), g.noeuds.get(k).getLon()));
                    lineOptions.width(8);
                    lineOptions.color(Color.rgb(0,153,153));
                    mMap.addPolyline(lineOptions);
                }
>>>>>>> 2c14f0ca584628e2420a30c24dea109b84a431de

            }

        }*/
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

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_Calendrier) {
            Intent i = new Intent(MapsActivity.this, CalendrierActivity.class);
            startActivityForResult(i, 1);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.nav_aide) {
            Intent i = new Intent(MapsActivity.this, AideActivity.class);
            startActivityForResult(i, 1);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.nav_itineraire) {
            Intent i = new Intent(MapsActivity.this, ItineraireActivity.class);
            startActivityForResult(i, 1);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

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
                else if(xpp.getName().equals("voisin_PMR")) { eventType = xpp.next(); voisin_pmr = Integer.parseInt(xpp.getText()); }
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
            }

            eventType = xpp.next();
        }
        // indicate app done reading the resource.
        xpp.close();
    }

}