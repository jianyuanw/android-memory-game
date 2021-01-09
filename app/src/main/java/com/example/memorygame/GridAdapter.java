package com.example.memorygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class GridAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflter;
    private List<File> image;

    public GridAdapter(Context context, List<File> image) {
        this.context = context;
        this.image = image;
        inflter = (LayoutInflater.from(context));
        
    }

    @Override
    public int getCount() {
        return image.size() * 2;
    }
    @Override
    public Object getItem(int pos) {
        return null;
    }
    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        if(view == null){
            view = inflter.inflate(R.layout.grid_item, viewGroup, false);
        }

        ImageView imageView = view.findViewById(R.id.gridItem);
        File images = image.get(pos);
        Bitmap bitmap = BitmapFactory.decodeFile(images.getPath());
        imageView.setImageBitmap(bitmap);

        return view;
    }

}
