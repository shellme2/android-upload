package com.eebbk.bfc.uploadsdk.module;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.eebbk.bfc.uploadsdk.repo.UploadSdkDataSource;
import com.eebbk.bfc.uploadsdk.repo.persistence.NewBfcUploads;
import com.eebbk.bfc.uploadsdk.upload.share.Constants;
import com.evernote.android.job.Job;

/**
 * Author: chenxiang
 * Date:   2018/10/12
 * Description:
 */

public class UploadJob extends Job {
    private static final int SECOND2MS = 1000;
    private static final int INT_3 = 3;
    private static final int INT_4 = 4;

    private long mSpeedSampleStart;
    private long mSpeedSampleBytes;
    private long mSpeed;
    private long mBytesNotified = 0;
    private long mTimeLastNotification = 0;
    protected Long mTaskId;
    protected Context mContext;
    protected NewBfcUploads mNewBfcUploads;
    protected UploadSdkDataSource mDataSource;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        return null;
    }
    protected void reportProgress(NewBfcUploads mNewBfcUploads,UploadSdkDataSource mDataSource) {
        final long now = SystemClock.elapsedRealtime();
        final long intervalTime = now - mSpeedSampleStart;
        if (intervalTime > (SECOND2MS * INT_3 / INT_4)) {
            long intervalSize = mNewBfcUploads.getCurrent_bytes() - mSpeedSampleBytes;
            mSpeed = SECOND2MS * intervalSize / intervalTime;
            mSpeedSampleStart = now;
            mSpeedSampleBytes = mNewBfcUploads.getCurrent_bytes();
            //  UploadHandler.getInstance().setCurrentSpeed(mInfo.mId, state.mSpeed);
        }
        if (shouldNotify(mNewBfcUploads)) {
            mNewBfcUploads.setCurrent_speed((int)mSpeed);
            mDataSource.updateNewBfcUploads(mNewBfcUploads);
            mBytesNotified = mNewBfcUploads.getCurrent_bytes();
            mTimeLastNotification = now;
        }
    }

    private boolean shouldNotify(NewBfcUploads mNewBfcUploads) {
        final long now = SystemClock.elapsedRealtime();
        long intervalSize = mNewBfcUploads.getCurrent_bytes() > mBytesNotified ? (mNewBfcUploads.getCurrent_bytes() - mBytesNotified) : 0;
        long intervalTime = now - mTimeLastNotification;
        if (intervalTime > 5000) {
            return true;
        }
        if (mNewBfcUploads.getFileSize() == 0 || mNewBfcUploads.getFileSize().longValue() == mNewBfcUploads.getCurrent_bytes().longValue()) {
            if (intervalSize > Constants.MIN_PROGRESS_STEP &&
                    now - mTimeLastNotification > Constants.MIN_PROGRESS_TIME) {
                return true;
            }
        } else {
            long minNotifySize = mNewBfcUploads.getFileSize() / 100;
            long minNotifyTime = SECOND2MS * 3 / 4;
            if (intervalSize >= minNotifySize && intervalTime >= minNotifyTime) {
                return true;
            }
        }
        return false;
    }
}
