package com.example.hypermedia2.Music;

import android.media.MediaPlayer;

public class MyMediaPlayer {
    static MediaPlayer instance;
    private static String currentPath;

    public static MediaPlayer getInstance(){
        if(instance == null){
            instance = new MediaPlayer();
        }
        return instance;
    }
    public static int getCurrentIndex() {
        return currentIndex;
    }

    public static void setCurrentIndex(int index) {
        currentIndex = index;
    }

    public static String getCurrentPath() {
        return currentPath;
    }

    public static void setCurrentPath(String path) {
        currentPath = path;
    }
    public static int currentIndex = -1;
}
