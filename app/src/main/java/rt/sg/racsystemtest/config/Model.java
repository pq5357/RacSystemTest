package rt.sg.racsystemtest.config;

import java.util.Objects;

/**
 * Created by sg on 2018/6/26.
 */

public class Model {


    public Model(String name, int pid, int vid) {
        this.name = name;
        this.pid = pid;
        this.vid = vid;
    }

    /**
     * 模块名称
     */
    private String name;

    /**
     * productId
     */
    private int pid;

    /**
     * vendorId
     */
    private int vid;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return pid == model.pid &&
                vid == model.vid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid, vid);
    }

    @Override
    public String toString() {
        return "Model{" +
                "name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                ", vid='" + vid + '\'' +
                '}';
    }
}
