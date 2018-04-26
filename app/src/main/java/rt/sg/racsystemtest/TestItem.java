package rt.sg.racsystemtest;

/**
 * 测试项
 * Created by sg on 2018/3/12.
 */

class TestItem {

    private TestContent testContent;

    private String result = "";

    public TestItem(TestContent testContent) {
        this.testContent = testContent;
    }

    public TestItem(TestContent testContent, String result) {
        this.testContent = testContent;
        this.result = result;
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
}
