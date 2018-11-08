package com.aphrodite.transferbywifi.view;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import com.aphrodite.transferbywifi.R;
import com.aphrodite.transferbywifi.application.MainApplication;
import com.aphrodite.transferbywifi.config.WifiConfig;
import com.aphrodite.transferbywifi.model.thread.ConnectThread;
import com.aphrodite.transferbywifi.model.thread.ListenerThread;

import java.io.IOException;
import java.net.Socket;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aphrodite on 2018/11/6.
 */
public class ClientActivity extends AppCompatActivity {
    private static final String TAG = ClientActivity.class.getSimpleName();

    @BindView(R.id.server_info)
    TextView mPromptInfo;

    private WifiManager mWifiManager;
    private ConnectThread mConnectThread;
    private ListenerThread mListenerThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        /**
         * 绑定Activity(注:必须在setContentView之后)
         */
        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initView() {

    }

    private void initData() {
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mListenerThread = new ListenerThread(handler, WifiConfig.PORT);
        mListenerThread.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(getWifiRouteIPAddress(), WifiConfig.PORT);
                    mConnectThread = new ConnectThread(socket, handler);
                    mConnectThread.start();
                } catch (IOException e) {
                    Log.e(TAG, "Enter initData method.IOException: " + e);
                }
            }
        }).start();
    }

    /**
     * wifi获取 已连接网络路由  路由ip地址---方法同上
     *
     * @return
     */
    private String getWifiRouteIPAddress() {
        WifiManager wifi_service = (WifiManager) MainApplication.getApplication()
                .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        String routeIp = Formatter.formatIpAddress(dhcpInfo.gateway);
        return routeIp;
    }

    @OnClick(R.id.connect)
    public void onConnectClick() {

    }

    @OnClick(R.id.send_data)
    public void onSendClick() {
        if (null == mConnectThread) {
            return;
        }

        mConnectThread.sendData("这是来自客户端" + WifiConfig.WIFI_HOTSPOT_SSID + "热点的消息");
    }

    @OnClick(R.id.open_file)
    public void onOpenFileClick() {
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WifiConfig.HandlerMsg.DEVICE_CONNECTING:
                    mConnectThread = new ConnectThread(mListenerThread.getSocket(), handler);
                    mConnectThread.start();
                    break;
                case WifiConfig.HandlerMsg.DEVICE_CONNECTED:
                    mPromptInfo.setText("设备连接成功");
                    break;
                case WifiConfig.HandlerMsg.SEND_MSG_SUCCSEE:
                    mPromptInfo.setText("发送消息成功:" + msg.getData().getString(WifiConfig.BundleKey.MESSAGE));
                    break;
                case WifiConfig.HandlerMsg.SEND_MSG_ERROR:
                    mPromptInfo.setText("发送消息失败:" + msg.getData().getString(WifiConfig.BundleKey.MESSAGE));
                    break;
                case WifiConfig.HandlerMsg.GET_MSG:
                    mPromptInfo.setText("收到消息:" + msg.getData().getString(WifiConfig.BundleKey
                            .MESSAGE).toString());
                    break;
            }
        }
    };

}
