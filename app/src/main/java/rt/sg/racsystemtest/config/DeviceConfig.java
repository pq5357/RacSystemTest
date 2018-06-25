package rt.sg.racsystemtest.config;

import java.util.List;

/**
 * Created by sg on 2018/6/25.
 */

public class DeviceConfig {


    /**
     * 当前设备是否为辅助测试设备
     */
    public boolean isAssistant;

    /**
     * 当前测试设备的串口名称集合
     */
    public List<String> serials;

    /**
     * 待测量的usb数量
     */
    public int usbCount;

    /**
     * 待验证的按键广播事件
     */
    public List<String> buttonActions;

    public boolean isAssistant() {
        return isAssistant;
    }

    public void setAssistant(boolean assistant) {
        isAssistant = assistant;
    }

    public List<String> getSerials() {
        return serials;
    }

    public void setSerials(List<String> serials) {
        this.serials = serials;
    }

    public int getUsbCount() {
        return usbCount;
    }

    public void setUsbCount(int usbCount) {
        this.usbCount = usbCount;
    }

    public List<String> getButtonActions() {
        return buttonActions;
    }

    public void setButtonActions(List<String> buttonActions) {
        this.buttonActions = buttonActions;
    }

    @Override
    public String toString() {
        return "DeviceConfig{" +
                "isAssistant=" + isAssistant +
                ", serials=" + serials +
                ", usbCount=" + usbCount +
                ", buttonActions=" + buttonActions +
                '}';
    }
}
