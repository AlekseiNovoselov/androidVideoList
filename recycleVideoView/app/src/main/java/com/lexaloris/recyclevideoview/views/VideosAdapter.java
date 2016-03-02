package com.lexaloris.recyclevideoview.views;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lexaloris.recyclevideoview.R;
import com.lexaloris.recyclevideoview.controller.VideoPlayerController;
import com.lexaloris.recyclevideoview.models.Video;
import com.lexaloris.recyclevideoview.utils.Utils;

import java.util.ArrayList;


/**
 * Класс адаптера наследуется от RecyclerView.Adapter с указанием класса, который будет хранить ссылки на виджеты элемента списка, т.е. класса, имплементирующего ViewHolder. В нашем случае класс объявлен внутри класса адаптера.
 */
public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    private static final String TAG = "VideosAdapter";
    Context context;
    private ArrayList<Video> urls;
    private VideoPlayerController videoPlayerController;
    IVideoVisibleListener iVideoVisibleListener;

    final int ScreenWidthToTextSize = 20;
    final int MarginTopToScreenWidth = 5;

    /**
     * Реализация класса ViewHolder, хранящего ссылки на виджеты.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView headerText;
        public TextView footerText;
        public RelativeLayout videoPlayerLayout;
        public RelativeLayout layout;

        public ViewHolder(View v) {
            super(v);
            layout = (RelativeLayout) v.findViewById(R.id.layout);
            videoPlayerLayout = (RelativeLayout) v.findViewById(R.id.videoPlayerLayout);
            headerText = (TextView) v.findViewById(R.id.headerText);
            footerText = (TextView) v.findViewById(R.id.footerText);
        }
    }

    public VideosAdapter(Context context, final ArrayList<Video> urls) {

        this.context = context;
        this.urls = urls;
        videoPlayerController = new VideoPlayerController(context);
    }

    public VideoPlayerController getVideoPlayerController() {
        return videoPlayerController;
    }

    /**
     * Создание новых View и ViewHolder элемента списка, которые впоследствии могут переиспользоваться.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_main, parent, false);

        Configuration configuration = context.getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;

        ViewHolder viewHolder = new ViewHolder(v);

        Utils utils = new Utils();
        int screenWidthPixels = utils.convertDpToPixel(screenWidthDp, context);
        // считаем формат видео 3*4
        int screenHeightPixels = (screenWidthPixels*3/4);
        RelativeLayout.LayoutParams rel_btn = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, screenHeightPixels);
        viewHolder.videoPlayerLayout.setLayoutParams(rel_btn);

        return viewHolder;
    }

    /**
     * Заполнение виджетов View данными из элемента списка с номером i
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Video video = urls.get(position);

        final VideoPlayer videoPlayer = new VideoPlayer(context);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        Configuration configuration = context.getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;
        int marginTop = screenWidthDp/(ScreenWidthToTextSize/MarginTopToScreenWidth);

        params.setMargins(0, marginTop, 0, 0);
        videoPlayer.setLayoutParams(params);

        holder.headerText.setText(video.getHeader());
        holder.headerText.setTextSize(screenWidthDp / ScreenWidthToTextSize);
        holder.footerText.setText(video.getFooter());
        holder.footerText.setTextSize(screenWidthDp/ ScreenWidthToTextSize);
        holder.videoPlayerLayout.addView(videoPlayer);

        iVideoVisibleListener.calculateVideoVisible();
        videoPlayerController.loadVideo(video, videoPlayer);
        videoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlayer.changePlayState();
            }
        });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.videoPlayerLayout.removeAllViews();
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public void setOnVideoVisibleListener(IVideoVisibleListener iVideoPreparedListener) {
        this.iVideoVisibleListener = iVideoPreparedListener;
    }

}