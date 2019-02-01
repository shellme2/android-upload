package com.bbk.bfcupload.bfcuploadtestdemo.upload;

import android.content.Context;
import android.os.Handler;

import com.eebbk.bfc.uploadsdk.upload.net.NetworkType;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ChatCloudTask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ITask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.UploadController;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.UploadListener;

import java.util.List;

public class UploadManager {
    private final ITask mTask = new ChatCloudTask();
    private final UploadController mUploadController;

    public UploadManager(Context context, String taskName, String upFileName, String upFilePath) {
        mTask.setFileName(taskName);
        mTask.addFileBody(upFileName, upFilePath);
        mTask.setOverWrite(false);
        mTask.setNetworkTypes(NetworkType.NETWORK_WIFI | NetworkType.NETWORK_MOBILE);
        mUploadController = new UploadController(context);
    }

    public void startUpLoad() {
        mUploadController.addTask(mListener, mTask);
    }

    private final UploadListener mListener = new UploadListener() {
        @Override
        public void onLoading(int i, long l, long l1, long l2, long l3, int i1) {
        }

        @Override
        public void onSuccess(int i, String s, long l, List<String> list) {
             mUploadController.deleteTask(mTask.getId());
        }

        @Override
        public void onFailure(int i, String s, String s1) {
            mUploadController.deleteTask(mTask.getId());
        }
    };

}
