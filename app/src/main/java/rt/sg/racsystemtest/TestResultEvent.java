package rt.sg.racsystemtest;

/**
 * Created by sg on 2018/3/12.
 */

public class TestResultEvent {

    private TestContent testContent;

    private String result;

    public static final int OK = 1;
    public static final int ERROR = 2;
    public static final int DEFALUT = 3;

    private int result_code;

    public TestResultEvent() {
    }

    public TestResultEvent(TestContent testContent) {
        this.testContent = testContent;
    }

    public TestResultEvent(TestContent testContent, String result) {
        this.testContent = testContent;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public TestContent getTestContent() {
        return testContent;
    }

    public void setTestContent(TestContent testContent) {
        this.testContent = testContent;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }
}
