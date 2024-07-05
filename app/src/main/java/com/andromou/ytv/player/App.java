package com.andromou.ytv.player;

import android.app.Application;
import android.widget.Toast;

//import com.yausername.ffmpeg.FFmpeg;
import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.YoutubeDL;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Completable.fromAction(this::init).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
            }
            @Override
            public void onError(Throwable e) {
                Toast.makeText(App.this, "Init failed: " + e.getCause(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void init() {
        YoutubeDL.getInstance().init(this);
        FFmpeg.getInstance().init(this);
    }


}
