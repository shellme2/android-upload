package com.eebbk.bfc.uploadsdk.module;

import android.database.Cursor;

import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ITask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.Query;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.UploadListener;

public interface UploadManager {
    int startUpload(ITask task, UploadListener listener);

    Cursor query(Query query);

    int restartUpload(UploadListener listener, long[] ids);

    void registerListener(long taskId, UploadListener listener);

    int remove(long[] ids);
}
