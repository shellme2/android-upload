package com.eebbk.bfc.uploadsdk.module.taskmanager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.eebbk.bfc.uploadsdk.module.clouduploader.CloudUploaderJob;
import com.eebbk.bfc.uploadsdk.module.clouduploader.chatclouduploader.ChatUploaderJob;
import com.eebbk.bfc.uploadsdk.module.httpuploader.HttpUploaderJob;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class UploadJobCreator implements JobCreator {

    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case HttpUploaderJob.TAG:
                return new HttpUploaderJob();
            case CloudUploaderJob.TAG:
                return new CloudUploaderJob();
            case ChatUploaderJob.TAG:
                return new ChatUploaderJob();
            default:
                return null;
        }
    }
}
