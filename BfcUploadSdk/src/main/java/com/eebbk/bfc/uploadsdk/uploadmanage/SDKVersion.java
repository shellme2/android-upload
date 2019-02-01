package com.eebbk.bfc.uploadsdk.uploadmanage;


import com.eebbk.bfc.sequence.version.Build;

public class SDKVersion {

    private SDKVersion(){
        // you can't instance me from outside this class
    }

    public static String getLibraryName(){
        return Build.LIBRARY_NAME;
    }

    /**
     * 构建时的版本值，如：1, 2, 3, ...
     *
     * @return
     */
    public static int getSDKInt(){
        return Build.VERSION.SDK_INT;
    }

    /**
     * 版本名称，如：1.0.0, 2.1.2-alpha, ...
     *
     * @return
     */
    public static String getVersionName(){
        return Build.VERSION.VERSION_NAME;
    }

    /**
     * 构建版本以及时间，主要从git获取,由GIT_TAG + "_" + GIT_SHA + "_" + BUILD_TIME组成
     *
     * @return
     */
    public static String getBuildName(){
        return Build.BUILD_NAME;
    }

    /**
     * 构建时间
     *
     * @return
     */
    public static String getBuildTime(){
        return Build.BUILD_TIME;
    }

    /**
     * 构建时的git 标签
     *
     * @return
     */
    public static String getBuildTag(){
        return Build.GIT_TAG;
    }

    /**
     * 构建时的git HEAD值
     *
     * @return
     */
    public static String getBuildHead(){
        return Build.GIT_HEAD;
    }
}
