package com.andromou.ytv.player.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.SimpleExoPlayer;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.ui.PlayerView;

import com.andromou.ytv.player.R;

public class Media3PlayerActivity extends AppCompatActivity {

    String url1;
    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    SharedPreferences sharedPreferences;
    private final String[] speed = {"0.25x", "0.5x", "Normal", "1.5x", "2x"};

    @OptIn(markerClass = UnstableApi.class) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("data", url1);
        editor.commit();

        // Retrieve video URL from intent or any other source
        url1 = getIntent().getStringExtra("url");

        // Keep screen on during video playback
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initialize PlayerView from layout
        playerView = findViewById(R.id.exoPlayerView);

        // Initialize track selector
        play();

        // Initialize UI components and set listeners
        initializePlayerControls();
    }

    @OptIn(markerClass = UnstableApi.class) private void initializePlayerControls() {
        ImageView forwardBtn = playerView.findViewById(R.id.fwd);
        ImageView rewindBtn = playerView.findViewById(R.id.rew);
        ImageView settingsBtn = playerView.findViewById(R.id.exo_track_selection_view);
        ImageView speedBtn = playerView.findViewById(R.id.exo_playback_speed);
        ImageView fullscreenBtn = playerView.findViewById(R.id.fullscreen);
        ImageView playBtn = findViewById(R.id.exo_play);
        ImageView pauseBtn = findViewById(R.id.exo_pause);

        // Speed control
        speedBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Media3PlayerActivity.this);
            builder.setTitle("Set Speed");
            builder.setItems(speed, (dialog, which) -> {
                float speedValue = 1f; // Normal speed
                switch (which) {
                    case 0:
                        speedValue = 0.25f;
                        break;
                    case 1:
                        speedValue = 0.5f;
                        break;
                    case 2:
                        speedValue = 1f;
                        break;
                    case 3:
                        speedValue = 1.5f;
                        break;
                    case 4:
                        speedValue = 2f;
                        break;
                }
                PlaybackParameters param = new PlaybackParameters(speedValue);
                simpleExoPlayer.setPlaybackParameters(param);
            });
            builder.show();
        });

        // Seek forward and rewind
        forwardBtn.setOnClickListener(v -> simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000));
        rewindBtn.setOnClickListener(v -> {
            long newPosition = simpleExoPlayer.getCurrentPosition() - 10000;
            simpleExoPlayer.seekTo(Math.max(0, newPosition));
        });

        // Fullscreen toggle
        fullscreenBtn.setOnClickListener(v -> {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

        // Play and pause buttons
        playBtn.setOnClickListener(v -> {
            if (simpleExoPlayer != null){
                simpleExoPlayer.play();
            } else play();

        });
        pauseBtn.setOnClickListener(v -> simpleExoPlayer.pause());

        // Track selection dialog
        /*   settingsBtn.setOnClickListener(v -> {
            if (!isShowingTrackSelectionDialog && TrackSelectionView.willHaveContent(trackSelector)) {
                isShowingTrackSelectionDialog = true;
                TrackSelectionView trackSelectionDialog = TrackSelectionView.createForTrackSelector(
                        trackSelector,
                        dismissedDialog -> isShowingTrackSelectionDialog = false
                );
                trackSelectionDialog.show(getSupportFragmentManager(), null);
            }
        });
        */

        // Player event listener
        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    // Show replay button when playback ends
                    playBtn.setVisibility(View.GONE);
                    pauseBtn.setVisibility(View.GONE);
                    //showReplayButton();
                } else {
                    // Show play or pause button based on player state
                    if (simpleExoPlayer.getPlayWhenReady()) {
                        playBtn.setVisibility(View.GONE);
                        pauseBtn.setVisibility(View.VISIBLE);
                    } else {
                        playBtn.setVisibility(View.VISIBLE);
                        pauseBtn.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
/*
    @OptIn(markerClass = UnstableApi.class) private void showReplayButton() {
        // Show replay button and handle replay functionality
        ImageView replayBtn = playerView.findViewById(R.id.restart);
        replayBtn.setVisibility(View.VISIBLE);
        replayBtn.setOnClickListener(v -> {
            simpleExoPlayer.seekTo(0);
            simpleExoPlayer.play();
            v.setVisibility(View.GONE); // Hide replay button after clicking it
        });
    }
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OptIn(markerClass = UnstableApi.class) private void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        url1 = sharedPreferences.getString("data", url1);
    }
    @OptIn(markerClass = UnstableApi.class) private void play(){
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);

        // Create a SimpleExoPlayer instance with track selector
        simpleExoPlayer = new SimpleExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build();

        // Bind the player to the view
        playerView.setPlayer(simpleExoPlayer);

        // Build the media item
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url1));

        // Set the media item to be played
        simpleExoPlayer.setMediaItem(mediaItem);

        // Prepare the player
        simpleExoPlayer.prepare();

        // Start the playback
        simpleExoPlayer.play();

    }
}

