package com.lexaloris.recyclevideoview.models;

public class Video {

    String id;
    String url;
    String header;
    String footer;

    public Video(String id, String header, String url, String footer) {
        this.id = id;
        this.url = url;
        this.header = header;
        this.footer = footer;

    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }
}
