package com.eebbk.bfc.uploadsdk.module.httpuploader;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.eebbk.bfc.uploadsdk.ErrorCode;
import com.eebbk.bfc.uploadsdk.Injection;
import com.eebbk.bfc.uploadsdk.common.Utils;
import com.eebbk.bfc.uploadsdk.module.UploadJob;
import com.eebbk.bfc.uploadsdk.upload.ExtrasConverter;
import com.eebbk.bfc.uploadsdk.upload.share.Constants;
import com.eebbk.bfc.uploadsdk.upload.share.Impl;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;


public class HttpUploaderJob extends UploadJob {
    public static final String TAG = "job_httpuploader_tag";
    private final HttpClient mHttpClient = new DefaultHttpClient();
    private final HttpContext mHttpContext = new BasicHttpContext();
    private HttpPost mHttpPost = null;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        LogUtils.i("HttpUploaderJob onRunJob");
        mTaskId = params.getExtras().getLong("taskId", -1);
        mDataSource = Injection.provideUploadSdkDataSource(getContext());
        mNewBfcUploads = mDataSource.getUploadObjectById(mTaskId.intValue());
        mHttpPost = new HttpPost(mNewBfcUploads.getUrl());
        return httpUpload();
    }

    private Result httpUpload() {
        mHttpClient.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, Constants.CONNECTION_TIMEOUT);
        mHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                Constants.SOCKET_TIMEOUT);

        CustomMultipartEntity multipartContent = new CustomMultipartEntity(new CustomMultipartEntity.ProgressListener() {
            @Override
            public void transferred(long num) {
                long temp = mNewBfcUploads.getCurrent_bytes() + num;
                mNewBfcUploads.setCurrent_bytes(temp);
                reportProgress(mNewBfcUploads, mDataSource);
            }
        });

        if (TextUtils.isEmpty(mNewBfcUploads.getExtraFiles())) {
            return Result.FAILURE;
        }
        Iterator iter = null;
        try {
            iter = ExtrasConverter.decode(mNewBfcUploads.getExtraFiles()).entrySet().iterator();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();

            if (TextUtils.isEmpty(key)) {
                continue;
            }

            if (key.startsWith("S:")) {
                String realKey = key.substring(2, key.length());
                LogUtils.v(TAG, "KEY:" + realKey);
                try {
                    multipartContent.addPart(realKey,
                            new StringBody(val, Charset.forName("utf-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (key.startsWith("F:")) {
                String realKey = key.substring(2, key.length());
                File file = new File(val);
                if (file.exists()) {
                    FileBody fileBody = null;
                    try {
                        fileBody = new FileBody(file, URLEncoder.encode(file.getName(), "utf-8"),
                                Utils.getMIMEType(file), "utf-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    multipartContent.addPart(realKey, fileBody);
                } else {
                    return upLoadFailure(ErrorCode.STATUS_CANCELED, " HttpuploadJob the file don't exists!!!!");
                }
            } else {
                return upLoadFailure(ErrorCode.STATUS_HTTP_EXCEPTION, " the key don't startswith F or S!!");
            }
        }

        mHttpPost.setEntity(multipartContent);
        try {
            mHttpClient.execute(mHttpPost, mHttpContext);
        } catch (ClientProtocolException e1) {
            return upLoadFailure(ErrorCode.STATUS_HTTP_EXCEPTION, "http execute exception!!");
        } catch (IOException e1) {
            e1.printStackTrace();
            return upLoadFailure(ErrorCode.STATUS_HTTP_IO_EXCEPTION, "http response IO exception!!" + e1);
        }

        if (mNewBfcUploads.getFileSize() != multipartContent.getContentLength()) {
            mNewBfcUploads.setStatus(Impl.STATUS_SUCCESS);
            mNewBfcUploads.setFileSize(multipartContent.getContentLength());
            mDataSource.updateNewBfcUploads(mNewBfcUploads);
        }

        mHttpClient.getConnectionManager().shutdown();
        if (mHttpPost == null) {
            return upLoadFailure(ErrorCode.STATUS_HTTP_RELEASE_EXCEPTION, "http post is null!");
        }
        mHttpPost.abort();
        return Result.SUCCESS;
    }

    private Result upLoadFailure(int status, String str) {
        LogUtils.e("HttpUploaderJob ErrorCode:" + status+"   ErrorMsg:" + str);
        mNewBfcUploads.setStatus(status);
        mNewBfcUploads.setErrorMsg(str);
        mDataSource.updateNewBfcUploads(mNewBfcUploads);
        return Result.RESCHEDULE;
    }
}
