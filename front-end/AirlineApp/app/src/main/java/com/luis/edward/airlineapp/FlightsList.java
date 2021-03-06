package com.luis.edward.airlineapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;

public class FlightsList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private ImageView imageUser;
    private TextView nameUser;
    private TextView emailUser;
    private View navHeader;

    UsersController userData;

    GridView gridView_flights;
    ArrayList<String> array_prices;
    ArrayList<String> array_origin_places;
    ArrayList<String> array_destiny_places;
    ArrayList<String> array_flight_departure;
    ArrayList<String> array_flight_arrival;

    ArrayList<ArrayList<String>> array_flights_apply = new ArrayList<ArrayList<String>>();

    TextView empty_txtview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//-----------------------------------------------------
        //---------Para conectar a google silenciosamente y cargar foto nombre email
        navHeader = navigationView.getHeaderView(0);
        imageUser = (ImageView) navHeader.findViewById(R.id.imageViewGoogle_user);
        nameUser = (TextView) navHeader.findViewById(R.id.nameGoogle_user);
        emailUser = (TextView) navHeader.findViewById(R.id.emailGoogle_user);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        //--------------------------------------------------------------------
        //--------------------------------------------------------------------


        /*array_prices.add("500");
        array_prices.add("250");
        array_origin_places.add("San Jose");
        array_origin_places.add("Budapest");
        array_destiny_places.add("Miami");
        array_destiny_places.add("Munich");
        array_flight_departure.add("13:00");
        array_flight_departure.add("21:30");
        array_flight_arrival.add("14:00");
        array_flight_arrival.add("23:00");*/
        array_prices = new ArrayList<String>();
        array_origin_places = new ArrayList<String>();
        array_destiny_places = new ArrayList<String>();
        array_flight_departure = new ArrayList<String>();
        array_flight_arrival = new ArrayList<String>();

        gridView_flights = findViewById(R.id.gridView_listFlights);
        GridAdapter adapter = new GridAdapter(FlightsList.this,array_prices,array_origin_places,array_destiny_places,array_flight_departure,array_flight_arrival);
        gridView_flights.setAdapter(adapter);

        gridView_flights.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"Item "+i+" seleccionado",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(),DetalleFlight.class);
                intent.putStringArrayListExtra("selected_flight",array_flights_apply.get(i));
                startActivity(intent);
            }
        });
    }

    private void seleccionaVuelos() {
        String origen = Search.info_selected_user.get(0).toLowerCase();
        String destino = Search.info_selected_user.get(1).toLowerCase();
        Log.d("mop","entra a selecciona vuelos");
        //recorro lista de listas
        for(int i=0; i<Search.all_flights_list.size(); i++){
            //agarro cada vuelo especifico
            ArrayList<String> vueloEspecifico = Search.all_flights_list.get(i);
            //pregunto si origen y destino es igual a lo que el usuario especifico
            Log.d("mop","origen user es"+origen);
            Log.d("mop","destino user es"+destino);
            Log.d("mop","origen api es"+vueloEspecifico.get(2).toLowerCase());
            Log.d("mop","destino api es"+vueloEspecifico.get(3).toLowerCase());
            if((vueloEspecifico.get(2).toLowerCase().compareTo(origen)==0) && (vueloEspecifico.get(3).toLowerCase().compareTo(destino))==0){
                array_flights_apply.add(vueloEspecifico);
                Log.d("mop","lista all vacia");
                empty_txtview = findViewById(R.id.txt_empty);
                empty_txtview.setVisibility(View.INVISIBLE);
                Log.d("mop","lista all dio positiva");
                //si si cumple entonces lleno las listas que inflan el grid
                array_origin_places.add(vueloEspecifico.get(2));
                array_destiny_places.add(vueloEspecifico.get(3));
                array_flight_departure.add(vueloEspecifico.get(8));
                array_flight_arrival.add(vueloEspecifico.get(9));
                array_prices.add(vueloEspecifico.get(6));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Se descarga la informacion sobre los usuario nuevamente
        userData = UsersController.getInstance();
        if (userData.getUserSessionState())
        {
            Log.d("Rino", "Va a descargar User del start");
            userData = UsersController.getInstance();
            userData.downloadDataFromAPi(getCacheDir());
            SystemClock.sleep(3000);
            userData.setSessionUser(userData.getIdSession());

            nameUser.setText(userData.getName());
            emailUser.setText(userData.getEmail());
            //para cargar la foto de la persona
            if (userData.getProfile_picture() == "null") {
                Log.d("perro", "foto es null");
                imageUser.setImageResource(R.drawable.plane_icon);
            }
            else
            {
                Glide.with(this).load(userData.getProfile_picture()).into(imageUser);
            }
        }
        else {

            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
            if(opr.isDone()){
                GoogleSignInResult result = opr.get();
                Log.d("gato","Llega antes del handle");
                handleSignInResult(result);
            }else{
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }
        array_flights_apply.clear();
        array_prices.clear();
        array_origin_places.clear();
        array_destiny_places.clear();
        array_flight_departure.clear();
        array_flight_arrival.clear();

        if(Search.all_flights_list.isEmpty()==true){
            Log.d("mop","lista all vacia");
            empty_txtview = findViewById(R.id.txt_empty);
            empty_txtview.setVisibility(View.VISIBLE);
        }else{
            Log.d("mop","lista all con cosas");
            seleccionaVuelos();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();

            nameUser.setText(account.getDisplayName());
            emailUser.setText(account.getEmail());
            //para cargar la foto de la persona
            Glide.with(this).load(account.getPhotoUrl()).into(imageUser);

        }else{
            //goLoginScreen();
            UsersController persona = UsersController.getInstance();
            nameUser.setText(persona.getName());
            emailUser.setText(persona.getEmail());
            //para cargar la foto de la persona
            if(persona.getProfile_picture()== "null"){
                Log.d("perro","foto es null");
                imageUser.setImageResource(R.drawable.plane_icon);
            }else{
                Glide.with(this).load(persona.getProfile_picture()).into(imageUser);
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
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            goMainActivity();
        } else if (id == R.id.nav_gallery) {
            go_map();

        } else if (id == R.id.nav_slideshow) {
            go_my_trips();

        } else if (id == R.id.nav_manage) {
            go_account();

        } else if (id == R.id.nav_share) {
            log_out();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, Search.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void go_map() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    private void go_my_trips() {

        Intent intent = new Intent(this, MyTrips.class);
        startActivity(intent);
    }

    public void log_out(){
        userData.setUserSessionState(false);
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess()){
                    goLoginScreen();
                }else{
                    Toast.makeText(FlightsList.this,"Session could not be closed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void go_account() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }
}
