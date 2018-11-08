package com.aphrodite.transferbywifi.model.thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aphrodite.transferbywifi.config.WifiConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Aphrodite on 2018/11/6.
 */
public class ConnectThread extends Thread {
    private static final String TAG = ConnectThread.class.getSimpleName();

    private Socket mSocket;
    private Handler mHandler;
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    public ConnectThread(Socket socket, Handler handler) {
        setName(TAG);
        this.mSocket = socket;
        this.mHandler = handler;
    }

    @Override
    public void run() {
        if (null == mSocket || null == mHandler) {
            return;
        }

        mHandler.sendEmptyMessage(WifiConfig.HandlerMsg.DEVICE_CONNECTED);

        try {
            //获取数据流
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();

            byte[] bytes = new byte[1024];
            int buffer;
            while (true) {
                //按字节读取数据
                buffer = mInputStream.read(bytes);
                if (buffer > 0) {
                    byte[] data = new byte[buffer];

                    Message message = Message.obtain();
                    message.what = WifiConfig.HandlerMsg.GET_MSG;
                    Bundle bundle = new Bundle();
                    bundle.putString(WifiConfig.BundleKey.MESSAGE, data.toString());
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "Enter run method.IOException: " + e);
        }
    }

    /**
     * 发送数据
     */
    public void sendData(String msg) {
        if (null == mOutputStream) {
            return;
        }

        try {
            mOutputStream.write(msg.getBytes());

            Message message = Message.obtain();
            message.what = WifiConfig.HandlerMsg.SEND_MSG_SUCCSEE;
            Bundle bundle = new Bundle();
            bundle.putString(WifiConfig.BundleKey.MESSAGE, new String(msg));
            message.setData(bundle);
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            Message message = Message.obtain();
            message.what = WifiConfig.HandlerMsg.SEND_MSG_ERROR;
            Bundle bundle = new Bundle();
            bundle.putString(WifiConfig.BundleKey.MESSAGE, new String(msg));
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }

}
