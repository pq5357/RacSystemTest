package rt.sg.racsystemtest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.tq.Shell;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rt.sg.racsystemtest.config.DeviceConfig;
import rt.sg.racsystemtest.serial.SerialPortManager;
import rt.sg.racsystemtest.serial.listener.OnSerialPortDataListener;

/**
 * 模拟系统测试服务
 * Created by sg on 2018/3/6.
 */

public class TestManagerService extends Service {

    private static final String TAG = "TestManagerService";

    private Context mContext;

    /*测试任务线程池*/
    private ExecutorService scheduleThreadPool = Executors.newScheduledThreadPool(3);
    /*同步线程池*/
    private ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
    //
    private ExecutorService singleThreadPool1 = Executors.newSingleThreadExecutor();
    /*保存测试结果,最终返回该Sp的数据*/
    private SharedPreferences sp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Log.i(TAG, TAG + "has started");
        sp = mContext.getSharedPreferences("rtest_result", Context.MODE_PRIVATE);

        //注册对来自测试界面的监听
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        //开启线程
        singleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket datagramSocket = null;

                    int port = 9999;

                    if (datagramSocket == null) {

                        datagramSocket = new MulticastSocket(port);

                        datagramSocket.setBroadcast(true);

                    }
                    while(true){
                        byte[] by = new byte[1024];
                        DatagramPacket datagramPacket = new DatagramPacket(by, by.length,
                                InetAddress.getByName("255.255.255.255"), port);

                        datagramSocket.receive(datagramPacket);

                        String str = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

                        processCommand(str);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        DeviceConfig testConfig = TestCore.getInstance(mContext).getTestConfig();

        if(testConfig.isAssistant()){
            initSerialsAsAssistant(testConfig);
        }
    }

    /**
     * 初始化所有串口设置串口监听，以应对作为辅助测试设备的情况
     * @param testConfig
     */
    private void initSerialsAsAssistant(DeviceConfig testConfig) {

        List<String> serials = testConfig.getSerials();

        sp.edit().putString("Version", "辅助测试").apply();

        for(final String serial : serials){

            final SerialPortManager mSerialPortManager = new SerialPortManager();

            mSerialPortManager.openSerialPort(new File("/dev/" + serial),115200);

            mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
                @Override
                public void onDataReceived(byte[] bytes) {
                    String content = new String(bytes);
                    Log.i("serial", "received" + content);
                    if (content.equals("send success")) {
                        mSerialPortManager.sendBytes(("received success").getBytes());
                    }
                }
                @Override
                public void onDataSent(byte[] bytes) {
                    String content = new String(bytes);
                    Log.i("serial", "send" + content);
                }
            });
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 处理接收到的UDP信息
     * */
    private void processCommand(String request) {

        switch (request) {
            case "r-connect":
                connection();
                break;
            case "ip":
                EventBus.getDefault().post(new TestRequestEvent(TestContent.IP));
                break;
            case "usb":
                EventBus.getDefault().post(new TestRequestEvent(TestContent.USB));
                break;
            case "mac":
                EventBus.getDefault().post(new TestRequestEvent(TestContent.MAC));
                break;
            case "music":
                EventBus.getDefault().post(new TestRequestEvent(TestContent.AUDIO));
                break;
        }
    }


    /**
     * 回复Udp消息的封装方法
     */
    private void responseUdp(String content){

        DatagramSocket sendSocket = null;

        if(content.isEmpty()){
            return;
        }
        byte[] sendBuf = content.getBytes();

        try {
            sendSocket = new DatagramSocket();
            InetAddress responseAddress = InetAddress.getByName("255.255.255.255");
            DatagramPacket sendPacket=new DatagramPacket(sendBuf,sendBuf.length,responseAddress, 9999);
            sendSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 统一的返回测试结果的方法
     */
    private void returnResult(TestResultEvent result){
        responseUdp(result.getResult());
        EventBus.getDefault().post(result);

    }

    private void connection() {

        scheduleThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                RTestResult rTestResult = new RTestResult("001","ok","nothing happen");

                RTestResultUtils.saveResultToSp(sp, "connection", rTestResult);

                RTestResult connection = RTestResultUtils.getResultFromSp(sp, "connection");

                responseUdp(connection.toString());
            }
        });
    }

    private long serialTesttime = 0l;

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onTestRequestEvent(TestRequestEvent requestEvent){
        TestContent request = requestEvent.getRequest();
        TestResultEvent resultEvent = new TestResultEvent(request);
        String result ="测试中...";
        resultEvent.setResult(result);
        EventBus.getDefault().post(resultEvent);
        switch (request){
            case ETHERNET:
                resultEvent = TestCore.getInstance(this).testEthernet(resultEvent);
                break;
            case DEVICE_INFO:
                resultEvent = TestCore.getInstance(this).getDeviceModel(resultEvent);
                break;
            case MODEL:
                resultEvent = TestCore.getInstance(this).test4gModel(resultEvent);
                break;
            case SERIAL:
                if((System.currentTimeMillis() - serialTesttime) > 40000){
                    serialTesttime = System.currentTimeMillis();
                    resultEvent = TestCore.getInstance(this).testAllSerials(resultEvent);
                }else{
                    resultEvent.setResult("操作过于频繁");
                }
                break;
            case IP:
                Shell.sendCmd("chmod 666 /sys/class/gpio/export");
                resultEvent = TestCore.getInstance(this).getIp(resultEvent);
                break;
            case MAC:
                resultEvent = TestCore.getInstance(this).getMacFromIp(resultEvent);
                break;
            case USB:
                resultEvent = TestCore.getInstance(this).testAllUsbDevice(resultEvent);
                break;
            case BUTTON:
                resultEvent = TestCore.getInstance(this).testButtons(resultEvent);
                break;
            case SDCARD:
                resultEvent = TestCore.getInstance(this).testSdCard(resultEvent);
                break;
            case DIDO:
                resultEvent = TestCore.getInstance(this).testAllGpio(resultEvent);
                break;
            case BUZZER:
                resultEvent = TestCore.getInstance(this).testBuzzer(resultEvent);
                break;
            case AUDIO:
                resultEvent = TestCore.getInstance(this).testAudio(resultEvent);
                break;
            case WIFI:
                resultEvent = TestCore.getInstance(this).testWifi(resultEvent);
                break;
            case BLUETEETH:
                resultEvent = TestCore.getInstance(this).testBlueTeeth(resultEvent);
                break;
            case MIC:
                resultEvent = TestCore.getInstance(this).testMIC(resultEvent);
                break;
        }
        Gson gson = new Gson();
        String json_results = gson.toJson(resultEvent);
        sp.edit().putString(request.getCode(),json_results).commit();
        returnResult(resultEvent);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onTestRessultEvent(){

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Log.i(TAG, TAG + "has stopped");
    }
}
