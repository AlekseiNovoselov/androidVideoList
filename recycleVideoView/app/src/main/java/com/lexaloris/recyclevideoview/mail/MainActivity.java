package com.lexaloris.recyclevideoview.mail;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.lexaloris.recyclevideoview.R;
import com.lexaloris.recyclevideoview.interfaces.IVideoDownloadListener;
import com.lexaloris.recyclevideoview.models.Video;
import com.lexaloris.recyclevideoview.utils.NetworkHelper;
import com.lexaloris.recyclevideoview.utils.Utils;
import com.lexaloris.recyclevideoview.utils.VideosDownloader;
import com.lexaloris.recyclevideoview.utils.XmlParser;
import com.lexaloris.recyclevideoview.views.IVideoVisibleListener;
import com.lexaloris.recyclevideoview.views.VideosAdapter;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements IVideoDownloadListener {

    private static String TAG = "MainActivity";

    private Context context;
    private RecyclerView mRecyclerView;
    private VideosAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Video> urls;
    VideosDownloader videosDownloader;
    XmlParser xmlParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        urls = new ArrayList<>();
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new VideosAdapter(MainActivity.this, urls);
        mRecyclerView.setAdapter(mAdapter);

        videosDownloader = new VideosDownloader(context);
        videosDownloader.setOnVideoDownloadListener(this);

        xmlParser = new XmlParser();
        if(NetworkHelper.isInternet(context))
        {
            try {
                getVideoUrls();

            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        refreshVisibility();
                    }
                }
            });
        }

        else
            Toast.makeText(context, "No internet available", Toast.LENGTH_SHORT).show();

        mAdapter.setOnVideoVisibleListener(new IVideoVisibleListener() {
            @Override
            public void calculateVideoVisible() {
                refreshVisibility();
            }
        });
    }

    private void refreshVisibility() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) mRecyclerView.getLayoutManager());
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        Utils utils = new Utils();
        ArrayList<Integer> visibleElements = utils.calculateVisibleElements(mRecyclerView, firstVisiblePosition, lastVisiblePosition);

        if (urls != null && urls.size() > 0) {
            mAdapter.getVideoPlayerController().setCurrentPositionOfItemToPlay(visibleElements);
            mAdapter.getVideoPlayerController().handlePlayBackVideos(urls);
        }
    }

    @Override
    public void onVideoDownloaded(Video video) {
        refreshVisibility();
    }

    private void getVideoUrls() throws ParserConfigurationException, IOException, SAXException {
        xmlParser.getVideoUrls(urls, getResources().openRawResource(R.raw.config));
        mAdapter.notifyDataSetChanged();
        videosDownloader.startVideosDownloading(urls);
    }
}
