package rt.sg.racsystemtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import rt.sg.racsystemtest.gpio.GpioConfig;

/**
 * 监听Gpio的配置操作，收到配置操作后将其保存到SP
 * Created by sg on 2018/3/23.
 */

public class GpioConfigReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String id = intent.getStringExtra("id");

        String configMode = intent.getStringExtra("configMode");

        GpioConfig gpioConfig = new GpioConfig(id,configMode);

        SharedPreferences sp = context.getSharedPreferences("GpioConfig",Context.MODE_PRIVATE);

        Gson gson = new Gson();

        sp.edit().putString(id,gson.toJson(gpioConfig)).commit();

    }
}
