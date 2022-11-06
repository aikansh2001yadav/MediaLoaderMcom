package com.example.medialoader;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;

public class MainActivity extends AppCompatActivity implements CacheListener {

    String videoName[]={"Big Buck Bunny",
            "Elephant Dream",
            "For Bigger Blazes",
            "For Bigger Escape"
    };
    String listOfURLS[]={"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
    };

    private HttpProxyCacheServer proxy;
    private VideoView videoView;
    private TextView videoHeading;
    private Button btnPrevious;
    private Button btnNext;

    private int currentVideoIndex = 0;

    private HttpProxyCacheServer getProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheFilesCount(20)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.idVideoView);
        videoHeading = findViewById(R.id.idTVHeading);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);

        videoHeading.setText(videoName[currentVideoIndex]);
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        startVideo();

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentVideoIndex == 0){
                    Toast.makeText(MainActivity.this, "No previous video found", Toast.LENGTH_SHORT).show();
                }else{
                    currentVideoIndex--;
                    videoHeading.setText(videoName[currentVideoIndex]);
                    startVideo();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentVideoIndex == 3){
                    Toast.makeText(MainActivity.this, "No video found", Toast.LENGTH_SHORT).show();
                }else{
                    currentVideoIndex++;
                    videoHeading.setText(videoName[currentVideoIndex]);
                    startVideo();
                }
            }
        });
    }

    private void startVideo() {
        proxy = App.getProxy(this);
        proxy.registerCacheListener(this, listOfURLS[currentVideoIndex]);
        String proxyUrl = proxy.getProxyUrl(listOfURLS[currentVideoIndex]);
        Log.d("CACHE_STATUS", "Use proxy url " + proxyUrl + " instead of original url " + listOfURLS[currentVideoIndex]);
        videoView.setVideoPath(proxyUrl);
        videoView.start();
    }

    @Override
    public void onCacheAvailable(File file, String url, int percentsAvailable) {
        Log.d("CACHE_STATUS", String.format("onCacheAvailable. percents: %d, file: %s, url: %s", percentsAvailable, file, url));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
        App.getProxy(this).unregisterCacheListener(this);
    }
}