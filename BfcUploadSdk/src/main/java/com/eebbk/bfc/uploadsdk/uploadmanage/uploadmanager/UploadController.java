package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.eebbk.bfc.uploadsdk.Injection;
import com.eebbk.bfc.uploadsdk.module.UploadManagerImpl;
import com.eebbk.bfc.uploadsdk.upload.share.Impl;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;
import com.eebbk.bfc.uploadsdk.uploadmanage.SDKVersion;

import java.util.ArrayList;


public class UploadController implements IController {
    private static final String TAG = "UploadController";
    private com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.UploadManager moldUploadManager;
    private com.eebbk.bfc.uploadsdk.module.UploadManager mUploadManager;
    private Context mContext;

    public UploadController(Context context){
        LogUtils.d("库名："+ SDKVersion.getLibraryName()+" 版本号："+ SDKVersion.getSDKInt()+" 构建名："+ SDKVersion.getVersionName() );
        if (context == null) {
            throw new IllegalArgumentException("初始化上载库UploadController传入的content是Null");
        }
        mContext = context.getApplicationContext();
        mUploadManager = UploadManagerImpl.getInstance(mContext.getApplicationContext(),Injection.provideUploadSdkDataSource(mContext));
    }

    @Override
    public int addTask(UploadListener listener, @NonNull ITask task){
        return mUploadManager.startUpload(task,listener);
    }

    @Override
    public ITask getTaskById(long id){
        Cursor cursor = null;
        ITask task = null;
        try{
            Query query = new Query();
            query.setFilterById(id);
            cursor = mUploadManager.query(query);
            if(null == cursor){
                LogUtils.v(TAG, " null == cursor ");
                return null;
            }
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                int pTaskType = cursor.getInt(cursor
                        .getColumnIndex(Impl.COLUMN_TASK_TYPE));
                task = TaskFactory.create(pTaskType, cursor);

                cursor.moveToNext();
            }
        }catch(Exception e){
            LogUtils.e("Exception", e.getMessage());
            e.printStackTrace();
        }finally{
            if(null != cursor){
                cursor.close();
            }
        }
        return task;
    }

    @Override
    public ArrayList<ITask> getTask(@NonNull Query query){
        Cursor cursor = null;
        ArrayList<ITask> downList = new ArrayList<>();
        try{
            cursor = mUploadManager.query(query);
            if(null == cursor){
                LogUtils.v(TAG, " null == cursor ");
                return null;
            }
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                int pTaskType = cursor.getInt(cursor
                        .getColumnIndex(Impl.COLUMN_TASK_TYPE));
                ITask task = TaskFactory.create(pTaskType, cursor);
                downList.add(task);
                cursor.moveToNext();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(null != cursor){
                cursor.close();
            }
        }
        return downList;
    }

    @Override
    public ArrayList<ITask> getTaskByExtras(@NonNull Query query, String[] keys,
                                            String[] values){
        Cursor cursor = null;
        ArrayList<ITask> downList = new ArrayList<>();
        try{
            query.addFilterByExtras(keys, values);
            cursor = mUploadManager.query(query);
            if(null == cursor){
                LogUtils.v(TAG, " null == cursor ");
                return null;
            }
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                int pTaskType = cursor.getInt(cursor
                        .getColumnIndex(Impl.COLUMN_TASK_TYPE));
                ITask task = TaskFactory.create(pTaskType, cursor);
                downList.add(task);
                cursor.moveToNext();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(null != cursor){
                cursor.close();
            }
        }
        return downList;
    }

    @Override
    public int deleteTask( @NonNull long... ids){
        if(null == ids || 0 == ids.length){
            return 0;
        }
        return mUploadManager.remove(ids);
    }

    @Override
    public int deleteTask( @NonNull ITask... pTasks){
        if(null == pTasks || 0 == pTasks.length){
            return 0;
        }
        try {
            int size = pTasks.length;
            long[] ids = new long[size];

            for(int i = 0; i < size; i++){

                ids[i] = pTasks[i].getId();
            }

            return mUploadManager.remove(ids);
        } catch (Exception e) {
            LogUtils.e(e, "删除任务异常");
            return 0;
        }
    }

    @Override
    public int deleteTask(ArrayList<ITask> pTasks){
        return deleteTask(pTasks.toArray(new ITask[0]));
    }

    @Override
    public int reloadTask(UploadListener listtener,  @NonNull ITask... pTasks){
        if(pTasks != null){
            return mUploadManager.restartUpload(listtener, getIds(pTasks));
        }
        return 0;
    }

    @Override
    public int reloadTask(UploadListener listtener, ArrayList<ITask> pTasks){
        return reloadTask(listtener, pTasks.toArray(new ITask[0]));
    }

    private long[] getIds(ITask... pTasks){
        if(pTasks != null){
            long[] ids = new long[pTasks.length];
            for(int i = 0; i < pTasks.length; i++){
                ids[i] = pTasks[i].getId();
            }
            return ids;
        }

        return null;
    }

    @Override
    public int refreshData(ITask... pTasks){
        if(null == pTasks || 0 == pTasks.length){
            return 0;
        }
        for(ITask task : pTasks){
            ITask srcTask = this.getTaskById(task.getId());
            cloneData(task, srcTask);
        }
        return pTasks.length;
    }

    @Override
    public void registerListener(long taskId, @NonNull UploadListener listener) {
        if(taskId > 0) {
            mUploadManager.registerListener(taskId, listener);
        }
    }

    private void cloneData(ITask task, ITask srcTask){
        if(null == srcTask){
            return;
        }
        task.setPriority(srcTask.getPriority());
        task.setUrl(srcTask.getUrl());
        task.setFileName(srcTask.getFileName());
        task.setFilePath(srcTask.getFilePath());
        task.setFileSize(srcTask.getFileSize());
        task.setUploadedSize(srcTask.getUploadedSize());
        task.setSpeed(srcTask.getSpeed());
        task.setState(srcTask.getState());
        task.setNeedtime(srcTask.getNeedtime());
        task.setExtrasMap(srcTask.getExtrasMap());
        task.setFilesMap(srcTask.getFilesMap());
        task.setOutput(srcTask.getOutput());
    }

}
