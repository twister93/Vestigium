package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santiagoalvarez_andrealiz.vestigium.model.Albums;

import java.util.List;

/**
 * Created by andrealiz on 27/05/18.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.AlbumsviewHolder> {

    List<Albums> albums;
    LayoutInflater inflater;
    private Context mContext;


    FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public Adapter(Context context, List<Albums> albums) {
        inflater = LayoutInflater.from(context);
        this.albums = albums;
        mContext = context;
    }

    @Override
    public AlbumsviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recycler,parent, false);
        AlbumsviewHolder holder =new AlbumsviewHolder(v);


        return holder;
    }

    @Override
    public void onBindViewHolder(final AlbumsviewHolder holder, final int position) {
        holder.tvAlbum.setText(albums.get(position).getAlbumName());
        holder.tvDate.setText(albums.get(position).getCreationDate());
        final String favorite = albums.get(position).getFavorite();

        if (favorite.equals("yes")){
            holder.ivFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.ivFavorite.setImageResource(R.drawable.ic_favorite_border);
        }

        holder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Log.d("favorite","Favorite: "+favorite);
                if (favorite.equals("no")){
                   databaseReference.child("users").child(firebaseUser.getUid()).child("albums")
                            .child(albums.get(position).getAlbumName()).child("favorite").setValue("yes");
                } else {
                    databaseReference.child("users").child(firebaseUser.getUid()).child("albums")
                            .child(albums.get(position).getAlbumName()).child("favorite").setValue("no");
                }
            }
        });

        holder.select_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("album","album click: "+albums.get(position).getAlbumName());
                Intent intent = new Intent(mContext, TabsActivity.class);
                intent.putExtra("album_name", albums.get(position).getAlbumName());
                databaseReference.child("users").child(firebaseUser.getUid()).child("flag_album").setValue(albums.get(position).getAlbumName());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    class AlbumsviewHolder extends RecyclerView.ViewHolder {

        TextView tvAlbum, tvDate;
        ImageView ivFavorite;
        LinearLayout select_album;

        public AlbumsviewHolder(View itemView) {
            super(itemView);
            tvAlbum = itemView.findViewById(R.id.tvAlbum);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            select_album = itemView.findViewById(R.id.select_album);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
        }
    }
}
