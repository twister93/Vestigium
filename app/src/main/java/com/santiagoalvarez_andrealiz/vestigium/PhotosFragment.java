package com.santiagoalvarez_andrealiz.vestigium;


import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santiagoalvarez_andrealiz.vestigium.model.LocationService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PhotosFragment extends Fragment {

    GridView gv;
    ArrayList<File> myimages;

    //-------------------------------------
    String albumName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_photos, container, false);


        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


        databaseReference.child("users").child(firebaseUser.getUid()).child("flag_album").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//valor de la flag
                //if (isMyServiceRunning(LocationService.class)){
                albumName = dataSnapshot.getValue(String.class);///etodo esto es lo que necesito
                Log.d("photos", "ALBUM SELECTED" + albumName);
                //getPoints();
                //}
                try {
                    File storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/" + albumName);
                    myimages = findImages(storageDir);
                    Log.d("photos", "path: " + Environment.DIRECTORY_PICTURES+"/"+albumName);
                    gv = view.findViewById(R.id.gvPhotos);
                    gv.setAdapter( new GridAdapter());
                }catch (NullPointerException e){
                    Log.d("photos", "path check es null el hpta " + Environment.DIRECTORY_PICTURES+"/"+albumName +"Error "+e);
                }
                /*myimages = findImages(storageDir);
                Log.d("photos", "path: " + Environment.DIRECTORY_PICTURES+"/"+albumName);
                gv = view.findViewById(R.id.gvPhotos);
                gv.setAdapter( new GridAdapter());*/


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });






        //_____________________________________________________________
        //ArrayList<File> myimages = findImages(Environment.getExternalStorageDirectory());
        //File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES+albumName);
        //Log.d("photos", "path: " + Environment.DIRECTORY_PICTURES+"/"+albumName);
        //myimages = findImages(storageDir);

        //gv = view.findViewById(R.id.gvPhotos);
        //gv.setAdapter( new GridAdapter());


        /*for (int i=0; i<myimages.size();i++){
            Toast.makeText(getActivity().getApplicationContext(), myimages.get(i).getName(), Toast.LENGTH_SHORT).show();
        }*/




        return view;

    }

    class GridAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return myimages.size();
        }

        @Override
        public Object getItem(int position) {
            return myimages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Log.d("PhotosFragment", "Este es el Uri"+Uri.parse(getItem(position).toString()));

            Bitmap bitmap = BitmapFactory.decodeFile(getItem(position).toString());
            Matrix matrix = new Matrix();

            matrix.postRotate(90);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,500,500,true);

            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);


            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setImageBitmap(rotatedBitmap);
            //imageView.setImageURI(Uri.parse(getItem(position).toString()));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setLayoutParams(new GridView.LayoutParams(450,450));


            return imageView;
        }
    }

   ArrayList<File> findImages(File root) {
       ArrayList<File> a1 = new ArrayList<>();

       File[] files = root.listFiles();
       for (int i=0;i<files.length;i++) {
           if (files[i].isDirectory()) {
               a1.addAll(findImages(files[i]));
           } else {
               if (files[i].getName().endsWith(".jpg")) {
                   a1.add(files[i]);
               }
           }
       }
       return a1;
   }

}
