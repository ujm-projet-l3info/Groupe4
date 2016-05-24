package com.example.jules.univLocation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import graphe.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GroundOverlayOptions calqueOptions;
    private GroundOverlay calqueFac;
    private ArrayList<Marker> marqueurs = new ArrayList<Marker>();
    private ArrayList<PolylineOptions> lignes = new ArrayList<PolylineOptions>();
    private Menu menu;
    private int vue = 0;
    private int depart = -1;
    private int arrivee = -1;
    private int etape = -1;
    private int niveau = 0;
    private boolean premiereConnexion = false;
    public static Graphe g;
    public static Bundle b = null;
    public static double latitude;
    public static double longitude;
    public GoogleApiClient mGoogleApiClient = null;
    public LocationRequest mLocationRequest = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Map
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bouton itineraire
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, ItineraireActivity.class);
                startActivityForResult(i, 1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Volet de navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Charger graphe
        try {
            parseGraphe();
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        // API Client localisation
        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if(mLocationRequest == null){
            mLocationRequest = new LocationRequest();

            mLocationRequest.setInterval(2000);
            mLocationRequest.setFastestInterval(100);
            //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    public void onMapReady(GoogleMap googleMap) throws SecurityException {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Checker gps
        LocationManager mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if(!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showDialogGPS();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.422949, 4.425735), 18));
        }

        // Bouton localisation
        FloatingActionButton maLoc = (FloatingActionButton) findViewById(R.id.ma_localisation);
        maLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws SecurityException {
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), mMap.getCameraPosition().zoom));
            }
        });

        // Localisation utilisateur
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // Calque
        creerCalque(0);

        // Etage
        TextView tv = (TextView) findViewById(R.id.etage);
        tv.setText("Niveau : " + niveau);

        // Changer niveau
        Button monter = (Button) findViewById(R.id.monter);
        monter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (niveau < 1 && vue == 0)
                    setCalqueNiveau(niveau + 1);
            }
        });

        Button descendre = (Button) findViewById(R.id.descendre);
        descendre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(niveau > -2 && vue == 0)
                    setCalqueNiveau(niveau - 1);
            }
        });

        /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("Lat :" +latLng.latitude + " Lon :"+latLng.longitude));
            }
        });

        for(int i = 0; i < g.noeuds.size(); i++){
            LatLng latLong = new LatLng(g.noeuds.get(i).getLat(), g.noeuds.get(i).getLon());
            mMap.addMarker(new MarkerOptions().position(latLong).title("" + i));
        }*/
    }

    protected void onActivityResult(int requestCode , int resultCode , Intent data) {
        if(b != null){
            tracerItineraire();
            b = null;
        }
    }

    public void tracerItineraire() throws SecurityException {
        mMap.clear();
        marqueurs = new ArrayList<Marker>();

        depart = b.getInt("depart");
        arrivee = b.getInt("arrivee");
        etape = b.getInt("etape");
        boolean pmr = b.getBoolean("pmr");

        /*Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }*/

        ArrayList<Integer> l = new ArrayList<>(); // Ajout de checkpoint pour itineraire

        if(depart == -2)
            depart = g.recollerGraphe(latitude, longitude);
        l.add(depart);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(g.noeuds.get(depart).getLat(), g.noeuds.get(depart).getLon()))
                .title("Départ")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        if(etape != -1) {
            if(etape == -2)
                etape = g.recollerGraphe(latitude, longitude);
            l.add(etape);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(g.noeuds.get(etape).getLat(), g.noeuds.get(etape).getLon()))
                    .title("Étape")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        }

        if(arrivee == -2)
            arrivee = g.recollerGraphe(latitude, longitude);
        l.add(arrivee);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(g.noeuds.get(arrivee).getLat(), g.noeuds.get(arrivee).getLon()))
                .title("Arrivée")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // Calcul itineraire le plus court
        Chemin chemin = g.itineraireMultiple(l, pmr);

        if(chemin.noeuds.size() == 1) {
            if(pmr)
                Snackbar.make(findViewById(R.id.fab), "Snackbar", Snackbar.LENGTH_LONG).setText("Pas d'itinéraire PMR pour cette destination").show();
            else
                Snackbar.make(findViewById(R.id.fab), "Snackbar", Snackbar.LENGTH_LONG).setText("Pas d'itinéraire pour cette destination").show();
        }

        lignes = new ArrayList<PolylineOptions>();

        // Tracage de l'itineraire
        PolylineOptions ligne = new PolylineOptions();

        int n = g.noeuds.get(chemin.noeuds.get(0)).getNiveau();

        if(vue == 0) {
            if(n != niveau) {
                creerCalque(n);
                TextView tv = (TextView) findViewById(R.id.etage);
                tv.setText("Niveau : " + niveau);
            }else {
                creerCalque(niveau);
            }
        }

        for (int i = 0; i < chemin.noeuds.size(); i++) {
            int j = chemin.noeuds.get(i);

            ligne.add(new LatLng(g.noeuds.get(j).getLat(), g.noeuds.get(j).getLon())); // Ajout noeud dans ligne en cours

            // Si en plus changment de niveau
            if(g.noeuds.get(j).getNiveau() != n) {
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

                ligne.width(15);
                lignes.add(ligne);
                mMap.addPolyline(ligne); // Tracer ligne

                n = g.noeuds.get(j).getNiveau(); // Changement de niveau
                ligne = new PolylineOptions(); // Initialisation ligne suivante

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

        ligne.width(15);
        lignes.add(ligne);
        mMap.addPolyline(ligne);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(g.noeuds.get(depart).getLat(), g.noeuds.get(depart).getLon()), 18));
    }

    public void creerCalque(int n) {
        effacerMarqueurs();

        calqueOptions = new GroundOverlayOptions();
        niveau = n;
        switch (niveau){
            case -2:
                calqueOptions.image(BitmapDescriptorFactory.fromResource(R.drawable.calque_2));
                break;
            case -1:
                calqueOptions.image(BitmapDescriptorFactory.fromResource(R.drawable.calque_1));
                break;
            case 0:
                calqueOptions.image(BitmapDescriptorFactory.fromResource(R.drawable.calque0));
                break;
            case 1:
                calqueOptions.image(BitmapDescriptorFactory.fromResource(R.drawable.calque1));
                break;
        }

        calqueOptions.position(new LatLng(45.4231698,4.4252605), 428.435f, 428.435f);
        calqueFac = mMap.addGroundOverlay(calqueOptions);
    }

    public void setCalqueNiveau(int n) {
        effacerMarqueurs();

        niveau = n;
        TextView tv = (TextView) findViewById(R.id.etage);
        tv.setText("Niveau : " + niveau);
        switch (niveau){
            case -2:
                calqueFac.setImage(BitmapDescriptorFactory.fromResource(R.drawable.calque_2));
                break;
            case -1:
                calqueFac.setImage(BitmapDescriptorFactory.fromResource(R.drawable.calque_1));
                break;
            case 0:
                calqueFac.setImage(BitmapDescriptorFactory.fromResource(R.drawable.calque0));
                break;
            case 1:
                calqueFac.setImage(BitmapDescriptorFactory.fromResource(R.drawable.calque1));
                break;
        }
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(!marqueurs.isEmpty()){
                effacerMarqueurs();
                marqueurs = new ArrayList<Marker>();
            }else if(!lignes.isEmpty()) {
                mMap.clear();

                if(vue == 0)
                    creerCalque(niveau);

                lignes = new ArrayList<PolylineOptions>();
            }else{
                super.onBackPressed();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;

        return true;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void effacerMarqueurs() {
        if(marqueurs != null) {
            for (int i = 0; i < marqueurs.size(); i++)
                marqueurs.get(i).remove();
        }
    }

    public  boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.trouver_wc)
            toilettes();
        else if(item.getItemId() == R.id.trouver_distrib)
            distributeurs();
        else if(item.getItemId() == R.id.trouver_manger)
            restauration();
        else if(item.getItemId() == R.id.trouver_prochain_cours)
            itineraireProchainCours();
        else if(item.getItemId() == R.id.recherche)
            recherche();
        else if(item.getItemId() == R.id.changer_vue)
            changerVue();

        return true;
    }

    public void itineraireProchainCours() throws SecurityException {
        Cursor mCursor = null;

        String[] ATTRIBUTS = new String[] {
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.CALENDAR_DISPLAY_NAME
        };

        try {
            mCursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, ATTRIBUTS, null, null, CalendarContract.Events.DTSTART + " ASC");
        } catch(SecurityException e){
            e.printStackTrace();
        }

        if(mCursor.getCount() != 0) {
            mCursor = CalendrierActivity.prochainCours(mCursor);

            ArrayList<Integer> listeNoeudsArr = MapsActivity.g.cherchePOI(mCursor.getString(3));

            b = new Bundle();

            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null)
                b.putInt("depart", -2);
            else
                b.putInt("depart", 0);

            b.putInt("etape", -1);
            b.putInt("arrivee", listeNoeudsArr.get(0));

            tracerItineraire();

            b = null;
        }
    }

    public void toilettes() {
        int nb = 0;
        LatLng l;
        ArrayList<Integer> listeToilettes = g.chercheToilettes();

        effacerMarqueurs();

        marqueurs = new ArrayList<Marker>();

        Noeud ok = new Noeud();
        for(int i = 0 ; i < listeToilettes.size() ; i++)
        {
            Noeud n = g.noeuds.get(listeToilettes.get(i));
            if(niveau == n.getNiveau()) {
                l = new LatLng(n.getLat(), n.getLon());
                ok = n;

                MarkerOptions options = new MarkerOptions()
                        .title("Toilettes")
                        .position(l)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                marqueurs.add(mMap.addMarker(options));
                nb++;
            }
        }

        if(nb == 0)
            Snackbar.make(findViewById(R.id.fab), "Snackbar", Snackbar.LENGTH_LONG).setText("Pas de toilettes au niveau " + niveau).show();
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ok.getLat(), ok.getLon()), 18));
    }

    public void distributeurs() {
        LatLng l;
        ArrayList<Integer> listeDistributeurs = g.chercheDistributeurs();

        effacerMarqueurs();

        marqueurs = new ArrayList<Marker>();

        Noeud n = new Noeud();
        for(int i = 0 ; i < listeDistributeurs.size() ; i++)
        {
            n = g.noeuds.get(listeDistributeurs.get(i));
            l = new LatLng(n.getLat(), n.getLon());

            MarkerOptions options = new MarkerOptions()
                    .title("Distributeurs (niveau " + n.getNiveau() + ")")
                    .position(l)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

            marqueurs.add(mMap.addMarker(options));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(n.getLat(), n.getLon()), 18));
    }

    public void restauration() {
        LatLng l;
        ArrayList<Integer> listeManger = new ArrayList<Integer>();

        listeManger.add(29);
        listeManger.add(91);
        listeManger.add(95);

        effacerMarqueurs();

        marqueurs = new ArrayList<Marker>();

        Noeud n = new Noeud();
        for(int i = 0 ; i < listeManger.size() ; i++)
        {
            n = g.noeuds.get(listeManger.get(i));
            l = new LatLng(n.getLat(), n.getLon());

            MarkerOptions options = new MarkerOptions()
                    .title(n.POIs.get(0) + " (niveau " + n.getNiveau() + ")")
                    .position(l)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

            marqueurs.add(mMap.addMarker(options));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(n.getLat(), n.getLon()), 18));
    }

    public void recherche() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        alertDialogBuilder.setTitle("Rechercher un lieu");

        alertDialogBuilder.setCancelable(true);

        //final AutoCompleteTextView recherche = new AutoCompleteTextView(this);
        final EditText recherche = new EditText(this);
        recherche.setTextColor(Color.parseColor("#63c1ba"));

        recherche.setSingleLine(true);
        recherche.setImeActionLabel("Ok", KeyEvent.KEYCODE_ENTER);

        /*String[] poi = ItineraireActivity.chargerPoi();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, poi);
        recherche.setAdapter(adapter);*/

        alertDialogBuilder.setView(recherche);

        alertDialogBuilder.setPositiveButton("Rechercher", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(!recherche.getText().toString().equals("") && !recherche.getText().toString().matches(" *")) {
                    ArrayList<Integer> liste = MapsActivity.g.cherchePOI(recherche.getText().toString());

                    if(!liste.isEmpty()) {
                        effacerMarqueurs();
                        marqueurs = new ArrayList<Marker>();

                        Noeud n = new Noeud();
                        for(int i = 0; i < liste.size(); i++) {
                            n = g.noeuds.get(liste.get(i));
                            LatLng l = new LatLng(n.getLat(), n.getLon());

                            MarkerOptions options = new MarkerOptions()
                                    .title(n.POIs.get(0) + " (niveau " + n.getNiveau() + ")")
                                    .position(l)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

                            marqueurs.add(mMap.addMarker(options));
                        }

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(n.getLat(), n.getLon()), 18));
                    }
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        recherche.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        alertDialog.setCanceledOnTouchOutside(true);

        alertDialog.show();
    }

    public void changerVue() {
        if (vue == 0) {
            mMap.clear();
            calqueFac.remove();
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            TextView tv = (TextView) findViewById(R.id.etage);
            tv.setText("");

            if(!lignes.isEmpty()) {
                for (int i = 0; i < lignes.size(); i++) {
                    mMap.addPolyline(lignes.get(i));
                }

                if(depart != -1) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(g.noeuds.get(depart).getLat(), g.noeuds.get(depart).getLon()))
                            .title("Départ")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                }
                if(etape != -1) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(g.noeuds.get(etape).getLat(), g.noeuds.get(etape).getLon()))
                            .title("Étape")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }
                if(arrivee != -1) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(g.noeuds.get(arrivee).getLat(), g.noeuds.get(arrivee).getLon()))
                            .title("Arrivée")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            }

            vue = 1;
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            creerCalque(niveau);

            TextView tv = (TextView) findViewById(R.id.etage);
            tv.setText("Niveau : " + niveau);

            vue = 0;
        }
    }

    @Override
    public void onConnected(Bundle bundle) throws SecurityException {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        updateLocation();
    }

    public void updateLocation() throws SecurityException {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLastLocation != null){
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            if(!premiereConnexion) {
                Noeud n = g.noeuds.get(g.recollerGraphe(latitude, longitude));

                LatLng maPos = new LatLng(n.getLat(), n.getLon());

                //new LatLng(45.422949, 4.425735)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(maPos, 18));

                premiereConnexion = true;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("log : ", " connexion suspendue avec GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("log :", " connexion échouée avec GoogleApiClient");
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location loc) {
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();

        //System.out.println("Nouvelle loc : (" + loc.getLatitude() + ";" + loc.getLongitude() + ")");
    }

    private void showDialogGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setCancelable(true);

        builder.setTitle("Localisation désactivée");
        builder.setMessage("Voulez-vous activer la localisation ?");

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

        AlertDialog alert = builder.create();

        alert.setCanceledOnTouchOutside(true);

        alert.show();
    }

    public boolean onNavigationItemSelected(MenuItem item) {
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

    private void parseGraphe() throws IOException, XmlPullParserException {
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