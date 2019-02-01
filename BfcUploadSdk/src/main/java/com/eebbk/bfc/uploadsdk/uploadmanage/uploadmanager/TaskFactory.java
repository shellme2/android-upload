package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import android.database.Cursor;

import com.eebbk.bfc.uploadsdk.upload.share.UploadConstants;

class TaskFactory {
    private TaskFactory(){}

    public static ITask create(int pTaskType, Cursor cursor){
        ITask task = null;
        switch(pTaskType){
            case UploadConstants.TASK_HTTP:{
                task = new HttpTask(cursor);
            }
            break;
            case UploadConstants.TASK_MULTI_CLOUD:{
                task = new MultiCloudTask(cursor);
            }
            break;
            case UploadConstants.TASK_CHAT_CLOUD:{
                task = new ChatCloudTask(cursor);
                break;
            }
            default:{
            }
            break;
        }
        return task;
    }

}
