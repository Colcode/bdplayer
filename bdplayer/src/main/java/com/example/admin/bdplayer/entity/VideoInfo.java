package com.example.admin.bdplayer.entity;

import java.io.Serializable;

public class VideoInfo implements Serializable {
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getAK() {
        return AK;
    }

    public void setAK(String AK) {
        this.AK = AK;
    }

    public String Title;
    public String Url;
    public String AK;
}
