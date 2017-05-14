package com.example.amirl2.atmfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    TextView tvCurrentLocation;
    ListView lvAtm;
    LocationManager locationManager;
    ArrayAdapter atmAdapter;
    List<String> atmListName;
    double longitude, latitude;
    private static final int MY_PERMISSION_REQUEST = 1;
    public final static String EXTRA_ATM_NAME = "ATM_NAME";
    public final static String EXTRA_ATM_VICINITY = "ATM_VICINITY";
    public final static String EXTRA_ATM_RATING = "ATM_RATING";
    public final static String EXTRA_ATM_OPEN_NOW= "ATM_OPEN_NOW";
    private final static int RADIUS = 5000;
    private final static String KEY = "AIzaSyCF18AB64AETsdKSDgGlIL27ZIX_ySFaPw";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCurrentLocation = (TextView) findViewById(R.id.tv_current_location);
        lvAtm = (ListView) findViewById(R.id.lv_atm);

        atmListName = new ArrayList<String>();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            tvCurrentLocation.setText("ATM/Branch in your current location:\n" + String.format("%.10f", longitude) + "/" + String.format("%.10f", latitude) + "\n within radius of "+ RADIUS/1000 + " KM");
        }

        StringBuilder querySt = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        querySt.append("location=" + latitude + "," + longitude);
        querySt.append("&radius=" + RADIUS);
        querySt.append("&types=" + "atm");
        querySt.append("&sensor=true");
        querySt.append("&key=" + KEY);


//        StringBuilder querySt = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json?");
//        StringBuilder querySt = new StringBuilder("https://m.chase.com/PSRWeb/location/list.action?");
//        querySt.append("lat=" + latitude + "&amp;lng=" + longitude);

        LocationsTask locationsTask = new LocationsTask();
        locationsTask.execute(querySt.toString());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 60 * 1000, 10, locationListenerGPS);

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSION_REQUEST);
                    }


                } else {

                    // permetion denied

                }
                return;
            }
        }
    }


    public final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }


    };


    private class LocationsTask extends AsyncTask<String, Integer, String> {

        String data = null;

        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream stream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            stream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(stream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
        } finally {
            stream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> atmsList = null;

            AtmJSON atmJson = new AtmJSON();

            try {
                jObject = new JSONObject(jsonData[0]);

                atmsList = atmJson.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return atmsList;
        }

        @Override
        protected void onPostExecute(final List<HashMap<String, String>> list) {

            for (int i = 0; i < list.size(); i++) {
                HashMap<String, String> atmPlace = list.get(i);

                String name = atmPlace.get("atm_name");
                atmListName.add(name);
            }

            atmAdapter = new ArrayAdapter(MainActivity.this,
                    android.R.layout.simple_list_item_1, atmListName);
            lvAtm.setAdapter(atmAdapter);
            atmAdapter.notifyDataSetChanged();

            lvAtm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    Intent intent = new Intent(MainActivity.this, ATMDetailsActivity.class);

                    list.get(position);
                    HashMap<String, String> atmPlace = list.get(position);
                    intent.putExtra(EXTRA_ATM_NAME, atmPlace.get("atm_name"));
                    intent.putExtra(EXTRA_ATM_VICINITY, atmPlace.get("vicinity"));
                    intent.putExtra(EXTRA_ATM_RATING, atmPlace.get("rating"));
                    intent.putExtra(EXTRA_ATM_OPEN_NOW, atmPlace.get("open_now"));

                    startActivity(intent);
                }
            });
        }




        }
    }








