package rt.sg.racsystemtest.config;

import java.util.List;

/**
 * Created by sg on 2018/6/25.
 */

public class DeviceConfig {


    /**
     * 当前设备是否为辅助测试设备
     */
    private boolean isAssistant;

    /**
     * 当前测试设备的串口名称集合
     */
    private List<String> serials;

    /**
     * 待测量的usb数量
     */
    private int usbCount;

    /**
     * 待验证的按键广播事件
     */
    private List<String> buttonActions;

    /**
     *可能使用到的model型号
     */
    private List<Model> models;

    /**
     * 待测试gpio端口
     */
    private List<String> gpioIds;

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


    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }

    public List<String> getGpioIds() {
        return gpioIds;
    }

    public void setGpioIds(List<String> gpioIds) {
        this.gpioIds = gpioIds;
    }

    @Override
    public String toString() {
        return "DeviceConfig{" +
                "isAssistant=" + isAssistant +
                ", serials=" + serials +
                ", usbCount=" + usbCount +
                ", buttonActions=" + buttonActions +
                ", models=" + models +
                ", gpioIds=" + gpioIds +
                '}';
    }
}
