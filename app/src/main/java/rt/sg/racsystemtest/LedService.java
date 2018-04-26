package rt.sg.racsystemtest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import gpio.Led;

/**
 * Created by sg on 2018/3/29.
 */

public class LedService extends Service{

    private String[] led_ids = new String[]{"running", "wifi", "lte", "green", "red", "beeper"};

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LedThread ledThread = new LedThread();
        Thread thread = new Thread(ledThread);
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

    private class LedThread implements Runnable {
        @Override
        public void run() {

        }
    }


}
