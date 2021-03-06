package rt.sg.racsystemtest.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLING;

/**
 * Created by sg on 2018/4/10.
 */

public class WifiAutoConnectManager {

    private static final String TAG = WifiAutoConnectManager.class
            .getSimpleName();

    private Context mContext;

    // 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    // 构造函数
    public WifiAutoConnectManager(Context context) {
        this.mContext = context;
    }

    // 提供一个外部接口，传入要连接的无线网
    public boolean connect(String ssid, String password, WifiCipherType type) {

        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        // 打开wifi
        openWifi(wifiManager);
        // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                // 为了避免程序一直while循环，让它睡个100毫秒检测……
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                Log.e(TAG, ie.toString());
            }
        }

        delWifiItem("Robustel-30008");
        connectToSpecSsid(addWifiItem("Robustel-30008", "Robustel123"));
        return isConnected("Robustel-30008", mContext);
    }


    private boolean isConnected(String ssid, Context context){
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
        if (null != wifiConfigurations) {
            for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
                if (wifiConfiguration.SSID.equals("\"" + ssid + "\"")) {
                    int wifiState = wifiManager.getWifiState();
                    if(wifiState == WIFI_STATE_ENABLED || wifiState ==WIFI_STATE_ENABLING){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean delWifiItem (String ssid) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
        boolean result = false;
        int networkId = -10;
        if (null != wifiConfigurations) {
            for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
                if (wifiConfiguration.SSID.equals("\"" + ssid + "\"")) {
                    networkId = wifiConfiguration.networkId;
                    Log.i(TAG, "delWifiItem: 找到对应的 ssid,编号为:" + networkId);
                    break;
                }
            }
        }

        if (networkId != -10) {
            Log.i(TAG, "delWifiItem: 执行删除指定网络的过程");
            wifiManager.removeNetwork(networkId);
            result = true;
        }

        wifiConfigurations = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
            if (wifiConfiguration.SSID.equals("\"" + ssid + "\"")) {
                Log.i(TAG, "delWifiItem: 又找到对应的 ssid,编号为:" + networkId);
                result = false;
                break;
            }
        }

        return result;
    }


    private boolean connectToSpecSsid(int netId) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.enableNetwork(netId, true);
    }

    private int addWifiItem(String ssid, String password) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
        int networkId = -10;
        if (null != wifiConfigurations) {
            for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
                if (wifiConfiguration.SSID.equals("\"" + ssid + "\"")) {
                    networkId = wifiConfiguration.networkId;
                    break;
                }
            }
        }

        if (networkId == -10) {
            networkId = wifiManager.addNetwork(createWifiInfo(ssid, password, WifiCipherType
                    .WIFICIPHER_WPA));
        }

        return networkId;
    }

    // 查看以前是否也配置过这个网络
    private WifiConfiguration isExsits(String SSID, WifiManager wifiManager) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();

        if (existingConfigs == null) {
            return null;
        }

        for (int i = 0; i < existingConfigs.size(); i++) {
            WifiConfiguration existingConfig = existingConfigs.get(i);
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password,
                                             WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // config.SSID = SSID;
        // nopass
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            // config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            // config.wepTxKeyIndex = 0;
        }
        // wep
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // wpa
        if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;

        }
        return config;
    }

    // 打开wifi功能
    private boolean openWifi(WifiManager wifiManager) {
        boolean bRet = true;
        if (!wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    // 关闭WIFI
    private void closeWifi(WifiManager wifiManager) {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }
        return true;
    }

    // 获取ssid的加密方式

    public static WifiCipherType getCipherType(Context context, String ssid) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);

        List<ScanResult> list = wifiManager.getScanResults();

        for (ScanResult scResult : list) {

            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                String capabilities = scResult.capabilities;
                // Log.i("hefeng","capabilities=" + capabilities);

                if (!TextUtils.isEmpty(capabilities)) {

                    if (capabilities.contains("WPA")
                            || capabilities.contains("wpa")) {
                        Log.i("hefeng", "wpa");
                        return WifiCipherType.WIFICIPHER_WPA;
                    } else if (capabilities.contains("WEP")
                            || capabilities.contains("wep")) {
                        Log.i("hefeng", "wep");
                        return WifiCipherType.WIFICIPHER_WEP;
                    } else {
                        Log.i("hefeng", "no");
                        return WifiCipherType.WIFICIPHER_NOPASS;
                    }
                }
            }
        }
        return WifiCipherType.WIFICIPHER_INVALID;
    }
}
