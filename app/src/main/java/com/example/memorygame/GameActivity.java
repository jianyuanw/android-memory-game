package com.example.memorygame;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        File[] allImages = getExternalFilesDir(Environment.DIRECTORY_PICTURES).listFiles();
        List<File> dupImages = new ArrayList<File>();
        for(File image : allImages){
            dupImages.add(image);
            dupImages.add(image);
        }
        Collections.shuffle(dupImages);
        GridView gridView = findViewById(R.id.gridView);
        GridAdapter adapter = new GridAdapter(this, dupImages);
    }

}