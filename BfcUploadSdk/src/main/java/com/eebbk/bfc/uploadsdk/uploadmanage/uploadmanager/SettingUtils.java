package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;


public  class SettingUtils {
    private static boolean isTest=false;
    public static boolean isTest() {
        return isTest;
    }
    public static void setTest(boolean test) {
        isTest = test;
        LogUtils.d("test:"+isTest);
    }
}
