package com.bbk.bfcupload.bfcuploadtestdemo.basic.ui;

import android.content.Context;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ITask;
import java.util.List;

public interface ICloudUploadView {

    Context getContext();

    ITask getTask();

    void updateSuccessView(ITask task);

    void updateUnfinishedView(ITask task);

    void onWaiting();

    void onLoading(long total, long current, long speed, int precent);

     void onSuccess(List<String> urlList);

     void onFailure(String msg);

}
