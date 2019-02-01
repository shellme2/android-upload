package com.eebbk.bfc.uploadsdk.da;

import android.content.Context;
import android.text.TextUtils;

import com.eebbk.bfc.uploadsdk.BuildConfig;
import com.eebbk.bfc.uploadsdk.common.Utils;
import com.eebbk.bfc.uploadsdk.repo.persistence.NewBfcUploads;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DaInfo {
    public static final String MODULE_DETAIL="上载功能";
    public static final String FUCTION_NAME_START_UPLOAD="开始上载";
    public static final String FUCTION_NAME_UPLOAD_SUCCESS="上载成功";
    public static final String FUCTION_NAME_UPLOAD_FAILURE="上载失败";
    public static final String MAP_KEY_APP_NAME="appName";
    public static final String MAP_KEY_APP_VERSION="appVersion";
    public static final String MAP_KEY_URL="url";
    public static final String MAP_KEY_TYPE="Type";
    public static final String MAP_KEY_ERROR_CODE="errorCode";
    public static final String MAP_KEY_ERROR_MSG="errorMsg";


    private String functionName;
    private String moduleDetail;
    private String trigValue;
    private String extendSdkVersion;
    private String extendRemoteIp;
    private String extendRemotePort;
    private String extend;

    public DaInfo setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    public String getFunctionName() {
        return functionName;
    }

    public DaInfo setModuleDetail(String moduleDetail) {
        this.moduleDetail = moduleDetail;
        return this;
    }

    public String getModuleDetail() {
        return moduleDetail;
    }

    public String getTrigValue() {
        return trigValue;
    }

    public DaInfo setTrigValue(String trigValue) {
        this.trigValue = trigValue;
        return this;
    }

    public DaInfo setExtendSdkVersion() {
        this.extendSdkVersion = BuildConfig.VERSION_NAME;
        return this;
    }

    public DaInfo setExtendRemoteIp(String extendRemoteIp) {
        this.extendRemoteIp = extendRemoteIp;
        return this;
    }

    public DaInfo setExtendRemotePort(String extendRemotePort) {
        this.extendRemotePort = extendRemotePort;
        return this;
    }

    public String getExtend() {
        return extend;
    }

    public DaInfo setExtend(String extend) {
        this.extend = extend;
        return this;
    }

    public String getExtendJson(Context context){
        Map<String, String> extend = new HashMap<>();
        extend.put(Da.extend.package_name, context.getPackageName());
        extend.put(Da.extend.PID, String.valueOf(android.os.Process.myPid()));
        if(!TextUtils.isEmpty(extendSdkVersion)){
            extend.put(Da.extend.SDK_VERSION, extendSdkVersion);
        }
        if(!TextUtils.isEmpty(extendRemoteIp)){
            extend.put(Da.extend.IP, extendRemoteIp);
        }
        if(!TextUtils.isEmpty(extendRemotePort)){
            extend.put(Da.extend.PORT, extendRemotePort);
        }
        setExtend(map2Json(extend));
        return getExtend();
    }

    private static String map2Json(Map map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        return new JSONObject(map).toString();
    }

    public static void uploadDaReport(Context context,NewBfcUploads newBfcUploads,String functionName){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(MAP_KEY_APP_NAME,newBfcUploads.getPackageName());
        hashMap.put(MAP_KEY_APP_VERSION, Utils.getAppVersionName(context,newBfcUploads.getPackageName()));
        hashMap.put(MAP_KEY_URL,newBfcUploads.getUrl());
        hashMap.put(MAP_KEY_TYPE,newBfcUploads.getTaskType().toString());
        if(functionName.equals(FUCTION_NAME_UPLOAD_FAILURE)){
            hashMap.put(MAP_KEY_ERROR_CODE,newBfcUploads.getStatus().toString());
            hashMap.put(MAP_KEY_ERROR_MSG,newBfcUploads.getErrorMsg());
        }

        DaInfo daInfo = new DaInfo();
        daInfo.setFunctionName(functionName);
        daInfo.setModuleDetail(MODULE_DETAIL);
        daInfo.setExtendSdkVersion();
        daInfo.setExtend(map2Json(hashMap));
        Da.record(context,daInfo);
    }
}
