package com.example.admin.bdplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.cloud.media.player.BDCloudMediaPlayer;
import com.baidu.cloud.media.player.IMediaPlayer;
import com.example.admin.bdplayer.entity.VideoInfo;
import com.example.admin.bdplayer.util.PlayerHelper;
import com.example.admin.bdplayer.util.SharedPreferencesBdplayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;


public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener, IMediaPlayer.OnBufferingUpdateListener, BDCloudVideoView.OnPlayerStateListener, IMediaPlayer.OnErrorListener {
    private PopupWindow popupWindow;
    private ImageView speed;
    private BDCloudVideoView mVV = null;
    private RelativeLayout mViewHolder = null;
    private SeekBar seekBar;
    private Timer positionTimer;
    private static final int POSITION_REFRESH_TIME = 500;
    private TextView positionView;
    private TextView durationView;
    private ImageView play;

    private LinearLayout header_bar;
    private LinearLayout ctrl_bar;

    private ImageView lock;
    private ImageView ratate;

    private TextView jindutishitext;

    int zongshichang;
    int dangqianshichang;
    int dangqianshituodong;
    float anxiazuobiaox;
    float anxiazuobiaoy;
    float huadongzongzuobiao;
    int dangqianliangdu;
    int currentVolume;
    int dangqianyinliang;
    int dangqianliandu;
    int anxiayinliang;
    int zuidayinliang;
    int dongzuo;
    AudioManager audioManager;
    int width;
    int height;
    private String TAG = "Plyer";
    private boolean suoding = false;
    private long currentPositionInMilliSeconds = 0L;
    boolean mbIsDragging = false;
    private VideoInfo info;
    private LinearLayout yujiazai;
    private TextView speed_text;
    private TextView title;
    private LinearLayout luckyTip;
    private TextView luckyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideBottomUIMenu();
        Intent intent = getIntent();
        info = (VideoInfo) intent.getSerializableExtra("VideoInfo");
        if (info == null) {
            String url = intent.getDataString();
            if (url != null) {
                if (!url.isEmpty()) {
                    info = new VideoInfo();
                    info.setUrl(url);
                    info.setTitle(url);
                }
            }
        }
        yujiazai = (LinearLayout) findViewById(R.id.yujiazai);
        speed_text = (TextView) findViewById(R.id.speed_txv);
        title = (TextView) findViewById(R.id.title);
        title.setText(info.getTitle());
        findViewById(R.id.player_set).setOnClickListener(this);
        findViewById(R.id.track).setOnClickListener(this);
        findViewById(R.id.player_set).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);
        speed = findViewById(R.id.speed);
        speed.setOnClickListener(this);
        header_bar = findViewById(R.id.header_bar);
        ctrl_bar = findViewById(R.id.ctrl_bar);
        jindutishitext = findViewById(R.id.jindutishi);
        play = findViewById(R.id.play);
        play.setOnClickListener(this);
        ratate = findViewById(R.id.ratate);
        ratate.setOnClickListener(this);
        lock = findViewById(R.id.lock);
        lock.setOnClickListener(this);
        luckyTip = findViewById(R.id.luckyTip);
        luckyTip.findViewById(R.id.jump).setOnClickListener(this);
        luckyTip.findViewById(R.id.cancle).setOnClickListener(this);
        luckyInfo = findViewById(R.id.info);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.width = displayMetrics.widthPixels;
        this.height = displayMetrics.heightPixels;

        this.audioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
        if (audioManager != null) {
            int j = this.audioManager.getStreamMaxVolume(3);
            this.zuidayinliang = j;
            this.zuidayinliang *= 6;
        }
        float f = this.currentVolume * 6;
        try {
            int k = Settings.System.getInt(getContentResolver(), "screen_brightness");
            f = 1.0F * k / 255.0F;
        } catch (Settings.SettingNotFoundException localSettingNotFoundException) {
            localSettingNotFoundException.printStackTrace();
        }
        this.dangqianliangdu = ((int) (f * 100.0F));
        this.dangqianliandu = ((int) (f * 100.0F));

        init_view();
    }

    private void init_view() {
        BDCloudVideoView.setAK(info.getAK());
        positionView = findViewById(R.id.tv_position);
        durationView = findViewById(R.id.tv_duration);
        seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePostion(progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                mbIsDragging = true;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mVV.getDuration() > 0) {
                    // 仅非直播的视频支持拖动
                    currentPositionInMilliSeconds = seekBar.getProgress();
                    if (mVV != null) {
                        mVV.seekTo(seekBar.getProgress());
                    }
                }
                mbIsDragging = false;
            }
        });
        mViewHolder = findViewById(R.id.view_holder);
        mVV = new BDCloudVideoView(this);
        mVV.setOnBufferingUpdateListener(this);
        mVV.setOnPlayerStateListener(this);
        mVV.setVideoScalingMode(info.getScaleModel());
        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(-1, -1);
        rllp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mViewHolder.addView(mVV, rllp);
        mVV.setLogEnabled(info.isLogEnabled());
        mVV.setDecodeMode(info.getDecodeModel());
        mVV.setMaxProbeTime(60000); // 设置首次缓冲的最大时长
        mVV.setMaxProbeSize(10 * 1024 * 1024);
        mVV.setTimeoutInUs(1000000);
        mVV.setVideoPath(info.getUrl());
        mVV.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.speed) {
            window_speed();
        } else if (v.getId() == R.id.track) {
            window_track();
        } else if (v.getId() == R.id.player_set) {
            window_set();
        } else if (v.getId() == R.id.play) {
            if (mVV != null) {
                if (mVV.isPlaying()) {
                    play.setImageResource(R.drawable.bd_ic_play_selector);
                    mVV.pause();
                } else {
                    play.setImageResource(R.drawable.bd_ic_pause_selector);
                    mVV.start();
                }
            }
        } else if (v.getId() == R.id.back) {
            releaseVideo();
        } else if (v.getId() == R.id.next) {
            int pos = mVV.getCurrentPosition();
            if (pos + 10000 <= mVV.getDuration()) {
                mVV.seekTo(pos + 10000);
            } else {
                Toasty.info(VideoPlayerActivity.this, "已到达视频结尾处！").show();
            }
        } else if (v.getId() == R.id.ratate) {
            Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
            int ori = mConfiguration.orientation; //获取屏幕方向
            if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
                //横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
            } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
                //竖屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//强制为横屏
            }
        } else if (v.getId() == R.id.lock) {
            if (suoding) {
                suoding = false;
                showBars();
                lock.setImageResource(R.drawable.bd_ic_unlock_selector);
            } else {
                lock.setImageResource(R.drawable.bd_ic_lock_selector);
                suoding = true;
                hideAfterFiveSecond();
            }
        } else if (v.getId() == R.id.cancle) {
            luckyTip.setVisibility(View.GONE);
            SharedPreferencesBdplayer.setParam(VideoPlayerActivity.this, Integer.toString(info.getUrl().hashCode()), 0);
        } else if (v.getId() == R.id.jump) {
            luckyTip.setVisibility(View.GONE);
            mVV.seekTo(lastPostion);
        }
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    private void hideAfterFiveSecond() {
        Animation animation = AnimationUtils.loadAnimation(VideoPlayerActivity.this, R.anim.anim_top_out);
        header_bar.startAnimation(animation);
        header_bar.setVisibility(View.GONE);
        Animation animation2 = AnimationUtils.loadAnimation(VideoPlayerActivity.this, R.anim.anim_bottom_out);
        ctrl_bar.startAnimation(animation2);
        ctrl_bar.setVisibility(View.GONE);
        Animation animation3 = AnimationUtils.loadAnimation(VideoPlayerActivity.this, R.anim.anim_left_out);
        lock.startAnimation(animation3);
        lock.setVisibility(View.GONE);
        Animation animation4 = AnimationUtils.loadAnimation(VideoPlayerActivity.this, R.anim.anim_right_out);
        ratate.startAnimation(animation4);
        ratate.setVisibility(View.GONE);
        speed_text.startAnimation(animation4);
        speed_text.setVisibility(View.GONE);
    }

    private void onClickEmptyArea() {
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                return;
            }
        }
        if (suoding) {
            if (lock.getVisibility() != View.VISIBLE) {
                lock.setVisibility(View.VISIBLE);
                Animation animation3 = AnimationUtils.loadAnimation(VideoPlayerActivity.this, R.anim.anim_left_in);
                lock.startAnimation(animation3);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation animation3 = AnimationUtils.loadAnimation(VideoPlayerActivity.this, R.anim.anim_left_out);
                        lock.startAnimation(animation3);
                        lock.setVisibility(View.GONE);
                    }
                }, 3000);
            }
            return;
        }
        if (header_bar.getVisibility() == View.GONE) {
            showBars();
        } else {
            hideBottomUIMenu();
            hideAfterFiveSecond();
        }
    }

    private void showBars() {
        header_bar.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(VideoPlayerActivity.this, R.anim.anim_top_in);
        header_bar.startAnimation(animation);

        ctrl_bar.setVisibility(View.VISIBLE);
        Animation animation2 = AnimationUtils.loadAnimation(VideoPlayerActivity.this, R.anim.anim_bottom_in);
        ctrl_bar.startAnimation(animation2);

        lock.setVisibility(View.VISIBLE);
        Animation animation3 = AnimationUtils.loadAnimation(VideoPlayerActivity.this, R.anim.anim_left_in);
        lock.startAnimation(animation3);

        ratate.setVisibility(View.VISIBLE);
        Animation animation4 = AnimationUtils.loadAnimation(VideoPlayerActivity.this, R.anim.anim_right_in);
        ratate.startAnimation(animation4);

        speed_text.setVisibility(View.VISIBLE);
        speed_text.startAnimation(animation4);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float f;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.zongshichang = this.mVV.getDuration() / 1000;
                this.dangqianshichang = this.mVV.getCurrentPosition() / 1000;
                this.anxiazuobiaox = x;
                this.anxiazuobiaoy = y;
                this.dongzuo = 1;
                this.currentVolume = this.audioManager.getStreamVolume(3);
                this.anxiayinliang = (this.currentVolume * 6);
                f = 1.0F;
                try {
                    int i = Settings.System.getInt(getContentResolver(), "screen_brightness");
                    f = 1.0F * i / 255.0F;
                } catch (Settings.SettingNotFoundException localSettingNotFoundException) {
                    localSettingNotFoundException.printStackTrace();
                }
                this.huadongzongzuobiao = y;
                break;
            case MotionEvent.ACTION_UP:

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        jindutishitext.setVisibility(View.GONE);
                        switch (dongzuo) {
                            case 2:
                                if (!suoding) {
                                    if (mVV.getDuration() <= 5) {
                                        return;
                                    }
                                    mVV.seekTo(dangqianshituodong * 1000);
                                    break;
                                }
                            case 3:
                                dangqianliandu = dangqianliangdu;
                                break;
                            case 4:
                                break;
                            default:
                                onClickEmptyArea();
                                break;
                        }
                    }
                }, 100L);

            case MotionEvent.ACTION_MOVE:
                if (!suoding) {
                    f = Math.abs(x - this.anxiazuobiaox);
                    float abs = Math.abs(y - this.anxiazuobiaoy);
                    if (this.dongzuo == 1) {
                        if (f > 50.0f && abs < 50.0f) {
                            this.dongzuo = 2;
                        }
                        if (f < 50.0f && abs > 50.0f && ((double) this.anxiazuobiaox) < ((double) this.width) * 0.25d) {
                            this.dongzuo = 3;
                        }
                        if (f < 50.0f && abs > 50.0f && ((double) this.anxiazuobiaox) > ((double) this.width) * 0.75d) {
                            this.dongzuo = 4;
                        }
                    }
                    switch (this.dongzuo) {
                        case 2:
                            this.dangqianshituodong = (int) ((float) ((((double) (((x - this.anxiazuobiaox) / ((float) this.width)) * ((float) this.zongshichang))) * 0.3d) + ((double) this.dangqianshichang)));
                            if (this.dangqianshituodong < 0) {
                                this.dangqianshituodong = 0;
                            }
                            if (this.dangqianshituodong > this.zongshichang) {
                                this.dangqianshituodong = this.zongshichang;
                            }
                            this.jindutishitext.setVisibility(View.VISIBLE);
                            this.jindutishitext.setText(updateTextViewWithTimeFormat2(this.dangqianshituodong) + "/" + updateTextViewWithTimeFormat2(this.zongshichang));
                            break;
                        case 3:
                            float f6 = (y - this.huadongzongzuobiao) * 100.0F / this.height;

                            this.dangqianliangdu = (this.dangqianliandu - (int) f6);
                            if (this.dangqianliangdu > 100) {
                                this.dangqianliangdu = 100;
                            }
                            if (this.dangqianliangdu < 7) {
                                this.dangqianliangdu = 7;
                            }
                            this.jindutishitext.setVisibility(View.VISIBLE);
                            int j = (this.dangqianliangdu - 7) * 100 / 93;
                            this.jindutishitext.setText("亮度：" + j + "%");

                            setBrightness(this.dangqianliangdu);
                            break;
                        case 4:
                            float f7 = (y - this.huadongzongzuobiao) * 100.0F / this.height;

                            this.dangqianyinliang = (this.anxiayinliang - (int) f7);
                            if (this.dangqianyinliang > this.zuidayinliang) {
                                this.dangqianyinliang = this.zuidayinliang;
                            }
                            if (this.dangqianyinliang < 0) {
                                this.dangqianyinliang = 0;
                            }
                            this.jindutishitext.setVisibility(View.VISIBLE);
                            int k = this.dangqianyinliang * 100 / this.zuidayinliang;
                            this.jindutishitext.setText("音量：" + k + "%");
                            int m = this.dangqianyinliang / 6;
                            this.audioManager.setStreamVolume(3, m, 0);
                            break;
                        default:
                            break;
                    }
                }
                break;
        }
        return true;
    }

    public void setBrightness(int paramInt) {
        if (paramInt < 0) {
            paramInt = 0;
        }
        if (paramInt > 100) {
            paramInt = 100;
        }
        WindowManager.LayoutParams localLayoutParams = this.getWindow().getAttributes();
        localLayoutParams.screenBrightness = (1.0F * paramInt / 100.0F);
        this.getWindow().setAttributes(localLayoutParams);
        this.dangqianliangdu = paramInt;
    }

    private String fitModel = "适应屏幕";

    private void window_set() {
        if (!mVV.isPlaying()) {
            return;
        }
        View popupWindowView = getLayoutInflater().inflate(R.layout.window_speed_select, null);
        //内容，高度，宽度
        popupWindow = new PopupWindow(popupWindowView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        //动画效果
        popupWindow.setAnimationStyle(R.style.AnimationRightFade);
        //宽度
        Display display = getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);//不能省略,必须有
        int width = outSize.x;

        popupWindow.setWidth((int) (width / 2.5));
        //高度
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setFocusable(false);
        //显示位置
        popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_video_player, null), Gravity.RIGHT, 0, 0);
        //关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                hideBottomUIMenu();
            }
        });
        popupWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });
        TextView title = popupWindowView.findViewById(R.id.title);
        title.setText("缩放模式");
        ListView list = popupWindowView.findViewById(R.id.list);
        final ArrayList<SpeedModel> data = new ArrayList<>();
        SpeedModel a = new SpeedModel();
        a.speedText = "适应屏幕";
        a.selected = a.speedText.equals(fitModel);
        SpeedModel a2 = new SpeedModel();
        a2.speedText = "裁剪视频";
        a2.selected = a2.speedText.equals(fitModel);
        SpeedModel a3 = new SpeedModel();
        a3.speedText = "填满屏幕";
        a3.selected = a3.speedText.equals(fitModel);
        data.add(a);
        data.add(a2);
        data.add(a3);
        SpeedItemAdapter adapter = new SpeedItemAdapter(this, data);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fitModel = data.get(position).speedText;
                switch (position) {
                    case 0:
                        mVV.setVideoScalingMode(BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                        break;
                    case 1:
                        mVV.setVideoScalingMode(BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                        break;
                    case 2:
                        mVV.setVideoScalingMode(BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_MATCH_PARENT);
                        break;
                }

                popupWindow.dismiss();
            }
        });
    }

    private String selectedSpeed = "1.0x";

    private void window_speed() {
        if (!mVV.isPlaying()) {
            return;
        }
        View popupWindowView = getLayoutInflater().inflate(R.layout.window_speed_select, null);
        //内容，高度，宽度
        popupWindow = new PopupWindow(popupWindowView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        //动画效果
        popupWindow.setAnimationStyle(R.style.AnimationRightFade);
        //宽度
        Display display = getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);//不能省略,必须有
        int width = outSize.x;

        popupWindow.setWidth((int) (width / 2.5));
        //高度
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setFocusable(false);
        //显示位置
        popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_video_player, null), Gravity.RIGHT, 0, 0);
        //关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                hideBottomUIMenu();
            }
        });
        popupWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });
        TextView title = popupWindowView.findViewById(R.id.title);
        title.setText("倍速播放");
        ListView list = popupWindowView.findViewById(R.id.list);
        final ArrayList<SpeedModel> data = new ArrayList<>();
        SpeedModel a = new SpeedModel();
        a.speedText = "0.8x";
        a.speed = (float) 0.8;
        a.selected = a.speedText.equals(selectedSpeed);
        SpeedModel a2 = new SpeedModel();
        a2.speedText = "1.0x";
        a2.speed = (float) 1.0;
        a2.selected = a2.speedText.equals(selectedSpeed);
        SpeedModel a3 = new SpeedModel();
        a3.speedText = "1.25x";
        a3.speed = (float) 1.25;
        a3.selected = a3.speedText.equals(selectedSpeed);
        SpeedModel a4 = new SpeedModel();
        a4.speedText = "1.5x";
        a4.speed = (float) 1.5;
        a4.selected = a4.speedText.equals(selectedSpeed);
        SpeedModel a5 = new SpeedModel();
        a5.speedText = "2.0x";
        a5.speed = (float) 2.0;
        a5.selected = a5.speedText.equals(selectedSpeed);
        data.add(a);
        data.add(a2);
        data.add(a3);
        data.add(a4);
        data.add(a5);

        SpeedItemAdapter adapter = new SpeedItemAdapter(this, data);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSpeed = data.get(position).speedText;
                switch (position) {
                    case 0:
                        speed.setImageResource(R.drawable.bd_speed_rate_0_8);
                        break;
                    case 1:
                        speed.setImageResource(R.drawable.bd_speed_rate_1_0);
                        break;
                    case 2:
                        speed.setImageResource(R.drawable.bd_speed_rate_1_25);
                        break;
                    case 3:
                        speed.setImageResource(R.drawable.bd_speed_rate_1_5);
                        break;
                    case 4:
                        speed.setImageResource(R.drawable.bd_speed_rate_2_0);
                        break;
                }
                mVV.setSpeed(data.get(position).speed);
                popupWindow.dismiss();
            }
        });
    }

    private void window_track() {
        if (!mVV.isPlaying()) {
            return;
        }
        final int pos = mVV.getCurrentPosition();
        View popupWindowView = getLayoutInflater().inflate(R.layout.window_speed_select, null);
        //内容，高度，宽度
        popupWindow = new PopupWindow(popupWindowView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        //动画效果
        popupWindow.setAnimationStyle(R.style.AnimationRightFade);
        //宽度
        Display display = getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);//不能省略,必须有
        int width = outSize.x;
        popupWindow.setWidth((int) (width / 2.5));
        //高度
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setFocusable(false);
        //显示位置
        popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_video_player, null), Gravity.RIGHT, 0, 0);
        //关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                hideBottomUIMenu();
            }
        });
        popupWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });
        TextView title = popupWindowView.findViewById(R.id.title);
        title.setText("音轨选择");
        ListView list = popupWindowView.findViewById(R.id.list);
        final ArrayList<SpeedModel> data = new ArrayList<>();
        com.baidu.cloud.media.player.misc.a[] track = mVV.getTrack();
        for (int i = 0; i < track.length; i++) {
            if (track[i].getTrackType() == 2) {
                Log.e("TAG", "音轨:" + i);
                Log.e("TAG", "语言:" + track[i].getLanguage());
                SpeedModel a = new SpeedModel();
                a.speedText = "音轨:" + i + ">" + track[i].getLanguage();
                a.speed = (float) i;
                if (mVV.getSelectedTrack() == i) {
                    a.selected = true;
                } else {
                    a.selected = false;
                }
                data.add(a);
            }
        }

        SpeedItemAdapter adapter = new SpeedItemAdapter(this, data);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (data.size() > 1 && mVV.getSelectedTrack() != (int) data.get(position).speed) {
                    mVV.setTrack((int) data.get(position).speed);
                    mVV.seekTo(pos);
                }
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent * mVV.getDuration() / 100);
    }

    @Override
    public void onPlayerStateChanged(BDCloudVideoView.PlayerState nowState) {
        if (nowState == BDCloudVideoView.PlayerState.STATE_IDLE || nowState == BDCloudVideoView.PlayerState.STATE_ERROR) {
            stopPositionTimer();
            play.setEnabled(true);
            play.setImageResource(R.drawable.bd_ic_play_selector);
            seekBar.setEnabled(false);
            updatePostion(mVV == null ? 0 : mVV.getCurrentPosition());
            updateDuration(mVV == null ? 0 : mVV.getDuration());
        } else if (nowState == BDCloudVideoView.PlayerState.STATE_PREPARED) {
            play.setEnabled(true);
            play.setImageResource(R.drawable.bd_ic_pause_selector);
            seekBar.setMax(mVV.getDuration());
            seekBar.setEnabled(true);
            updateDuration(mVV.getDuration());
            hideAfterFiveSecond();
            if (info.isOpenHestoryPlay()) {
                new Thread(runnable).start();
            }
        } else if (nowState == BDCloudVideoView.PlayerState.STATE_PLAYING) {
            play.setEnabled(true);
            play.setImageResource(R.drawable.bd_ic_pause_selector);
            seekBar.setEnabled(true);
            startPositionTimer();
        } else if (nowState == BDCloudVideoView.PlayerState.STATE_PLAYBACK_COMPLETED) {
            stopPositionTimer();
            releaseVideo();
        } else if (nowState == BDCloudVideoView.PlayerState.STATE_PREPARING) {
            play.setEnabled(false);
            startPositionTimer();
            seekBar.setEnabled(false);
        } else if (nowState == BDCloudVideoView.PlayerState.STATE_PAUSED) {
            play.setEnabled(true);
            play.setImageResource(R.drawable.bd_ic_play_selector);
            stopPositionTimer();
        }

    }

    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    private void startPositionTimer() {
        if (positionTimer != null) {
            positionTimer.cancel();
            positionTimer = null;
        }
        positionTimer = new Timer();
        positionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mVV == null) {
                            return;
                        }
                        speed_text.setText(PlayerHelper.convertFileSize(mVV.getDownloadSpeed()) + "/s");
                        onPositionUpdate();

                    }
                });
            }
        }, 0, POSITION_REFRESH_TIME);
    }

    private void stopPositionTimer() {
        if (positionTimer != null) {
            positionTimer.cancel();
            positionTimer = null;
        }
    }

    private String formatMilliSecond(int milliSecond) {
        int seconds = milliSecond / 1000;
        int hh = seconds / 3600;
        int mm = seconds % 3600 / 60;
        int ss = seconds % 60;
        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        return strTemp;
    }

    public boolean getIsDragging() {
        return mbIsDragging;
    }

    public void onPositionUpdate() {
        if (mVV == null) {
            return;
        }
        long newPositionInMilliSeconds = mVV.getCurrentPosition();
        long previousPosition = currentPositionInMilliSeconds;
        if (newPositionInMilliSeconds > 0 && !getIsDragging()) {
            currentPositionInMilliSeconds = newPositionInMilliSeconds;
        }
        if (seekBar.getVisibility() != View.VISIBLE) {
            // 如果控制条不可见，则不设置进度
            return;
        }
        if (!getIsDragging()) {
            int durationInMilliSeconds = mVV.getDuration();
            if (durationInMilliSeconds > 0) {
                seekBar.setMax(durationInMilliSeconds);
                // 直播视频的duration为0，此时不设置进度
                if (previousPosition != newPositionInMilliSeconds) {
                    //DebugUtil.e("update", "set..."+newPositionInMilliSeconds);
                    seekBar.setProgress((int) newPositionInMilliSeconds);
                }
            }
        }
    }

    private void updateDuration(int milliSecond) {
        if (durationView != null) {
            durationView.setText(formatMilliSecond(milliSecond));
        }
    }

    private void updatePostion(int milliSecond) {
        if (positionView != null) {
            positionView.setText(formatMilliSecond(milliSecond));
        }
    }

    private String updateTextViewWithTimeFormat2(int i) {
        int i2 = (i % 3600) / 60;
        int i3 = i % 60;
        if (i / 3600 != 0) {
            return String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(i / 3600), Integer.valueOf(i2), Integer.valueOf(i3)});
        }
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)});
    }

    boolean isPausedByOnPause = false;

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        // 当home键切出，想暂停视频的话，反注释下面的代码。同时要反注释onResume中的代码
        if (mVV != null) {
            if (mVV.isPlaying()) {
                isPausedByOnPause = true;
                mVV.pause();
            }
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 当home键切出，暂停了视频此时想回复的话，反注释下面的代码
        if (mVV != null) {
            if (isPausedByOnPause) {
                isPausedByOnPause = false;
                mVV.start();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mVV != null) {
            mVV.enterForeground();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // enterBackground should be invoke before super.onStop()
        if (mVV != null) {
            mVV.enterBackground();
        }
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        String Msg = "未知错误,可能该资源已失效";
        switch (what) {
            case BDCloudMediaPlayer.MEDIA_ERROR_UNKNOWN:
                Msg = "未知错误,可能该资源失效了哦";
                break;
            case BDCloudMediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Msg = "媒体服务器挂掉了";
                break;
            case BDCloudMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Msg = "视频播放比较慢或视频本身有问题";
                break;
            case BDCloudMediaPlayer.MEDIA_ERROR_IO:
                Msg = "IO错误";
                break;
            case BDCloudMediaPlayer.MEDIA_ERROR_MALFORMED:
                Msg = "比特流不符合相关的编码标准和文件规范";
                break;
            case BDCloudMediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Msg = "暂不支持当前格式";
                break;
            case BDCloudMediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Msg = "播放连接超时";
                break;
            case -10000:
                Msg = "资源连接失败,可能资源已失效";
                break;
        }
        Toasty.info(VideoPlayerActivity.this, Msg).show();
        releaseVideo();
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!suoding) {
                releaseVideo();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void releaseVideo() {
        try {

            stopPositionTimer();
            if (mVV != null) {
                if (info.isOpenHestoryPlay()) {
                    if (mVV.getCurrentPosition() + 20000 < mVV.getDuration()) {
                        SharedPreferencesBdplayer.setParam(VideoPlayerActivity.this, Integer.toString(info.getUrl().hashCode()), mVV.getCurrentPosition());
                    }
                }
                mVV.stopPlayback(); // 释放播放器资源
                mVV.release(); // 释放播放器资源和显示资源
            }
            mVV = null;
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int lastPostion = 0;
    private Handler handler = new MyHandler(VideoPlayerActivity.this);

    static class MyHandler extends Handler {
        WeakReference<Activity> mWeakReference;

        private MyHandler(Activity activity) {
            mWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final VideoPlayerActivity activity = (VideoPlayerActivity) mWeakReference.get();
            if (activity != null) {
                if (msg.what == 1) {
                    activity.luckyInfo.setText("检测到您上次观看到: " + activity.formatMilliSecond(activity.lastPostion) + " 是否恢复?");
                    activity.luckyTip.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            lastPostion = (int) SharedPreferencesBdplayer.getParam(VideoPlayerActivity.this, Integer.toString(info.getUrl().hashCode()), 0);
            if (lastPostion != 0) {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }
    };
}
