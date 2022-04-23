# VideoSubtitles

This project is to show subtitles on VideoView. 
Following steps need to be done before using "com.tk.apkdemo.videosubtitles.subtitles".
1. Add "CustomVideoView" in layout file.
2. Implement for OnUpdateListener for "customevideoview". 

Ex: 


customVideoView.setOnUpdateListener(new OnUpdateListener(){
            @Override
            public void onUpdate(Object object, String error) {
                if(error==null)
                {
                // Following line is to show updated subtitles  
                    tvSubTitle.setText(((String)object) +"");
                }
            }
        });
3. Add subtitleView.start( subtitled file path,uri for video file ) (Add permissions to manifestfile and app to access External Storage)
