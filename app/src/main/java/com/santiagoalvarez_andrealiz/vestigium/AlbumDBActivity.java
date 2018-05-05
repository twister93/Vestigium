package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Context;
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
import android.widget.EditText;
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
import com.santiagoalvarez_andrealiz.vestigium.model.Points;

import java.util.ArrayList;

public class AlbumDBActivity extends AppCompatActivity {

    private EditText etAlbumName,etCreationD,etFavorite;
    private ListView listView;
    private ArrayList<Albums> albumslist;
    private DatabaseReference databaseReference; //referencia que necesitamos
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_db);
        etAlbumName = findViewById(R.id.etAlbumName);
        etCreationD = findViewById(R.id.etCreationD);
        etFavorite = findViewById(R.id.etFavorite);

        listView = findViewById(R.id.listView);


        albumslist = new ArrayList<>();

        final AlbumDBActivity.AlbumAdapter albumAdapter = new AlbumDBActivity.AlbumAdapter(this, albumslist);
        listView.setAdapter(albumAdapter);


        FirebaseDatabase.getInstance(); //.setPersistenceEnabled(true); //Toma instancia de la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //metodo para cargar los datos desde la base de datos
        databaseReference.child("albums").addValueEventListener(new ValueEventListener() {
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
    public void guardarClicked (View view){
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        //id - databaseReference.push().getKey()
        Albums albums= new Albums (databaseReference.push().getKey(),
                etAlbumName.getText().toString(),
                etCreationD.getText().toString(),
                etFavorite.getText().toString());
        Log.d("FirebaseSave", "Entra al guardar");
        databaseReference.child("users").child(firebaseUser.getUid()).child("albums").child(albums.getAlbumId()).setValue(albums);

        Points points = new Points(databaseReference.push().getKey(),
                "-75.5684253","6.2639381");
        databaseReference.child("users").child(firebaseUser.getUid()).child("albums").child(albums.getAlbumId()).child("points").child(points.getPointId()).setValue(points);
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
}


