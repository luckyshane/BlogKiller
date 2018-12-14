package com.github.luckyshane.blogkiller.widget;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.luckyshane.github.sutil.CancellableRunnable;
import com.luckyshane.github.sutil.HandlerTimer;

import java.util.concurrent.TimeUnit;

public class SimpleReadWebView {
    private WebView delegate;
    private WebViewClient delegateClient;
    private final String autoReadUrl;
    private final int AUTO_READ_COUNT;
    private int readCount;
    private HandlerTimer timer;
    private CancellableRunnable loadTask;
    private CallBack callBack;
    private volatile boolean isStopped;

    public SimpleReadWebView(@NonNull WebView webView, @Nullable WebViewClient delegateClient, @NonNull String autoReadUrl, int autoReadCount) {
        if (autoReadCount <= 0) {
            throw new IllegalArgumentException("AUTO_READ_COUNT must > 0");
        }
        delegate = webView;
        this.delegateClient = delegateClient;
        this.AUTO_READ_COUNT = autoReadCount;
        this.autoReadUrl = autoReadUrl;
        init();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        delegate.getSettings().setJavaScriptEnabled(true);
        delegate.setWebViewClient(new ProxyWebViewClient());
        timer = HandlerTimer.getMainTimer();
    }

    private class ProxyWebViewClient extends WebViewClient {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (delegateClient != null) {
                return delegateClient.shouldOverrideUrlLoading(view, request);
            }
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (delegateClient != null) {
                return delegateClient.shouldOverrideUrlLoading(view, url);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (delegateClient != null) {
                delegateClient.onPageFinished(view, url);
                SimpleReadWebView.this.onPageFinished(url);
                return;
            }
            super.onPageFinished(view, url);
            SimpleReadWebView.this.onPageFinished(url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (delegateClient != null) {
                delegateClient.onPageStarted(view, url, favicon);
                onPageStart(url);
                return;
            }
            super.onPageStarted(view, url, favicon);
            onPageStart(url);
        }
    }

    private void onPageStart(String url) {
        if (url.equals(autoReadUrl)) {
        readCount++;
        if (callBack != null) {
            callBack.onReadCountChange(readCount, AUTO_READ_COUNT);
        }
    }
}

    private void onPageFinished(String url) {
        if (url.equals(autoReadUrl) && !isStopped) {
            if (readCount < AUTO_READ_COUNT) {
                if (loadTask != null) {
                    loadTask.cancel();
                }
                loadTask = timer.schedule(new Runnable() {
                    @Override
                    public void run() {
                        delegate.loadUrl(autoReadUrl);
                    }
                }, 500, TimeUnit.MILLISECONDS);
            }
        }
    }

    public void toggle() {
        if (isStopped) {
            start();
        } else {
            stop();
        }
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void start() {
        if (readCount < AUTO_READ_COUNT) {
            isStopped = false;
            delegate.loadUrl(autoReadUrl);
        }
    }

    public void stop() {
        if (loadTask != null) {
            loadTask.cancel();
            timer.cancel(loadTask);
            loadTask = null;
        }
        isStopped = true;
    }

    public void onDestroy() {
        if (loadTask != null) {
            loadTask.cancel();
            timer.cancel(loadTask);
            loadTask = null;
        }
        delegate.destroy();
    }

    public interface CallBack {
        void onReadCountChange(int readCount, int maxReadCount);
    }


}
