package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import java.util.List;


public abstract class UploadListener {
    /**
     * 等待中
     */
    public void onWaiting(long id) {
    }
    /**
     * 上传启动时，调用
     */
    public void onStart() {
    }
    /**
     * 正在上传，更新进度信息
     */
    public abstract void onLoading(int id, long total, long current, long remainingMillis,
                          long speed, int precent) ;
    /**
     * 上传成功，回调
     */
    public abstract void onSuccess(int id,String taskName,long total,List<String> urlList);
    /**
     * 上传失败，回调
     */
    public abstract void onFailure(int id,String taskName,String msg);
}
