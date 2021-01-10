package com.example.memorygame;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

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

    List<String> strHighscores = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        SharedPreferences preferences = getSharedPreferences("HS_PREF", Activity.MODE_PRIVATE);
        int currentScore =  preferences.getInt("currentTime", 0);
        String scoreToAdd = convertTime(currentScore);
        strHighscores = getArray();

        if(currentScore !=0){
            strHighscores.add(scoreToAdd);
            preferences.edit().remove("currentTime").apply();
        }
        if(strHighscores != null){
            Collections.sort(strHighscores);

            if(strHighscores.size() > 5){
                for(int i = 0; i < strHighscores.size()-5; i++)
                {
                    strHighscores.remove(5);
                }
            }

            saveArray(strHighscores);

            TextView highscore1 = (TextView)findViewById(R.id.highscore1);
            TextView highscore2 = (TextView)findViewById(R.id.highscore2);
            TextView highscore3 = (TextView)findViewById(R.id.highscore3);
            TextView highscore4 = (TextView)findViewById(R.id.highscore4);
            TextView highscore5 = (TextView)findViewById(R.id.highscore5);

            TextView[] highscores = {highscore1,highscore2,highscore3,highscore4,highscore5};
            for(int i = 0; i < strHighscores.size(); i++) {
                highscores[i].setText(strHighscores.get(i));
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

   /* public ArrayList<String> getArray() {
        SharedPreferences sp = this.getSharedPreferences("HIGHSCORE", Activity.MODE_PRIVATE);

        //NOTE: if shared preference is null, the method return empty Hashset and not null
        Set<String> set = sp.getStringSet("list", new HashSet<String>());

        return new ArrayList<String>(set);
    }

    public void saveArray() {
        SharedPreferences sp = this.getSharedPreferences("HIGHSCORE", Activity.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(strHighscores);
        mEdit1.putStringSet("list", set);
        mEdit1.apply();
    }*/