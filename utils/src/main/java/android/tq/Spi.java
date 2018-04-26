package android.tq;

import android.support.annotation.NonNull;

/**
 * Created by sg on 2018/3/20.
 */

public class Spi {

    static final boolean DEBUG = false;
    static final String TAG = "Spi";
    private int bits;
    private int delay;
    private int len;
    private int mode;
    private int speed;
    private int spi_fd;
    private byte[] spi_rx;
    private byte[] spi_tx;

    static
    {
        System.loadLibrary("android_tq");
    }

    public Spi() {}

    public Spi(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
        this.bits = paramInt1;
        this.speed = paramInt2;
        this.mode = paramInt3;
        this.delay = paramInt4;
    }

    private native void native_closeSpi();

    private native int native_openSpi(String paramString);

    private native int native_transfer();

    public void closeSpi()
    {
        this.spi_fd = -1;
        this.spi_tx = null;
        this.spi_rx = null;
        native_closeSpi();
    }

    public int getLen()
    {
        return this.len;
    }

    public byte[] getSpi_rx()
    {
        return this.spi_rx;
    }

    public byte[] getSpi_tx()
    {
        return this.spi_tx;
    }

    public int openSpi(String paramString)
    {
        if ((paramString == null) || (paramString.equals(""))) {
            return -1;
        }
        return native_openSpi(paramString);
    }

    public void setLen(int paramInt)
    {
        this.len = paramInt;
    }

    public void setSpi_fd(int paramInt)
    {
        this.spi_fd = paramInt;
    }

    public void setSpi_rx(@NonNull byte[] paramArrayOfByte)
    {
        this.spi_rx = paramArrayOfByte;
    }

    public void setSpi_tx(@NonNull byte[] paramArrayOfByte)
    {
        this.spi_tx = paramArrayOfByte;
    }

    public int transfer()
    {
        if ((this.spi_fd > 0) && (this.spi_tx != null) && (this.spi_rx != null)) {
            return native_transfer();
        }
        return this.spi_fd;
    }
}
