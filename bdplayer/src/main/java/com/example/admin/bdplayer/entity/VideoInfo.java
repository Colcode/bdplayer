package com.example.admin.bdplayer.entity;

import android.content.Context;
import com.baidu.cloud.media.player.BDCloudMediaPlayer;
import com.example.admin.bdplayer.BDCloudVideoView;
import com.example.admin.bdplayer.util.SharedPreferencesBdplayer;

import java.io.Serializable;

public class VideoInfo implements Serializable {
    private String Title;
    private String Url;
    private String AK;
    private int DecodeModel = 0;
    private boolean LogEnabled = false;
    private boolean openHestoryPlay = false;
    private int ScaleModel = BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT;
    public int DECODE_AUTO = BDCloudMediaPlayer.DECODE_AUTO;
    public int DECODE_SW = BDCloudMediaPlayer.DECODE_SW;
    public int VIDEO_SCALING_MODE_SCALE_TO_FIT = BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT;
    public int VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;
    public int VIDEO_SCALING_MODE_SCALE_TO_MATCH_PARENT = BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_MATCH_PARENT;

    public int getScaleModel() {
        return ScaleModel;
    }

    public boolean isOpenHestoryPlay() {
        return openHestoryPlay;
    }

    public void setOpenHestoryPlay(boolean openHestoryPlay) {
        this.openHestoryPlay = openHestoryPlay;
    }

    public void setScaleModel(int scaleModel) {
        ScaleModel = scaleModel;
    }


    public boolean isLogEnabled() {
        return LogEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        LogEnabled = logEnabled;
    }


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

    public int getDecodeModel() {
        return DecodeModel;
    }

    public void setDecodeModel(int decodeModel) {
        DecodeModel = decodeModel;
    }

    public static void clearBdHestory(Context context) {
        SharedPreferencesBdplayer.clear(context);
    }
}
