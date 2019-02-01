package com.eebbk.bfc.uploadsdk.upload.share;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Implementation details
 * <p/>
 * Exposes constants used to interact with the upload manager's content
 * provider. The constants URI ... STATUS are the names of columns in the
 * uploads table.
 *
 * @hide
 */
public final class Impl implements BaseColumns{

    public static final String JOB_ID = "job_id";

    /**
     * The content URI for accessing all uploads across all UIDs (requires the
     * ACCESS_ALL_BBK_UPLOADS permission).
     */
    private static final Uri ALL_UPLOADS_CONTENT_URI = null;

    public static Uri getAllUploadContentUri() {
        return ALL_UPLOADS_CONTENT_URI;
    }

    private static final int[] INFORMATIONAL_STATUS_CODE = new int[]{100, 200};
    private static final int[] SUCCESS_STATUS_CODE = new int[]{200, 300};
    private static final int[] CLIENT_STATUS_CODE = new int[]{400, 500};
    private static final int[] SERVER_STATUS_CODE = new int[]{500, 600};

    /**
     * This upload hasn't stated yet
     */
    public static final int STATUS_PENDING = 190;

    /**
     * This upload has started
     */
    public static final int STATUS_RUNNING = 192;

    /**
     * This upload has been paused by the owning app.
     */
    public static final int STATUS_PAUSED_BY_APP = 193;

    /**
     * This upload encountered some network error and is waiting before retrying
     * the request.
     */
    public static final int STATUS_WAITING_TO_RETRY = 194;

    /**
     * This upload is waiting for network connectivity to proceed.
     */
    public static final int STATUS_WAITING_FOR_NETWORK = 195;

    /**
     * This upload exceeded a size limit for mobile networks and is waiting for
     * a Wi-Fi connection to proceed.
     */
    public static final int STATUS_QUEUED_FOR_WIFI = 196;

    /**
     * This upload has successfully completed. Warning: there might be other
     * status values that indicate success in the future. Use isSucccess() to
     * capture the entire category.
     */
    public static final int STATUS_SUCCESS = 200;


    /**
     * This upload was canceled
     */
    public static final int STATUS_CANCELED = 490;



    /**
     * This upload is allowed to run.
     */
    public static final int CONTROL_RUN = 0;

    /**
     * This upload must pause at the first opportunity.
     */
    public static final int CONTROL_PAUSED = 1;

    /**
     * The name of the column containing the package name of the application
     * that initiating the upload. The upload manager will send notifications to
     * a component in this package when the upload completes.
     * <p/>
     * Type: TEXT
     * </P>
     * <p/>
     * Owner can Init/Read
     * </P>
     */
    public static final String COLUMN_NOTIFICATION_PACKAGE = "notificationpackage";

    /**
     * The name of the column containing the component name of the class that
     * will receive notifications associated with the upload. The package/class
     * combination is passed to Intent.setClassName(String,String).
     * <p/>
     * Type: TEXT
     * </P>
     * <p/>
     * Owner can Init/Read
     * </P>
     */
    public static final String COLUMN_NOTIFICATION_CLASS = "notificationclass";

    /**
     * If extras are specified when requesting a upload they will be provided in
     * the intent that is sent to the specified class and package when a upload
     * has finished.
     * <p/>
     * Type: TEXT
     * </P>
     * <p/>
     * Owner can Init
     * </P>
     */
    public static final String COLUMN_NOTIFICATION_EXTRAS = "notificationextras";

    /**
     * BBK upload info for Extras data
     * <p/>
     * Type: TEXT
     * </P>
     * <p/>
     * Owner can Init/Read
     * </P>
     * <p/>
     * added by xym 2014/07/16
     */
    public static final String COLUMN_EXTRAS = "extras";

    /**
     * BBK upload info for Extras data
     * <p/>
     * Type: TEXT
     * </P>
     * <p/>
     * Owner can Init/Read
     * </P>
     * <p/>
     * added by xym 2014/07/16
     */
    public static final String COLUMN_EXTRA_FILES = "extrafiles";

    /**
     * file size just for display
     * <p/>
     * Type: INTEGER
     * </P>
     * <p/>
     * Owner can Init/Read
     * </P>
     */
    public static final String COLUMN_FILE_SIZE = "file_size";
    /**
     * file size just for display
     * <p/>
     * Type: INTEGER
     * </P>
     * <p/>
     * Owner can Init/Read
     * </P>
     */
    public static final String COLUMN_TASK_TYPE = "task_type";
    /**
     * file size just for display
     * <p/>
     * Type: INTEGER
     * </P>
     * <p/>
     * Owner can Init/Read
     * </P>
     */
    public static final String COLUMN_OUTPUT = "output";
    /**
     * The name of the column containing the URI of the data being uploaded.
     * <p/>
     * Type: TEXT
     * </P>
     * <p/>
     * Owner can Init/Read
     * </P>
     */
    public static final String COLUMN_URL = "url";

    /**
     * The name of the column containing the priority that indicates it's upload
     * order. add by ly20050516@gmail.com
     * <p/>
     * <p/>
     * Type: INTEGER
     * </P>
     * <p/>
     * Owner can INTEGER
     * </P>
     */
    public static final String COLUMN_PRIORITY = "priority";

    /**
     * The name of the column containing the flags that controls whether the
     * upload is displayed by the UI. See the VISIBILITY_* constants for a list
     * of legal values.
     * <p/>
     * Type: INTEGER
     * </P>
     * <p/>
     * Owner can Init/Read/Write
     * </P>
     */
    public static final String COLUMN_VISIBILITY = "visibility";

    /**
     * The name of the column containing the current control state of the
     * upload. Applications can write to this to control (pause/resume) the
     * upload. the CONTROL_* constants for a list of legal values.
     * <p/>
     * Type: INTEGER
     * </P>
     * <p/>
     * Owner can Read
     * </P>
     */
    public static final String COLUMN_CONTROL = "control";

