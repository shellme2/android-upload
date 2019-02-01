package com.bbk.bfcupload.bfcuploadtestdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbk.bfcupload.bfcuploadtestdemo.R;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.IController;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ITask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.Query;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.UploadController;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: chenxiang
 * Date:   2018/10/23
 * Description:
 */

public class DataBaseActivity extends BaseActivity {
    @BindView(R.id.taskCount)
    TextView mTaskCount;
    @BindView(R.id.back_iv)
    ImageView mBack;
    @BindView(R.id.title_tv)
    TextView mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        initView();
    }

    private void initView(){
        ButterKnife.bind(this);
        mTitle.setText("数据库兼容性测试");
        IController uploadController = new UploadController(DataBaseActivity.this);

        ArrayList<ITask> tasks= uploadController.getTask(new Query());
        mTaskCount.setText("数据库任务记录："+tasks.size());
    }


    @OnClick({R.id.back_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
            default:
                break;
        }
    }



}
