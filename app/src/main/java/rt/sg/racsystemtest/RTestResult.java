package rt.sg.racsystemtest;

import java.io.Serializable;

/**
 * RTest测试结果返回类
 * Created by sg on 3/7/18.
 */

public class RTestResult  implements Serializable{

    private static final long  serialVersionUID = 872165832;

    private String code = "";

    private String reslut = "";

    private String message = "";

    public RTestResult(String code, String reslut, String message) {
        this.code = code;
        this.reslut = reslut;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReslut() {
        return reslut;
    }

    public void setReslut(String reslut) {
        this.reslut = reslut;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "RTestResult{" +
                "code='" + code + '\'' +
                ", reslut='" + reslut + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