    /**
     * The name of the column containing the current status of the upload.
     * Applications can read this to follow the progress of each upload. See the
     * STATUS_* constants for a list of legal values.
     * <p/>
     * Type: INTEGER
     * </P>
     * <p/>
     * Owner can Read
     * </P>
     */
    public static final String COLUMN_STATUS = "status";

    /**
     * The name of the column containing the date at which some interesting
     * status changed in the upload. Stored as a System.currentTimeMillis()
     * value.
     * <p/>
     * Type: BIGINT
     * </P>
     * <p/>
     * Owner can Read
     * </P>
     */
    public static final String COLUMN_LAST_MODIFICATION = "lastmod";

    /**
     * The name of the column containing the size of the part of the file that
     * has been uploaded so far.
     * <p/>
     * Type: INTEGER
     * </P>
     * <p/>
     * Owner can Read
     * </P>
     */
    public static final String COLUMN_CURRENT_BYTES = "current_bytes";

    /**
     * The name of the column express the speed of the current uploaded.
     * <p/>
     * Type: INTEGER
     * </P>
     * <p/>
     * Owner can Read
     * </P>
     */
    public static final String COLUMN_CURRENT_SPEED = "current_speed";
    /**
     * The name of the column where the initiating application can provided the
     * title of this upload. The title will be displayed ito the user in the
     * list of uploads.
     * <p/>
     * Type: TEXT
     * </P>
     * <p/>
     * Owner can Init/Read/Write
     * </P>
     */
    public static final String COLUMN_FILE_NAME = "file_name";
    /**
     * The name of the column where the initiating application can provided the
     * title of this upload. The title will be displayed ito the user in the
     * list of uploads.
     * <p/>
     * Type: TEXT
     * </P>
     * <p/>
     * Owner can Init/Read/Write
     * </P>
     */
    public static final String COLUMN_FILE_PATH = "file_path";

    /**
     * The name of the column holding a bitmask of allowed network types. This
     * is only used for public API uploads.
     * <p/>
     * Type: INTEGER
     * </P>
     * <p/>
     * Owner can Init/Read
     * </P>
     */
    public static final String COLUMN_ALLOWED_NETWORK_TYPES = "allowed_network_types";

    /**
     * The name of the column indicating whether roaming connections can be
     * used. This is only used for public API uploads.
     * <p/>
     * Type: BOOLEAN
     * </P>
     * <p/>
     * Owner can Init/Read
     * </P>
     */
    public static final String COLUMN_ALLOW_ROAMING = "allow_roaming";

    /**
     * The name of the column indicating whether metered connections can be
     * used. This is only used for public API uploads.
     * <p/>
     * Type: BOOLEAN
     * </P>
     * <p/>
     * Owner can Init/Read
     * </P>
     */
    public static final String COLUMN_ALLOW_METERED = "allow_metered";

    /**
     * If true, the user has confirmed that this upload can proceed over the
     * mobile network even though it exceeds the recommended maximum size.
     * <p/>
     * Type: BOOLEAN
     * </P>
     */
    public static final String COLUMN_BYPASS_RECOMMENDED_SIZE_LIMIT = "bypass_recommended_size_limit";

    /**
     * Set to true if this upload is deleted. It is completely removed from the
     * database when MediaProvider database also deletes the metadata asociated
     * with this uploaded file.
     * <p/>
     * Type: BOOLEAN
     * </P>
     * <p/>
     * Owner can Read
     * </P>
     */
    public static final String COLUMN_DELETED = "deleted";

    /**
     * The column with errorMsg for a failed uploaded. Used only for debugging
     * purposes.
     * <p/>
     * Type: TEXT
     * </P>
     */
    public static final String COLUMN_ERROR_MSG = "errorMsg";

    /**
     * ADD BY XYM
     */
    public static final String COLUMN_REMOTE_FILE_PATH = "remoteFilePath";
    public static final String COLUMN_REMOTE_FILE_NAME = "remoteFileName";
    public static final String COLUMN_USER_NAME = "userName";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_SERVER_ADDRESS = "serverAddress";
    public static final String COLUMN_SERVER_PORT = "serverPort";

    public static final String COLUMN_OVERWRITE = "overWrite";

    private Impl(){
    }

    /**
     * Returns whether the status is informational (i.e. 1xx).
     */
    public static boolean isStatusInformational(int status){
        return status >= INFORMATIONAL_STATUS_CODE[0] && status < INFORMATIONAL_STATUS_CODE[1];
    }

    /**
     * Returns whether the status is a success (i.e. 2xx).
     */
    public static boolean isStatusSuccess(int status){
        return status >= SUCCESS_STATUS_CODE[0] && status < SUCCESS_STATUS_CODE[1];
    }

    /**
     * Returns whether the status is an error (i.e. 4xx or 5xx).
     */
    public static boolean isStatusError(int status){
        return status >= CLIENT_STATUS_CODE[0] && status < SERVER_STATUS_CODE[1];
    }

    /**
     * Returns whether the status is a client error (i.e. 4xx).
     */
    public static boolean isStatusClientError(int status){
        return status >= CLIENT_STATUS_CODE[0] && status < CLIENT_STATUS_CODE[1];
    }

    /**
     * Returns whether the status is a server error (i.e. 5xx).
     */
    public static boolean isStatusServerError(int status){
        return status >= SERVER_STATUS_CODE[0] && status < SERVER_STATUS_CODE[1];
    }

    /**
     * Returns whether the upload has completed (either with success or error).
     */
    public static boolean isStatusCompleted(int status){
        return isStatusSuccess(status) || isStatusError(status);
    }

}
