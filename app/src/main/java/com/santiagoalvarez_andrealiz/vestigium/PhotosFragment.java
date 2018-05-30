package com.santiagoalvarez_andrealiz.vestigium;


import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

import java.io.File;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PhotosFragment extends Fragment {

    GridView gv;
    ArrayList<File> myimages;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        /*gv = view.findViewById(R.id.gvPhotos);
        gv.setAdapter( new GridAdapter());*/



        //_____________________________________________________________
        //ArrayList<File> myimages = findImages(Environment.getExternalStorageDirectory());
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/Test6");
        myimages = findImages(storageDir);

        for (int i=0; i<myimages.size();i++){
            Toast.makeText(getActivity().getApplicationContext(), myimages.get(i).getName(), Toast.LENGTH_SHORT).show();
        }

        /*gv = view.findViewById(R.id.gvPhotos);
        gv.setAdapter( new GridAdapter());*/

        //_____________________________________________________________


       GridView gridView = (GridView) view.findViewById(R.id.gvPhotos);

        gridView.setAdapter(new ImageAdapter(getActivity()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(),FullImageActivity.class);
                intent.putExtra("id", position);
                startActivity(intent);
            }
        });

        return view;

    }
/*
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

            convertView = getLayoutInflater().inflate(R.layout.my_singlegrid,parent,false);
            ImageView iv = convertView.findViewById(R.id.ivGrid);

            iv.setImageURI(Uri.parse(getItem(position).toString()));

            return convertView;
        }
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        File storageDir = getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/Test6");
        myimages = findImages(storageDir);

    }*/


    /*public ArrayList<File> findImages(File root){
        ArrayList<File> a1 = new ArrayList<File>();
        File[] files = root.listFiles();
        for(File singleFile : files){
            if (singleFile.isDirectory() && !singleFile.isHidden()){
                a1.addAll(findImages(singleFile));
            }else {
                if(singleFile.getName().endsWith(".jpg")){
                    a1.add(singleFile);
                }
            }
        }
        return a1;
   }*/
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
