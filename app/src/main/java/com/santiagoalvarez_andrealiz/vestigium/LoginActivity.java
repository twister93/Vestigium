package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santiagoalvarez_andrealiz.vestigium.model.Albums;
import com.santiagoalvarez_andrealiz.vestigium.model.Users;
import com.santiagoalvarez_andrealiz.vestigium.model.Usuarios;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by andrealiz on 3/04/18. Branch TB_06
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private GoogleApiClient googleApiClient;
    private SignInButton btnSignInGoogle;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private DatabaseReference databaseReference;


    TextView tvRegister;
    EditText etUser, etEmail, etPass, etName;
    String user, pass, name, lastname, email;
    Button btLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseDatabase.getInstance(); //.setPersistenceEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnSignInGoogle = findViewById(R.id.btnSignInGoogle);

        btnSignInGoogle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(i,1);//LOGIN_CON_GOOGLE = 1
            }
        });

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.santiagoalvarez_andrealiz.vestigium",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        etName = findViewById(R.id.etName);
        tvRegister = findViewById(R.id.tvRegister);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        btLogin = findViewById(R.id.btLogin);
        loginButton = findViewById(R.id.btnSignInFacebook);
        loginButton.setReadPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Login Facebook: ","OK");
                signInFacebook(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d("Login Facebook: ","Cancelado por usuario");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Login Facebook: ","Error");
            }
        });

        inicializar();
    }

    private void signInFacebook(AccessToken accessToken){

        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());

        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Sesión iniciada", Toast.LENGTH_SHORT).show();
                    goMainActivity();
                    saveClicked();
                }else{
                    //if(task.getException().equals())
                    Log.d("Facebook Error:",task.getException().toString());
                    //Toast.makeText(LoginActivity.this, "Error:"+task.getException(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void signInGoogle (GoogleSignInResult googleSignInResult){
        if (googleSignInResult.isSuccess()){//hasta acá ya es exitoso en google, falta firebase
            AuthCredential authCredential = GoogleAuthProvider.getCredential(
                    googleSignInResult.getSignInAccount().getIdToken(),null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            goMainActivity();
                            saveClicked();
                        }
                    });
        }else {
            Toast.makeText(LoginActivity.this,"Autencitación con Google exitosa",Toast.LENGTH_LONG).show();
        }
    }

    private void inicializar() {
        //Inicialización con Firebase Email y Contraseña
        firebaseAuth = FirebaseAuth.getInstance(); //instancia el objeto firebaseauth
        authStateListener = new FirebaseAuth.AuthStateListener() { //inicializa el listener
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser(); // carga los datos del usuario una vez logueado
                if (firebaseUser != null) {//alguien está logueado
                    Log.d("FirebaseUser", "Usuario Logueado: " + firebaseUser.getDisplayName());
                    Log.d("FirebaseUser", "Usuario Logueado: " + firebaseUser.getEmail());
                    goMainActivity();
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

    public void register(View view) {
        int id = view.getId();

        if (id == R.id.tvRegister) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivityForResult(intent, 1234);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            signInGoogle(googleSignInResult);
        } else if (requestCode == 1234 && resultCode == RESULT_OK) {
            Users users = new Users (databaseReference.push().getKey(),
                    etName.getText().toString(),
                    etEmail.getText().toString());
            databaseReference.child("usuarios").child(users.getId()).setValue(users);
        } else {
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }

    }

    private void goMainActivity(){
        Intent i = new Intent(LoginActivity.this,/*MainActivity*//*AlbumDBActivity*/BottomActivity.class);
        startActivity(i);
        finish();
    }

    public void loginclicked(View view) {
        email = etEmail.getText().toString();
        pass = etPass.getText().toString();
        login(email,pass);
    }

    public void login(String email, String pass) {
        if (email.equals("")|| pass.equals("")) {
            Toast.makeText(LoginActivity.this, "Escriba su usuario y contraseña", Toast.LENGTH_SHORT).show();
        }else{
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                       goMainActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error en inicio de sesión" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        Log.d("vacio: ", "Usuario no registrado");
                    }
                }
            });
        }
    }



    private void crearUsuario(){
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("usuario").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.d("Existe: ", "SI");
                } else {
                    Log.d("Existe","NO");
                    //Usuarios usuarios = new Usuarios(firebaseUser.getUid(),  firebaseUser.getDisplayName(), firebaseUser.getPhoneNumber(), firebaseUser.getEmail());
                    databaseReference.child("usuarios").child(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void saveClicked(){
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.d("Existe: ", "SI");
                } else {
                    Log.d("Existe","NO");
                    Users users = new Users(firebaseUser.getUid(),  firebaseUser.getDisplayName(), firebaseUser.getEmail(), "url foto");
                    databaseReference.child("users").child(firebaseUser.getUid()).setValue(users);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        Log.d("Metodo", "finish_Login");
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

    /*
    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Metodo", "OnPause_Login");
    }

   @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Metodo", "OnRestart_Login");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Metodo", "OnDestroy_Login");
    }*/

}
