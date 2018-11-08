package com.aphrodite.transferbywifi.view;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.aphrodite.transferbywifi.R;
import com.aphrodite.transferbywifi.config.WifiConfig;
import com.aphrodite.transferbywifi.model.thread.ConnectThread;
import com.aphrodite.transferbywifi.model.thread.ListenerThread;
import com.aphrodite.transferbywifi.utils.ToastUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aphrodite on 2018/11/6.
 */
public class ServerActivity extends AppCompatActivity {
    private static final String TAG = ServerActivity.class.getSimpleName();

    @BindView(R.id.server_info)
    TextView mPromptInfo;

    private WifiManager mWifiManager;
    private ConnectThread mConnectThread;
    private ListenerThread mListenerThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
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
    }

    private void createWifiHot() {
        if (mWifiManager.isWifiEnabled()) {
            //如果wifi处于打开状态，则关闭wifi,
            mWifiManager.setWifiEnabled(false);
        }

        final WifiConfiguration config = new WifiConfiguration();
        config.SSID = WifiConfig.WIFI_HOTSPOT_SSID;
        config.hiddenSSID = false;
        //开放系统认证
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        Method method = null;
        try {
            method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            boolean enable = (Boolean) method.invoke(mWifiManager, config, true);
            if (enable) {
                ToastUtil.showMessage("创建成功，热点名称：" + WifiConfig.WIFI_HOTSPOT_SSID);
            } else {
                ToastUtil.showMessage("创建失败，请重试");
            }
        } catch (NoSuchMethodException e) {
            ToastUtil.showMessage("创建失败，请重试");
            Log.e(TAG, "Enter createWifiHot method.NoSuchMethodException: " + e);
        } catch (IllegalAccessException e) {
            ToastUtil.showMessage("创建失败，请重试");
            Log.e(TAG, "Enter createWifiHot method.IllegalAccessException: " + e);
        } catch (InvocationTargetException e) {
            ToastUtil.showMessage("创建失败，请重试");
            Log.e(TAG, "Enter createWifiHot method.InvocationTargetException: " + e);
        }
    }

    /**
     * 关闭WiFi热点
     */
    public void closeWifiHot() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
            Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method2.invoke(mWifiManager, config, false);

            ToastUtil.showMessage("已关闭");
        } catch (NoSuchMethodException e) {
            ToastUtil.showMessage("关闭异常，请重试");
            Log.e(TAG, "Enter closeWifiHot method.NoSuchMethodException: " + e);
        } catch (IllegalArgumentException e) {
            ToastUtil.showMessage("关闭异常，请重试");
            Log.e(TAG, "Enter closeWifiHot method.IllegalArgumentException: " + e);
        } catch (IllegalAccessException e) {
            ToastUtil.showMessage("关闭异常，请重试");
            Log.e(TAG, "Enter closeWifiHot method.IllegalAccessException: " + e);
        } catch (InvocationTargetException e) {
            ToastUtil.showMessage("关闭异常，请重试");
            Log.e(TAG, "Enter closeWifiHot method.InvocationTargetException: " + e);
        }
    }

    public String getWifiIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && (WifiConfig.WIFI_HOTSPOT_SSID.length() == inetAddress.getAddress().length)) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, "Enter getWifiIpAddress method.SocketException: " + e);
        }
        return null;
    }

    @OnClick(R.id.create_wifi_hot)
    public void onCreateHotClick() {
        createWifiHot();
    }

    @OnClick(R.id.close_wifi_hot)
    public void onCloseHotClick() {
        closeWifiHot();
    }

    @OnClick(R.id.send)
    public void onSendClick() {
        if (null == mConnectThread) {
            return;
        }

        mConnectThread.sendData("这是来自服务端" + WifiConfig.WIFI_HOTSPOT_SSID + "热点的消息");
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
