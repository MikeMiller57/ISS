package com.example.glados.iss;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String[] result = new String[2];
    FetchISSTask fetchTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        result[0] = "0";
        result[1] = "0";
        setUpMapIfNeeded();
        fetchTask = new FetchISSTask();
        fetchTask.execute();
    }



    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
            mMap = supportMapFragment.getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    public void postLoc(double lat, double lng){
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("ISS location"));
    }


    public class FetchISSTask extends AsyncTask<Void, Void, String[]>
    {

        private final String LOG_TAG = FetchISSTask.class.getSimpleName();

        private String[] getJsonData(String jsonStr) throws JSONException {
            JSONObject jsonObj = new JSONObject(jsonStr);
            String[] rs = new String[2];
            rs[0] = jsonObj.getString("latitude");
            rs[1] = jsonObj.getString("longitude");
            //rs.add("Altitude: " + jsonObj.getString("altitude"));
            //rs.add("Velocity: " + jsonObj.getString("velocity"));
            return rs;
        }

        @Override
        protected String[] doInBackground(Void... params) {
            String jsonStr = null;
            try {
                URL url = new URL("https://api.wheretheiss.at/v1/satellites/25544");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                jsonStr = buffer.toString();
                conn.disconnect();
                return getJsonData(jsonStr);

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            double lng = Double.parseDouble(strings[1]);
            double lat = Double.parseDouble(strings[0]);
            postLoc(lat,lng);
        }
    }

}
