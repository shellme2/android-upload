package com.bbk.bfcupload.bfcuploadtestdemo.basic.presenter;

import com.bbk.bfcupload.bfcuploadtestdemo.basic.ui.IUploadTaskConfig;
import com.bbk.bfcupload.bfcuploadtestdemo.basic.model.CloudUploadModel;
import com.bbk.bfcupload.bfcuploadtestdemo.basic.ui.ICloudUploadView;
import com.eebbk.bfc.uploadsdk.upload.share.UploadConstants;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.IController;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ITask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.MultiCloudTask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.UploadController;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.UploadListener;
import com.qiniu.android.utils.AsyncRun;

import java.io.File;
import java.util.List;


public class CloudUploadPresenter {
    private final ICloudUploadView mCloudUploadView;
    private IUploadTaskConfig mTaskConfig;
    private final IController mUploadController;
    private UploadListener mUploadListener;
    private final CloudUploadModel mModel;

    public CloudUploadPresenter(ICloudUploadView cloudUploadView) {
        mModel = new CloudUploadModel(cloudUploadView.getContext());
        mCloudUploadView = cloudUploadView;
        mUploadController = new UploadController(cloudUploadView.getContext());
    }

    public void bindTaskConfig(IUploadTaskConfig uploadTaskConfig) {
        mTaskConfig = uploadTaskConfig;
    }

    public MultiCloudTask createTask() {
        MultiCloudTask task = new MultiCloudTask();
        List<File> fileList = mTaskConfig.getFileList();
        if (fileList.size() == 0) {
            return null;
        }
        addFileToTask(task, fileList);
        task.setOverWrite(true);
        task.setFileName(mTaskConfig.getTaskName());
        task.setNetworkTypes(mTaskConfig.getNetworkTypes());
        task.putExtra("key1", "value1");
        task.putExtra("key2", "value2");

        return task;
    }

    private void addFileToTask(MultiCloudTask task, List<File> fileList) {
        for (int i = 0; i < fileList.size(); i++) {
            String pFilePath = fileList.get(i).getAbsolutePath();
            String pFileName = fileList.get(i).getName();
            LogUtils.i("taskFilePath:"+pFilePath);
            task.addFileBody(pFileName, pFilePath);
        }
    }

    public void startTask(ITask task) {
        mUploadController.addTask(getUploadListener(), task);
    }

    public void deleteTask(ITask task) {
        mUploadController.deleteTask(task);
        mModel.saveTaskId(-1);
    }

    private UploadListener getUploadListener() {
        if (mUploadListener == null) {
            mUploadListener = new UploadListener() {

                @Override
                public void onWaiting(long id) {
                    mModel.saveTaskId(id);
                    AsyncRun.runInMain(new Runnable() {
                        @Override
                        public void run() {
                            mCloudUploadView.onWaiting();
                        }
                    });
                }

                @Override
                public void onLoading(int id, final long total, final long current, long remainingMillis, final long speed, final int precent) {
                    AsyncRun.runInMain(new Runnable() {
                        @Override
                        public void run() {
                            mCloudUploadView.onLoading(total, current, speed, precent);
                        }
                    });
                }

                @Override
                public void onSuccess(int id, final String taskName, final long total, final List<String> urlList) {
                    mUploadController.refreshData(mCloudUploadView.getTask());
                    AsyncRun.runInMain(new Runnable() {
                        @Override
                        public void run() {
                            mCloudUploadView.onSuccess(urlList);
                        }
                    });
                }

                @Override
                public void onFailure(int id, final String taskName, final String msg) {
                    mUploadController.refreshData(mCloudUploadView.getTask());
                    AsyncRun.runInMain(new Runnable() {
                        @Override
                        public void run() {
                            mCloudUploadView.onFailure(msg);
                        }
                    });
                }
            };
        }

        return mUploadListener;
    }
}
