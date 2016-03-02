package com.lexaloris.recyclevideoview.controller;

import android.content.Context;

import com.lexaloris.recyclevideoview.interfaces.IVideoPreparedListener;
import com.lexaloris.recyclevideoview.models.Video;
import com.lexaloris.recyclevideoview.utils.Utils;
import com.lexaloris.recyclevideoview.views.VideoPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class VideoPlayerController {

    private static String TAG = "VideoPlayerController";

    Context context;
    ArrayList<Integer> visibleElements;
    ArrayList<Integer> playingElements;

    private Map<String, VideoPlayer> videos = Collections.synchronizedMap(new WeakHashMap<String, VideoPlayer>());

    public VideoPlayerController(Context context) {
        this.context = context;
        visibleElements = new ArrayList<>();
        playingElements = new ArrayList<>();
    }

    public void loadVideo(Video video, VideoPlayer videoPlayer) {
        videos.put(video.getId(), videoPlayer);
    }

    public void handlePlayBack(Video video)
    {
        //  Проверка на то, что видео доступно
        if(isVideoDownloaded(video))
        {
            // Проверка, находится ли видео в поле зрения
            if(isVideoVisible(video)) {
                playVideo(video);
            }
        }
    }

    private void playVideo(final Video video)
    {
        //Проигрывание еще не проигрываемых видео
        if(!playingElements.contains(Integer.valueOf(video.getId())))
        {
            if(videos.containsKey(video.getId()))
            {
                final VideoPlayer videoPlayer2 = videos.get(video.getId());
                Utils utils = new Utils();
                String filename = utils.getEntireFileName(video.getUrl());
                String localPath = new File(context.getFilesDir(), filename).getAbsolutePath();
                if(!videoPlayer2.isLoaded()) {
                    videoPlayer2.loadVideo(localPath, video);
                    videoPlayer2.setOnVideoPreparedListener(new IVideoPreparedListener() {
                        @Override
                        public void onVideoPrepared(Video mVideo) {
                            if(Objects.equals(video.getId(), mVideo.getId())) {
                                videoPlayer2.mp.start();
                                playingElements.add(Integer.valueOf(video.getId()));
                            }
                        }
                    });
                }
                else {
                    videoPlayer2.startPlay();
                    playingElements.add(Integer.valueOf(video.getId()));
                }
            }
        }
    }

    private boolean isVideoVisible(Video video) {
        int positionOfVideo = Integer.valueOf(video.getId());
        return visibleElements.contains(positionOfVideo);

    }

    private boolean isVideoDownloaded(Video video) {
        Utils utils = new Utils();
        String isVideoDownloaded = utils.readPreferences(context, video.getUrl());
        return Boolean.valueOf(isVideoDownloaded);
    }

    public void setCurrentPositionOfItemToPlay(ArrayList<Integer> mCurrentPositionOfItemToPlay) {
        visibleElements = mCurrentPositionOfItemToPlay;
    }

    public void handlePlayBackVideos(ArrayList<Video> urls) {
        for (int i : visibleElements) {
            handlePlayBack(urls.get(i));
        }
        for (int i : playingElements) {
            if (!visibleElements.contains(i)) {
                VideoPlayer videoPlayer1 = videos.get(String.valueOf(i));
                if (videoPlayer1 != null) {
                    videoPlayer1.pausePlay();
                }
            }
        }
        playingElements = visibleElements;
    }
}
