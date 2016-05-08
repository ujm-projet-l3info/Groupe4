package com.example.jules.univLocation;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.Format;
import java.util.Date;

public class CalendrierActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Cursor mCursor = null;
    private static final String[] ATTRIBUTS = new String[] {
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

        Button itineraire = (Button) findViewById(R.id.y_aller);
        itineraire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvSalle = (TextView)findViewById(R.id.salle);

                if(tvSalle != null){
                    if(!tvSalle.getText().toString().equals("")){
                        Intent i = new Intent(CalendrierActivity.this, ItineraireActivity.class);
                        Bundle b = new Bundle();

                        b.putString("arrivee", tvSalle.getText().toString());

                        i.putExtras(b);

                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                }
            }
        });
    }

    private void recupererCalendrier() {
        if(mCursor == null) {
            try {
                mCursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, ATTRIBUTS, null, null, CalendarContract.Events.DTSTART + " ASC");
            } catch(SecurityException e){
                e.printStackTrace();
            }
        }

        mCursor.moveToFirst();
        Long mtn = (new Date()).getTime();
        while(mCursor.getLong(1) < mtn){
            if(!mCursor.isLast())
                mCursor.moveToNext();
        }

        mCursor.moveToPrevious();

        Button b = (Button)findViewById(R.id.suivant);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.precedent);
        b.setOnClickListener(this);

        onClick(findViewById(R.id.suivant));
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

        int tmp1 = mCursor.getPosition();

        if(v.getId() == R.id.suivant) {
            if(!mCursor.isLast()) {
                do{
                    mCursor.moveToNext();

                    if(!mCursor.isLast()) {
                        System.out.println(mCursor.getString(0));
                        if(mCursor.getString(3) != null) {
                            if(!mCursor.getString(3).equals(""))
                                break;
                        }
                    }
                }while(!mCursor.isLast());

                if(mCursor.isLast()) {
                    if (mCursor.getString(3) != null) {
                        if (mCursor.getString(3).equals(""))
                            mCursor.moveToPosition(tmp1);
                    } else {
                        mCursor.moveToPosition(tmp1);
                    }
                }
            }
        }else if(v.getId() == R.id.precedent){
            if (!mCursor.isFirst()) {
                do {
                    mCursor.moveToPrevious();

                    if (!mCursor.isFirst()) {
                        if (mCursor.getString(3) != null) {
                            if (!mCursor.getString(3).equals(""))
                                break;
                        }
                    }
                } while (!mCursor.isFirst());

                if (mCursor.isFirst()){
                    if(mCursor.getString(3) != null) {
                        if(mCursor.getString(3).equals(""))
                            mCursor.moveToPosition(tmp1);
                    }else{
                        mCursor.moveToPosition(tmp1);
                    }
                }
            }
        }

        int tmp2 = mCursor.getPosition();

        if(tmp1 != tmp2) {
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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendrier, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_carte) {
            Intent i = new Intent();
            setResult(CalendrierActivity.this.RESULT_CANCELED, i);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (id == R.id.nav_aide) {
            Intent i = new Intent(CalendrierActivity.this, AideActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else if (id == R.id.nav_itineraire) {
            Intent i = new Intent(CalendrierActivity.this, ItineraireActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }

        return true;
    }
}