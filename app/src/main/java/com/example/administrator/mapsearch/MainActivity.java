package com.example.administrator.mapsearch;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Color;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.Stack;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    GoogleMap googleMap;
    ImageView imageView;

    Stack<PolygonData> polygonDataStack = new Stack<>();
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        createNewPolygonData();

        final Button addMarkerButton = (Button) findViewById(R.id.addMarker);
        addMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarker(polygonDataStack.peek());
                createPolygon(polygonDataStack.peek());

            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        imageView = (ImageView) findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarker(polygonDataStack.peek());
                createPolygon(polygonDataStack.peek());
            }
        });

        final Button clearMarker = (Button) findViewById(R.id.clearMarker);
        clearMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Stack<Marker> markers = polygonDataStack.peek().markers;
                if (!markers.isEmpty()) {
                    markers.peek().remove();
                    markers.pop();
                    createPolygon(polygonDataStack.peek());
                }
                if (polygonDataStack.size()>1&&markers.isEmpty()){
                   polygonDataStack.pop();
                }
            }
        });

        Button stopButton = (Button) findViewById(R.id.stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!polygonDataStack.peek().isEmpty())
                    createNewPolygonData();
                else
                    Log.d("polygondata empty?", "yes");
            }
        });

        Button AddHole = (Button) findViewById(R.id.addHole);
        AddHole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    private void createNewPolygonData() {
        PolygonData polygonData = new PolygonData();
        polygonDataStack.push(polygonData);
    }

    private void addMarker(PolygonData polygonData) {
        Marker marker = googleMap.addMarker(new MarkerOptions().position(
                googleMap.getCameraPosition().target));
        polygonData.markers.push(marker);
        MediaPlayer mpEffect
                = MediaPlayer.create(MainActivity.this, R.raw.thumpsoundeffect);
        mpEffect.start();
    }


    public void createPolygon(PolygonData polygonData) {
        PolygonOptions rectGon = new PolygonOptions();
        Stack<Marker> markers = polygonData.markers;
        for (Marker eachMarker : markers) {
            rectGon.add(eachMarker.getPosition());
        }
        rectGon.strokeColor(Color.RED);
        rectGon.fillColor(Color.YELLOW);
        rectGon.strokeWidth(3);

        if (polygonData.polygon != null)
            polygonData.polygon.remove();
        if (!markers.isEmpty())
            polygonData.polygon = googleMap.addPolygon(rectGon);

    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().isMyLocationButtonEnabled();
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi
                .getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            // Do something when location provider not available
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
       /* LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));*/
    }

    public class PolygonData {
        Stack<Marker> markers;
        private Polygon polygon;

        public PolygonData() {
            markers = new Stack<>();
        }

        public boolean isEmpty() {
            return markers.isEmpty() && polygon == null;
        }
    }

}

