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

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 2;
    private BottomNavigationView bottomNavigationView;
    private DBaseHelper dBaseHelper;
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dBaseHelper = new DBaseHelper(this);
        try {
            dBaseHelper.createDataBase();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST);
            }

        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);


            }
        }
        }

    }
