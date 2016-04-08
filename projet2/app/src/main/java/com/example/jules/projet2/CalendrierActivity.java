package com.example.jules.projet2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.text.Format;
import java.util.Date;

public class CalendrierActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Cursor mCursor = null;
    private static final String[] ATTRIBUTS = new String[]
            {
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.DTSTART,
                    CalendarContract.Events.DTEND,
                    CalendarContract.Events.EVENT_LOCATION,
                    CalendarContract.Events.CALENDAR_DISPLAY_NAME
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendrier);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recupererCalendrier();
    }

    private void recupererCalendrier() {
        if(mCursor == null)
            mCursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, ATTRIBUTS, null, null, CalendarContract.Events.DTSTART + " ASC");

        mCursor.moveToFirst();
        Long mtn = (new Date()).getTime();
        while(mCursor.getLong(1) < mtn || mCursor.getString(4).equals("Numéros de semaine") || mCursor.getString(4).equals("Jours feriés en France")) {
            if(!mCursor.isLast())
                mCursor.moveToNext();
        }

        Button b = (Button)findViewById(R.id.suivant);
        b.setOnClickListener(this);

        b = (Button)findViewById(R.id.precedent);
        b.setOnClickListener(this);

        if(!mCursor.isLast())
            mCursor.moveToNext();
        onClick(findViewById(R.id.precedent));
    }


    public void onClick(View v) {
        TextView tvDate = (TextView)findViewById(R.id.date);
        TextView tvHeure = (TextView)findViewById(R.id.heure);
        TextView tvNom = (TextView)findViewById(R.id.nom);
        TextView tvSalle = (TextView)findViewById(R.id.salle);

        Long debut = 0L;
        Long fin = 0L;
        String nom = "";
        String salle = "";

        Format df = DateFormat.getDateFormat(this);
        Format tf = DateFormat.getTimeFormat(this);

        switch(v.getId()) {
            case R.id.suivant:
                do
                    mCursor.moveToNext();
                while((mCursor.getString(4).equals("Numéros de semaine") || mCursor.getString(4).equals("Jours feriés en France")) &&
                        !mCursor.isLast());

                if(mCursor.getString(4).equals("Numéros de semaine") || mCursor.getString(4).equals("Jours feriés en France"))
                    mCursor.moveToPrevious();

                break;
            case R.id.precedent:
                do
                    mCursor.moveToPrevious();
                while((mCursor.getString(4).equals("Numéros de semaine") || mCursor.getString(4).equals("Jours feriés en France")) &&
                        !mCursor.isFirst());

                if(mCursor.getString(4).equals("Numéros de semaine") || mCursor.getString(4).equals("Jours feriés en France"))
                    mCursor.moveToNext();

                break;
        }

        try {
            nom = mCursor.getString(0);
            debut = mCursor.getLong(1);
            fin = mCursor.getLong(2);
            salle = mCursor.getString(3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvNom.setText(nom);
        tvSalle.setText(salle);
        tvDate.setText(df.format(debut));
        tvHeure.setText(tf.format(debut) + " - " + tf.format(fin));
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
        getMenuInflater().inflate(R.menu.calendrier, menu);
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
            Intent i = new Intent(CalendrierActivity.this, MapsActivity.class);
            startActivity(i);
            this.finish();
        } else if (id == R.id.nav_aide) {
            Intent i = new Intent(CalendrierActivity.this, AideActivity.class);
            startActivity(i);
            this.finish();
        } else if (id == R.id.nav_itineraire) {
            Intent i = new Intent(CalendrierActivity.this, ItineraireActivity.class);
            startActivity(i);
            this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
