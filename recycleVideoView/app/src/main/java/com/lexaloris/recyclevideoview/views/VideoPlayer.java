package com.lexaloris.recyclevideoview.views;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.lexaloris.recyclevideoview.interfaces.IVideoPreparedListener;
import com.lexaloris.recyclevideoview.models.Video;

import java.io.IOException;

public class VideoPlayer extends TextureView implements TextureView.SurfaceTextureListener {

    private static String TAG = "VideoPlayer";

    private boolean isLoaded;
    boolean isMpPrepared;

    IVideoPreparedListener iVideoPreparedListener;

    Video video;
    String url;
    public MediaPlayer mp;
    Surface surface;

    public VideoPlayer(Context context) {
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void loadVideo(String localPath, Video video) {
        this.url = localPath;
        this.video = video;
        if (this.isAvailable()) {
            prepareVideo(getSurfaceTexture());
        }
        isLoaded = true;
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable");
        isMpPrepared = false;
        prepareVideo(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

        if(mp!=null) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    public void prepareVideo(SurfaceTexture t) {
        Log.d(TAG, "prepareVideo");
        this.surface = new Surface(t);
        mp = new MediaPlayer();
        mp.setSurface(this.surface);

        try {
            mp.setDataSource(url);
            mp.prepareAsync();

            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    isMpPrepared = true;
                    mp.setLooping(true);
                    iVideoPreparedListener.onVideoPrepared(video);
                }


            });
        } catch (IllegalArgumentException | SecurityException |
                IllegalStateException | IOException e1) {
            e1.printStackTrace();
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    public boolean startPlay() {
        if(mp!=null)
            if(!mp.isPlaying()) {
                mp.start();
                return true;
            }

        return false;
    }

    public void pausePlay() {
        if(mp!=null)
            mp.pause();
    }

    public void stopPlay() {
        if(mp!=null)
            mp.stop();
    }

    public void changePlayState() {
        if(mp!=null) {
            if(mp.isPlaying())
                mp.pause();
            else
                mp.start();
        }
    }

    public void setOnVideoPreparedListener(IVideoPreparedListener iVideoPreparedListener) {
        this.iVideoPreparedListener = iVideoPreparedListener;
    }

    public boolean isLoaded() {
        return isLoaded;
    }
}
