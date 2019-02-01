package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import android.database.Cursor;
import com.eebbk.bfc.uploadsdk.upload.share.UploadConstants;

public class ChatCloudTask extends CloudTask {
    public ChatCloudTask() {
        super(UploadConstants.TASK_CHAT_CLOUD);
    }

    public ChatCloudTask(Cursor cursor) {
        super(cursor);
    }

}
