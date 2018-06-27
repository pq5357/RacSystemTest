package rt.sg.racsystemtest;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.tq.Shell;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.robustel.misc.NativeCTL;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rt.sg.racsystemtest.config.ConfigReader;
import rt.sg.racsystemtest.config.Model;
import rt.sg.racsystemtest.databinding.ActivityTestBinding;
import rt.sg.racsystemtest.config.DeviceConfig;


/**
 * Created by sg on 2018/3/12.
 */

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "TestActivity";

    private Context mContext;

    private ActivityTestBinding activityTestBinding;

    private List<TestItem> testItems = new ArrayList<>();

    private TestAdapter testAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, TAG + "has started");

        mContext = this;

        activityTestBinding = DataBindingUtil.setContentView(this , R.layout.activity_test);

/*        DeviceConfig config = new DeviceConfig();

        config.setUsbCount(3);

        List<String> serList = Arrays.asList(new String[]{"ttySAC1", "ttysWK0","ttysWK2","ttysWK1", "ttySAC4", "ttySAC2"});

        config.setSerials(serList);

        List<String> list = new ArrayList<>();

        list.add("com.rac.broadcast.home");
        list.add("com.rac.broadcast.home");
        list.add("com.rac.broadcast.home");
        list.add("com.rac.broadcast.home");
        config.setButtonActions(list);

        List<Model> models = new ArrayList<>();
        Model model = new Model("ME3630","5238", "6610");
        Model model1 = new Model("ME120","1111", "2222");

        models.add(model);
        models.add(model1);

        config.setModels(models);

        String json = new Gson().toJson(config);

        System.out.println(json);*/


        EventBus.getDefault().register(this);

        ConfigReader.getConfigFromFile();

        initTestItems();

        activityTestBinding.rcyTest.setLayoutManager(new LinearLayoutManager(mContext));

        testAdapter = new TestAdapter(R.layout.item_test, testItems);

        testAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                TestContent content = testItems.get(position).getTestContent();
                EventBus.getDefault().post(new TestRequestEvent(content));
            }
        });

        activityTestBinding.rcyTest.setAdapter(testAdapter);

        activityTestBinding.btnCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(TestItem testItem : testItems){
                    TestContent content = testItem.getTestContent();
                    EventBus.getDefault().post(new TestRequestEvent(content));
                }
            }
        });

        activityTestBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
                finish();
            }
        });

        Intent intent = new Intent(this, TestManagerService.class);

        startService(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_zte:
                //Shell.sendCmd("cp -f /system/lib/libzte-ril.so /system/lib/libreference-ril.so");
                NativeCTL.sendShellCMD("cp -f /system/lib/libzte-ril.so /system/lib/libreference-ril.so");
                break;
            case R.id.action_huawei:
                //Shell.sendCmd("cp -f /system/lib/libhuawei-ril.so /system/lib/libreference-ril.so");
                NativeCTL.sendShellCMD("cp -f /system/lib/libhuawei-ril.so /system/lib/libreference-ril.so");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Log.i(TAG, TAG + "has stopped");
    }

    /**
     * 初始化测试项
     */
    private void initTestItems() {
        testItems.add(new TestItem(TestContent.ETHERNET));
        testItems.add(new TestItem(TestContent.DEVICE_INFO));
        testItems.add(new TestItem(TestContent.MODEL));
        testItems.add(new TestItem(TestContent.SERIAL));
        testItems.add(new TestItem(TestContent.USB));
        testItems.add(new TestItem(TestContent.BUTTON));
        testItems.add(new TestItem(TestContent.SDCARD));
        testItems.add(new TestItem(TestContent.DIDO));
        testItems.add(new TestItem(TestContent.BUZZER));
        testItems.add(new TestItem(TestContent.AUDIO));
        testItems.add(new TestItem(TestContent.WIFI));
        testItems.add(new TestItem(TestContent.BLUETEETH));
        testItems.add(new TestItem(TestContent.MIC));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTestMessageEvent(TestResultEvent event){

        String result = event.getResult();

        TestContent testContent = event.getTestContent();

        int result_code = event.getResult_code();

        int resultPosition = -1;
        for(int i=0;i < testItems.size();i++){
            if(testItems.get(i).getTestContent() == testContent){
                resultPosition = i;
                break;
            }
        }
        testItems.get(resultPosition).setResult(result);

        testItems.get(resultPosition).setResult_code(result_code);

        testAdapter.notifyItemChanged(resultPosition);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event. KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onAttachedToWindow() {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }

}
