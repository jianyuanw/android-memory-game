package com.example.memorygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    // Responsible for displaying items in RecyclerView
    // Creates rows and maps items in list to rows
    ArrayList<String> urls;
    Context context;
    HashMap<Integer, String> selected;
    final private ListItemClickListener onClickListener;

    public RecyclerAdapter(Context context) {
        this.context = context;
        urls = new ArrayList<>();
        selected = new HashMap<>();
        this.onClickListener = (ListItemClickListener) context;
    }

    public void addUrl(String url) {
        urls.add(url);
        this.notifyItemChanged(urls.size() - 1);
    }

    public void clearUrls() {
        urls.clear();
        selected.clear();
        this.notifyDataSetChanged();
    }

    public int countUrls() {
        return urls.size();
    }

    public ArrayList<String> getSelectedUrls() {
        return new ArrayList<>(selected.values());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create individual rows necessary
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageView imageView = holder.imageView;
        ImageView tickBox = holder.tickBox;

        Glide.with(context).load(urls.get(position)).into(imageView);

        imageView.setTag(urls.get(position));

        if (selected.containsKey(position)) {
            imageView.setBackground(ContextCompat.getDrawable(context, R.drawable.green_border));
            tickBox.setVisibility(View.VISIBLE);
        } else {
            imageView.setBackground(null);
            tickBox.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        // Represents number of rows in your RecyclerView
        return urls.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        ImageView tickBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            tickBox = itemView.findViewById(R.id.tickBox);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (selected.containsKey(position)) {
                selected.remove(position);
            } else {
                selected.put(position, (String) imageView.getTag());
            }
            onClickListener.onListItemClick(position);
        }
    }
}
