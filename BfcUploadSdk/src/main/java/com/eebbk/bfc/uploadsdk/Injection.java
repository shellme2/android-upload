package com.eebbk.bfc.uploadsdk;

import android.content.Context;

import com.eebbk.bfc.uploadsdk.repo.UploadSdkDataSource;
import com.eebbk.bfc.uploadsdk.repo.persistence.LocalUploadSdkDataSource;
import com.eebbk.bfc.uploadsdk.repo.persistence.UploadSdkDatabase;

public class Injection {
    public static UploadSdkDataSource provideUploadSdkDataSource (Context context){
        UploadSdkDatabase database = UploadSdkDatabase.getInstance(context);
        return new LocalUploadSdkDataSource(database.newBfcUploadsDao());
    }
}
