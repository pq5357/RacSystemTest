package rt.sg.racsystemtest;

/**
 * Created by sg on 2018/3/12.
 */

public class TestRequestEvent {


    private TestContent request;


    public TestRequestEvent(TestContent request) {
        this.request = request;
    }


    public TestContent getRequest() {
        return request;
    }

    public void setRequest(TestContent request) {
        this.request = request;
    }
}
