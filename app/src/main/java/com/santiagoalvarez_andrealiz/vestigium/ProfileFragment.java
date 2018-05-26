package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * Created by andrealiz on 5/05/18.
 */

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;


    String user, pass, name, lastname, email;
    private TextView tvEmailUser, tvUserName,tvPhoneUser;
    private ImageView ivPhoto;

    public ProfileFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null){ //No user login
            Intent i =new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
        }

        tvEmailUser = view.findViewById(R.id.tvEmailUser);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvPhoneUser = view.findViewById(R.id.tvPhoneUser);
        ivPhoto = view.findViewById(R.id.ivPhoto);
        inicializar();



        return view;
    }
    private void inicializar() {
        firebaseAuth = FirebaseAuth.getInstance(); //instancia el objeto firebaseauth
        authStateListener = new FirebaseAuth.AuthStateListener() { //inicializa el listener
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser(); // carga los datos del usuario una vez logueado
                if (firebaseUser != null) {//alguien está logueado
                    tvEmailUser.setText(firebaseUser.getEmail());
                    tvUserName.setText(firebaseUser.getDisplayName());
                    tvPhoneUser.setText(firebaseUser.getPhoneNumber());
                    Picasso.get().load(firebaseUser.getPhotoUrl()).into(ivPhoto);
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
        googleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(getActivity()))
                .enableAutoManage(getActivity(), (GoogleApiClient.OnConnectionFailedListener) getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
        googleApiClient.disconnect();
    }
    public void onPause() {
        super.onPause();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }
    public void onResume() {
        super.onResume();
        googleApiClient.connect();
    }
    public void onDestroy() {
        super.onDestroy();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }

}
