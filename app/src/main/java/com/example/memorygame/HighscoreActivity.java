package com.example.memorygame;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HighscoreActivity extends AppCompatActivity {

    List<String> strHighscores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        strHighscores = getArray();

            TextView highscore1 = findViewById(R.id.highscore1);
            TextView highscore2 = findViewById(R.id.highscore2);
            TextView highscore3 = findViewById(R.id.highscore3);
            TextView highscore4 = findViewById(R.id.highscore4);
            TextView highscore5 = findViewById(R.id.highscore5);

            TextView[] highscores = {highscore1, highscore2, highscore3, highscore4, highscore5};
            for(int i = 0; i < strHighscores.size(); i++) {
                highscores[i].setText(String.format(Locale.ENGLISH, "%s %s", highscores[i].getText(), strHighscores.get(i)));
            }

            final Button button = findViewById(R.id.resetButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i = 0; i < strHighscores.size(); i++){
                        highscores[i].setText("");
                    }
                    saveArray(new ArrayList<>());
                    Toast.makeText(getApplicationContext(), "Scores reset successfully!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
    public void saveArray(List<String> highscoreList){
        String highscoreString = "";
        SharedPreferences sp = this.getSharedPreferences("HIGHSCORE", Activity.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        if(highscoreList != null){
            highscoreString = String.join(",", highscoreList);
            mEdit1.putString("highscoreString",highscoreString);
            mEdit1.apply();
            }
        }
    public List<String> getArray(){
        ;
        SharedPreferences sp = this.getSharedPreferences("HIGHSCORE", Activity.MODE_PRIVATE);
        String highscoreString = sp.getString("highscoreString","");
        if (highscoreString == ""){
            return new ArrayList<String>();
        }
        else{
            List<String> highscoreList = new ArrayList<String>(Arrays.asList(highscoreString.split(",")));
        return highscoreList;}
    }
}