package com.eebbk.bfc.uploadsdk.upload.share;


public interface UploadConstants {
    /**
     * Value of {@link } when the upload is waiting to start.
     */
    int STATUS_PENDING = 0x1;

    /**
     * Value of {@link } when the upload is currently running.
     */
    int STATUS_RUNNING = 0x2;

    /**
     * Value of {@link} when the upload is waiting to retry or resume.
     */
    int STATUS_PAUSED = 0x4;

    /**
     * Value of {@link } when the upload has successfully completed.
     */
    int STATUS_SUCCESSFUL = 0x8;

    /**
     * Value of {@link} when the upload has failed (and will not be retried).
     */
    int STATUS_FAILED = 0x10;

    /**
     * priority normal for our ordinary application
     */
    int PRIORITY_NORMAL = 1;
    /**
     * task type for http client to upload file.
     */
    int TASK_HTTP = 2;
    /**
     * task type for cloud http client to upload multi file.
     */
    int TASK_MULTI_CLOUD = 4;
    /**
     * task type for chat cloud to upload voice
     */
    int TASK_CHAT_CLOUD = 5;

    /**
     * columns to request from UploadProvider.
     *
     * @hide
     */
    String[] UNDERLYING_COLUMNS = new String[]{
            Impl._ID,
            Impl.COLUMN_URL,
            Impl.COLUMN_TASK_TYPE,
            Impl.COLUMN_OUTPUT,
            Impl.COLUMN_FILE_NAME,
            Impl.COLUMN_FILE_PATH,
            Impl.COLUMN_STATUS,
            Impl.COLUMN_EXTRAS,
            Impl.COLUMN_EXTRA_FILES,
            Impl.COLUMN_CURRENT_SPEED,
            Impl.COLUMN_CURRENT_BYTES,
            Impl.COLUMN_FILE_SIZE,
            Impl.COLUMN_LAST_MODIFICATION,
            Impl.COLUMN_PRIORITY,
            Impl.COLUMN_VISIBILITY,
            Impl.COLUMN_OVERWRITE,
            Impl.COLUMN_NOTIFICATION_PACKAGE
    };
}
