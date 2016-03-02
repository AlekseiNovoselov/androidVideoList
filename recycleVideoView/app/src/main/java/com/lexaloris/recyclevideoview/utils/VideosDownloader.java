package com.lexaloris.recyclevideoview.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.lexaloris.recyclevideoview.interfaces.IVideoDownloadListener;
import com.lexaloris.recyclevideoview.models.Video;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class VideosDownloader {

    private static String TAG = "VideosDownloader";

    Context context;
    IVideoDownloadListener iVideoDownloadListener;

    public VideosDownloader(Context context) {
        this.context = context;
    }


    public void startVideosDownloading(final ArrayList<Video> videosList)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {

                Utils utils = new Utils();
                for(int i=0; i<videosList.size(); i++)
                {
                    final Video video = videosList.get(i);
                    String url = video.getUrl();
                    String isVideoDownloaded = utils.readPreferences(context, video.getUrl());
                    boolean isVideoAvailable = Boolean.valueOf(isVideoDownloaded);
                    if(!isVideoAvailable)
                    {
                        downloadVideo(url);
                        Activity activity = (Activity) context;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils utils = new Utils();
                                utils.savePreferences(context, video.getUrl());
                                iVideoDownloadListener.onVideoDownloaded(video);
                            }
                        });
                    }
                }
            }
        });
        thread.start();
    }

    private String downloadVideo(String urlStr)
    {
        URL url;
        File file = null;
        try
        {
            Utils utils = new Utils();
            String filename = utils.getEntireFileName(urlStr);
            file = new File(context.getFilesDir(), filename);
            url = new URL(urlStr);
            long startTime = System.currentTimeMillis();
            URLConnection ucon;
            ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
            FileOutputStream outStream = new FileOutputStream(file);
            byte[] buff = new byte[5 * 1024];

            int len;
            while ((len = inStream.read(buff)) != -1) {
                outStream.write(buff, 0, len);
            }
            long finishTime = System.currentTimeMillis();
            Log.d(TAG, "download time: " + String.valueOf(finishTime - startTime));

            outStream.flush();
            outStream.close();
            inStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }


    public void setOnVideoDownloadListener(IVideoDownloadListener iVideoDownloadListener) {
        this.iVideoDownloadListener = iVideoDownloadListener;
    }
}
