package rt.sg.racsystemtest;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import gpio.Gpio;
import rt.sg.racsystemtest.gpio.GpioConfig;


/**
 * Gpio的权限操作和配置服务
 * Created by sg on 2018/3/23.
 */

public class GpioService extends Service {

    private String[] gpio_ids = new String[]{"3", "4", "164", "165", "146", "147", "148", "149"};

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GpioThread gpioThread = new GpioThread();
        Thread thread = new Thread(gpioThread);
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 系统启动根据之前配置，设置GPIO的方向
     */
    private class GpioThread implements Runnable {

        @Override
        public void run() {
            SharedPreferences sp = getSharedPreferences("GpioConfig", MODE_PRIVATE);
            Gson gson = new Gson();
            for (String id : gpio_ids) {
                String config = sp.getString(id, "");
                if(!config.isEmpty()){
                    GpioConfig gpioConfig = gson.fromJson(config, GpioConfig.class);
                    Gpio.configGpioDirection(GpioService.this,gpioConfig.getId(),gpioConfig.getMode());
                }
            }
            stopSelf();
        }
    }
}
