package com.eebbk.bfc.uploadsdk.repo.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import io.reactivex.Flowable;

@Dao
public interface NewBfcUploadsDao {
    @Insert
    Long insertTask(NewBfcUploads newBfcUploads);

    @Update
    int updateTask(NewBfcUploads newBfcUploads);

    @Query("SELECT * FROM newbfcuploads WHERE _id = :tid")
    @Transaction
    Flowable<NewBfcUploads> getNewBfcUploadsById(int tid);

    @Query("SELECT * FROM newbfcuploads WHERE _id = :tid")
    @Transaction
    NewBfcUploads getUploadObjectById(int tid);

}
