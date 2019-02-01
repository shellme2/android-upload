package com.eebbk.bfc.uploadsdk.module;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.eebbk.bfc.uploadsdk.common.UploadSdkPreCondition;
import com.eebbk.bfc.uploadsdk.common.Utils;
import com.eebbk.bfc.uploadsdk.module.clouduploader.CloudUploaderJob;
import com.eebbk.bfc.uploadsdk.module.clouduploader.chatclouduploader.ChatUploaderJob;
import com.eebbk.bfc.uploadsdk.module.httpuploader.HttpUploaderJob;
import com.eebbk.bfc.uploadsdk.module.taskmanager.UploadJobCreator;
import com.eebbk.bfc.uploadsdk.repo.UploadSdkDataSource;
import com.eebbk.bfc.uploadsdk.repo.persistence.NewBfcUploads;
import com.eebbk.bfc.uploadsdk.upload.ExtrasConverter;
import com.eebbk.bfc.uploadsdk.upload.share.Impl;
import com.eebbk.bfc.uploadsdk.upload.share.UploadConstants;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.CursorTranslator;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ITask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ListenerManager;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.Query;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.UploadListener;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import static com.eebbk.bfc.uploadsdk.upload.share.UploadConstants.UNDERLYING_COLUMNS;

public class UploadManagerImpl implements UploadManager{
    @SuppressLint("StaticFieldLeak")
    private static volatile UploadManagerImpl INSTANCE;
    private final UploadSdkDataSource mDataSource;
    private final Context mContext;

    private UploadManagerImpl(Context applicationContext, UploadSdkDataSource dataSource){
        mContext = applicationContext;
        mDataSource = dataSource;
        JobManager.create(applicationContext).addJobCreator(new UploadJobCreator());
        ListenerManager.getInstance().init(applicationContext,dataSource);
    }

    public static UploadManagerImpl getInstance(Context applicationContext, UploadSdkDataSource dataSource) {
        if (INSTANCE == null) {
            synchronized (UploadManagerImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UploadManagerImpl(applicationContext,dataSource);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public synchronized int startUpload(final ITask task, final UploadListener listener) {
        UploadSdkPreCondition.checkITask(task);
        Long rowId= mDataSource.insertNewBfcUploads(initNewBfcUpload(task));
        LogUtils.i("UploadManagerImpl rowId:"+rowId);
        ListenerManager.getInstance().registerListener(rowId,listener);
        NewBfcUploads newBfcUploads = mDataSource.getUploadObjectById(rowId.intValue());
        JobRequest jobRequest = createJobRequest(newBfcUploads);
        newBfcUploads.setJobId(jobRequest.getJobId());
        mDataSource.updateNewBfcUploads(newBfcUploads);
        LogUtils.i("UploadManagerImpl startUpload");
        jobRequest.schedule();
        return rowId.intValue();
    }

    private JobRequest createJobRequest(NewBfcUploads newBfcUploads){
        String jobTag = "";
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putLong("taskId",newBfcUploads.getId());
        if (UploadConstants.TASK_HTTP == newBfcUploads.getTaskType()){
            jobTag = HttpUploaderJob.TAG;
        }else if (UploadConstants.TASK_MULTI_CLOUD == newBfcUploads.getTaskType()){
            jobTag = CloudUploaderJob.TAG;
        }else if(UploadConstants.TASK_CHAT_CLOUD==newBfcUploads.getTaskType()){
            jobTag= ChatUploaderJob.TAG;
        }

        return new JobRequest.Builder(jobTag)
                .setBackoffCriteria(1_000L, JobRequest.BackoffPolicy.LINEAR)
                .setExtras(extras)
                .startNow()
                .build();
    }


    private NewBfcUploads initNewBfcUpload(ITask task){
        NewBfcUploads newBfcUploads = new NewBfcUploads();
        newBfcUploads.setStatus(Impl.STATUS_RUNNING);
        if (UploadConstants.TASK_HTTP == task.getTaskType()){
            newBfcUploads.setUrl(task.getUrl());
        }
        newBfcUploads.setFileName(task.getFileName());
        newBfcUploads.setFileSize(task.getFileSize());
        newBfcUploads.setPriority(task.getPriority());
        newBfcUploads.setExtras(ExtrasConverter.encode(task.getExtrasMap()));
        LogUtils.i("extraFile:"+ExtrasConverter.encode(task.getFilesMap()));
        newBfcUploads.setExtraFiles(ExtrasConverter.encode(task.getFilesMap()));
        newBfcUploads.setTaskType(task.getTaskType());
        newBfcUploads.setNotifiVisibility(task.getNotificationVisibility());
        newBfcUploads.setAllowNetWorkType(task.getNetworkTypes());
        newBfcUploads.setOverWrite(task.getOverWrite());
        boolean meteredAllowed = true;
        newBfcUploads.setAllowMetered(meteredAllowed);
        boolean roamingAllowed = true;
        newBfcUploads.setAllowRoaming(roamingAllowed);
        newBfcUploads.setPackageName(Utils.getPackageName(mContext));
        LogUtils.i("initNewBfcUpload success");
        return newBfcUploads;
    }

    @Override
    public Cursor query(Query query){
        UploadSdkPreCondition.checkQuery(query);
        Cursor underlyingCursor = query.runQuery(mContext,UNDERLYING_COLUMNS);
        if(underlyingCursor == null){
            return null;
        }
        return new CursorTranslator(underlyingCursor);
    }

    @Override
    public synchronized int restartUpload(UploadListener listener, long[] ids) {
        UploadSdkPreCondition.checkIds(ids);
        int updateRows = 0;
        for (long id : ids) {
            NewBfcUploads newBfcUploads = mDataSource.getUploadObjectById((int) id);
            if (newBfcUploads != null) {
                ListenerManager.getInstance().registerListener(id, listener);
                newBfcUploads.setCurrent_bytes(0L);
                newBfcUploads.setStatus(Impl.STATUS_RUNNING);

                JobRequest request = JobManager.instance().getJobRequest(newBfcUploads.getJobId());
                if (request == null) {
                    request = createJobRequest(newBfcUploads);
                    newBfcUploads.setJobId(request.getJobId());
                    updateRows += mDataSource.updateNewBfcUploads(newBfcUploads);
                }
                request.schedule();
            } else {
                LogUtils.i("restartUpload newBfcUploads is null");
            }
        }
        LogUtils.i("restartUpload, effect " +updateRows+"rows");
        return updateRows;
    }

    @Override
    public void registerListener(long taskId, UploadListener listener) {
        UploadSdkPreCondition.checkTaskId(taskId);
        ListenerManager.getInstance().registerListener(taskId, listener);
    }

    @Override
    public synchronized int remove(long[] ids) {
        UploadSdkPreCondition.checkIds(ids);
        int effectRows = 0;
        for (long id: ids){
            NewBfcUploads newBfcUploads = mDataSource.getUploadObjectById((int) id);
            if (newBfcUploads != null){
                newBfcUploads.setDeleted(true);
                effectRows = mDataSource.updateNewBfcUploads(newBfcUploads);
            }
        }
        LogUtils.i("removeUploadTask, effect " +effectRows+"rows");
        return effectRows;
    }
}
