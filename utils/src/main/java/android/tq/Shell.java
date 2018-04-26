package android.tq;

/**
 * Created by sg on 2018/3/20.
 */

public class Shell {

    static final String TAG = "ShellCmd";

    static
    {
        System.loadLibrary("android_tq");
    }

    private static native void native_shellcmd(String paramString);

    public static void sendCmd(String paramString)
    {
        native_shellcmd(paramString);
    }

}
