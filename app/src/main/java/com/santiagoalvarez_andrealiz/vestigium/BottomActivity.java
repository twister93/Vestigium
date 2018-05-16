package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Menu bottomNavigationViewMenu = navigation.getMenu();
        bottomNavigationViewMenu.findItem(R.id.mAlbum).setChecked(false);
        bottomNavigationViewMenu.findItem(R.id.mHome).setChecked(true);


        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        MainFragment fragment = new MainFragment();
        //MapsFragment fragment = new MapsFragment();
        ft.add(android.R.id.content, fragment).commit();


        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null){ //No user login
            Intent i =new Intent(BottomActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

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
                    //MapsFragment frag2 = new MapsFragment();
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
        }
        return super.onOptionsItemSelected(item);
    }

    /* public void logout(){
        firebaseAuth.signOut();
        if (Auth.GoogleSignInApi != null){
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()){
                        goLoginActivity();
                    }else {
                        Toast.makeText(BottomActivity.this,"Error cerrando sesión con Google",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (LoginManager.getInstance() != null){
            LoginManager.getInstance().logOut();
        }
    }

    private void goLoginActivity(){
        Intent i = new Intent(BottomActivity.this,LoginActivity.class);
        startActivity(i);
        finish();
    }*/

   /* @Override
    public void onBackPressed() {
        finish();
        Log.d("Metodo", "finish_activity");
        super.onBackPressed();
    }*/

   /* @Override
    protected void onStart() {
        //onBackPressed();
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
        Log.d("Metodo", "OnStart_Profile");
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
        googleApiClient.disconnect();
        Log.d("Metodo", "OnStop_Profile");
    }

    @Override
    protected void onPause() {
        super.onPause();
        googleApiClient.stopAutoManage(this);
        googleApiClient.disconnect();
        Log.d("Metodo", "OnPause_Profile");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Metodo", "OnResume_Profile");
        googleApiClient.connect();
    }

   @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Metodo", "OnRestart_Profile");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        googleApiClient.stopAutoManage(this);
        googleApiClient.disconnect();
        Log.d("Metodo", "OnDestroy_Profile");
    }*/

   @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



}
