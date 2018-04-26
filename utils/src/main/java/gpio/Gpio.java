package gpio;

import android.content.Context;
import android.content.Intent;
import android.tq.Shell;
import android.util.Log;

import java.io.File;

/**
 * RAC7010 GPIO操作封装
 * Created by sg on 2018/3/22.
 */

public class Gpio {

    /**
     * 修改/sys/class/gpio/export的读写权限
     */
    private static void changeExportPermission() {
        if (!FileHelper.isFileCanWrite(Constance.exportPath)) {
            Shell.sendCmd("chmod 666 " + Constance.exportPath);
        }
    }

    /**
     * 修改/sys/class/gpio/unexport的读写权限
     */
    private  static void changeUnExportPermission() {
        if (!FileHelper.isFileCanWrite(Constance.unexportPath)) {
            Shell.sendCmd("chmod 666 " + Constance.unexportPath);
        }
    }

    /**
     * 获取gpio操作的位数
     *
     * @param base  寄存器基数
     * @param digit 寄存器位数
     * @return
     */
    public static String getRealID(String base, String digit) {

        int i = (Integer.valueOf(base) - 1) * 32 + Integer.valueOf(digit);

        return String.valueOf(i);
    }


    /**
     * 判断指定端口的gpio文件是否存在
     *
     * @param id
     * @return
     */
    public static boolean isPortExist(String id) {
        return new File(Constance.parentGpioPath + "gpio" + id).exists();
    }

    /**
     * export 指定id的gpio接口
     *
     * @param id
     */
    private static void exportGpioById(String id) {
        if (FileHelper.isFileCanWrite(Constance.exportPath)) {
            if (!isPortExist(id)) {
                try {
                    FileHelper.save(Constance.exportPath, id);
                } catch (Exception paramString) {
                    paramString.printStackTrace();
                }
            }
        }
    }

    /**
     * 修改指定id gpio口方向和值的权限
     */
    private static void changeDirValuePermission(String id) {

        String gpio_value = Constance.parentGpioPath + "gpio" + id + "/value";
        String gpio_dir = Constance.parentGpioPath + "gpio" + id + "/direction";
        if (!FileHelper.isFileCanWrite(gpio_value) || !FileHelper.isFileCanWrite(gpio_dir)) {
            String cmd = "chmod 666 " + gpio_value + " " + gpio_dir;
            Shell.sendCmd(cmd);
        }
    }

    /**
     * 配置gpio端口的输入输出方向
     * @param context
     * @param base
     * @param digit
     * @param configMode
     */

    public static void configGpioDirection(Context context, String base, String digit, String
            configMode){
        String realID = getRealID(base, digit);
        configGpioDirection(context, realID, configMode);
    }


    /**
     * 配置gpio端口的输入输出方向，传入处理后的gpio_id
     *
     * @param
     */
    public static void configGpioDirection(Context context, String id, String configMode) {
        String direction_path = (new StringBuilder()).append(Constance.parentGpioPath).append
                ("gpio").append(id).append("/direction").toString();
        if (!FileHelper.isFileCanWrite(direction_path) || configMode.equals("")) {
            return;
        }
        try {
            FileHelper.save(direction_path, configMode);
            Intent intent = new Intent("Config_Gpio");
            intent.putExtra("id", id);
            intent.putExtra("configMode", configMode);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置输出Gpio的值,传入处理后的gpio_id
     */
    public static void setOutGpio(String id, String vaule) {
        String gpio_dir = (new StringBuilder()).append(Constance.parentGpioPath).append("gpio")
                .append(id).append("/direction").toString();
        if (FileHelper.read(gpio_dir).equals("out\n")) {
            try {
                FileHelper.save((new StringBuilder()).append(Constance.parentGpioPath).append
                        ("gpio").append(id).append("/value").toString(), vaule);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置输出gpio的值
     * @param base  寄存器基数
     * @param digit 寄存器位数
     * @param value
     */
    public static void setOutGpio(String base, String digit, String value) {
        String realID = getRealID(base, digit);
        setOutGpio(realID, value);
    }

    /**
     * 读取指定id,gpio的value
     */
    public static String getInGpio(String id) {
        String gpio_dir = (new StringBuilder()).append(Constance.parentGpioPath).append("gpio")
                .append(id).append("/direction").toString();
        if (FileHelper.read(gpio_dir).equals("in\n")) {
            String value = FileHelper.read((new StringBuilder()).append(Constance.parentGpioPath)
                    .append("gpio").append(id).append("/value").toString());
            return value;
        } else {
            return null;
        }
    }

    /**
     * 根据硬件标识，读取gpio的值,
     *
     * @param base
     * @param digit
     * @return
     */
    public static String getInGpio(String base, String digit) {
        String realID = getRealID(base, digit);

        String inGpio = getInGpio(realID);

        return inGpio;
    }

}
