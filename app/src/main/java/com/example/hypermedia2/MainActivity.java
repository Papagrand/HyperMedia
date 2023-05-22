package com.example.hypermedia2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.hypermedia2.Music.Music;
import com.example.hypermedia2.Music.MusicAdapter;
import com.example.hypermedia2.Music.MyMediaPlayer;
import com.example.hypermedia2.Music.PlayerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PlayerFragment.PlayerInteractionListener {

    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 2;
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    public ImageButton playPauseButton;

    private MusicAdapter musicAdapter;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    private static final int MY_PERMISSION_REQUEST = 1;

    @Override
    protected void onStart() {
        super.onStart();
        bottomNavigationView = findViewById(R.id.navigation_menu);
        navController = Navigation.findNavController(this, R.id.main_container);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    public void onExpandPlayer(ArrayList<Music> musicArrayList) {
        // Expand the player fragment or perform any desired actions
        // For example, replace the current fragment with the player fragment

        PlayerFragment playerFragment = new PlayerFragment(musicArrayList);
        // Pass the musicArrayList to the player fragment if needed

        playerFragment.show(this.getSupportFragmentManager(), playerFragment.getTag());
    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    private void playNextMusic(ArrayList<Music> musicArrayList, ImageButton playPauseButton){
        if(MyMediaPlayer.currentIndex==musicArrayList.size()-1){
            return;
        }
        MyMediaPlayer.currentIndex+=1;
        mediaPlayer.reset();
        mediaPlayer.start();
    }
    private void pausePlayMusic(ArrayList<Music> musicArrayList,ImageButton playPauseButton){
        if (mediaPlayer.isPlaying()){
            playPauseButton.setImageResource(R.drawable.play);
            mediaPlayer.pause();
        }else{
            playPauseButton.setImageResource(R.drawable.pause);
            mediaPlayer.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain why we need the permission and then ask for the permission again
            } else {
                // No explanation needed, we can request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST);
            }

        }
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        musicAdapter = new MusicAdapter(this);
        ArrayList<Music> musicArrayList = musicAdapter.getMusicArrayList(this);
        musicAdapter.setMusicList(musicArrayList);

        ConstraintLayout miniPlayerLayout = findViewById(R.id.miniPlayer);
        miniPlayerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyMediaPlayer.getCurrentIndex()!=-1) {
                    onExpandPlayer(musicArrayList);
                }
            }
        });

        ImageButton playPauseButtonMini = findViewById(R.id.playButtonMini);
        playPauseButtonMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausePlayMusic(musicArrayList,playPauseButtonMini);
            }
        });

        ImageButton nextButtonMini = findViewById(R.id.nextButtonMini);

        nextButtonMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextMusic(musicArrayList, playPauseButton);
            }
        });
        }

    }
