package com.github.luckyshane.blogkiller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.github.luckyshane.blogkiller.widget.SimpleReadWebView;
import com.luckyshane.github.sutil.ThreadUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SimpleReadActivity extends AppCompatActivity {
    private static final String KEY_URL = "key_url";
    private static final String KEY_READ_COUNT = "key_read_count";

    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.load_desc_tv)
    TextView descTv;
    @BindView(R.id.toggle_btn)
    Button toggleBtn;
    private SimpleReadWebView readWebView;
    private String readUrl;
    private int readCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.activity_simple_read);
        ButterKnife.bind(this);
        initView();
    }

    private void initData() {
        readUrl = getIntent().getStringExtra(KEY_URL);
        readCount = getIntent().getIntExtra(KEY_READ_COUNT, 100);
    }

    private void initView() {
        readWebView = new SimpleReadWebView(webView, null, readUrl, readCount);
        readWebView.setCallBack(new SimpleReadWebView.CallBack() {
            @Override
            public void onReadCountChange(final int readCount, final int maxReadCount) {
                ThreadUtil.runOnUI(new Runnable() {
                    @Override
                    public void run() {
                        descTv.setText(String.format("read count: %d, maxReadCount: %d", readCount, maxReadCount));
                    }
                });
            }
        });
        readWebView.start();
        if (readWebView.isStopped()) {
            toggleBtn.setText("Start");
        } else {
            toggleBtn.setText("Stop");
        }
    }

    @OnClick(R.id.toggle_btn)
    void onToggleBtnClick() {
        readWebView.toggle();
        if (readWebView.isStopped()) {
            toggleBtn.setText("Start");
        } else {
            toggleBtn.setText("Stop");
        }
    }

    @Override
    protected void onDestroy() {
        readWebView.onDestroy();
        super.onDestroy();
    }

    public static void openThis(Context context, @NonNull String url, int readCount) {
        if (readCount <= 0) {
            throw new IllegalArgumentException();
        }
        Intent intent = new Intent(context, SimpleReadActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_READ_COUNT, readCount);
        context.startActivity(intent);
    }



}
