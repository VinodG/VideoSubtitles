package com.tk.apkdemo.videosubtitles.subtitles;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CustomVideoView extends VideoView
{
    private static final String TAG = "CustomVideoView";
    private long TIME_INTERVAL_TO_UPDATE_SUBTITLES =1500;
    private OnUpdateListener listener ;
    private ArrayList<SubTitleDO> alSubTitle =new ArrayList<SubTitleDO>();
    private MediaController mMc=null;
    private boolean isSubtitleIsEnable=true;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setOnUpdateListener(  OnUpdateListener listener) {
        this.listener=listener;
    }
    public void start(String subtitleFilePath, Uri uri)
    {
        mMc = new MediaController(getContext());
        CustomVideoView.this.setMediaController(mMc);
        CustomVideoView.this.setVideoURI(uri);
        CustomVideoView.this.start(); // to start Video
        readFile(subtitleFilePath);
    }
    public void setSubtitles(boolean isEnable)
    {
        isSubtitleIsEnable = isEnable;
    }

    private void readFile(String filepath) {
        String ccFileName = filepath.substring(0,filepath.lastIndexOf('.')) + ".srt";
        File file = new File(ccFileName);
        if (file.exists() == false)
        {
            listener.onUpdate(null,"File is not existed");
        }else {
            BufferedReader reader = null;
            try {
                UnicodeReader objEncoded = new  UnicodeReader(new FileInputStream(file), "UTF-8");
                String encoded = objEncoded.getEncoding();
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoded));
//            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1));
                String line;
                while ((line = reader.readLine()) != null) {
                    SubTitleDO subTitleDO;
                    if (!line.isEmpty()) {
                        System.out.println(line);
                        try {
                            Integer tempIndex;
                            line = line.trim();
                            try {
                                tempIndex = Integer.parseInt(line.trim().substring(0, line.length()));
                            } catch (NumberFormatException ex) {
                                try {

                                    tempIndex = Integer.parseInt(line.substring(1, line.length()));
                                } catch (NumberFormatException ex2) {
                                    tempIndex = 0;
                                    ex2.printStackTrace();

                                }
                            }
                            subTitleDO = new SubTitleDO();
                            subTitleDO.id = tempIndex;
                            line = reader.readLine();
                            subTitleDO.startTime = line.substring(0, 12);
                            subTitleDO.endTime = line.substring(18, line.length());
                            subTitleDO.startTimeInMills = timeInMilliSeconds(subTitleDO.startTime);
                            subTitleDO.endTimeInMills = timeInMilliSeconds(subTitleDO.endTime);

                            String tempString = "";
                            line = reader.readLine();
                            while (!(line.isEmpty())) {
                                tempString = tempString + line;
                                line = reader.readLine();
                            }
                            subTitleDO.extractedText = tempString;
                            alSubTitle.add(subTitleDO);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }


                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(alSubTitle==null || alSubTitle.size()==0)
            {
                listener.onUpdate(null,"Subtitle file is not supported");
            }
            else {
                updateEndTime(alSubTitle);
                new MyAsync().execute();
            }
        }
    }
    private long timeInMilliSeconds(String strTime)
    {
        long totTimeMils=0;
        String time1 [] = strTime.split( ":");
        try {
            if(time1!=null && time1.length>2) {
                String time2[] = time1[2].split(",");
                totTimeMils=Long.parseLong(time1[0])*1000*60*60;
                totTimeMils+=Long.parseLong(time1[1])*1000*60;
                totTimeMils+=Long.parseLong(time2[0])*1000;
                totTimeMils+=Long.parseLong(time2[1]);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return  totTimeMils;

    }
    private void updateEndTime(ArrayList<SubTitleDO> alSubTitle) {
        if(alSubTitle!=null && alSubTitle.size()>1)
            for (int i=0;i<alSubTitle.size()-1;i++)
            {
                alSubTitle.get(i).endTimeInMills =alSubTitle.get(i+1).startTimeInMills;
                Log.e("Content", alSubTitle.get(i).startTimeInMills+"");
            }
    }

    private class MyAsync extends AsyncTask<Void, Integer, Void>
    {
        int duration = 0;
        int current = 0;
        @Override
        protected Void doInBackground(Void... params) {
            CustomVideoView.this.start();
            CustomVideoView.this.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    duration =  CustomVideoView.this.getDuration();
                }
            });

            do {
                current =  CustomVideoView.this.getCurrentPosition();
                try {
                    if(isSubtitleIsEnable)
                    {
                        publishProgress(current);
                    }
                    Thread.sleep(TIME_INTERVAL_TO_UPDATE_SUBTITLES);
                } catch (Exception e) {
                }
            } while (true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            listener.onUpdate(Html.fromHtml(getStringFromTime(values[0])).toString(),null);
        }
        private String getStringFromTime(long time) {
            String extractedString="";
            if(time>1)
                for (int i=0;i<alSubTitle.size();i++)
                {
                    if(alSubTitle.get(i).isContains(time)) {
                        extractedString = alSubTitle.get(i).extractedText;
                        break;
                    }
                }
            return extractedString;
        }

    }
}
