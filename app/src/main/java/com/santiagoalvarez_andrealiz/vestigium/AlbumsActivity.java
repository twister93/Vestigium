package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santiagoalvarez_andrealiz.vestigium.model.Albums;

import java.util.ArrayList;

public class AlbumsActivity extends AppCompatActivity {
    //Para visualizar información en Database
    private ListView listView;
    private ArrayList<Albums> albumslist;
    private DatabaseReference databaseReference; //referencia que necesitamos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        listView = findViewById(R.id.listView);

        //-----------------Chequeo de usuario logueado------------
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null){ //No user login
            Intent i =new Intent(AlbumsActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        //List View
        albumslist = new ArrayList<>();
        final AlbumsActivity.AlbumAdapter albumAdapter = new AlbumsActivity.AlbumAdapter(this, albumslist);
        listView.setAdapter(albumAdapter);
        FirebaseDatabase.getInstance(); //.setPersistenceEnabled(true); //Toma instancia de la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //metodo para cargar los datos desde la base de datos
        databaseReference.child("users").child("albums").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                albumslist.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Albums albums = snapshot.getValue(Albums.class);
                        Log.d("Album name",albums.getAlbumName());
                        albumslist.add(albums);
                    }
                }
                albumAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String uid = albumslist.get(position).getAlbumId();
                albumslist.remove(position);
                albumslist.remove(position);
                databaseReference.child("albums").child(uid).removeValue();
                return false;
            }
        });


    }
    //Adaptador para pasarle el listado de las personas
    class AlbumAdapter extends ArrayAdapter<Albums>{

        public AlbumAdapter(@NonNull Context context, ArrayList<Albums> data){
            super(context, R.layout.list_item, data);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View item = inflater.inflate(R.layout.list_item,null);

            Albums albums = getItem(position);

            TextView albumName = item.findViewById(R.id.tvName);
            albumName.setText(albums.getAlbumName());

            TextView creationD = item.findViewById(R.id.tvEmail);
            creationD.setText(albums.getCreationDate());

            TextView favorite = item.findViewById(R.id.tvphone);
            favorite.setText(albums.getFavorite());

            return item;
        }
    }

    public void gohomeActivity(View view) {
        Intent i = new Intent(AlbumsActivity.this,AlbumDBActivity.class);
        startActivity(i);
        finish();
    }

    public void goprofileActivity(View view) {
        Intent i = new Intent(AlbumsActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}
