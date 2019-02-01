package com.eebbk.bfc.uploadsdk.da;

import android.content.Context;
import android.content.Intent;

public class Da {

    public static void record(Context context, DaInfo info){
        if(context == null){
            return;
        }
        // 由于目前主要是分析家长管理推送问题，统一把埋点放到家长管理中
        // 1.方便对埋点上报管理
        // 2.保证埋点数据不会散落到各个app，导致上报不及时
        // 3.方便出现推送异常后，如果埋点没有及时上报，可以触发家长管理埋点上报后门进行上报
        Intent intent = new Intent();
        intent.setClass(context,UploadSdkDaService.class);
        intent.putExtra(constant.bundleKey.FUNCTION_NAME, info.getFunctionName());
        intent.putExtra(constant.bundleKey.MODULE_DETAIL, info.getModuleDetail());
        intent.putExtra(constant.bundleKey.TRIG_VALUE, info.getTrigValue());
        intent.putExtra(constant.bundleKey.EXTEND, info.getExtend());
        try {
            context.startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface extend {
        String package_name = "pkg";
        String PID = "pid";
        String SDK_VERSION = "sdkVer";
        String IP = "ip";
        String PORT = "port";
    }

    interface constant {

        String MODULE_NAME = "bfc-uploadsdk";


        interface bundleKey {
            String FUNCTION_NAME = "functionName";
            String MODULE_DETAIL = "moduleDetail";
            String TRIG_VALUE = "trigValue";
            String EXTEND = "extend";
        }
    }
}
