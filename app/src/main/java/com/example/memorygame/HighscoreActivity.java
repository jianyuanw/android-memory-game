package com.example.memorygame;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HighscoreActivity extends AppCompatActivity {

    List<String> strHighscores = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        SharedPreferences preferences = getSharedPreferences("HS_PREF", 0);
        int currentScore =  preferences.getInt("currentTime", 0);
        String scoreToAdd = convertTime(currentScore);
        strHighscores = getArray();

        if(currentScore !=0){
            strHighscores.add(scoreToAdd);
        }
        if(strHighscores != null){
            Collections.sort(strHighscores);

            if(strHighscores.size() > 5){
                for(int i = 0; i < strHighscores.size()-5; i++)
                {
                    strHighscores.remove(5);
                }
            }

            saveArray();

            TextView highscore1 = (TextView)findViewById(R.id.highscore1);
            TextView highscore2 = (TextView)findViewById(R.id.highscore2);
            TextView highscore3 = (TextView)findViewById(R.id.highscore3);
            TextView highscore4 = (TextView)findViewById(R.id.highscore4);
            TextView highscore5 = (TextView)findViewById(R.id.highscore5);

            int scoreId = strHighscores.size();
            switch(scoreId){
                case 1:
                    highscore1.setText(strHighscores.get(0));
                    break;
                case 2:
                    highscore1.setText(strHighscores.get(0));
                    highscore2.setText(strHighscores.get(1));
                    break;
                case 3:
                    highscore1.setText(strHighscores.get(0));
                    highscore2.setText(strHighscores.get(1));
                    highscore3.setText(strHighscores.get(2));
                    break;
                case 4:
                    highscore1.setText(strHighscores.get(0));
                    highscore2.setText(strHighscores.get(1));
                    highscore3.setText(strHighscores.get(2));
                    highscore4.setText(strHighscores.get(3));
                    break;
                case 5:
                    highscore1.setText(strHighscores.get(0));
                    highscore2.setText(strHighscores.get(1));
                    highscore3.setText(strHighscores.get(2));
                    highscore4.setText(strHighscores.get(3));
                    highscore5.setText(strHighscores.get(4));
                    break;
            }

        }

    }

    public String convertTime(Integer intTime){
        int hours = intTime / 3600;
        int minutes = (intTime % 3600) / 60;
        int seconds = intTime % 60;
        String score = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                    hours, minutes, seconds);
        return score;
    }

    public ArrayList<String> getArray() {
        SharedPreferences sp = this.getSharedPreferences("HIGHSCORE", Activity.MODE_PRIVATE);

        //NOTE: if shared preference is null, the method return empty Hashset and not null
        Set<String> set = sp.getStringSet("list", new HashSet<String>());

        return new ArrayList<String>(set);
    }

    public boolean saveArray() {
        SharedPreferences sp = this.getSharedPreferences("HIGHSCORE", Activity.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(strHighscores);
        mEdit1.putStringSet("list", set);
        return mEdit1.commit();
    }
}

