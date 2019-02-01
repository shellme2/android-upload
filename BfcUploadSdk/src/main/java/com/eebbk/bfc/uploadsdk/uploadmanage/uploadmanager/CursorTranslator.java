package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import com.eebbk.bfc.uploadsdk.upload.share.Impl;
import com.eebbk.bfc.uploadsdk.upload.share.UploadConstants;



public class CursorTranslator extends CursorWrapper{

    @SuppressWarnings("unused")
    private Uri mBaseUri;
    public CursorTranslator(Cursor cursor) {
        super(cursor);
    }

    public CursorTranslator(Cursor cursor, Uri baseUri){
        this(cursor);
        mBaseUri = baseUri;
    }

    @Override
    public int getInt(int columnIndex){
        return (int) getLong(columnIndex);
    }

    @Override
    public long getLong(int columnIndex){
        if(getColumnName(columnIndex).equals(Impl.COLUMN_STATUS)){
            return translateStatus(super.getInt(getColumnIndex(Impl.COLUMN_STATUS)));
        }else{
            return super.getLong(columnIndex);
        }
    }

    @SuppressLint("Assert")
    private int translateStatus(int status){
        switch(status){
            case Impl.STATUS_PENDING:{
                return UploadConstants.STATUS_PENDING;
            }
            case Impl.STATUS_RUNNING:{
                return UploadConstants.STATUS_RUNNING;
            }
            case Impl.STATUS_PAUSED_BY_APP:{
                return UploadConstants.STATUS_PAUSED;
            }
            case Impl.STATUS_WAITING_TO_RETRY:
            case Impl.STATUS_WAITING_FOR_NETWORK:
            case Impl.STATUS_QUEUED_FOR_WIFI:{
                return UploadConstants.STATUS_PENDING;
            }
            case Impl.STATUS_SUCCESS:{
                return UploadConstants.STATUS_SUCCESSFUL;
            }
            default:{
                assert Impl.isStatusError(status);
                return UploadConstants.STATUS_FAILED;
            }
        }
    }

}
