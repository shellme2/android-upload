package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import android.database.Cursor;
import com.eebbk.bfc.uploadsdk.upload.share.Constants;
import com.eebbk.bfc.uploadsdk.upload.share.Impl;

public abstract class CloudTask extends UploadTask {
    private boolean mOverWrite = false;


    CloudTask(int type){
        super(type);
    }

    CloudTask(Cursor cursor){
        super(cursor);
        mOverWrite = cursor.getInt(cursor.getColumnIndex(Impl.COLUMN_OVERWRITE)) != 0;
    }

    @Override
    public void setOverWrite(boolean overWrite){
        mOverWrite = overWrite;
    }

    @Override
    public boolean getOverWrite(){
        return mOverWrite;
    }


    @Override
    public void setSupportProcess(boolean process) {
        Constants.isSupportCrossProcess=process;

    }
}
