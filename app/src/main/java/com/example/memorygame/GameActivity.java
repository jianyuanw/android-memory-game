package com.example.memorygame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<GameImage> gameImages;
    private int gridColumns = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViewById(R.id.backButton).setOnClickListener(this);

        RecyclerView gameRecyclerView = findViewById(R.id.gameRecyclerView);
        gameImages = GameImage.createGameImageList(this);
        GameImagesAdapter adapter = new GameImagesAdapter(gameImages);
        gameRecyclerView.setAdapter(adapter);
        gameRecyclerView.setLayoutManager(new GridLayoutManager(this, gridColumns));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            finish();
        }
    }
}