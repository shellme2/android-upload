package com.eebbk.bfc.uploadsdk.common;


import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;

public class StopRequestException extends Exception{
    private final int mFinalStatus;

    public StopRequestException(int finalStatus, String message){
        super(message);
        LogUtils.v("StopRequestException", "state : " + finalStatus + " errorMsg :" + message);
        mFinalStatus = finalStatus;
    }

    public StopRequestException(int finalStatus, String message, Throwable throwable){
        super(message, throwable);
        mFinalStatus = finalStatus;
    }

}
