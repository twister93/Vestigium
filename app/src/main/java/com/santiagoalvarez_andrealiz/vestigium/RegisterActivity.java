package com.santiagoalvarez_andrealiz.vestigium;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/**
 * Created by andrealiz on 3/04/18.
 */

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    Button btSignup;
    EditText etName, etLastname, etLogin, etPass, etPass2, etEmail;
    String name, lastname, user, pass, pass2, email;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etLastname = findViewById(R.id.etLastname);
        etLogin = findViewById(R.id.etLogin);
        etPass = findViewById(R.id.etPass);
        etPass2 = findViewById(R.id.etPass2);
        etEmail = findViewById(R.id.etEmail);
        btSignup = findViewById(R.id.btSignup);

        inicializar();
    }

    private void inicializar(){
        firebaseAuth = FirebaseAuth.getInstance(); //instancia el objeto firebaseauth
        authStateListener = new FirebaseAuth.AuthStateListener() { //inicializa el listener
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser(); // carga los datos del usuario una vez logueado
                if (firebaseUser != null){//alguien está logueado
                    Log.d("FirebaseUser","Usuario Logueado: "+firebaseUser.getDisplayName());
                    Log.d("FirebaseUser","Usuario Logueado: "+firebaseUser.getEmail());
                } else {
                    Log.d("FirebaseUser","El usuario ha cerrado sesión");
                }
            }
        };

    }

    public void signupclicked(View view) {

        int id = view.getId();
        name = etName.getText().toString();
        lastname = etLastname.getText().toString();
        user = etLogin.getText().toString();
        pass = etPass.getText().toString();
        pass2 = etPass2.getText().toString();
        email = etEmail.getText().toString();

        if (id == R.id.btSignup && TextUtils.isEmpty(name) || TextUtils.isEmpty(lastname) || TextUtils.isEmpty(pass)
                || TextUtils.isEmpty(pass2) || TextUtils.isEmpty(email)) {
            //Toast
            Toast.makeText(this, "Faltan campos por llenar, por favor verifique para continuar proceso de registro", Toast.LENGTH_LONG).show();
        } else if (!Objects.equals(etPass.getText().toString(), etPass2.getText().toString())) {
            Toast.makeText(this, "Por favor revisar contraseñas, NO SON IGUALES!!", Toast.LENGTH_SHORT).show();
        } else if(pass.length() < 6){
            Toast.makeText(this, "La contraseña debe ser de mínimo 6 carácteres", Toast.LENGTH_SHORT).show();
        }else {
            signup(email,pass);

          /*  Intent intent = new Intent();//No le digo donde estoy ni para donde voy
            intent.putExtra("user", user);
            intent.putExtra("pass", pass);
            intent.putExtra("name", name);
            intent.putExtra("lastname", lastname);
            intent.putExtra("email", email);
            setResult(RESULT_OK, intent);
            finish();*/
        }
    }

    public void signup(String email, String pass){
        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) { //Informa si fue exitosa la creación del usuario en Firebase o no
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"Cuenta creada", Toast.LENGTH_SHORT).show();
                    goLoginActivity();
                }else {
                    Toast.makeText(RegisterActivity.this,"Error al crear cuenta"/*task.getException().toString()*/, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void goLoginActivity(){
        Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(i);
        finish();
    }


}
