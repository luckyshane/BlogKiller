package com.github.luckyshane.blogkiller;

import android.app.Application;
import android.util.Log;

import com.github.luckyshane.blogkiller.bean.MyObjectBox;
import com.luckyshane.github.sutil.SUtil;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

public class App extends Application {
    private static App instance;
    private BoxStore boxStore;
    @Override
    public void onCreate() {
        super.onCreate();
        SUtil.init(this);
        instance = this;
        boxStore = MyObjectBox.builder().androidContext(this).build();
        if (BuildConfig.DEBUG) {
            boolean started = new AndroidObjectBrowser(boxStore).start(this);
            Log.i("ObjectBrowser", "Started: " + started);
        }
    }

    public static App getInstance() {
        return instance;
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}
