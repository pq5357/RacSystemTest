package rt.sg.racsystemtest;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gpio.Gpio;
import gpio.Led;
import rt.sg.racsystemtest.MediaRecord.MediaRecorderDemo;
import rt.sg.racsystemtest.serial.SerialPortManager;
import rt.sg.racsystemtest.serial.listener.OnSerialPortDataListener;
import rt.sg.racsystemtest.wifi.WifiAutoConnectManager;

/**
 * 测试核心类
 * Created by sg on 2018/3/12.
 */

public class TestCore {


    private static final String TAG = "TestCore";

    public static final String HOME = "com.rac.broadcast.home";
    public static final String VOLUME_UP = "com.rac.broadcast.volume_up";
    public static final String VOLUME_DOWN = "com.rac.broadcast.volume_down";
    public static final String ANDROID_BLUETOOTH_DEVICE_ACTION_FOUND = "android.bluetooth.device" +
            ".action.FOUND";
    private static volatile TestCore instance = null;

    /*正常情况下除OTG端口外的USB接口数*/
    private static final int NORMAL_USB_COUNT = 3;

    private volatile boolean isEthernetTestEnd = false;

    private Context mContext;

    private TestCore(Context context) {
        mContext = context;
    }

    public static TestCore getInstance(Context context) {
        if (instance == null) {
            synchronized (TestCore.class) {
                if (instance == null) {
                    instance = new TestCore(context);
                }
            }
        }
        return instance;
    }


    /**
     * 测试以太网
     *
     * @return
     */
    public TestResultEvent testEthernet(TestResultEvent resultEvent) {

        isEthernetTestEnd = false;

        Intent intent = new Intent("rac.intent.action.CLOSE_DATA");

        mContext.sendBroadcast(intent);

        boolean isLinked = ping("www.baidu.com");

        isEthernetTestEnd = true;

        Intent intent1 = new Intent("rac.intent.action.OPEN_DATA");

        mContext.sendBroadcast(intent1);

        if(isLinked){
            resultEvent.setResult("网口正常");
            resultEvent.setResult_code(TestResultEvent.OK);
        }else{
            resultEvent.setResult("网口异常");
            resultEvent.setResult_code(TestResultEvent.ERROR);
        }

        return resultEvent;
    }

    /**
     * 获取IP
     */
    public TestResultEvent getIp(TestResultEvent resultEvent) {

        String localIpAddress = TestUtils.getLocalIpAddress();
        resultEvent.setResult(localIpAddress);

        return resultEvent;

    }

    /**
     * 打印全部的USB端口和设备信息
     */
    public TestResultEvent testAllUsbDevice(TestResultEvent resultEvent) {

        int usb_count = 0;

        UsbManager mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);

        mUsbManager.getAccessoryList();

        HashMap<String, UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
        Iterator<UsbDevice> iterator = deviceHashMap.values().iterator();
        while (iterator.hasNext()) {
            UsbDevice device = iterator.next();
            Log.i("usb", device.toString());
            if (!device.getProductName().equals("Android")) {
                usb_count++;
            }
        }

        if(usb_count == NORMAL_USB_COUNT){
            resultEvent.setResult(NORMAL_USB_COUNT + "个USB端口均正常");
            resultEvent.setResult_code(TestResultEvent.OK);
        }else{
            resultEvent.setResult("存在USB端口异常");
            resultEvent.setResult_code(TestResultEvent.ERROR);
        }

