package rt.sg.racsystemtest;

import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 *Rtest测试结果处理工具类
 * Created by sg on 3/7/18.
 */

public class RTestResultUtils {


    /**
     * 保存测试结果到SP，并且返回是否保存成功
     * @param sp
     * @param test_name
     * @param result
     * @return
     */
    public static boolean saveResultToSp(SharedPreferences sp,String test_name,RTestResult result){

        Gson gs = new Gson();

        String resultString = gs.toJson(result);

        boolean commit = sp.edit().putString(test_name, resultString).commit();

        return commit;

    }


    /**
     * 从Sp中查询测试项的结果
     * @param sp
     * @param test_name
     * @return
     */
    public static RTestResult getResultFromSp(SharedPreferences sp, String test_name){

        String resultString = sp.getString(test_name, "");

        Gson gs = new Gson();

        RTestResult rTestResult = gs.fromJson(resultString, RTestResult.class);

        return  rTestResult;

    }





}
