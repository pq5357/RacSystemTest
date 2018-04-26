package android.tq;

/**
 * Created by sg on 2018/3/20.
 */

public class CanBus {

    static final boolean DEBUG = false;
    static final String TAG = "CanService";
    public int can_dlc = 0;
    public int can_fd = 0;
    public int can_id = 0;
    public String can_rxdata = "";
    private String mName = "";
    private int mboudrate = 0;
    private int mcid = 0;

    static
    {
        System.loadLibrary("android_tq");
    }

    private native void native_closeCan();

    private native int native_filterCan(int paramInt1, int[] paramArrayOfInt, int paramInt2);

    private native void native_initCan(String paramString1, int paramInt, String paramString2);

    private native int native_openCan(String paramString);

    private native int native_readCan(int paramInt);

    private native int native_sendCan(int paramInt, String paramString);

    private native void native_stopCan();

    public int receiveCan(int paramInt)
    {
        int i = 0;
        if (this.can_fd > 0) {
            i = native_readCan(paramInt);
        }
        return i;
    }

    public int sendCan(int paramInt, String paramString)
    {
        int i = 0;
        if (this.can_fd > 0) {
            i = native_sendCan(paramInt, paramString);
        }
        return i;
    }

    public int start(String paramString1, int paramInt, String paramString2)
    {
        this.mName = paramString1;
        this.mboudrate = paramInt;
        native_initCan(this.mName, this.mboudrate, paramString2);
        this.can_fd = native_openCan(this.mName);
        if (this.can_fd == 0)
        {
            native_closeCan();
            return -1;
        }
        return this.can_fd;
    }

    public void stop()
    {
        this.can_fd = 0;
        native_closeCan();
        native_stopCan();
    }

}
