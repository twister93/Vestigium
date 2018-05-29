package com.santiagoalvarez_andrealiz.vestigium;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import com.santiagoalvarez_andrealiz.vestigium.model.Albums;
import com.santiagoalvarez_andrealiz.vestigium.model.LocationService;
import com.santiagoalvarez_andrealiz.vestigium.model.Points;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by andrealiz on 7/05/18.
 */

public class MainFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    GoogleMap map;
    LocationManager locationManager;
    LocationListener locationListener;

    //Min distance for take points
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 metros
    //Min time for updates (miliseconds)
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000 * 60 * 1; // 1 minutos
    //to draw the path
    private ArrayList<LatLng> points;
    Polyline line;
    //Boton play/pause/stop -> 0/1/2
    String c="0";

    String albumName;
    Double lat,log;

    FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    ActivityManager manager;

    public MainFragment(){

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);

        points = new ArrayList<LatLng>();
        final FloatingActionButton btPlay = view.findViewById(R.id.btPlay);

        // Keep play/stop button during background process
        if(isMyServiceRunning(LocationService.class)){
            Log.d("service", "SERVICE RUNNING");
            btPlay.setImageResource(R.drawable.ic_stop);
            c="2";
        } else {
            Log.d("service", "SERVICE NOT RUNNING");
            btPlay.setImageResource(R.drawable.ic_play);
            c="0";
        }

        // When the button play is pressed -> create album and start service background
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(c == "0"){
                    btPlay.setImageResource(R.drawable.ic_stop);
                    c="2";
                    Log.d("service","PLAY ");
                    // Save button status in DB
                    databaseReference.child("users").child(firebaseUser.getUid()).child("flag_button").setValue(2);

                    //Clean map
                    map.clear();

                    //Create Album and save flag(current album name)and album in DB
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                    View mView = getLayoutInflater().inflate(R.layout.dialog_album, null);
                    Button btSave = (Button) mView.findViewById(R.id.btSave);
                    Button btCancel = (Button) mView.findViewById(R.id.btCancel);
                    final EditText etAlbum = (EditText) mView.findViewById(R.id.etAlbum);

                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();

                    //Save album
                    btSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getActivity(), "Album created", Toast.LENGTH_SHORT).show();
                            Log.d("save", "Save Album");
                            String DateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            Albums albums = new Albums(etAlbum.getText().toString(), etAlbum.getText().toString() ,DateTime, "no");
                            databaseReference.child("users").child(firebaseUser.getUid()).child("albums").child(albums.getAlbumName()).setValue(albums);
                            databaseReference.child("users").child(firebaseUser.getUid()).child("flag").setValue(albums.getAlbumName());
                            databaseReference.child("users").child(firebaseUser.getUid()).child("flag_button").setValue(c);

                            // ****** STARTING SERVICE *******

                            Intent i = new Intent(getActivity(), LocationService.class);
                            getActivity().startService(i);

                            dialog.hide();
                        }
                    });

                    //Cancel album
                    btCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.hide();
                            btPlay.setImageResource(R.drawable.ic_play);
                            databaseReference.child("users").child(firebaseUser.getUid()).child("flag_button").setValue(c);
                            Log.d("service","STOP ");
                            c="0";
                        }
                    });
                // When stop button is pressed
                }else if (c == "2"){
                    btPlay.setImageResource(R.drawable.ic_play);
                    c="0";
                    // Save button status in DB (flag_button)
                    databaseReference.child("users").child(firebaseUser.getUid()).child("flag_button").setValue(0);
                    Log.d("service","STOP ");

                    // ****** STARTING SERVICE *******
                    Intent i = new Intent(getActivity(), LocationService.class);
                    getActivity().stopService(i);
                }
            }
        });

        // Get the current album name for put points in it
        databaseReference.child("users").child(firebaseUser.getUid())
                .child("flag").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isMyServiceRunning(LocationService.class)){
                    albumName = dataSnapshot.getValue(String.class);
                    Log.d("service", "ALBUM NAME " + albumName);
                    getPoints();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return view;
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
            //ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Para agarrar el mapa del fragmento
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map1);
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
        /*map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);*/

        /*LatLng udea = new LatLng(6.266996,-75.5708923);
        MarkerOptions options = new MarkerOptions();
        options.position(udea).title("UdeA");
        float zoomlevel = 16;
        map.addMarker(options);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(udea, zoomlevel));*/
    }

    public void getPoints (){
        databaseReference.child("users").child(firebaseUser.getUid()).child("albums")
                .child(albumName).child("points").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String i=dataSnapshot.getKey();
                Log.d("service", "KEY CHILD " + i);
                Double latitude = getLat(i);
                Double longitude = getLog(i);
                addSavedPoints(i);
                /*if (latitude != null && longitude != null){
                   Log.d("service","COORDENADAS "+latitude + " "+longitude);
                   LatLng latLng = new LatLng(latitude, longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
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
                        marker(latitude,longitude,str);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }*/
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

    public Double getLat(String i){
        databaseReference.child("users").child(firebaseUser.getUid()).child("albums")
                .child(albumName).child("points").child(i).child("lat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String i=dataSnapshot.getValue().toString();
                //Log.d("service", "LAT VALUE " + i);
               lat=Double.parseDouble(i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return lat;
    }

    public Double getLog(String i){
        databaseReference.child("users").child(firebaseUser.getUid()).child("albums")
                .child(albumName).child("points").child(i).child("log").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String i=dataSnapshot.getValue().toString();
                //Log.d("service", "LOG VALUE " + i);
                log = Double.parseDouble(i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return log;
    }

    // Take points from DB and draw them
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


    /*@Override
    public void onStart() {
        //onBackPressed();
        super.onStart();
        Log.d("Metodo", "OnStart_MainFrag");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Metodo", "OnResume_MainFrag");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Metodo", "OnPause_MainFrag");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Metodo", "OnStop_MainFrag");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Metodo", "OnDestroy_MainFrag");
    }*/

    /* public void stopTrip(){
        if(locationManager != null){
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }
    }*/
    /*public void startTrip(){
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
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double altitude = location.getAltitude();
                LatLng latLng = new LatLng(latitude, longitude);
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    String str = addressList.get(0).getLocality() + ",";
                    str += addressList.get(0).getCountryName();
                    //Mostrar cada vez que tome un punto
                    String date = new Date().toString();
                    Log.d("map", "take location_NET_GPS "+ date);
                    //Dibujar la linea
                    points.add(latLng);
                    redrawLine();
                    //Dibujar el marcador
                    marker(latitude,longitude,str);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
        }
    }*/

}


