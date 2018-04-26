package gpio;

import android.tq.Shell;

/**
 * Created by sg on 2018/3/29.
 */

public class Led {

    /**
     * 初始化节点 "echo timer > /sys/class/led/~/trigger ",初始化后默认500ms闪烁
     */
    private static void initTimer(String device_name){

        String path = Constance.ledsPath + device_name + "/trigger";
        if(FileHelper.isFileCanWrite(path)){
            try {
                FileHelper.save(path, "timer");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改delay_off
     */
    public static void changeDelayOff(String device_name, String value){

        String path = Constance.ledsPath + device_name + "/delay_off";

        if(FileHelper.isFileCanWrite(path)){
            try {
                FileHelper.save(path, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 修改delay_on
     */
    public static void changeDelayOn(String device_name, String value){

        String path = Constance.ledsPath + device_name + "/delay_on";

        if(FileHelper.isFileCanWrite(path)){
            try {
                FileHelper.save(path, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 蜂鸣器操作接口
     */
    public static void turnBeeper(boolean open){
        if(open){
            changeDelayOn("beeper", "500");
            changeDelayOff("beeper", "0");
        }else{
            changeDelayOff("beeper", "500");
            changeDelayOn("beeper", "0");
        }
    }




}
