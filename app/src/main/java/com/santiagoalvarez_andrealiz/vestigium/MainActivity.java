package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private GoogleApiClient googleApiClient;

    String user, pass, name, lastname, email;
    private TextView tvUseremail;
    private ImageView ivFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null){ //No user login
            Intent i =new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        tvUseremail = findViewById(R.id.tvUseremail);
        ivFoto = findViewById(R.id.ivFoto);
        inicializar();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mProfile) {
            goProfileActivity();
        }
        if (id == R.id.mExit) {
            goLoginActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializar() {
        firebaseAuth = FirebaseAuth.getInstance(); //instancia el objeto firebaseauth
        authStateListener = new FirebaseAuth.AuthStateListener() { //inicializa el listener
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser(); // carga los datos del usuario una vez logueado
                if (firebaseUser != null) {//alguien está logueado
                    tvUseremail.setText("Correo Usuario: "+firebaseUser.getEmail());
                    Picasso.get().load(firebaseUser.getPhotoUrl()).into(ivFoto);
                } else {
                    Log.d("FirebaseUser", "El usuario ha cerrado sesión");
                }
            }
        };

        //Inicialización con cuenta Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }


    public void logoutclicked(View view) { logout(); }
    public void logout(){
        firebaseAuth.signOut();
        if (Auth.GoogleSignInApi != null){
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()){
                        goLoginActivity();
                    }else {
                        Toast.makeText(MainActivity.this,"Error cerrando sesión con Google",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (LoginManager.getInstance() != null){
            LoginManager.getInstance().logOut();
        }
    }
    private void goLoginActivity(){
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void goProfileActivity(){
        Intent i = new Intent(MainActivity.this,ProfileActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
        googleApiClient.disconnect();
    }
    protected void onPause() {
        super.onPause();
        googleApiClient.stopAutoManage(this);
        googleApiClient.disconnect();
    }
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
    }
    protected void onDestroy() {
        super.onDestroy();
        googleApiClient.stopAutoManage(this);
        googleApiClient.disconnect();
    }

    @Override
    public void onBackPressed() {
        finish();
        Log.d("Metodo", "finish_main");
        super.onBackPressed();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
