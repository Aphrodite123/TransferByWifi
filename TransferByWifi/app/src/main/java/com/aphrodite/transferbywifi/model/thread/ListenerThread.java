package com.aphrodite.transferbywifi.model.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aphrodite.transferbywifi.config.WifiConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Aphrodite on 2018/11/6.
 */
public class ListenerThread extends Thread {
    private static final String TAG = ListenerThread.class.getSimpleName();

    private ServerSocket mServerSocket;
    private Socket mSocket;
    private Handler mHandler;
    private int mPort;

    public ListenerThread(Handler handler, int port) {
        setName(TAG);
        this.mHandler = handler;
        this.mPort = port;
        initData();
    }

    private void initData() {
        try {
            this.mServerSocket = new ServerSocket(mPort);
        } catch (IOException e) {
            Log.e(TAG, "Enter initData method.IOException: " + e);
        }
    }

    @Override
    public void run() {
        if (null == mServerSocket || null == mHandler) {
            return;
        }

        try {
            mSocket = mServerSocket.accept();

            Message message = Message.obtain();
            message.what = WifiConfig.HandlerMsg.DEVICE_CONNECTING;
            mHandler.sendMessage(message);
        } catch (IOException e) {
            Log.e(TAG, "Enter run method.IOException: " + e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

}
