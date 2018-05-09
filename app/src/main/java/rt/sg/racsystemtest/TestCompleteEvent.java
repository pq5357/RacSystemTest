package rt.sg.racsystemtest;

/**
 * Created by sg on 2018/3/12.
 */

public class TestCompleteEvent {

    private TestContent testContent;

    private String result;

    public TestCompleteEvent() {
    }

    public TestCompleteEvent(TestContent testContent) {
        this.testContent = testContent;
    }

    public TestCompleteEvent(TestContent testContent, String result) {
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
}
