package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by andrealiz on 3/04/18.
 */

public class ProfileActivity extends AppCompatActivity {

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
            user = getIntent().getExtras().getString("user");
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
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        user = getIntent().getExtras().getString("user");
        name = getIntent().getExtras().getString("name");
        pass = getIntent().getExtras().getString("pass");
        lastname = getIntent().getExtras().getString("lastname");
        email = getIntent().getExtras().getString("email");
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("name", name);
        intent.putExtra("pass", pass);
        intent.putExtra("lastname", lastname);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    /*@Override
    protected void onStart() {
        //onBackPressed();

        super.onStart();
        Log.d("Metodo", "OnStart_Profile");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Metodo", "OnResume_Profile");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Metodo", "OnPause_Profile");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Metodo", "OnStop_Profile");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Metodo", "OnRestart_Profile");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Metodo", "OnDestroy_Profile");
    } */

}