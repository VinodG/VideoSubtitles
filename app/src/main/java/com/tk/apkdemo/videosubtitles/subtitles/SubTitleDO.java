package com.tk.apkdemo.videosubtitles.subtitles;

public class SubTitleDO
{
    
    public int id =0;
    public String startTime="";
    public String endTime="";
    public long startTimeInMills=0;
    public long endTimeInMills=0;
    public String extractedText="";
    public boolean isContains(long time)
    {
        return time<=endTimeInMills && time>=startTimeInMills;
    }
}
