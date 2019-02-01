package com.eebbk.bfc.uploadsdk.repo;

import com.eebbk.bfc.uploadsdk.repo.persistence.NewBfcUploads;

import io.reactivex.Flowable;

public interface UploadSdkDataSource {
    Long insertNewBfcUploads(NewBfcUploads newBfcUploads);

    int updateNewBfcUploads(NewBfcUploads newBfcUploads);

    Flowable<NewBfcUploads> getNewBfcUploadsById(int tid);

    NewBfcUploads getUploadObjectById(int tid);

}
