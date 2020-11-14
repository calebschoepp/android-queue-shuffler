package com.calebschoepp.spotifyqueueshuffler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.calebschoepp.spotifyqueueshuffler.syncplayerapi.SyncPlayerApi;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "a13b7f5ec1c7478f92ce6eb811323012";
    private static final String REDIRECT_URI = "com.calebschoepp.spotifyqueueshuffler://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private SyncPlayerApi mPlayer;
    // TODO choose a better sentinel song
    private final String mSentinelTrackUri = "spotify:track:4uLU6hMCjMI75M1A2tKUQC"; // Never gonna give you up


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button shuffleQueueButton = findViewById(R.id.shuffleQueue);
        shuffleQueueButton.setOnClickListener(v -> shuffleQueue());
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        // TODO don't immediately foist Spotify auth upon user. Maybe only connect when shuffle is clicked
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        mPlayer = new SyncPlayerApi(mSpotifyAppRemote.getPlayerApi(), 500);
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void shuffleQueue() {
        // TODO don't use asyncTask
        // TODO be more granular with try/catches
        // TODO refactor this to a viewModel or something like that
        // TODO write this in a more testable manner
        // TODO debug why this behaves weird when desktop is playing
        AsyncTask.execute(() -> {
            try {
                // Get current song and seek position
                PlayerState playerState = mPlayer.getPlayerState();
                String currentSongUri = playerState.track.uri;
                long currentSongPosition = playerState.playbackPosition;
                boolean currentSongIsPaused = playerState.isPaused;

                // Pause the player
                mPlayer.pause();

                // Add sentinel song to queue
                mPlayer.queue(mSentinelTrackUri);

                // Log and skip songs until sentinel song is found
                List<String> queuedSongs = new ArrayList<>();
                while (true) {
                    mPlayer.skipNext();
                    mPlayer.pause();
                    playerState = mPlayer.getPlayerState();

                    // Found the sentinel song
                    if (playerState.track.uri.equals(mSentinelTrackUri)) {
                        break;
                    }

                    queuedSongs.add(playerState.track.uri);
                }

                // Shuffle queued songs
                Collections.shuffle(queuedSongs);

                // Reset player
                mPlayer.queue(currentSongUri);
                mPlayer.skipNext();
                mPlayer.seekTo(currentSongPosition);
                if (currentSongIsPaused) {
                    mPlayer.pause();
                }

                // Add shuffled songs to the queue
                for (String uri : queuedSongs) {
                    mPlayer.queue(uri);
                }

            } catch (Exception e) {
                System.out.println("FAILURE");
                System.out.println(e.getMessage());
            }
        });
    }
}