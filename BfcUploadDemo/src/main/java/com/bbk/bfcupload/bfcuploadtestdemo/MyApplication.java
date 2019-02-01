package com.bbk.bfcupload.bfcuploadtestdemo;

import android.app.Application;
import android.content.Context;

import com.bbk.bfcupload.bfcuploadtestdemo.util.BlockCanaryConfig;
import com.bbk.bfcupload.bfcuploadtestdemo.util.CrashHandler;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.SettingUtils;
import com.github.moduth.blockcanary.BlockCanary;
import com.squareup.leakcanary.LeakCanary;

public class MyApplication extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        sContext = this;
        initLeakCanary();
        initBlockCanary();
        SettingUtils.setTest(true);
        CrashHandler.getInstance().init(this);

    }

    private void initBlockCanary() {
        BlockCanary.install(this, new BlockCanaryConfig()).start();
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    public static Context getAppContext() {
        return sContext;
    }
}
