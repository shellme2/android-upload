package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import android.database.Cursor;

import com.eebbk.bfc.uploadsdk.upload.share.UploadConstants;


public class MultiCloudTask extends CloudTask {

    public MultiCloudTask(){
        super(UploadConstants.TASK_MULTI_CLOUD);
    }

    public MultiCloudTask(Cursor cursor){
        super(cursor);
    }

}
