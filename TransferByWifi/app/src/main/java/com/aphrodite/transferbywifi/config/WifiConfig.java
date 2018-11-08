package com.aphrodite.transferbywifi.config;

/**
 * Created by Aphrodite on 2018/11/6.
 */
public class WifiConfig extends BaseConfig {
    /**
     * 热点名称
     */
    public static final String WIFI_HOTSPOT_SSID = "aphrodite-wifi";
    /**
     * 端口号
     */
    public static final int PORT = 54321;

    public interface HandlerMsg {
        int BASE = 0x1;
        int DEVICE_CONNECTING = BASE + 1;//有设备正在连接热点
        int DEVICE_CONNECTED = BASE + 2;//有设备连上热点
        int SEND_MSG_SUCCSEE = BASE + 3;//发送消息成功
        int SEND_MSG_ERROR = BASE + 4;//发送消息失败
        int GET_MSG = BASE + 5;//获取新消息
    }

    public interface BundleKey {
        String MESSAGE = "message";
    }

}
