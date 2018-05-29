package com.santiagoalvarez_andrealiz.vestigium;

import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by andrealiz on 28/05/18.
 */

public class MapsFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    GoogleMap map;

    private ArrayList<LatLng> points;
    Polyline line;
    String albumName;

    FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    ActivityManager manager;

    public MapsFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        points = new ArrayList<LatLng>();
        // Get the current album name for put points in it
        databaseReference.child("users").child(firebaseUser.getUid())
                .child("flag_album").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    albumName = dataSnapshot.getValue(String.class);
                    Log.d("service", "ALBUM NAME " + albumName);
                    getPoints();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Para agarrar el mapa del fragmento
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
    }

    public void getPoints (){
        databaseReference.child("users").child(firebaseUser.getUid()).child("albums")
                .child(albumName).child("points").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String i=dataSnapshot.getKey();
                Log.d("service", "KEY CHILD " + i);
                addSavedPoints(i);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void addSavedPoints(String i){
        final String cont = i;
        databaseReference.child("users").child(firebaseUser.getUid()).child("albums")
                .child(albumName).child("points").child(cont).child("lat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String i=dataSnapshot.getValue().toString();
                Log.d("service", "Lat VALUE " + i);
                final Double lat1 = Double.parseDouble(i);
                databaseReference.child("users").child(firebaseUser.getUid()).child("albums")
                        .child(albumName).child("points").child(cont).child("log").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String i=dataSnapshot.getValue().toString();
                        Log.d("service", "LOG VALUE " + i);
                        final Double log1 = Double.parseDouble(i);
                        LatLng latLng = new LatLng(lat1, log1);
                        points.add(latLng);
                        Log.d("points","points size "+points.size());

                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(lat1, log1, 1);
                            String str = addressList.get(0).getLocality() + ",";
                            str += addressList.get(0).getCountryName();
                            //Show each time a point is taken
                            String date = new Date().toString();
                            Log.d("map", "take location_NET_GPS "+ date);
                            //Dibujar la linea
                            // points.add(latLng);
                            //Log.d("points","points size "+points.size());
                            redrawLine();
                            // Draw marker
                            marker(lat1,log1,str);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void marker(double lat, double lng, String str){
        LatLng latLng = new LatLng(lat, lng);
        map.addMarker(new MarkerOptions().position(latLng).title(str).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        float zoomlevel = 25;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomlevel));
        //map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void redrawLine(){

        map.clear();  //clears all Markers and Polylines

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }
        //marker(); //add Marker in current position
        line = map.addPolyline(options); //add Polyline
        //Mostrar que se añadió la linea
        String date = new Date().toString();
        Log.d("map", "DRAW LINE "+ date);
    }
}
