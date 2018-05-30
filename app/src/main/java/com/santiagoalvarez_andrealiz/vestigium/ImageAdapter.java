package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Context;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;

    public Integer[] images = {

            R.drawable.ic_account_box,
            R.drawable.ic_camera_alt,
            R.drawable.ic_stop,
            R.drawable.vestigium
    };

    public ImageAdapter (Context c){
        context = c;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(images[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setLayoutParams(new GridView.LayoutParams(450,450));

        return imageView;
    }
}
