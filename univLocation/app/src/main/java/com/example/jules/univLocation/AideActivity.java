package com.example.jules.univLocation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class AideActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aide);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView tv = (TextView) findViewById(R.id.text_aide);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText("Bienvenue sur Univ'Location !" +
                "\n\nCette application mobile vous permettra de vous repérer dans le campus de la Métare sans aucune difficulté !" +
                "\n\nVous pouvez accéder au menu latéral des fonctionnalités en glissant votre doigt depuis le bord gauche de l'écran," +
                "ou en cliquant sur les trois barres de menu en haut à gauche." +
                "\n\n4 possibilités au total s'offrent à vous :" +
                "\n - Carte : vous affiche la carte du campus, sur laquelle seront dessinés les itinéraires. Vous pouvez aussi changer la vue entre satellite et plan, ainsi que changer les calques du plan du campus selon les différents étages : -2, -1, 0, +1." +
                "\n - Calendrier : récupère votre calendrier stocké dans votre appareil et vous affiche le prochain cours. Vous avez la possibilité de naviguer de cours en cours et d'accéder directement à un itinéraie depuis votre position jusqu'à la salle d'un cours donné" +
                "\n - Itinéraire : vous affiche le formulaire par lequel vous pouvez trouver un itinéraire dans le campus. Vous devez renseigner un départ et une arrivée et éventuellement une étape sur le parcours. Un switch permet d'indiquer si vous êtes une personne à mobilité réduite." +
                "Pour chaque champ à remplir appaîtra une liste déroulante de propositions en fonction de votre saisie, ainsi vous ne pourrez pas entrer de lieux inexistants." +
                "\n - Aide : vous affiche cette aide" +
                "\n\nPour l'utilisation de votre position et des itinéraires, n'oubliez pas d'activer la géolocalisation sur votre appareil. Sachez aussi que la localisation couplée aux données mobiles et au wifi améliorent la localisation." +
                "\n\nEn ce qui concerne l'emploi du temps, il faut que vous l'ayez à l'avance intégré dans votre appareil, via un compte de calendrier ou directement via l'URL récupérée sur l'ENT." +
                "\n\nBonne navigation !\n\nL'équipe d'Univ'Location");
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
        getMenuInflater().inflate(R.menu.aide, menu);
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

        if (id == R.id.nav_carte) {
            Intent i = new Intent();
            setResult(AideActivity.this.RESULT_CANCELED, i);
            finish();
        } else if (id == R.id.nav_Calendrier) {
            Intent i = new Intent(AideActivity.this, CalendrierActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else if (id == R.id.nav_itineraire) {
            Intent i = new Intent(AideActivity.this, ItineraireActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }

        return true;
    }
}
