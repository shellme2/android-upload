package com.bbk.bfcupload.bfcuploadtestdemo.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.bbk.bfcupload.bfcuploadtestdemo.MyApplication;
import com.github.moduth.blockcanary.BlockCanaryContext;
import java.util.List;

/**
 * Desc:
 * Author: ZengJingFang
 * Time:   2017/4/19 15:40
 * Email:  zengjingfang@foxmail.com
 */

public class BlockCanaryConfig extends BlockCanaryContext {
    private static final String TAG = "BlockCanaryConfig";

    @Override
    public String provideQualifier() {
        String qualifier = "";
        try {
            PackageInfo info = MyApplication.getAppContext().getPackageManager()
                    .getPackageInfo(MyApplication.getAppContext().getPackageName(), 0);
            qualifier += info.versionCode + "_" + info.versionName + "_YYB";
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "provideQualifier exception", e);
        }

        return qualifier;
    }

    @Override
    public String provideUid() {
        // 用户ID，对于我们来讲，可以随便写
        return "C2";
    }

    @Override
    public String provideNetworkType() {
        // 这个不知道是干什么的，先不管它
        return "4G";
    }

    @Override
    public int provideMonitorDuration() {
        // 单位是毫秒
        return 10*1000;
    }

    @Override
    public int provideBlockThreshold() {
        // 主要配置这里，多长时间的阻塞算是卡顿，单位是毫秒
        return 500;
    }

    @Override
    public boolean displayNotification() {
        return true;
    }

    @Override
    public List<String> concernPackages() {

        return super.provideWhiteList();
    }

    @Override
    public List<String> provideWhiteList() {
        // 白名单，哪些包名的卡顿不算在内
        return super.provideWhiteList();
    }

    @Override
    public boolean stopWhenDebugging() {
        return true;
    }
}
