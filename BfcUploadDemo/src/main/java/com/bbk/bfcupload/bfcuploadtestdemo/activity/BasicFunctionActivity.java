package com.bbk.bfcupload.bfcuploadtestdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bbk.bfcupload.bfcuploadtestdemo.R;
import com.bbk.bfcupload.bfcuploadtestdemo.upload.UploadManager;
import com.bbk.bfcupload.bfcuploadtestdemo.util.MyUtils;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BasicFunctionActivity extends Activity{
    @BindView(R.id.cloud_fun)
    Button cloudFun;
    @BindView(R.id.http_fun)
    Button httpFun;
    @BindView(R.id.chat_fun)
    Button chatFun;
    @BindView(R.id.back_iv)
    ImageView mBack;
    @BindView(R.id.title_tv)
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_function);
        assignViews();
    }

    private void assignViews() {
        ButterKnife.bind(this);
        mTitle.setText("基本功能测试");
    }

    @OnClick({R.id.cloud_fun, R.id.http_fun,R.id.chat_fun,R.id.back_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cloud_fun:
                MyUtils.skipAnotherActivity(this, CloudUploadActivity.class);
                break;
            case R.id.http_fun:
                MyUtils.skipAnotherActivity(this, HTTPActivity.class);
                break;
            case R.id.chat_fun:
                chatUpload();
                break;
            case R.id.back_iv:
                finish();
                break;
            default:
                break;
        }
    }

    private void chatUpload(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/uploadtest/" + "ChatUpload.mp4");
        if(file.exists()){
            UploadManager uploadManager = new UploadManager(this, "recordFile", file.getName(), file.getAbsolutePath());
            uploadManager.startUpLoad();
            Toast.makeText(this,"文件正在上传。。",Toast.LENGTH_SHORT).show();
        }else {
            LogUtils.i("chatUpload 语聊上传的文件不存在！！");
        }
    }
}
