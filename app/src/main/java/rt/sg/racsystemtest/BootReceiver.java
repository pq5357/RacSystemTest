package rt.sg.racsystemtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * 监听系统启动完成
 * Created by sg on 2018/3/12.
 */

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent startService = new Intent(context, TestManagerService.class);

        context.startService(startService);

        Intent startGpioService = new Intent(context, GpioService.class);

        context.startService(startGpioService);

        Intent startLedService = new Intent(context, LedService.class);

        context.startService(startLedService);


    }



}
