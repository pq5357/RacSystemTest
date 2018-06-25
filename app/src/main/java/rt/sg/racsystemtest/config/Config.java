package rt.sg.racsystemtest.config;

/**
 * Created by sg on 2018/6/1.
 */

public class Config {

    public static class Rac7010{

        public static final int USB_COUNT = 3;
        public static final String[] SERIALS = new String[]{"ttySAC1", "ttysWK0","ttysWK2" ,
                "ttysWK1", "ttySAC4", "ttySAC2"};


    }

    public static class Rac7000{

        public static final int USB_COUNT = 3;
        public static final String[] SERIALS = new String[]{"ttymxc0", "ttymxc1","ttymxc2" ,
                "ttymxc3", "ttymxc4", "ttymxc5"};

    }


}
