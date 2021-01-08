package com.example.memorygame;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private final int GRID_COLUMNS = 3;

    private ArrayList<GameImage> gameImages;
    private int numberOfImagesOpened = 0;
    private ImageView firstImage;
    private ImageView secondImage;
    private int firstImageId;
    private int secondImageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViewById(R.id.backButton).setOnClickListener(this);

        RecyclerView gameRecyclerView = findViewById(R.id.gameRecyclerView);
        gameImages = GameImage.createGameImageList(this);
        GameImagesAdapter adapter = new GameImagesAdapter(gameImages);
        adapter.setOnItemClickListener(new GameImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                // Open first image
                if (numberOfImagesOpened == 0) {
                    firstImage = itemView.findViewById(R.id.gameImageView);
                    firstImage.setForeground(null);
                    firstImageId = gameImages.get(position).getId();
                    numberOfImagesOpened = 1;

                // Open second image
                } else if (numberOfImagesOpened == 1) {
                    secondImage = itemView.findViewById(R.id.gameImageView);
                    secondImage.setForeground(null);
                    secondImageId = gameImages.get(position).getId();

                    // Images matched
                    // TODO: Correct match logic

                    // Images did not match
                    if (firstImageId != secondImageId) {
                        Toast.makeText(GameActivity.this, "Wrong! Closing images...",
                                Toast.LENGTH_SHORT).show();
                        // Close both images after 2 seconds
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                firstImage.setForeground(new ColorDrawable(
                                        ContextCompat.getColor(
                                                GameActivity.this, R.color.teal_200)));
                                secondImage.setForeground(new ColorDrawable(
                                        ContextCompat.getColor(
                                                GameActivity.this, R.color.teal_200)));
                            }
                        }, 2000);
                    }

                    numberOfImagesOpened = 0;
                }
            }
        });
        gameRecyclerView.setAdapter(adapter);
        gameRecyclerView.setLayoutManager(new GridLayoutManager(this, GRID_COLUMNS));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            finish();
        }
    }
}