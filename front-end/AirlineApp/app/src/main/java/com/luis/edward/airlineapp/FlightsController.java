package com.luis.edward.airlineapp;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by estadm on 7/6/2018.
 */

public class FlightsController {
    private static FlightsController instanceFlights;

    private RequestQueue mRequestQueue;
    private Cache cache;
    private Network network;
    private JsonArrayRequest jsonArrayRequest;


    public String URL_api="https://vuela-tiquicia-airline.herokuapp.com/flights";


    //Variable utilizada para el PUT
    String textName;
    String textLastName;
    String textViewNationality;
    String textViewPhone;
    String textViewEmail;
    String textPassword;




    //Arrelgar y quitar
    static ArrayList<ArrayList <String>> all_json_flights = new ArrayList<ArrayList<String>>();


    public static FlightsController getInstance(){
        if(instanceFlights == null){
            instanceFlights = new FlightsController();
        }
        return instanceFlights;
    }

    public String impDatos()
    {
        Log.i("Imp","Llammmo funcion");
        String temp="";
       return "";
    }

    public ArrayList<ArrayList<String>> getAll_json_flights()
    {
        return  all_json_flights;
    }

    public void downloadDataFromAPi(File getCacheDir) // Pasar getCacheDir()
    {
        //--------------------Bloque para bajar users de API
        Log.d("Bandera","LLega a la funcion de descargar");

        //request_json(activityName);
        //Instantiate the cache
        cache = new DiskBasedCache(getCacheDir, 1024 * 1024); // 1MB cap

        //Set up the network to use HttpURLConnection as the HTTP client.
        network = new BasicNetwork(new HurlStack());
        //Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);
        //Start the queue
        mRequestQueue.start();
        // Initialize a new JsonArrayRequest instance
        jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                //ip de la maquina, cel y compu deben estar en misma red
                URL_api+".json",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Do something with response
                        // Process the JSON
                        Log.d("mop",response.toString());
                        try{
                            // Loop through the array elements
                            for(int i=0;i<response.length();i++){
                                // Get current json object
                                JSONObject flight = response.getJSONObject(i);
                                //lista donde sera guardada la info
                                ArrayList<String> json_flight = new ArrayList<String>();
                                String id = flight.getString("id");
                                String id_flight = flight.getString("id_flight");
                                String from = flight.getString("from");
                                String to = flight.getString("to");
                                String date = flight.getString("date");
                                String duration_hours = flight.getString("duration_hours");
                                String price = flight.getString("price");
                                String airplane_model = flight.getString("airplane_model");
                                String time_departure = flight.getString("time_departure");
                                String time_arrival = flight.getString("time_arrival");
                                String flight_distance = flight.getString("flight_distance");

                                json_flight.add(id);
                                json_flight.add(id_flight);
                                json_flight.add(from);
                                json_flight.add(to);
                                json_flight.add(date);
                                json_flight.add(duration_hours);
                                json_flight.add(price);
                                json_flight.add(airplane_model);
                                json_flight.add(time_departure);
                                json_flight.add(time_arrival);
                                json_flight.add(flight_distance);




                                all_json_flights.add(json_flight);
                                //Actualizar todos los credenciales para el login

                                //flight_Data.add(json_flight.get(1)+":"+json_flight.get(2)+":"+json_flight.get(4)+":"+json_flight.get(5)+":"+json_flight.get(6));

                            }
                            /*//Actualizar todos los credenciales
                            int cont=0;
                            for (ArrayList<ArrayList<>> flightTemp :all_json_flights)
                            {
                                flight_CREDENTIALS[cont]=flightTemp.get(6)+":"+flightTemp.get(7);
                                Log.d("flightS-JSON:",flight_CREDENTIALS[cont]);
                                cont++;
                            }*/
                        }catch (JSONException e){
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Log.d("Error","No pudo entrar al API /flights: "+error);
                    }
                }

        );

        SystemClock.sleep(3000);
        // Adding request to request queue
        mRequestQueue.add(jsonArrayRequest);




//-------------------- FIN Bloque para bajar flights de API

    }

    public void putFlight(Context contexto,String idSelected,String ptextName,String ptextLastName, String ptextViewEmail)
    {
        textName=ptextName;
        textLastName=ptextLastName;
        textViewEmail=ptextViewEmail;


        //---------Hacer PUT al API
        RequestQueue MyRequestQueue = Volley.newRequestQueue(contexto);


        String url = URL_api+idSelected+".json";
        StringRequest MyStringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.toString());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("name", textName); //Add the data you'd like to send to the server.
                MyData.put("last_name", textLastName);
                MyData.put("email", textViewEmail);

                return MyData;
            }
        };
        MyRequestQueue.add(MyStringRequest);




        // //---------FIN PUT al API

    }

    public void postFlight(Context contexto,String ptextName,String ptextLastName, String ppassword ,String ptextViewEmail)
    {
        //POST a un api con Volley
        textName=ptextName;
        textLastName=ptextLastName;
        textViewEmail=ptextViewEmail;
        textPassword=ppassword;

        RequestQueue MyRequestQueue = Volley.newRequestQueue(contexto);

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, URL_api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.toString());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("name", textName); //Add the data you'd like to send to the server.
                MyData.put("last_name", textLastName);
                MyData.put("password", textPassword);
                MyData.put("email", textViewEmail);

                return MyData;
            }
        };
        MyRequestQueue.add(MyStringRequest);

        Log.d("FIN:","TERMINO COD");


//-------------------- FIN Bloque POST

    }


}
