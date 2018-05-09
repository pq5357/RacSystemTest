package rt.sg.racsystemtest;

/**
 * Created by sg on 2018/3/14.
 */

public enum TestContent {

    ETHERNET("001","以太网"),
    DEVICE_INFO("002","设备信息"),
    MODEL("003","模块和SIM卡"),
    SERIAL("004","串口"),
    IP("005","IP"),
    MAC("006","MAC"),
    USB("007","USB"),
    BUTTON("008","按键"),
    SDCARD("009","SD卡"),
    DIDO("010","DIDO"),
    BUZZER("011", "蜂鸣器"),
    AUDIO("012", "音频输出"),
    WIFI("013", "WIFI"),
    BLUETEETH("014", "蓝牙"),
    MIC("015", "麦克风");


    private String code;

    private String name;

    TestContent(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
