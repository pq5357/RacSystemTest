package gpio;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by sg on 2018/3/20.
 */

public class FileHelper {

    private Context mContext;

    public FileHelper() {
    }

    public FileHelper(Context context) {
        mContext = context;
    }

    public static boolean isFileCanWrite(String s) {
        File file = new File(s);
        if (file.exists()) {
            return file.canWrite();
        } else {
            return false;
        }
    }

    public static ArrayList getDevList(String s, String s1) {
        Log.w("file", "getDevList");
        ArrayList arraylist = new ArrayList();
        File[] files = (new File(s)).listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (!file.isDirectory() && file.getName().startsWith(s1)) {
                arraylist.add(file.getName());
            }
        }
        return arraylist;
    }

    public static ArrayList getFileDir(String s) {
        ArrayList arraylist = new ArrayList();
        File[] files = (new File(s)).listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (!file.isDirectory())
                continue;
            String s1 = file.getName();
            if (!s1.contains("chip") && s1.startsWith("gpio")) {
                arraylist.add(file.getName());
            }
        }
        return arraylist;
    }


    public static String read(String filepath) {
        StringBuilder sb = new StringBuilder();
        String s ="";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            while( (s = br.readLine()) != null) {
                sb.append(s + "\n");
            }

            br.close();
            String str = sb.toString();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void save(String path, String value) throws Exception {
        FileOutputStream outputStream
                = new FileOutputStream(new File(path), false);
        outputStream.write(value.getBytes());
        outputStream.close();
    }


}
