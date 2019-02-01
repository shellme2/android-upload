package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import android.content.Context;

import com.eebbk.bfc.uploadsdk.repo.UploadSdkDataSource;
import com.eebbk.bfc.uploadsdk.repo.persistence.NewBfcUploads;
import com.eebbk.bfc.uploadsdk.upload.ExtrasConverter;
import com.eebbk.bfc.uploadsdk.upload.share.Impl;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class ListenerManager {
    private Context mContext;
    private UploadSdkDataSource mDataSource;
    private static volatile ListenerManager INSTANCE;
    private final Map<Long, UploadListener> mListeners = new HashMap<>();
    private final Map<Long, Disposable> mDisposables = new HashMap<>();
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private static final String TAG = "BfcUpload.ListenerManager";

    public static ListenerManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ListenerManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ListenerManager();
                }
            }
        }
        return INSTANCE;
    }

    private ListenerManager() {
    }

    public void init(Context context,UploadSdkDataSource dataSource) {
        mContext=context;
        mDataSource = dataSource;
    }

    /**
     * 同步注册监听器
     */
     public synchronized void registerListener(long id, UploadListener listener) {
         addNewBfcUploadsObsrverById(id);
         mListeners.put(id, listener);
         onWaiting(id);
         LogUtils.d(TAG, "registerListener成功：： " + id);
    }

    private void addNewBfcUploadsObsrverById(long id) {
        Disposable disposable = mDataSource.getNewBfcUploadsById((int) id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NewBfcUploads>() {
                    @Override
                    public void accept(NewBfcUploads newBfcUploads) throws Exception {
                        LogUtils.i("observeTaskById status is " + newBfcUploads.getStatus());
                        notifyToApp(newBfcUploads);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e(throwable);
                    }
                });
        mDisposables.put(id,disposable);
        mCompositeDisposable.add(disposable);

    }

    private void notifyToApp(NewBfcUploads newBfcUploads) {
        if (Impl.isStatusSuccess(newBfcUploads.getStatus())){
            try {
                List<String> urlList = new ArrayList<>();
                urlList.addAll(ExtrasConverter.decode(newBfcUploads.getExtraFiles()).values());
                onSuccess(newBfcUploads.getId(),newBfcUploads.getFileName(),newBfcUploads.getFileSize(),urlList);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else if (Impl.isStatusError(newBfcUploads.getStatus())){
            onFailure(newBfcUploads.getId().longValue(),newBfcUploads.getFileName(),"taskState="+newBfcUploads.getStatus());
        }else if (Impl.STATUS_WAITING_FOR_NETWORK == newBfcUploads.getStatus()){
            onFailureWithNoNetwork(newBfcUploads.getId().longValue(),newBfcUploads.getFileName(),"taskState="+newBfcUploads.getStatus());
        }else if (Impl.STATUS_RUNNING == newBfcUploads.getStatus()){
            if (newBfcUploads.getCurrent_bytes() == 0){
                onStart(newBfcUploads.getId());
            }else {
                float uploadPercent = 0;
                if (newBfcUploads.getFileSize() != 0){
                    uploadPercent = (float)newBfcUploads.getCurrent_bytes() / newBfcUploads.getFileSize() * 100;
                }
                onLoading(newBfcUploads.getId().longValue(),newBfcUploads.getFileSize(),newBfcUploads.getCurrent_bytes(),100,newBfcUploads.getCurrent_speed(),(int)uploadPercent);
            }

        }
    }

    /**
     * 同步解绑监听器
     */
    private synchronized void unregisterListener(long id) {
        mCompositeDisposable.remove(mDisposables.get(id));
        mListeners.remove(id);
        LogUtils.d(TAG, "unregisterListener成功：： " + id);
    }


    /**
     * 调用监听器的onWaiting方法
     */
    private void onWaiting(long id) {
        if (mListeners.get(id) != null) {
            mListeners.get(id).onWaiting(id);
        }
    }

    /**
     * 调用监听器的onStart方法，上传启动
     */
    private void onStart(long id) {
        if (mListeners.get(id) != null) {
            mListeners.get(id).onStart();
        }
    }

    /**
     * 调用监听器的onSuccess方法，上传成功
     */
    private void onSuccess(long id, String taskName, long total, List<String> urlList) {
        UploadListener listener = mListeners.get(id);
        if (listener != null) {
            LogUtils.d(TAG, "onSuccess--Listener成功：： " + taskName + "::" + total + "LIST:" + urlList.size());
            listener.onSuccess((int) id, taskName, total, urlList);
        }

        unregisterListener(id);
    }

    /**
     * 调用监听器的onFailure方法，上传失败
     */
    private void onFailure(Long id, String taskName, String msg) {
        UploadListener listener = mListeners.get(id);
        if (listener != null) {
            LogUtils.d(TAG, "onFailure--Listener成功：： " + taskName);
            listener.onFailure(id.intValue(), taskName, msg);
        }

        unregisterListener(id);
    }

    /**
     * 调用监听器的onFailure方法，上传失败(无网络使用)
     */
    private void onFailureWithNoNetwork(Long id, String taskName, String msg) {
        UploadListener listener = mListeners.get(id);
        if (listener != null) {
            LogUtils.d(TAG, "onFailureWithNoNetwork--Listener成功：： " + taskName);
            listener.onFailure(id.intValue(), taskName, msg);
        }
    }

    /**
     * 调用监听器的onLoading方法，正在上传，更新进度数据
     */
    private void onLoading(Long id, long total, long current, long remainingMills, long speed, int precent) {
        UploadListener listener = mListeners.get(id);
        if (listener != null) {
            listener.onLoading(id.intValue(), total, current, remainingMills, speed, precent);
        }
    }

}
