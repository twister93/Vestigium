package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    String user,pass,name,lastname,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.mProfile){
            user = getIntent().getExtras().getString("user");
            name = getIntent().getExtras().getString("name");
            pass = getIntent().getExtras().getString("pass");
            lastname = getIntent().getExtras().getString("lastname");
            email = getIntent().getExtras().getString("email");
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("name",name);
            intent.putExtra("pass",pass);
            intent.putExtra("lastname",lastname);
            intent.putExtra("email",email);
            startActivity(intent);
            finish();
        }
        if(id == R.id.mExit){
            user = getIntent().getExtras().getString("user");
            name = getIntent().getExtras().getString("name");
            pass = getIntent().getExtras().getString("pass");
            lastname = getIntent().getExtras().getString("lastname");
            email = getIntent().getExtras().getString("email");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("name",name);
            intent.putExtra("pass",pass);
            intent.putExtra("lastname",lastname);
            intent.putExtra("email",email);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        Log.d("Metodo", "finish_main");
        super.onBackPressed();
    }

    /*@Override
    protected void onStart() {
        //onBackPressed();

        super.onStart();
        Log.d("Metodo", "OnStart_Main");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Metodo", "OnResume_Main");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Metodo", "OnPause_Main");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Metodo", "OnStop_Main");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Metodo", "OnRestart_Main");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Metodo", "OnDestroy_Main");
    }*/
}
