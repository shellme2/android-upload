package com.eebbk.bfc.uploadsdk.common;

import android.text.TextUtils;

import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ITask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.Query;

import java.io.File;
import java.util.Map;

public final class UploadSdkPreCondition {


    public static void checkITask(ITask task){
        if (task == null){
            throw new NullPointerException("ITask 为null");
        }

        for (Object o : task.getExtrasMap().entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();

            if (TextUtils.isEmpty(key)) {
                continue;
            }
            if (key.startsWith("F:")) {
                File file = new File(val);
                if (!file.exists()) {
                    throw new IllegalArgumentException("文件不存在");
                }
            }
        }
    }

    public static void checkIds(long[] ids){
        if(ids==null){
            throw new NullPointerException("ids 为null");
        }
        for(long id:ids){
            if(id<0){
                throw new IllegalArgumentException("任务id不存在");
            }
        }
    }

    public static void checkQuery(Query query){
        if (query == null){
            throw new NullPointerException("query 为null");
        }
    }

    public static void checkTaskId(long id){
        if (id <0){
            throw new IllegalArgumentException("任务id不存在");
        }
    }
}
