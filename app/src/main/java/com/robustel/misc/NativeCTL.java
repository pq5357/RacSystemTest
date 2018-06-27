package com.robustel.misc;

/**
 * Created by sg on 2018/6/26.
 */

public class NativeCTL {

    static {
        System.loadLibrary("misc");
    }

    private static native boolean SendCMD(String str);

    public static void sendShellCMD(String cmd){
        SendCMD(cmd);
    };

}
