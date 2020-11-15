package com.calebschoepp.spotifyqueueshuffler.syncapis;

import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.PlayerState;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SyncPlayerApi {
    private final PlayerApi mPlayerApi;
    private final long mTimeout;

    public SyncPlayerApi(PlayerApi playerApi, long timeout) {
        mPlayerApi = playerApi;
        mTimeout = timeout;
    }

    public PlayerState getPlayerState() throws ExecutionException, InterruptedException, TimeoutException {
        SimpleSettableFuture<PlayerState> future = new SimpleSettableFuture<>();
        mPlayerApi.getPlayerState()
                .setResultCallback(future::set)
                .setErrorCallback(throwable -> future.setException((Exception) throwable));

        return future.get(mTimeout, TimeUnit.MILLISECONDS);
    }

    public void pause() throws ExecutionException, InterruptedException, TimeoutException {
        SimpleSettableFuture<Empty> future = new SimpleSettableFuture<>();
        mPlayerApi.pause()
                .setResultCallback(future::set)
                .setErrorCallback(throwable -> future.setException((Exception) throwable));

        future.get(mTimeout, TimeUnit.MILLISECONDS);
    }

    public void queue(String uri) throws ExecutionException, InterruptedException, TimeoutException {
        SimpleSettableFuture<Empty> future = new SimpleSettableFuture<>();
        mPlayerApi.queue(uri)
                .setResultCallback(future::set)
                .setErrorCallback(throwable -> future.setException((Exception) throwable));

        future.get(mTimeout, TimeUnit.MILLISECONDS);
    }

    public void skipNext() throws ExecutionException, InterruptedException, TimeoutException {
        SimpleSettableFuture<Empty> future = new SimpleSettableFuture<>();
        mPlayerApi.skipNext()
                .setResultCallback(future::set)
                .setErrorCallback(throwable -> future.setException((Exception) throwable));

        future.get(mTimeout, TimeUnit.MILLISECONDS);
    }

    public void seekTo(long position) throws ExecutionException, InterruptedException, TimeoutException {
        SimpleSettableFuture<Empty> future = new SimpleSettableFuture<>();
        mPlayerApi.seekTo(position)
                .setResultCallback(future::set)
                .setErrorCallback(throwable -> future.setException((Exception) throwable));

        future.get(mTimeout, TimeUnit.MILLISECONDS);
    }
}
