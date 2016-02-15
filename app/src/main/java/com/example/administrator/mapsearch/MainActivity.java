package com.example.administrator.mapsearch;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Color;

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


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap googleMap;
    ImageView imageView;

    ArrayList<LatLng> positionList = new ArrayList<>();
    Stack<Marker> markers = new Stack<>();
    private Polygon polygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button addMarkerButton = (Button) findViewById(R.id.addMarker);
        addMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarker();
                createPolygon();
            }
        });

        imageView = (ImageView) findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarker();
                createPolygon();
            }
        });

        final Button clearMarker = (Button) findViewById(R.id.clearMarker);
        clearMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (!markers.isEmpty()){

                  markers.peek().remove();
                  markers.pop();
                  positionList.remove(positionList.size() - 1);
              }
                if (!positionList.isEmpty())
                createPolygon();
            }
        });


    }

    private void addMarker() {
        Marker marker = googleMap.addMarker(new MarkerOptions().position(
                googleMap.getCameraPosition().target));
        markers.push(marker);
        positionList.add(googleMap.getCameraPosition().target);

        MediaPlayer mpEffect
                = MediaPlayer.create(MainActivity.this, R.raw.sound);
        mpEffect.start();
    }


    public void createPolygon() {
        PolygonOptions rectGon = new PolygonOptions();
        rectGon.addAll(positionList)
                .strokeColor(Color.RED)
                .fillColor(Color.YELLOW)
                .strokeWidth(3);
        if (polygon != null)
            polygon.remove();
        polygon = googleMap.addPolygon(rectGon);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }
}
