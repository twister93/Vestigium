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

import java.util.Objects;

/**
 * Created by andrealiz on 3/04/18.
 */

public class RegisterActivity extends AppCompatActivity {

    Button btSignup;
    EditText etName, etLastname, etLogin, etPass, etPass2, etEmail;
    String name, lastname,user, pass, pass2, email;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

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

        //start firebase components
        inicializar();
    }
    private void inicializar(){
        //Conecting with firebase service
        firebaseAuth = FirebaseAuth.getInstance();
        //Listener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //bring the information user (the current user)
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){ //someone logged
                    Log.d("FirebaseUser", "Usuario Logeado: "+firebaseUser.getEmail());
                }else{
                    Log.d("FirebaseUser","No hay usuario logeado");
                }
            }
        };
    }

    public void signup(View view) {
        int id = view.getId();


        name = etName.getText().toString();
        lastname = etLastname.getText().toString();
        user = etLogin.getText().toString();
        pass = etPass.getText().toString();
        pass2 = etPass2.getText().toString();
        email = etEmail.getText().toString();

        signup_db(email,pass);

        if (id==R.id.btSignup && TextUtils.isEmpty(name) ||TextUtils.isEmpty(lastname) || TextUtils.isEmpty(pass)
                ||TextUtils.isEmpty(pass2)||TextUtils.isEmpty(email)){
            //Toast
            Toast.makeText(this, "Faltan campos por llenar, por favor verifique para continuar proceso de registro", Toast.LENGTH_LONG).show();
        }else if (!Objects.equals(etPass.getText().toString(), etPass2.getText().toString())){
            Toast.makeText(this, "Por favor revisar contraseñas, NO SON IGUALES!!", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent();//No le digo donde estoy ni para donde voy
            intent.putExtra("user", user);
            intent.putExtra("pass", pass);
            intent.putExtra("name",name);
            intent.putExtra("lastname",lastname);
            intent.putExtra("email",email);
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    private void signup_db(String mail, String passwd){
        firebaseAuth.createUserWithEmailAndPassword(mail, passwd).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Cuenta Creada", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(RegisterActivity.this, "NO", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
