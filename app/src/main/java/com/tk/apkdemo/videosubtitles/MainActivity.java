package com.tk.apkdemo.videosubtitles;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.tk.apkdemo.videosubtitles.subtitles.CustomVideoView;
import com.tk.apkdemo.videosubtitles.subtitles.OnUpdateListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private int i=0;
    private TextView tvSubTitle;
    private CustomVideoView subtitleView;
    private Switch swSubtitles;
    private Translator englishGermanTranslator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subtitleView = findViewById(R.id.subtitleView);
        tvSubTitle = (TextView)findViewById(R.id.tvSubTitle);
        swSubtitles = (Switch)findViewById(R.id.swSubtitles);
        showVideo(externalStoragePath+"/video/");
        createTranslator(TranslateLanguage.TAMIL);
        downloadTranslatorModel();
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
        Uri uri = Uri.parse(path+"kisses24.mp4");
        Log.e(TAG, "video path "+path+"video.mp4" );
        subtitleView.setOnUpdateListener(new OnUpdateListener(){
            @Override
            public void onUpdate(Object object, String error) {
                if(error==null) {
//                    tvSubTitle.setText(((String)object) +"");
                    translateText(tvSubTitle,(String)object);
                }
                else
                    Toast.makeText(MainActivity.this,error+"",Toast.LENGTH_LONG).show();
            }
        });
        subtitleView.start(path+"kisses24.srt",uri);
        Log.e(TAG, "subtitle path "+path+"video.srt" );

    }

    private void translateText(final TextView tvSubTitle, final  String str) {
        englishGermanTranslator.translate(str)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                // Translation successful.
                                tvSubTitle.setText(translatedText);
                                Log.e(TAG, "onSuccess: "+translatedText );
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error.
                                // ...
                                tvSubTitle.setText(str);
                                Log.e(TAG, "onFailure: "+str );
                            }
                        });

    }

    public void toast(String str)  {
        Toast.makeText(MainActivity.this,str+"",Toast.LENGTH_LONG).show();
    }
    private void createTranslator(String translator) {
        // Create an English-German translator:
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(translator)
                        .build();
        englishGermanTranslator =  Translation.getClient(options);

    }

    private void downloadTranslatorModel() {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        englishGermanTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                // Model downloaded successfully. Okay to start translating.
                                // (Set a flag, unhide the translation UI, etc.)
                                toast("Model is downloaded");
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be downloaded or other internal error.
                                // ...
                                toast("error occured while Model is being downloaded : "+e.getMessage());
                            }
                        });

    }

}

