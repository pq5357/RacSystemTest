package rt.sg.racsystemtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import gpio.Led;

/**
 * 测试网络状态切换
 * Created by sg on 2018/3/30.
 */

public class ConnectionChangeReceiver extends BroadcastReceiver {


    private static  final String TAG =ConnectionChangeReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"网络状态改变");
        /**
         * 获得网络连接服务
         */

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State state =connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (NetworkInfo.State.CONNECTED==state){
            Log.i(TAG,"当前wifi已连接");
            Led.changeDelayOff("wifi", "500");
            Led.changeDelayOn("wifi", "500");
        }else{
            Led.changeDelayOff("wifi", "500");
            Led.changeDelayOn("wifi", "0");
        }
        state =connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (NetworkInfo.State.CONNECTED==state){
            Log.i(TAG,"当前4g已连接");
            Led.changeDelayOff("lte", "500");
            Led.changeDelayOn("lte", "500");
        }else{
            Led.changeDelayOff("lte", "500");
            Led.changeDelayOn("lte", "0");
        }

        state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).getState();
        if (NetworkInfo.State.CONNECTED==state){
            Log.i(TAG,"当前以太网已连接");
        }
    }
}
