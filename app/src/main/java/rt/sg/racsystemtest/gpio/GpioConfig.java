package rt.sg.racsystemtest.gpio;

/**
 * Gpio端口配置
 * Created by sg on 2018/3/23.
 */

public class GpioConfig {

    private String id;


    private String mode;


    public GpioConfig(String id, String mode) {
        this.id = id;
        this.mode = mode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "GpioConfig{" +
                "id='" + id + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }
}
