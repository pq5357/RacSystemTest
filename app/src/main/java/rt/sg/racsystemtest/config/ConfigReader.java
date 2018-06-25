package rt.sg.racsystemtest.config;

import android.util.Log;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * Created by sg on 2018/6/23.
 */

public class ConfigReader {

    private static final String TAG = ConfigReader.class.getSimpleName();

    public static DeviceConfig getConfigFromFile(){

        try {

            InputStream is = new FileInputStream("/system/etc/test_config.json");
            if(is == null){
                return;
            }
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = br.readLine()) != null){
                builder.append(line);
            }
            br.close();
            isr.close();

            if(builder.toString() != null){

                DeviceConfig deviceConfig = new Gson().fromJson(builder.toString(), DeviceConfig
                        .class);

                return deviceConfig;
            }else{
                return null;
            }
        } catch (Exception e){


        }


    }






}
