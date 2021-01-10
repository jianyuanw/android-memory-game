package com.example.memorygame;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, NotifyDialogFragment.NotifyDialogListener {

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
    private boolean flipping;
    private boolean processing;
    private boolean timerIsRunning;
    private boolean isPaused;
    private int timerSeconds;

    private Button pauseButton;
    private TextView infoTextView;
    private TextView pauseForeground;
    private String infoText;
  
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViewById(R.id.backButton).setOnClickListener(this);

        pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(this);

        pauseForeground = findViewById(R.id.pauseForeground);

        SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);

        RecyclerView gameRecyclerView = findViewById(R.id.gameRecyclerView);
        gameImages = GameImage.createGameImageList(this);
        GameImagesAdapter adapter = new GameImagesAdapter(gameImages, sharedPreferences.getString("glide", "No").equals("Yes"));
        adapter.setOnItemClickListener(new GameImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                // Start timer on first click
                if (!timerIsRunning && !isPaused) {
                    timerIsRunning = true;
                    isPaused = false;
                    pauseButton.setVisibility(View.VISIBLE);
                    startTimer();
                }

                if (isPaused || flipping || processing ||
                        itemView.findViewById(R.id.gameImageView).getForeground() == null) {
                    return;
                }

                if (wrongImagePairIsStillOpen) {
                    waitToast();
                    return;
                }

                if (numberOfImagesOpened == 0) {
                    // Clicked on first image
                    firstImage = itemView.findViewById(R.id.gameImageView);
                    // Reveal image
                    flipCard(firstImage);
                    firstImageId = gameImages.get(position).getId();
                    numberOfImagesOpened = 1;
                } else if (numberOfImagesOpened == 1) {
                    // Clicked on second image
                    secondImage = itemView.findViewById(R.id.gameImageView);
                    // Reveal image
                    flipCard(secondImage);
                    secondImageId = gameImages.get(position).getId();
                    processing = true;
                    if (firstImageId == secondImageId) {
                        // Images matched
                        updateScore();

                        if (score == maxScore) {
                            // Game ended
                            stopTimer();
                            winGameText();
                            // Sound effect for winning
                            playSound(R.raw.win_audio);

                            // returnToMainActivityAfterFourSeconds();
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
                    processing = false;
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
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), soundId);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mediaPlayer = null;
                }
            });
            mediaPlayer.start();
        } else {
            MediaPlayer extraMediaPlayer = MediaPlayer.create(getApplicationContext(), soundId);
            extraMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            extraMediaPlayer.start();
        }
    }

    public void flipCard(View v) {
        flipping = true;
        if (v.getForeground() != null) {
            v.animate().withLayer().rotationY(90).setDuration(300).withEndAction(
                    new Runnable() {
                        @Override public void run() {
                            // second quarter turn
                            v.setForeground(null);
                            v.setRotationY(-90);
                            v.animate().withLayer().rotationY(0).setDuration(300).start();
                            flipping = false;
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
                            flipping = false;
                        }
                    }
            ).start();
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.backButton) {
            if( score == maxScore && isHitBest5()) {
                askingRecord();
            }
            else {
                finish();
            }
        } else if (id == R.id.pauseButton) {
            if (isPaused) {
                isPaused = false;
                timerIsRunning = true;
                playSound(R.raw.game_resume);
                pauseForeground.setVisibility(View.INVISIBLE);
                pauseButton.setText("Pause");
                startTimer();
            } else {
                isPaused = true;
                playSound(R.raw.game_pause);
                pauseForeground.setVisibility(View.VISIBLE);
                pauseButton.setText("Resume");
                stopTimer();
            }
        }
    }

    private void closeBothImagesAfterTwoSeconds() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                flipCard(firstImage);
                flipCard(secondImage);

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
                mediaPlayer.release();
                finish();
            }
        }, 4000);
    }

    public String convertTime(Integer intTime){
        int hours = intTime / 3600;
        int minutes = (intTime % 3600) / 60;
        int seconds = intTime % 60;
        String score = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                hours, minutes, seconds);
        return score;
    }


        // check whether hit the best 5
    private boolean isHitBest5() {
        String filepath = "RecordFolder";
        String filename = "Records.txt";
        mTargetRecords = new File(getFilesDir(), filepath+"/"+filename);
        BestRecordReader bestRecordReader = new BestRecordReader(mTargetRecords);
        List<Record> sortedBest5 = bestRecordReader.getSortedBest5();
        if(sortedBest5.size() >=5 && timerSeconds<sortedBest5.get(4).getTime()) {
            return true;
        }
        else if(sortedBest5.size() < 5) {
            return true;
        }
        else{
            return false;
        }
    }

    // Submit record feature

    NotifyDialogFragment.NotifyDialogListener listener;
    boolean notifyChoice;
    String userName;
    private void askingRecord() {
        NotifyDialogFragment notifyDialogFragment = new NotifyDialogFragment();
        notifyDialogFragment.show(getSupportFragmentManager(), "notify");
        notifyDialogFragment.dismiss();
    }

    @Override
    public void onDialogPositiveClick(NotifyDialogFragment dialog) {
        notifyChoice = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please input your name");
        final View v = getLayoutInflater().inflate(R.layout.dialog_submit_record,null);
        builder.setView(v);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText editText = v.findViewById(R.id.username);
                sendDialogDataToActivity(editText.getText().toString());
                RecordToAppSpecificInternal(userName, timerSeconds);
            }
        });
        AlertDialog submitDialog = builder.create();
        submitDialog.show();
    }

    private void sendDialogDataToActivity(String data) {
        userName = data;
    }

    @Override
    public void onDialogNegativeClick(NotifyDialogFragment dialog) {
        notifyChoice = false;
        finish();
    }

    File mTargetRecords;
    // update the record file
    private void RecordToAppSpecificInternal(String userName, int timerSeconds) {
        String namePlusTime = userName+"+"+timerSeconds+"\r\n";
        String filepath = "RecordFolder";
        String filename = "Records.txt";
        mTargetRecords = new File(getFilesDir(), filepath+"/"+filename);
        String existedRecord = "";
        try {
            FileInputStream fis = new FileInputStream(mTargetRecords);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(dis));
            String strLine;
            while((strLine = br.readLine())!=null){
                existedRecord = existedRecord + strLine + "\r\n";
            }
            dis.close();
        }catch (FileNotFoundException fileNot) {
            fileNot.printStackTrace();
            existedRecord = "";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Make sure that the parent folder exists
                File parent = mTargetRecords.getParentFile();
                if(!parent.exists() && !parent.mkdirs()) {
                    throw new IllegalStateException("Cannot create dir "+parent);
                }
                FileOutputStream fos = new FileOutputStream(mTargetRecords);
                fos.write((existedRecord+namePlusTime).getBytes("utf-8"));
                fos.close();
                Toast.makeText(this,userName+"'s record saved!", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this,"Error occured when saving.", Toast.LENGTH_LONG).show();
            }
            finally {
                finish();
            }
        }
    }
}