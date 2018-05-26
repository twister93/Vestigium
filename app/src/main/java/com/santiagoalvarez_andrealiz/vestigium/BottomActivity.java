package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santiagoalvarez_andrealiz.vestigium.model.Albums;
import com.santiagoalvarez_andrealiz.vestigium.model.Points;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static okhttp3.internal.http.HttpDate.format;

/**
 * Created by andrealiz on 5/05/18.
 */

public class BottomActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView mTextMessage;
    FragmentManager fm;
    FragmentTransaction ft;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;

    Button btConfig;
    //-------------------------------------
    ImageView iPhoto;
    String mCurrentPhotoPath="";
    private Marker marker;
    double latitude = 0;
    double longitude = 0;
    double altitude = 0;
    private DatabaseReference databaseReference; //referencia que necesitamos
    //-------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Menu bottomNavigationViewMenu = navigation.getMenu();
        bottomNavigationViewMenu.findItem(R.id.mAlbum).setChecked(false);
        bottomNavigationViewMenu.findItem(R.id.mHome).setChecked(true);


        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        MainFragment fragment = new MainFragment();
        ft.add(android.R.id.content, fragment).commit();

        //-----------------Chequeo de usuario logueado------------
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null){ //No user login
            Intent i =new Intent(BottomActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
//-------------------------------------------------------
        iPhoto = findViewById(R.id.ivLogin);
//-------------------------------------------------------

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            ft = fm.beginTransaction();

            switch (item.getItemId()) {
                case R.id.mAlbum:
                    AlbumFragment frag = new AlbumFragment();
                    ft.replace(android.R.id.content, frag).commit();
                    return true;
                case R.id.mHome:
                    MainFragment frag2 = new MainFragment();
                    ft.replace(android.R.id.content, frag2).commit();
                    return true;
                case R.id.mProfile:
                    ProfileFragment frag3 = new ProfileFragment();
                    ft.replace(android.R.id.content, frag3).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ft = fm.beginTransaction();

        if (id == R.id.mSetting) {
            SettingFragment frag = new SettingFragment();
            ft.replace(android.R.id.content, frag).commit();
        }else if(id==R.id.mCamera){
            dispatchTakePictureIntent();
        }
        return super.onOptionsItemSelected(item);
    }


    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.santiagoalvarez_andrealiz.vestigium.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        myLocation();
        return image;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void myLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        updateLocation(location);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1500,0,locationListener);

    }

    private void updateLocation(Location location) {
        if (location != null) {//Esto se debe hacer siempre para evitar app crash
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            //--------------------Acumulando en base de datos
            FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
            final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            String DateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            //id - databaseReference.push().getKey()
            Albums albums= new Albums (databaseReference.push().getKey(),
                    "Prueba",
                    DateTime,
                    "Yes");
            Log.d("FirebaseSave", "Entra al guardar");
            databaseReference.child("users").child(firebaseUser.getUid()).child("albums").child(albums.getAlbumId()).setValue(albums);

            Points points = new Points(databaseReference.push().getKey(),
                    Double.toString(latitude),
                    Double.toString(longitude),
                    Double.toString(altitude),
                    mCurrentPhotoPath);
            databaseReference.child("users").child(firebaseUser.getUid()).child("albums").child(albums.getAlbumId()).child("points").child(points.getPointId()).setValue(points);
            Log.d("FirebaseLocation:", "latitude:"+latitude+" longitude:"+longitude+" altitude: "+altitude);
        }

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



}
