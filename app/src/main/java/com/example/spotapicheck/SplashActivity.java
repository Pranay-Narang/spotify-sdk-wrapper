package com.example.spotapicheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spotapicheck.utils.SAR;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;


public class SplashActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private Boolean firstTime;

    private Button button;

    private SpotifyAppRemote mSpotifyAppRemote;

    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "50aa79a5179c4fb0bab115da0267414b";
    private static final String REDIRECT_URI = "com.example.spotapicheck://callback";
    public static final String[] SCOPES = new String[]{"user-read-recently-played,user-library-modify,user-read-email,user-read-private,streaming"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);

        firstTime = getSharedPreferences("Intro", 0).getBoolean("FirstTime", true);
        if(firstTime) {
            startActivity(new Intent(SplashActivity.this, IntroActivity.class));
        }

        button = findViewById(R.id.loginButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateSpotify();
            }
        });

        sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
    }

    public void authenticateSpotify() {
        // Authenticate with com.spotify.sdk.android.auth
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(SCOPES);
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

        // Authenticate with com.spotify.android.appremote
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(false)
                .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");
                        SAR.mSpotifyAppRemote = mSpotifyAppRemote;

                        // Now you can start interacting with App Remote
                        startMainActivity();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                case TOKEN:
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    editor.commit();
                    Log.d("Auth", "Received token " + response.getAccessToken());
                    break;

                case ERROR:
                    break;
            }
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }
}