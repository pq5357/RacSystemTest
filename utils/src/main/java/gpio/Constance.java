package gpio;

/**
 * Created by sg on 2018/3/20.
 */

public class Constance {

    public static final int cmdtype_config = 3;
    public static final int cmdtype_export = 1;
    public static final int cmdtype_value = 2;
    public static String exportPath;
    public static String i2cDev = "i2c-";
    public static String parentDevPath;
    public static String parentGpioPath;
    public static String shellaction = "com.shellcmd";
    public static String spiDev;
    public static String unexportPath;

    public static String ledsPath;
    static
    {
        exportPath = "/sys/class/gpio/export";
        unexportPath = "/sys/class/gpio/unexport";
        parentGpioPath = "/sys/class/gpio/";
        parentDevPath = "/dev/";
        spiDev = "spidev";


        ledsPath = "/sys/class/leds/";



    }




}
