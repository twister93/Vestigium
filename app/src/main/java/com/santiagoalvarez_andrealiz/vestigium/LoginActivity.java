package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by andrealiz on 3/04/18.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;

    private SignInButton btnSignInGoogle;

//
    TextView tvRegister;
    EditText etUser, etEmail, etPass;
    String user, pass, name, lastname, email;
    Button btLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSignInGoogle = findViewById(R.id.btnSignInGoogle);

        btnSignInGoogle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(i,1);//LOGIN_CON_GOOGLE = 1
            }
        });

        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            user = "1237842543420389702932874603894503487512890309";
        } else {
            user = extra.getString("user");
            pass = extra.getString("pass");
            email = extra.getString("email");
            lastname = extra.getString("lastname");
            name = extra.getString("name");
        }


        tvRegister = findViewById(R.id.tvRegister);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        btLogin = findViewById(R.id.btLogin);

        inicializar();

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
        }

        /*if (requestCode == 1234 && resultCode == RESULT_OK) {
            user = String.valueOf(data.getExtras().getString("user"));
            pass = String.valueOf(data.getExtras().getString("pass"));
            name = String.valueOf(data.getExtras().getString("name"));
            lastname = String.valueOf(data.getExtras().getString("lastname"));
            email = String.valueOf(data.getExtras().getString("email"));
        }*/

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
                        }
                    });
        }else {
            Toast.makeText(LoginActivity.this,"Autencitación con Google exitosa",Toast.LENGTH_LONG).show();
        }
    }

    private void goMainActivity(){
        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    public void loginclicked(View view) {
        email = etEmail.getText().toString();
        pass = etPass.getText().toString();
        login(email,pass);
        /*int id = view.getId();

        if (id == R.id.btLogin) {
            if (user.equals(etUser.getText().toString()) && pass.equals(etPass.getText().toString())) {
                //Actividad principal -> enviar datos a MainActivity para luego enviar a perfil
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("pass", pass);
                intent.putExtra("name", name);
                intent.putExtra("lastname", lastname);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            } else {
                //Toast
                Toast.makeText(this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    public void login(String email, String pass){
        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                   Intent i = new Intent(LoginActivity.this,MainActivity.class);
                   startActivity(i);
                   finish();
                }else {
                    Toast.makeText(LoginActivity.this,"Error en inicio de sesión"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        finish();
        Log.d("Metodo", "finish_Login");
        super.onBackPressed();
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
