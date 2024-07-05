package com.andromou.ytv.player.tasks;

import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import com.yausername.youtubedl_android.mapper.VideoInfo;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;



public class YoutubeDlTask {
    private VideoInfo streamInfo = null;
    private static final String DEFAULT_FORMAT = "best";

    public Observable<VideoInfo> execute(String url) {
        return Observable.fromCallable(() -> {
                    YoutubeDLRequest request = new YoutubeDLRequest(url);

                    // Add options based on the format parameter
                    if (url.contains("youtube.com")) {
                        // For YouTube
                        request.addOption("-f", DEFAULT_FORMAT);
                    }
                    request.addOption("-f", DEFAULT_FORMAT);
                    // Get stream info
                    streamInfo = YoutubeDL.getInstance().getInfo(request);
                    return streamInfo;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

