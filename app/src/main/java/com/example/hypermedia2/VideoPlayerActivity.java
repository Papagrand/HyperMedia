package com.example.hypermedia2;

import static com.example.hypermedia2.Video.VideosAdapter.videoFolder;
import static com.example.hypermedia2.Home.VideosInPlaylistAdapter.playlistNewVideos;
import static com.example.hypermedia2.ChildModeThings.VideosInChildModePlaylistAdapter.videosForChild;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private int position = -1;
    private int savedPosition = 0;
    private PlayerView videoView;
    private int brightness;
    private Handler handler;
    private Runnable runnable;
    SimpleExoPlayer player;
    ConcatenatingMediaSource concatenatingMediaSource;

    LinearLayout one, two, three, four, five, lockControls, unlockControls, tapToUnlock, audiotrack;
    ImageButton goBack, rewind, forward, playPause;
    TextView title,currentTime, remainTime;
    SeekBar videoSeekBar, brightnessSeekBar;
    String videoTitle;
    boolean isOpen = true;
    boolean isVideoPlaying = true;

    private static final String KEY_POSITION = "position";
    private static final String KEY_PLAYBACK_POSITION = "playback_position";
    private static final String KEY_CURRENT_WINDOW = "current_window";
    private static final String KEY_PLAY_WHEN_READY = "play_when_ready";

    private long playbackPosition = C.TIME_UNSET;
    private int currentWindow;
    private boolean playWhenReady = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        videoView = findViewById(R.id.videoplayer_view);
        title = findViewById(R.id.videoView_title);
        one = findViewById(R.id.videoView_one_layout);
        two = findViewById(R.id.videoView_two_layout);
        three = findViewById(R.id.videoView_three_layout);
        four = findViewById(R.id.videoView_four_layout);
        five = findViewById(R.id.video_five_layout);
        videoSeekBar = findViewById(R.id.videoView_seekbar);
        currentTime = findViewById(R.id.videoView_currentTime);
        remainTime = findViewById(R.id.videoView_remainTime);
        goBack = findViewById(R.id.videoView_go_back);
        rewind = findViewById(R.id.videoView_rewind);
        forward = findViewById(R.id.videoView_forward);
        playPause = findViewById(R.id.videoView_play_pause_btn);
        lockControls = findViewById(R.id.videoView_lock_screen);
        unlockControls = findViewById(R.id.video_five_child_layout);
        tapToUnlock = findViewById(R.id.videoView_tap_to_unlock);
        audiotrack = findViewById(R.id.videoView_track);
        brightnessSeekBar = findViewById(R.id.videoView_brightness);

        videoSeekBar.setMax(1000);
        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && player != null) {
                    long duration = player.getDuration();
                    long newPosition = duration * progress / 1000L;
                    player.seekTo(newPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.postDelayed(runnable, 100);

            }
        });

        brightnessSeekBar.setMax(255);
        brightnessSeekBar.setKeyProgressIncrement(1);

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress<=20)
                {
                    brightness=20;
                }
                else
                {
                    brightness = progress;
                }
                float perc = (brightness /(float)255)*100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.screenBrightness = brightness / (float)255;
                getWindow().setAttributes(layoutParams);
            }
        });


        videoTitle = getIntent().getStringExtra("video_title");
        title.setText(videoTitle);

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(KEY_POSITION, -1);
            playbackPosition = savedInstanceState.getLong(KEY_PLAYBACK_POSITION, C.TIME_UNSET);
            currentWindow = savedInstanceState.getInt(KEY_CURRENT_WINDOW);
            playWhenReady = savedInstanceState.getBoolean(KEY_PLAY_WHEN_READY);
        }

        position = getIntent().getIntExtra("p", -1);
        String path;
        if (videoFolder != null && position >= 0 && position < videoFolder.size()) {
            path = videoFolder.get(position).getPath();
            if (path != null) {
                Uri uri = Uri.parse(path);
                player = new SimpleExoPlayer.Builder(this).build();
                isVideoPlaying = true;
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "app"));
                concatenatingMediaSource = new ConcatenatingMediaSource();
                for (int i = 0; i < videoFolder.size(); i++) {
                    new File(String.valueOf(videoFolder.get(i)));
                    MediaItem mediaItem = MediaItem.fromUri(uri); // Create MediaItem using uri
                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(mediaItem); // Pass the mediaItem
                    concatenatingMediaSource.addMediaSource(mediaSource);
                }
                videoView.setPlayer(player);
                videoView.setKeepScreenOn(true);
                player.prepare(concatenatingMediaSource);

                player.addListener(new Player.EventListener() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if (playbackState == Player.STATE_READY){
                            long duration = player.getDuration();
                            long currentPosition = player.getCurrentPosition();
                            if (duration > 0) {
                                int progress = (int) (1000L * currentPosition / duration);
                                videoSeekBar.setProgress(progress);
                            }
                        }
                    }
                });

                player.seekTo(position, C.TIME_UNSET);
                player.play();
                audiotrack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
        else if (playlistNewVideos != null && position >= 0 && position < playlistNewVideos.size()) {

            path = playlistNewVideos.get(position).getPath();
            if (path != null) {
                Uri uri = Uri.parse(path);
                player = new SimpleExoPlayer.Builder(this).build();
                isVideoPlaying = true;
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "app"));
                concatenatingMediaSource = new ConcatenatingMediaSource();
                for (int i = 0; i < playlistNewVideos.size(); i++) {
                    new File(String.valueOf(playlistNewVideos.get(i)));
                    MediaItem mediaItem = MediaItem.fromUri(uri); // Create MediaItem using uri
                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(mediaItem); // Pass the mediaItem
                    concatenatingMediaSource.addMediaSource(mediaSource);
                }
                videoView.setPlayer(player);
                videoView.setKeepScreenOn(true);
                player.prepare(concatenatingMediaSource);

                player.addListener(new Player.EventListener() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if (playbackState == Player.STATE_READY){
                            long duration = player.getDuration();
                            long currentPosition = player.getCurrentPosition();
                            if (duration > 0) {
                                int progress = (int) (1000L * currentPosition / duration);
                                videoSeekBar.setProgress(progress);
                            }
                        }
                    }
                });

                player.seekTo(position, C.TIME_UNSET);
                player.play();
                audiotrack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
        else if (videosForChild != null && position >= 0 && position < videosForChild.size()) {

            path = videosForChild.get(position).getPath();
            if (path != null) {
                Uri uri = Uri.parse(path);
                player = new SimpleExoPlayer.Builder(this).build();
                isVideoPlaying = true;
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "app"));
                concatenatingMediaSource = new ConcatenatingMediaSource();
                for (int i = 0; i < videosForChild.size(); i++) {
                    new File(String.valueOf(videosForChild.get(i)));
                    MediaItem mediaItem = MediaItem.fromUri(uri); // Create MediaItem using uri
                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(mediaItem); // Pass the mediaItem
                    concatenatingMediaSource.addMediaSource(mediaSource);
                }
                videoView.setPlayer(player);
                videoView.setKeepScreenOn(true);
                player.prepare(concatenatingMediaSource);

                player.addListener(new Player.EventListener() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if (playbackState == Player.STATE_READY){
                            long duration = player.getDuration();
                            long currentPosition = player.getCurrentPosition();
                            if (duration > 0) {
                                int progress = (int) (1000L * currentPosition / duration);
                                videoSeekBar.setProgress(progress);
                            }
                        }
                    }
                });

                player.seekTo(position, C.TIME_UNSET);
                player.play();
                audiotrack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
        else {
            Toast.makeText(this, "path didn't exist", Toast.LENGTH_SHORT).show();
        }

        goBack.setOnClickListener(this);
        rewind.setOnClickListener(this);
        forward.setOnClickListener(this);
        playPause.setOnClickListener(this);
        lockControls.setOnClickListener(this);
        tapToUnlock.setOnClickListener(this);
        unlockControls.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.videoView_go_back:{
                finish();
                break;
            }
            case R.id.videoView_rewind:{
                rewindVideoBy(10);
                break;
            }
            case R.id.videoView_forward:{
                rewindVideoBy(-10);
                break;
            }
            case R.id.videoView_play_pause_btn:{
                if(player.isPlaying()){
                    player.pause();
                    playPause.setImageDrawable(getResources().getDrawable(R.drawable.play_song_button));
                }else {
                    player.play();
                    playPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_song_button));
                }
                break;
            }
            case R.id.videoView_lock_screen:{
                one.setVisibility(View.GONE);
                two.setVisibility(View.GONE);
                three.setVisibility(View.GONE);
                four.setVisibility(View.GONE);
                five.setVisibility(View.VISIBLE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                break;
            }
            case R.id.videoView_tap_to_unlock: {
                unlockControls.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        unlockControls.setVisibility(View.GONE);
                    }
                }, 5000);
                break;
            }

            case R.id.video_five_child_layout:{
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                unlockControls.setVisibility(View.GONE);
                one.setVisibility(View.VISIBLE);
                two.setVisibility(View.VISIBLE);
                three.setVisibility(View.VISIBLE);
                four.setVisibility(View.VISIBLE);
                five.setVisibility(View.GONE);
                break;
            }
        }
    }

    public void rewindVideoBy(int seconds) {
        if (player != null) {
            long currentPosition = player.getCurrentPosition();
            long newPosition = currentPosition - seconds * 1000; // Конвертируем секунды в миллисекунды
            player.seekTo(newPosition);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (isVideoPlaying) {
            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isVideoPlaying) {
            player.stop();
            savePlayerState();

        }
    }

    private void savePlayerState() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (player != null) {
            if (player.isPlaying()) {
                // Если видео уже воспроизводится, ничего не делаем
                return;
            }
            player.seekTo(savedPosition);
            player.setPlayWhenReady(true);
            isVideoPlaying = true;
            playPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_song_button));

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (player != null) {
                        long duration = player.getDuration();
                        long currentPosition = player.getCurrentPosition();
                        currentTime.setText(convertToMMSS(String.valueOf(currentPosition)));
                        remainTime.setText(convertToMMSS(String.valueOf(duration - currentPosition)));
                        if (duration > 0) {
                            int progress = (int) (1000L * currentPosition / duration);
                            videoSeekBar.setProgress(progress);
                        }
                    }
                    handler.postDelayed(this, 1000); // Обновление каждую секунду
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        isVideoPlaying = true;
        player.play();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    long duration = player.getDuration();
                    long currentPosition = player.getCurrentPosition();
                    currentTime.setText(convertToMMSS(String.valueOf(currentPosition)));
                    remainTime.setText(convertToMMSS(String.valueOf(duration-currentPosition)));
                    if (duration > 0) {
                        int progress = (int) (1000L * currentPosition / duration);
                        videoSeekBar.setProgress(progress);
                    }
                }
                handler.postDelayed(this, 1000); // Update every 1 second
            }
        };
        handler.postDelayed(runnable, 1000);
    }




    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (player != null) {
            outState.putInt(KEY_POSITION, position);
            outState.putLong(KEY_PLAYBACK_POSITION, player.getCurrentPosition());
            outState.putInt(KEY_CURRENT_WINDOW, player.getCurrentWindowIndex());
            outState.putBoolean(KEY_PLAY_WHEN_READY, player.getPlayWhenReady());
        }
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt(KEY_POSITION, -1);
        playbackPosition = savedInstanceState.getLong(KEY_PLAYBACK_POSITION, C.TIME_UNSET);
        currentWindow = savedInstanceState.getInt(KEY_CURRENT_WINDOW);
        playWhenReady = savedInstanceState.getBoolean(KEY_PLAY_WHEN_READY);
    }
    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }



}
