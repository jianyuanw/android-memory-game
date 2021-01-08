package com.example.memorygame;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class GameImage {
    private int id;
    private String filePath;

    public GameImage(int id, String filePath) {
        this.id = id;
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public static ArrayList<GameImage> createGameImageList(Context context) {
        ArrayList<GameImage> gameImages = new ArrayList<>();

        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] filesInDir = dir.listFiles();

        int id = 1;

        for (File file : filesInDir) {
            String filePath = file.getAbsolutePath();
            GameImage gameImage = new GameImage(id, filePath);
            gameImages.add(gameImage);
            gameImages.add(gameImage);
            id++;
        }

        // TODO: Shuffle list

        return gameImages;
    }
}
