package rt.sg.racsystemtest;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 测试界面适配器
 * Created by sg on 2018/3/12.
 */

public class TestAdapter extends BaseQuickAdapter<TestItem, BaseViewHolder> {

    public TestAdapter(int layoutResId, @Nullable List<TestItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, TestItem item) {

        viewHolder.setText(R.id.btn_test_name, item.getTestContent().getName())
                .setText(R.id.tv_test_result, item.getResult())
                .addOnClickListener(R.id.btn_test_name);
        if(item.getResult_code() != 0 ){
            if(item.getResult_code() == 1){
                viewHolder.setBackgroundColor(R.id.tv_test_result, Color.parseColor("#00ff00"));
            }else if(item.getResult_code() == 2){
                viewHolder.setBackgroundColor(R.id.tv_test_result, Color.parseColor("#ff0000"));
            }else{
                viewHolder.setBackgroundColor(R.id.tv_test_result, Color.TRANSPARENT);
            }
        }else{
            viewHolder.setBackgroundColor(R.id.tv_test_result, Color.TRANSPARENT);
        }
    }
}
