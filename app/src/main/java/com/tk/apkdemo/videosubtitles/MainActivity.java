package com.tk.apkdemo.videosubtitles;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tk.apkdemo.videosubtitles.subtitles.CustomVideoView;
import com.tk.apkdemo.videosubtitles.subtitles.OnUpdateListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private int i=0;
    private TextView tvSubTitle;
    private CustomVideoView subtitleView;
    private Switch swSubtitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subtitleView = findViewById(R.id.subtitleView);
        tvSubTitle = (TextView)findViewById(R.id.tvSubTitle);
        swSubtitles = (Switch)findViewById(R.id.swSubtitles);
        showVideo(externalStoragePath+"/video/");
    }

    private void showVideo(String path)
    {
        swSubtitles.setChecked(true);
        swSubtitles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    subtitleView.setSubtitles(true);
                    toast("Subtitles are ON");
                }
                else {
                    subtitleView.setSubtitles(false);
                    toast("Subtitles are OFF");
                    tvSubTitle.setText("");
                }

            }
        });
        Uri uri = Uri.parse(path+"video.mp4");
        subtitleView.setOnUpdateListener(new OnUpdateListener(){
            @Override
            public void onUpdate(Object object, String error) {
                if(error==null)
                {
                    tvSubTitle.setText(((String)object) +"");
                }
                else
                    Toast.makeText(MainActivity.this,error+"",Toast.LENGTH_LONG).show();
            }
        });
        subtitleView.start(path+"video.srt",uri);

    }
    public void toast(String str)
    {
        Toast.makeText(MainActivity.this,str+"",Toast.LENGTH_LONG).show();
    }


}