        return resultEvent;
    }

    /**
     * 测试按键
     */
    public TestResultEvent testButtons(TestResultEvent resultEvent) {

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(HOME);
        intentFilter.addAction(VOLUME_UP);
        intentFilter.addAction(VOLUME_DOWN);

        ButtonReceiver receiver = new ButtonReceiver();

        mContext.registerReceiver(receiver, intentFilter);

        Thread time = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        time.start();
        try {
            time.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mContext.unregisterReceiver(receiver);

        if(receiver.getResult()){
            resultEvent.setResult("按键均正常");
            resultEvent.setResult_code(TestResultEvent.OK);
        }else{
            resultEvent.setResult("按键存在问题");
            resultEvent.setResult_code(TestResultEvent.ERROR);
        }

        return resultEvent;
    }


    private class ButtonReceiver extends BroadcastReceiver {

        boolean home_ok = false;
        boolean volume_up_ok = false;
        boolean volume_down_ok = false;

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(HOME)) {
                home_ok = true;
            } else if (action.equals(VOLUME_UP)) {
                volume_up_ok = true;
            } else if (action.equals(VOLUME_DOWN)) {
                volume_down_ok = true;
            }
        }

        public boolean getResult() {

            return home_ok && volume_up_ok && volume_down_ok;
        }

    }


    /**
     * 测试SD卡
     */
    public TestResultEvent testSdCard(TestResultEvent resultEvent) {

        String extendedMemoryPath = getExtendedMemoryPath(mContext);

        if(extendedMemoryPath != null ){
            resultEvent.setResult("SD卡正常");
            resultEvent.setResult_code(TestResultEvent.OK);
        }else{
            resultEvent.setResult("SD卡异常");
            resultEvent.setResult_code(TestResultEvent.ERROR);
        }

        return resultEvent;
    }


    /**
     * 通过判断挂载路径能否移除确定外置SD卡是否存在
     *
     * @param mContext
     * @return
     */
    private static String getExtendedMemoryPath(Context mContext) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context
                .STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Method getDescription = storageVolumeClazz.getMethod("getDescription", Context.class);
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                String descprition = (String) getDescription.invoke(storageVolumeElement, mContext);
                if (removable && descprition.contains("SD")) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 测试4G模块和SIM卡
     *
     * @return
     */
    public TestResultEvent test4gModel(TestResultEvent resultEvent) {
        // 1. 断开其他连接方式
/*        EthernetManager mEthernetManager =  (EthernetManager)mContext.getSystemService(Context
.ETHERNET_SERVICE);

        mEthernetManager.stop();*/

        if (isEthernetTestEnd == true) {

            Intent intent = new Intent("rac.intent.action.CLOSE_ETHERNET");

            mContext.sendBroadcast(intent);
        } else {
            try {
                Thread.sleep(5000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent("rac.intent.action.CLOSE_ETHERNET");

            mContext.sendBroadcast(intent);
        }


        // 2. 等待4g模块连接
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 3. ping www.baidu.com
        boolean isLinked = ping("www.baidu.com");

        // 4. 打开其他连接方式
        Intent intent_open = new Intent("rac.intent.action.OPEN_ETHERNET");

        mContext.sendBroadcast(intent_open);

        // 5. 等待以太网恢复
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(isLinked){
            resultEvent.setResult("模块和SIM卡正常");
            resultEvent.setResult_code(TestResultEvent.OK);
        }else{
            resultEvent.setResult("模块和SIM卡异常");
            resultEvent.setResult_code(TestResultEvent.ERROR);
        }


        return resultEvent;
    }


    /**
     * 判断当前的网络连接状态是否能用
     * return ture  可用   flase不可用
     *
     * @param s
     */
    public static final boolean ping(String s) {

        String result = null;
        try {
            String ip = s;// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;

    }

    /**
     * 待测试
     */
    private static String[] serials = new String[]{"ttySAC1", "ttysWK0","ttysWK2" ,"ttysWK1",
            "ttySAC4", "ttySAC2"};

    private volatile boolean isSerialTesting = false;

    /**
     * 测试全部串口
     */
    public TestResultEvent testAllSerials(TestResultEvent resultEvent) {

        Log.i("serial", "++++++++++++++开始测量");

        for (String serial : serials) {

            boolean flag = testOneSerial(serial);

            if (!flag) {
                resultEvent.setResult(serial + "出错");
                resultEvent.setResult_code(TestResultEvent.ERROR);
                return resultEvent;
            }
        }

        resultEvent.setResult("全部串口正常");
        resultEvent.setResult_code(TestResultEvent.OK);
        return resultEvent;
    }


    /**
     * 测试单一串口
     *
     * @return
     */
    private boolean testOneSerial(String device_name) {
        //1. 设置串口波特率
        int baudrate = 115200;
        final boolean[] isReceived = {false};
        //2. 注册串口监听
        final SerialPortManager mSerialPortManager = new SerialPortManager();

        mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                String content = new String(bytes);
                Log.i("serial", "received" + content);
                if (content.equals("received success")) {
                    isReceived[0] = true;
                }
            }

            @Override
            public void onDataSent(byte[] bytes) {
                String content = new String(bytes);
                Log.i("serial", "send" + content);
            }
        });

        //4. 打开串口
        boolean openSerialPort = mSerialPortManager.openSerialPort(new File("/dev/" + device_name)
                , baudrate);

        boolean sendBytes = mSerialPortManager.sendBytes(("send success").getBytes());

        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mSerialPortManager.closeSerialPort();

        return isReceived[0];
    }


    /**
     * 根据IP获取本地Mac
     */
    public static TestResultEvent getMacFromIp(TestResultEvent resultEvent) {
        String localMacAddressFromIp = TestUtils.getLocalMacAddressFromIp();
        resultEvent.setResult(localMacAddressFromIp);
        return resultEvent;
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public TestResultEvent getDeviceModel(TestResultEvent resultEvent ) {
        resultEvent.setResult(Build.MODEL);
        return resultEvent;
    }

    //同步线程池，处理gpio口的测试
    private boolean isGpioRecycle = false;

    private ExecutorService testThreadPool = Executors.newScheduledThreadPool(3);

    private String[] ids = new String[]{"3", "4", "164", "165", "146", "147", "148", "149"};

    /**
     * 测试全部GPIO接口,存在输入和输出两种情况
     *
     * @return
     */
    public TestResultEvent testAllGpio(TestResultEvent resultEvent) {
        isGpioRecycle = true;
        /*
        输入
        echo 3 > /sys/class/gpio/export
        echo 4 > /sys/class/gpio/export
        echo 164 > /sys/class/gpio/export
        echo 165 > /sys/class/gpio/export
        cat /sys/class/gpio/gpio3/value

        输出
        echo 147 > /sys/class/gpio/export
        echo out > /sys/class/gpio/gpio147/direction
        echo 146 > /sys/class/gpio/export
        echo out > /sys/class/gpio/gpio146/direction
        echo 149 > /sys/class/gpio/export
        echo out > /sys/class/gpio/gpio149/direction
        echo 148 > /sys/class/gpio/export
        echo out > /sys/class/gpio/gpio148/direction

        echo 1 > /sys/class/gpio/gpio148/value
        */
        for (final String id : ids) {
            testThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    testOutGpio(id);
                }
            });
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                isGpioRecycle = false;
            }
        }, 50000);

        resultEvent.setResult("看看闪了没");
        return resultEvent;
    }

    private void testOutGpio(final String id) {

        Gpio.configGpioDirection(mContext, id, "out");

        if (id.equals("148")) {
            Gpio.configGpioDirection(mContext, id, "in");
            return;
        }

        getExecutorService(id).execute(new Runnable() {
            @Override
            public void run() {
                while (isGpioRecycle) {
                    try {
                        Gpio.setOutGpio(id, "0");
                        Thread.sleep(1000);
                        Gpio.setOutGpio(id, "1");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private HashMap<String, ExecutorService> executorMaps = new HashMap<>();

    private ExecutorService getExecutorService(String id) {
        if (executorMaps.containsKey(id)) {
            return executorMaps.get(id);
        } else {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorMaps.put(id, executorService);
            return executorService;
        }
    }

    boolean state = true;

    /**
     * 测试蜂鸣器
     *
     * @return
     */
    public TestResultEvent testBuzzer(TestResultEvent resultEvent) {
        String s = "听听响了没";
        Led.turnBeeper(true);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Led.turnBeeper(false);
        resultEvent.setResult(s);
        return resultEvent;
    }

    /**
     * 播放音频测试
     *
     * @return
     */
    public TestResultEvent testAudio(TestResultEvent resultEvent) {
        final MediaPlayer[] mMediaPlayer = {MediaPlayer.create(mContext, R.raw.testmusic)};
        mMediaPlayer[0].start();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                mMediaPlayer[0].stop();
                mMediaPlayer[0].release();
                mMediaPlayer[0] = null;
            }
        }, 60000);
        resultEvent.setResult("播放测试音乐");
        return resultEvent;
    }


    private static final int WIFICIPHER_NOPASS = 0;
    private static final int WIFICIPHER_WEP = 1;
    private static final int WIFICIPHER_WPA = 2;

    /**
     * 测试wifi
     *
     * @return
     */
    public TestResultEvent testWifi(TestResultEvent resultEvent) {

/*        WifiAutoConnectManager wifiAutoConnectManager = new WifiAutoConnectManager(mContext);

        boolean enable = wifiAutoConnectManager.connect("Robustel-308", "Robustel123",
                WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA);*/

        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();

        try {
            Thread.sleep(20000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<ScanResult> wifiList = wifiManager.getScanResults();

        Log.i(TAG, wifiList.size() + "wifi列表");

        if(wifiList.size() > 0){
            resultEvent.setResult("wifi正常");
            resultEvent.setResult_code(TestResultEvent.OK);
        }else{
            resultEvent.setResult("wifi异常");
            resultEvent.setResult_code(TestResultEvent.ERROR);
        }

        return resultEvent;
    }


    /**
     * 测试蓝牙
     *
     * @return
     */
    public TestResultEvent testBlueTeeth(TestResultEvent resultEvent) {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        try {
            Thread.sleep(10000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mBluetoothAdapter.startDiscovery();

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ANDROID_BLUETOOTH_DEVICE_ACTION_FOUND);

        DeviceReceiver receiver = new DeviceReceiver();

        mContext.registerReceiver(receiver, intentFilter);

        Thread time = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(40000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        time.start();
        try {
            time.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mBluetoothAdapter.cancelDiscovery();

        mBluetoothAdapter.disable();

        mContext.unregisterReceiver(receiver);

        if(receiver.getResult()){
            resultEvent.setResult_code(TestResultEvent.OK);
            resultEvent.setResult("蓝牙正常");
        }else{
            resultEvent.setResult_code(TestResultEvent.ERROR);
            resultEvent.setResult("蓝牙异常");
        }

        return resultEvent;
    }

    private class DeviceReceiver extends BroadcastReceiver {

        boolean isOk = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ANDROID_BLUETOOTH_DEVICE_ACTION_FOUND)) {
                isOk = true;
            }
        }

        public boolean getResult() {
            return isOk;
        }
    }


    boolean isHasMicInput = false;

    /**
     * 测试麦克风
     *
     * @return
     */
    public TestResultEvent testMIC(TestResultEvent resultEvent) {


        Thread thread = Thread.currentThread();

        isHasMicInput = false;

        EventBus.getDefault().register(this);

        MediaRecorderDemo mediaRecorderDemo = new MediaRecorderDemo();

        mediaRecorderDemo.startRecord();

        try {
            Thread.sleep(20000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mediaRecorderDemo.stopRecord();

        EventBus.getDefault().unregister(this);

        if(isHasMicInput){
            resultEvent.setResult("麦克风正常");
            resultEvent.setResult_code(TestResultEvent.OK);
        }else {
            resultEvent.setResult("麦克风异常");
            resultEvent.setResult_code(TestResultEvent.ERROR);
        }

        return resultEvent;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onReceivedMediaRecord(MediaEvent event) {

        double db = event.getDb();

        if (db > 0) {

            isHasMicInput = true;

        }
    }


}




