package com.eebbk.bfc.uploadsdk;

public final class ErrorCode {

    /**
     * This upload was canceled
     */
    public static final int STATUS_CANCELED = 490;
    /**
     * This upload couldn't be completed because of an HttpException while
     * setting up the request.
     */
    public static final int STATUS_HTTP_EXCEPTION = 496;

    /**
     * ADD BY XYM
     */
    public static final int STATUS_HTTP_RESPONSE_PARSE_EXCEPTION = 497;

    /**
     * Qiniu exception
     */
    public static final int STATUS_QI_NIU_EXCEPTION = 499;
    /**
     * http release exception
     */
    public static final int STATUS_HTTP_RELEASE_EXCEPTION = 483;
    /**
     * http io exception
     */
    public static final int STATUS_HTTP_IO_EXCEPTION = 482;
    /**
     * http token exception
     */
    public static final int STATUS_HTTP_TOKEN_EXCEPTION = 481;
    /**
     * server return url exception
     */
    public static final int STATUS_SERVER_RETURN_URL_EXCEPTION = 480;
    /**
     * unsupport multi files exception
     */
    public static final int STATUS_UNSUPPORT_MULTI_FILE_EXCEPTION = 479;
    private ErrorCode(){}
}
