package com.example.spotapicheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.spotapicheck.utils.SAR;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import org.w3c.dom.Text;

public class PlayerActivity extends AppCompatActivity {
    private TextView song;
    private TextView artist;
    private SeekBar seekBar;
    private BottomNavigationView bottomNavigationView;
    private ImageView artwork;
    private ImageButton stateButton;

    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().hide();

        artwork = findViewById(R.id.artwork);
        stateButton = findViewById(R.id.stateButton);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.player);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.player:
                        return true;

                    case R.id.songs:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        song = findViewById(R.id.song);
        artist = findViewById(R.id.artist);
        seekBar = findViewById(R.id.seekBar);

        //SAR.mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
        handler.postDelayed(fetchAndUpdateTime, 1000);

        // Subscribe to PlayerState
        SAR.mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    Log.d("PlayerState ", String.valueOf(playerState));
                    final Track track = playerState.track;
                    seekBar.setMax((int) track.duration / 1000);
                    Log.d("Player ", String.valueOf(track));
                    if (track != null) {
                        SAR.mSpotifyAppRemote.getImagesApi().getImage(track.imageUri, Image.Dimension.LARGE).setResultCallback(bitmap -> {
                            artwork.setImageBitmap(bitmap);
                        });
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                        song.setText(track.name);
                        artist.setText(track.artist.name);
                    }

                    if (playerState.isPaused) {
                        handler.removeCallbacks(fetchAndUpdateTime);
                    }

                    if (!playerState.isPaused) {
                        updateProgress();
                    }
                });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    Log.d("SeekBar", "Current Progress: " + String.valueOf(i));
                    SAR.mSpotifyAppRemote.getPlayerApi().seekTo((long) i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(fetchAndUpdateTime);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(fetchAndUpdateTime);
                SAR.mSpotifyAppRemote.getPlayerApi().seekTo((long) seekBar.getProgress() * 1000);

                updateProgress();
            }
        });

    }

    public void updateProgress() {
        handler.postDelayed(fetchAndUpdateTime, 1000);
    }

    private Runnable fetchAndUpdateTime = new Runnable() {
        @Override
        public void run() {
            SAR.mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                @Override
                public void onResult(PlayerState data) {
                    //Log.d("RunnableThread ", String.valueOf(data.playbackPosition));
                    seekBar.setProgress((int) data.playbackPosition / 1000);
                }
            });
            handler.postDelayed(this, 1000);
        }
    };

    public void playPauseMusic(View view) {
        SAR.mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(
                playerState -> {
                    if (playerState.isPaused) {
                        SAR.mSpotifyAppRemote.getPlayerApi().resume();
                        stateButton.setImageResource(R.drawable.pause_button);

                    } else {
                        SAR.mSpotifyAppRemote.getPlayerApi().pause();
                        stateButton.setImageResource(R.drawable.play_button);
                    }
                }
        );
        //SAR.mSpotifyAppRemote.getPlayerApi().pause();
        //handler.removeCallbacks(fetchAndUpdateTime);
    }

    public void nextTrack(View view) {
        SAR.mSpotifyAppRemote.getPlayerApi().skipNext();
        stateButton.setImageResource(R.drawable.pause_button);
    }

    public void prevTrack(View view) {
        SAR.mSpotifyAppRemote.getPlayerApi().skipPrevious();
        stateButton.setImageResource(R.drawable.pause_button);
    }
}