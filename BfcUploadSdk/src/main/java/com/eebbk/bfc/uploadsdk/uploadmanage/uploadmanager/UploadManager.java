/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import com.eebbk.bfc.uploadsdk.common.UriUtils;
import com.eebbk.bfc.uploadsdk.upload.share.Impl;
import com.eebbk.bfc.uploadsdk.upload.share.UploadConstants;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;

public class UploadManager implements UploadConstants {
    private final ContentResolver mResolver;
    private final Uri mBaseUri;
    private final Handler mWorkThreadHandler;
    private static UploadManager INSTANCE;

    public static UploadManager getInstance(Context context){
        if (INSTANCE == null){
            synchronized (UploadManager.class){
                if (INSTANCE == null){
                    INSTANCE = new UploadManager( context );
                }
            }
        }
        return INSTANCE;
    }

    private UploadManager(Context context){
        mResolver = context.getContentResolver();
        HandlerThread handlerThread = new HandlerThread("UploadManagerWorkThread");
        handlerThread.start();
        mWorkThreadHandler = new Handler(handlerThread.getLooper());
        mBaseUri = UriUtils.getContentUri(context);
    }

    private int markRowDeleted(final long... ids){
        if(ids == null || ids.length == 0){
            throw new IllegalArgumentException(
                    "input param 'ids' can't be null");
        }
        mWorkThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(Impl.COLUMN_DELETED, 1);
                if(ids.length == 1){
                    LogUtils.d("更新了provider:" + ids[0]);
                    mResolver.update(
                            ContentUris.withAppendedId(mBaseUri, ids[0]), values, null,
                            null);
                }
                Query query = new Query();
                mResolver.update(mBaseUri, values, query.getWhereClauseForIds(ids),
                        query.getWhereArgsForIds(ids));
            }
        });
        return 1;
    }
    public int remove(long... ids){
        return markRowDeleted(ids);
    }
    public Cursor query(Query query){
        Cursor underlyingCursor = query.runQuery(mResolver, UNDERLYING_COLUMNS,
                mBaseUri);
        if(underlyingCursor == null){
            return null;
        }
        return new CursorTranslator(underlyingCursor, mBaseUri);
    }

}
