package android.tq;

import android.support.annotation.NonNull;

/**
 * Created by sg on 2018/3/20.
 */

public class I2c {

    static final boolean DEBUG = false;
    static final String TAG = "I2c";
    private int i2c_fd;
    private byte[] i2c_rxbuf;

    static
    {
        System.loadLibrary("android_tq");
    }

    private native void native_closeI2c();

    private native int native_openI2c(@NonNull String paramString, int paramInt1, int paramInt2);

    private native int native_read(int paramInt1, int paramInt2, @NonNull byte[] paramArrayOfByte, int paramInt3);

    private native int native_write(int paramInt1, @NonNull byte[] paramArrayOfByte, int paramInt2);

    public void closeI2c()
    {
        native_closeI2c();
        this.i2c_fd = -1;
        this.i2c_rxbuf = null;
    }

    public int getI2c_fd()
    {
        return this.i2c_fd;
    }

    public byte[] getI2c_rxbuf()
    {
        return this.i2c_rxbuf;
    }

    public int openI2c(@NonNull String paramString, int paramInt1, int paramInt2)
    {
        if ((paramInt1 >= 0) && (paramInt2 >= 0)) {
            return native_openI2c(paramString, paramInt1, paramInt2);
        }
        return -1;
    }

    public int read(int paramInt, @NonNull byte[] paramArrayOfByte)
    {
        if ((this.i2c_fd > 0) && (paramInt > 0) && (this.i2c_rxbuf != null)) {
            native_read(paramInt, this.i2c_rxbuf.length, paramArrayOfByte, paramArrayOfByte.length);
        }
        return -1;
    }

    public void setI2c_fd(@NonNull int paramInt)
    {
        this.i2c_fd = paramInt;
    }

    public void setI2c_rxbuf(@NonNull byte[] paramArrayOfByte)
    {
        this.i2c_rxbuf = paramArrayOfByte;
    }

    public int write(int paramInt, @NonNull byte[] paramArrayOfByte)
    {
        if ((this.i2c_fd > 0) && (paramInt > 0)) {
            return native_write(paramInt, paramArrayOfByte, paramArrayOfByte.length);
        }
        return -1;
    }
}
