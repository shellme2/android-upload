package com.bbk.bfcupload.bfcuploadtestdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbk.bfcupload.bfcuploadtestdemo.R;
import com.eebbk.bfc.uploadsdk.uploadmanage.SDKVersion;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VersionActivity extends Activity {
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.tv3)
    TextView tv3;
    @BindView(R.id.tv4)
    TextView tv4;
    @BindView(R.id.tv5)
    TextView tv5;
    @BindView(R.id.tv6)
    TextView tv6;
    @BindView(R.id.back_iv)
    ImageView mBack;
    @BindView(R.id.title_tv)
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        assignViews();
    }

    private void assignViews() {
        ButterKnife.bind(this);
        tv1.setText("head:" + SDKVersion.getBuildHead() + "");
        tv2.setText("构造名:" + SDKVersion.getBuildName() + "");
        tv3.setText("库名：" + SDKVersion.getLibraryName());
        tv4.setText("创建时间：" + SDKVersion.getBuildTime() + "");
        tv5.setText("SDK号：" + SDKVersion.getSDKInt() + "");
        tv6.setText("版本号：" + SDKVersion.getVersionName());
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
