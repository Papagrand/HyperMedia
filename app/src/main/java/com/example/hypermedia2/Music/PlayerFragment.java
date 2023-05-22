package com.example.hypermedia2.Music;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.hypermedia2.Music.Music;
import com.example.hypermedia2.Music.MusicService;
import com.example.hypermedia2.Music.MyMediaPlayer;
import com.example.hypermedia2.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerFragment extends BottomSheetDialogFragment {
    private final ArrayList<Music> musicArrayList;
    private ImageButton arrowButton;
    private Handler handler = new Handler();
    Music currentSong;
    private MusicService musicService;
    BottomSheetDialog dialog;
    BottomSheetBehavior<View> bottomSheetBehavior;
    View rootView;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

    TextView currentTime, remainingTime, songName, artistName;
    SeekBar seekBar;
    ImageButton previousButton,playPauseButton,nextButton;
    ImageView songImage;

    public PlayerFragment(ArrayList<Music> musicArrayList) {
        this.musicArrayList=musicArrayList;
    }
    public interface PlayerInteractionListener {
        void onExpandPlayer(ArrayList<Music> musicArrayList);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };
    private void bindMusicService() {
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindMusicService() {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public void onStart() {
        super.onStart();
        bindMusicService();
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindMusicService();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_player, container, false);
        return rootView;
    }
    public boolean isMusicPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        CoordinatorLayout layout = dialog.findViewById(R.id.bottomSheetLayout);
        assert layout != null;
        layout.setBackgroundResource(R.drawable.bottom_sheet_background);
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);


        currentTime = view.findViewById(R.id.ongoingSongTime);
        remainingTime = view.findViewById(R.id.remainingSongTime);
        songName = view.findViewById(R.id.playerSongName);
        artistName = view.findViewById(R.id.playerArtistName);
        seekBar = view.findViewById(R.id.songSeekBar);
        playPauseButton = view.findViewById(R.id.playSongImageButton);
        previousButton = view.findViewById(R.id.previousSongImageButton);
        nextButton = view.findViewById(R.id.nextSongImageButton);
        setResourcesWithMusic();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    currentTime.setText(convertToMMSS(String.valueOf(mediaPlayer.getCurrentPosition())));
                    if (mediaPlayer.isPlaying()){
                        playPauseButton.setImageResource(R.drawable.pressed_button_selector);
                    }else{
                        playPauseButton.setImageResource(R.drawable.pressed_play_button_selector);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start();
            }
        });



        //Сворачивание по нажатию
        ImageButton collapsePlayer = view.findViewById(R.id.arrow_down);
        collapsePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }
    void setResourcesWithMusic(){
        currentSong = musicArrayList.get(MyMediaPlayer.currentIndex);

        songName.setText(currentSong.getMusicName());
        artistName.setText(currentSong.getArtistName());

        //currentTime.setText(convertToMMSS(currentSong.getDurationSong()));
        remainingTime.setText(convertToMMSS(String.valueOf(currentSong.getDurationSong())));
        playPauseButton.setOnClickListener(v -> pausePlayMusic());
        nextButton.setOnClickListener(v -> playNextMusic());
        previousButton.setOnClickListener(v -> playPreviousMusic());
        playMusic();

    }

    private void playMusic(){
        if (mediaPlayer.isPlaying() && isPlayingCurrentSong()){
            return;
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            MyMediaPlayer.setCurrentPath(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNextMusic();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isPlayingCurrentSong() {
        // Проверьте, играет ли медиаплеер текущую песню
        return currentSong.getPath().equals(MyMediaPlayer.getCurrentPath());
    }
    private void playNextMusic(){
        if(MyMediaPlayer.currentIndex==musicArrayList.size()-1){
            return;
        }
        MyMediaPlayer.currentIndex+=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }
    private void playPreviousMusic(){
        if (MyMediaPlayer.currentIndex == 0) {
            return;
        }
        if (Long.parseLong(String.valueOf(mediaPlayer.getCurrentPosition())) < 3000) {
            MyMediaPlayer.currentIndex -= 1;
        } else {
            mediaPlayer.seekTo(0);
        }
        setResourcesWithMusic();
    }

    private void pausePlayMusic(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }else{
            mediaPlayer.start();
        }
    }
    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }
}