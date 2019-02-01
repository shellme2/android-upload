package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import android.database.Cursor;
import com.eebbk.bfc.uploadsdk.upload.share.Constants;
import com.eebbk.bfc.uploadsdk.upload.share.Impl;
import com.eebbk.bfc.uploadsdk.upload.share.UploadConstants;


public class HttpTask extends UploadTask{
    private String mUrl;
    private HttpTask(){
        super(UploadConstants.TASK_HTTP);
    }

    public HttpTask(String pUrl){
        this();
        this.setUrl(pUrl);
    }

    public HttpTask(Cursor cursor){
        super(cursor);
        mUrl = cursor.getString(cursor.getColumnIndex(Impl.COLUMN_URL));
    }

    @Override
    public String toString(){
        return "HttpTask [mUrl=" + mUrl + "]" + super.toString();
    }

    @Override
    public String getUrl(){
        return mUrl;
    }

    @Override
    public void setUrl(String pUrl){
        mUrl = pUrl;
    }

    @Override
    public void setSupportProcess(boolean process) {
        Constants.isSupportCrossProcess=process;
    }
}
