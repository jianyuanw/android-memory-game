package com.example.memorygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GameImagesAdapter extends RecyclerView.Adapter<GameImagesAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView gameImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gameImageView = itemView.findViewById(R.id.gameImageView);
        }
    }

    private List<GameImage> gameImages;

    public GameImagesAdapter(List<GameImage> gameImages) {
        this.gameImages = gameImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.game_row_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameImage gameImage = gameImages.get(position);
        Bitmap bitmap = BitmapFactory.decodeFile(gameImage.getFilePath());

        ImageView gameImageView = holder.gameImageView;
        gameImageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return gameImages.size();
    }
}
