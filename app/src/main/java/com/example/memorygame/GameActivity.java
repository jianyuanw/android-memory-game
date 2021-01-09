package com.example.memorygame;

import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private final int GRID_COLUMNS = 3;

    private ArrayList<GameImage> gameImages;

    private int numberOfImagesOpened;
    private ImageView firstImage;
    private ImageView secondImage;
    private int firstImageId;
    private int secondImageId;
    private int score;
    private int maxScore;
    private boolean wrongImagePairIsStillOpen;

    private boolean timerIsRunning;
    private int timerSeconds;

    private TextView infoTextView;
    private String infoText;

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
                // Start timer on first click
                if (!timerIsRunning) {
                    timerIsRunning = true;
                    startTimer();
                }

                if (wrongImagePairIsStillOpen) {
                    waitToast();
                    return;
                }

                if (numberOfImagesOpened == 0) {
                    // Clicked on first image
                    firstImage = itemView.findViewById(R.id.gameImageView);
                    // Image already matched before, do not do anything
                    if (firstImage.getForeground() == null) {
                        return;
                    }
                    // Reveal image
                    flipCard(firstImage);
                    firstImageId = gameImages.get(position).getId();
                    numberOfImagesOpened = 1;

                } else if (numberOfImagesOpened == 1) {
                    // Clicked on second image
                    secondImage = itemView.findViewById(R.id.gameImageView);
                    // Image already matched before, do not do anything
                    if (secondImage.getForeground() == null) {
                        return;
                    }
                    // Reveal image
                    flipCard(secondImage);
                    secondImageId = gameImages.get(position).getId();

                    if (firstImageId == secondImageId) {
                        // Images matched
                        updateScore();

                        if (score == maxScore) {
                            // Game ended
                            stopTimer();
                            winGameText();
                            // Sound effect for winning
                            playSound(R.raw.win_audio);
                            returnToMainActivityAfterFourSeconds();
                        } else {
                            // Game not yet end
                            matchedText();
                            // Sound effect for matching
                            playSound(R.raw.success_bell2);
                        }
                    } else {
                        // Images did not match
                        wrongImagePairIsStillOpen = true;
                        didNotMatchText();
                        // Sound effect for wrong match
                        playSound(R.raw.failure_beep);
                        closeBothImagesAfterTwoSeconds();
                    }

                    numberOfImagesOpened = 0;
                }
            }
        });
        gameRecyclerView.setAdapter(adapter);
        gameRecyclerView.setLayoutManager(new GridLayoutManager(this, GRID_COLUMNS));

        numberOfImagesOpened = 0;
        score = 0;
        maxScore = gameImages.size() / 2;
        timerIsRunning = false;
        timerSeconds = 0;
        infoTextView = findViewById(R.id.textInfo);
    }

    public void playSound(int soundId) {
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), soundId);
        mp.start();
    }

    public void flipCard(View v) {
        if (v.getForeground() != null) {
            v.animate().withLayer().rotationY(90).setDuration(300).withEndAction(
                    new Runnable() {
                        @Override public void run() {
                            // second quarter turn
                            v.setForeground(null);
                            v.setRotationY(-90);
                            v.animate().withLayer().rotationY(0).setDuration(300).start();
                        }
                    }
            ).start();
        } else {
            v.animate().withLayer().rotationY(-90).setDuration(300).withEndAction(
                    new Runnable() {
                        @Override public void run() {
                            // second quarter turn
                            v.setForeground(new ColorDrawable(
                                            ContextCompat.getColor(GameActivity.this, R.color.teal_200)));
                            v.setRotationY(90);
                            v.animate().withLayer().rotationY(0).setDuration(300).start();
                        }
                    }
            ).start();
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            finish();
        }
    }

    private void closeBothImagesAfterTwoSeconds() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                flipCard(firstImage);
                flipCard(secondImage);
                /*firstImage.setForeground(new ColorDrawable(
                        ContextCompat.getColor(GameActivity.this, R.color.teal_200)));
                secondImage.setForeground(new ColorDrawable(
                        ContextCompat.getColor(GameActivity.this, R.color.teal_200)));*/
                wrongImagePairIsStillOpen = false;
                selectImageText();
            }
        }, 2000);
    }

    private void updateScore() {
        score++;
        String textScore = score + "/6 matches";
        TextView textMatches = findViewById(R.id.textMatches);
        textMatches.setText(textScore);
    }

    private void startTimer() {
        final TextView timerTextView = findViewById(R.id.textTimer);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = timerSeconds / 3600;
                int minutes = (timerSeconds % 3600) / 60;
                int seconds = timerSeconds % 60;
                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                        hours, minutes, seconds);
                timerTextView.setText(time);
                if (timerIsRunning) {
                    timerSeconds++;
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void stopTimer() {
        timerIsRunning = false;
    }

    private void didNotMatchText() {
        infoText = "Wrong! Closing images...";
        infoTextView.setText(infoText);
    }

    private void selectImageText() {
        infoText = "Open a pair of images.";
        infoTextView.setText(infoText);
    }

    private void matchedText() {
        infoText = "Nice match. Keep going!";
        infoTextView.setText(infoText);
    }

    private void winGameText() {
        infoText = "Congrats, you have won!\nGoing back to main page...";
        infoTextView.setText(infoText);
    }

    private void waitToast() {
        Toast.makeText(this, "Please wait for wrong image pair to close.",
                Toast.LENGTH_SHORT).show();
    }

    private void returnToMainActivityAfterFourSeconds() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 4000);
    }
}