package rt.sg.racsystemtest;

/**
 * 测试项
 * Created by sg on 2018/3/12.
 */

class TestItem {
    public static final int OK = 1;
    public static final int ERROR = 2;
    public static final int DEFALUT = 3;

    private TestContent testContent;

    private String result = "";

    private int result_code;

    public TestItem(TestContent testContent) {
        this.testContent = testContent;
    }

    public TestItem(TestContent testContent, String result) {
        this.testContent = testContent;
        this.result = result;
    }

    public TestItem(TestContent testContent, String result, int result_code) {
        this.testContent = testContent;
        this.result = result;
        this.result_code = result_code;
    }

    public TestContent getTestContent() {
        return testContent;
    }

    public void setTestContent(TestContent testContent) {
        this.testContent = testContent;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }
}
