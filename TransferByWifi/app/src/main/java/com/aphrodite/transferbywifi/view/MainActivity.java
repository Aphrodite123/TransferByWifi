package com.aphrodite.transferbywifi.view;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.aphrodite.transferbywifi.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 绑定Activity(注:必须在setContentView之后)
         */
        ButterKnife.bind(this);

        handlerInChildThread();
    }

    /**
     * 主线程给子线程发消息
     * Android的消息机制遵循三个步骤：
     * 1　　创建当前线程的Looper
     * 2　　创建当前线程的Handler
     * 3　　调用当前线程Looper对象的loop方法
     */
    private void handlerInChildThread() {
        HandlerThread handlerThread = new HandlerThread("ChildThread");
        handlerThread.start();

        Handler handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "Enter handleMessage method." + msg);
            }
        };

        handler.sendEmptyMessage(1);
    }

    @OnClick(R.id.main_server)
    public void onServerClick() {
        Intent intent = new Intent(this, ServerActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.main_client)
    public void onClientClick() {
        Intent intent = new Intent(this, ClientActivity.class);
        startActivity(intent);
    }

}
