package com.bbk.bfcupload.bfcuploadtestdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bbk.bfcupload.bfcuploadtestdemo.R;
import com.bbk.bfcupload.bfcuploadtestdemo.util.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity{
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.button3)
    Button button3;
    @BindView(R.id.button4)
    Button button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button1,R.id.button2,R.id.button3,R.id.button4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                MyUtils.skipAnotherActivity(this, BasicFunctionActivity.class);
                break;
            case R.id.button2:
                MyUtils.skipAnotherActivity(this, UploadTypeActivity.class);
                break;
            case R.id.button3:
                MyUtils.skipAnotherActivity(this, DataBaseActivity.class);
                break;
            case R.id.button4:
                MyUtils.skipAnotherActivity(this, VersionActivity.class);
                break;
            default:
                break;
        }
    }
}
