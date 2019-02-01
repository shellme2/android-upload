package com.eebbk.bfc.uploadsdk.repo.persistence;


import com.eebbk.bfc.uploadsdk.repo.UploadSdkDataSource;

import io.reactivex.Flowable;

public class LocalUploadSdkDataSource implements UploadSdkDataSource{
    private final NewBfcUploadsDao mNewBfcUploadsDao;

    public LocalUploadSdkDataSource(NewBfcUploadsDao newBfcUploadsDao) {
        this.mNewBfcUploadsDao = newBfcUploadsDao;
    }

    @Override
    public Long insertNewBfcUploads(NewBfcUploads newBfcUploads) {
        return mNewBfcUploadsDao.insertTask(newBfcUploads);
    }

    @Override
    public int updateNewBfcUploads(NewBfcUploads newBfcUploads) {
        return mNewBfcUploadsDao.updateTask(newBfcUploads);
    }

    @Override
    public Flowable<NewBfcUploads> getNewBfcUploadsById(int tid) {
        return mNewBfcUploadsDao.getNewBfcUploadsById(tid);
    }

    @Override
    public NewBfcUploads getUploadObjectById(int tid) {
        return mNewBfcUploadsDao.getUploadObjectById(tid);
    }
}
