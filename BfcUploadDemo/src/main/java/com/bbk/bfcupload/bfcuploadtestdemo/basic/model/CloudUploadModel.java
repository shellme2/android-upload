package com.bbk.bfcupload.bfcuploadtestdemo.basic.model;

import android.content.Context;
import com.bbk.bfcupload.bfcuploadtestdemo.util.SharedPreferencesUtil;

public class CloudUploadModel {

    private static final String SINGLE_TASK_SHARE_CONFIG = "cloud_task_config";
    private static final String SHARE_KEY_TASK_ID = "task_id";
    private final SharedPreferencesUtil mSharePfe;

    public CloudUploadModel(Context context){
        mSharePfe = new SharedPreferencesUtil(context, SINGLE_TASK_SHARE_CONFIG);
    }

    public void saveTaskId(long taskId){
        mSharePfe.putLong(SHARE_KEY_TASK_ID, taskId);
    }

    public long getTaskId(){
        return mSharePfe.getLong(SHARE_KEY_TASK_ID, -1);
    }

}
