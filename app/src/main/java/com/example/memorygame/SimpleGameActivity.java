package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.state.State;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class SimpleGameActivity extends AppCompatActivity implements View.OnClickListener{

    ArrayList<ImageView> images = new ArrayList<>();
    Button backButton;
    boolean imageSelected = false;
    boolean paused = false;
    ImageView previous;
    TextView scoreText;
    int maxGuesses = 5;
    int guesses = 0;
    int score = 0;
    ArrayList<Integer> selectList = new ArrayList<>();
    Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simplegame);

        scoreText = findViewById(R.id.scoreText);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        ConstraintLayout gameLayout = findViewById(R.id.matchGameLayout);

        Random r = new Random();

        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        for (int i = 0; i < gameLayout.getChildCount(); i++) {
            if (gameLayout.getChildAt(i) instanceof ImageView) {
                ImageView image = (ImageView) gameLayout.getChildAt(i);
                image.setOnClickListener(this);
                images.add(image);
                selectList.add(image.getId());
            }
        }

        for (int i = 1; i <= 6; i++) {
            int randomNumber = r.nextInt(selectList.size());
            int firstSelected = selectList.remove(randomNumber);
            randomNumber = r.nextInt(selectList.size());
            int secondSelected = selectList.remove(randomNumber);
            findViewById(firstSelected).setTag(new File(dir, "selectedImage" + i + ".jpg").getPath());
            findViewById(secondSelected).setTag(new File(dir, "selectedImage" + i + ".jpg").getPath());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backButton) {
            finish();
        } else {
            ImageView image = (ImageView) v;
            if (!paused) {
                paused = true;
                Glide.with(this).load(new File((String) image.getTag())).into(image);
                if (!imageSelected) {
                    previous = image;
                    imageSelected = true;
                    paused = false;
                } else {
                    if (previous.getTag().equals(image.getTag())) {
                        scoreText.setText(String.format(Locale.ENGLISH, "Score: %d", ++score));
                        imageSelected = false;
                        paused = false;
                        if (score == 6) {
                            Toast.makeText(this, "You Win!", Toast.LENGTH_SHORT).show();
                            paused = true;
                        }
                    } else {
                        guesses++;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                previous.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher_background));
                                image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher_background));
                                paused = false;
                                imageSelected = false;
                            }
                        }, 2500);
                    }
                }
            }
        }
    }

}