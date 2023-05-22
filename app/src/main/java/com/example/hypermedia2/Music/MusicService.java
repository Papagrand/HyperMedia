package com.example.hypermedia2.Music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private ArrayList<Music> musicArrayList;
    private int currentIndex;
    private final IBinder binder = new MusicBinder();

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        musicArrayList = intent.getParcelableArrayListExtra("musicList");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public void play() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }
    public void playNext() {
        if (currentIndex < musicArrayList.size() - 1) {
            currentIndex++;
            playMusic();
        }
    }

    public void playPrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            playMusic();
        }
    }

    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }
    public void reset(){
        mediaPlayer.reset();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    private void playMusic() {
        Music currentSong = musicArrayList.get(currentIndex);
        String path = currentSong.getPath();

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
