package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
 * Created by andrealiz on 3/04/18.
 */

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;

    String user = "*", pass, name, lastname, email;
    TextView etNameP, etLastnameP, etUserP, etEmailP;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        etNameP = findViewById(R.id.etNameP);
        etLastnameP = findViewById(R.id.etLastnameP);
        etUserP = findViewById(R.id.etUserP);
        etEmailP = findViewById(R.id.etEmailP);

        user = getIntent().getExtras().getString("user");
        name = getIntent().getExtras().getString("name");
        pass = getIntent().getExtras().getString("pass");
        lastname = getIntent().getExtras().getString("lastname");
        email = getIntent().getExtras().getString("email");

        etNameP.setText(name);
        etLastnameP.setText(lastname);
        etUserP.setText(user);
        etEmailP.setText(email);

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null){ //No user login
            Intent i =new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        inicializar();
    }

    private void inicializar() {
        firebaseAuth = FirebaseAuth.getInstance(); //instancia el objeto firebaseauth
        authStateListener = new FirebaseAuth.AuthStateListener() { //inicializa el listener
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser(); // carga los datos del usuario una vez logueado
                if (firebaseUser != null) {//alguien está logueado
                    //tvUseremail.setText("Correo Usuario: "+firebaseUser.getEmail());
                    //Picasso.get().load(firebaseUser.getPhotoUrl()).into(ivFoto);
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


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mHome) {
            onBackPressed();
        }
        if (id == R.id.mExit) {
            logout();
            /*user = getIntent().getExtras().getString("user");
            name = getIntent().getExtras().getString("name");
            pass = getIntent().getExtras().getString("pass");
            lastname = getIntent().getExtras().getString("lastname");
            email = getIntent().getExtras().getString("email");
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("name", name);
            intent.putExtra("pass", pass);
            intent.putExtra("lastname", lastname);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();*/
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout(){
        firebaseAuth.signOut();
        if (Auth.GoogleSignInApi != null){
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()){
                        goLoginActivity();
                    }else {
                        Toast.makeText(ProfileActivity.this,"Error cerrando sesión con Google",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (LoginManager.getInstance() != null){
            LoginManager.getInstance().logOut();
        }
    }
    private void goLoginActivity(){
        Intent i = new Intent(ProfileActivity.this,LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
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

    /*@Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Metodo", "OnRestart_Profile");
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        googleApiClient.stopAutoManage(this);
        googleApiClient.disconnect();
        Log.d("Metodo", "OnDestroy_Profile");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}