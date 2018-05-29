package com.santiagoalvarez_andrealiz.vestigium;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santiagoalvarez_andrealiz.vestigium.model.Albums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrealiz on 7/05/18.
 */

public class AlbumFragment extends Fragment {

    RecyclerView rv;
    List<Albums> albums;
    Adapter adapter;

    FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public AlbumFragment(){

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        rv = view.findViewById(R.id.my_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));


        albums = new ArrayList<>();
        adapter = new Adapter(getActivity(),albums);

        rv.setAdapter(adapter);


        databaseReference.child("users").child(firebaseUser.getUid()).child("albums").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                albums.removeAll(albums);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Albums album = snapshot.getValue(Albums.class);
                    albums.add(album);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}
