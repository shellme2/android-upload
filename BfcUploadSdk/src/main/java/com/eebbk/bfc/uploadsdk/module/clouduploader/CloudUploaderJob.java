package com.eebbk.bfc.uploadsdk.module.clouduploader;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.eebbk.bfc.json.reflect.TypeToken;
import com.eebbk.bfc.uploadsdk.ErrorCode;
import com.eebbk.bfc.uploadsdk.Injection;
import com.eebbk.bfc.uploadsdk.common.CloudWorkUtils;
import com.eebbk.bfc.uploadsdk.common.GsonUtil;
import com.eebbk.bfc.uploadsdk.common.StopRequestException;
import com.eebbk.bfc.uploadsdk.common.Utils;
import com.eebbk.bfc.uploadsdk.module.UploadJob;
import com.eebbk.bfc.uploadsdk.upload.ExtrasConverter;
import com.eebbk.bfc.uploadsdk.upload.share.Constants;
import com.eebbk.bfc.uploadsdk.upload.share.Impl;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ITask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.SettingUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Author: chenxiang
 * Date:   2018/10/11
 * Description:
 */

public class CloudUploaderJob extends UploadJob implements CloudUploadListener {
    public static final String TAG = "job_clouduploader_tag";
    private static final String TOKEN_URL = "http://jiajiaoji.eebbk.net/fileServer/token/geToken";
    private TokenPojo mTokenPojo = null;
    private HttpPost mHttpPost = null;
    private ResumeCloudUploader mResumeCloudUploader;
    private SimpleCloudUploader mSimpleCloudUploader;
    private final String mTokenUrl = TOKEN_URL;
    private final HashMap<String, String> mExtraFilesTemp = new HashMap<>();
    private int translateBigFileState = QiniuUploadState.FREE;
    private StopRequestException mBigFileStopRequestException;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params paramms) {
        LogUtils.i("CloudUploaderJob onRunJob");
        mTaskId = paramms.getExtras().getLong("taskId", -1);
        return cloudUpload();
    }

    private Result cloudUpload() {
        mContext = getContext().getApplicationContext();
        mDataSource = Injection.provideUploadSdkDataSource(getContext());
        mNewBfcUploads = mDataSource.getUploadObjectById(mTaskId.intValue());
        getTokenPojo();

        if (mTokenPojo == null) {
            return upLoadFailure(ErrorCode.STATUS_HTTP_EXCEPTION, "The http upload task tokenpojo is null!!");
        } else if (!mTokenPojo.getStateCode().equals("0")) {
            return upLoadFailure(ErrorCode.STATUS_HTTP_EXCEPTION, "The http upload task stateCode: " +
                    mTokenPojo.getStateCode() + " stateMsg: " + mTokenPojo.getStateInfo());
        } else if (mTokenPojo.getData() == null) {
            return upLoadFailure(ErrorCode.STATUS_HTTP_EXCEPTION, "The http upload task tokenpojo data is null!!");
        } else if (mTokenPojo.getData().getClientToken() == null || mTokenPojo.getData().getClientToken().isEmpty()) {
            return upLoadFailure(ErrorCode.STATUS_HTTP_EXCEPTION, "The http upload task tokenpojo data token list is null!!");
        } else if (TextUtils.isEmpty(mTokenPojo.getData().getPut_url())) {
            return upLoadFailure(ErrorCode.STATUS_HTTP_EXCEPTION, "The http upload task tokenpojo put url is null!!");
        }


        //TODO:后期要都改成OKHttp

        mHttpPost = new HttpPost(mTokenPojo.getData().getPut_url());
        LogUtils.d("CloundWork:" + mTokenPojo.getData().getPut_url());
        Set<Map.Entry<String, String>> sets = null;
        try {
            sets = ExtrasConverter.decode(mNewBfcUploads.getExtraFiles()).entrySet();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, String> entry : sets) {
            mExtraFilesTemp.put(entry.getKey(), entry.getValue());
        }
        for (TokenPojo.DataPojo.ClientToken clientToken : mTokenPojo.getData().getClientToken()) {
            if (clientToken == null) {
                return upLoadFailure(ErrorCode.STATUS_HTTP_EXCEPTION, "The clientToken item is null!!");
            } else if (clientToken.getFileBody() == null) {
                LogUtils.v(TAG, "clientToken.getFileBody() == null)");
            } else if (clientToken.getStringBody() == null) {
                LogUtils.v(TAG, "clientToken.getStringBody() == null");
            }
            try {
                translateFile(mHttpPost, clientToken);
            } catch (StopRequestException e) {
                e.printStackTrace();
            }
        }
        if (mHttpPost == null) {
            return upLoadFailure(ErrorCode.STATUS_HTTP_RELEASE_EXCEPTION, "http post is null!");
        }
        mHttpPost.abort();
        if (mResumeCloudUploader != null) {
            mResumeCloudUploader.cancelUpload();
            mResumeCloudUploader = null;
        }
        return Result.SUCCESS;
    }

    private void getTokenPojo() {
        String packageName = "";
        try {
            String extras = mNewBfcUploads.getExtras();
            String bucketId = ExtrasConverter.decode(extras).get(ITask.EXTRA_KEY_BUCKET_ID);
            if (SettingUtils.isTest()) {
                if (bucketId == null) {
                    packageName = ITask.BUCKET_TEST;
                } else {
                    packageName = bucketId;
                }
            } else {
                if (bucketId == null) {
                    packageName = mNewBfcUploads.getPackageName();
                } else {
                    packageName = bucketId;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        FormBody formBody = null;
        try {
            formBody = new FormBody.Builder()
                    .add("fileNames", Utils.toString(ExtrasConverter.decode(mNewBfcUploads.getExtraFiles())))
                    .add("overwrite", mNewBfcUploads.getOverWrite()?"1":"0")
                    .add("devicemodel", android.os.Build.MODEL)
                    .add("deviceosversion", android.os.Build.VERSION.INCREMENTAL)
                    .add("machineId", android.os.Build.SERIAL)
                    .add("versionName", Utils.getAppVersionName(mContext, mNewBfcUploads.getPackageName()))
                    .add("packageName", packageName)
                    .add("sdkVersion", Utils.getAppVersionCode(mContext, mContext.getPackageName()) + "")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Request request = new Request.Builder()
                .url(mTokenUrl)
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String strResult = response.body().string();
            LogUtils.i("response:" + strResult);
            mTokenPojo = GsonUtil.fromJson(strResult, new TypeToken<TokenPojo>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Result upLoadFailure(int status, String str) {
        LogUtils.e("CloudUploaderJob ErrorCode:" + status + str);
        mNewBfcUploads.setStatus(status);
        mNewBfcUploads.setErrorMsg(str);
        mDataSource.updateNewBfcUploads(mNewBfcUploads);
        return Result.RESCHEDULE;
    }

    private void translateFile(HttpPost httpPost, TokenPojo.DataPojo.ClientToken clientToken) throws StopRequestException {
        translateBigFileState = QiniuUploadState.FREE;
        String filePath = CloudWorkUtils.getFilePathByClientToken(clientToken, mExtraFilesTemp);
        if (CloudWorkUtils.isBigFile(filePath)) {
            translateBigFile(clientToken);
        } else {
            translateSimpleFile(httpPost, clientToken);
        }
    }

    private void translateBigFile(TokenPojo.DataPojo.ClientToken clientToken) throws StopRequestException {
        if(mResumeCloudUploader==null){
            mResumeCloudUploader = new ResumeCloudUploader(mContext, this);
        }
        mResumeCloudUploader.upload(clientToken, mExtraFilesTemp, mNewBfcUploads.getCurrent_bytes());

        translateBigFileState = QiniuUploadState.UPLOADING;
        mBigFileStopRequestException = null;
        while (translateBigFileState == QiniuUploadState.UPLOADING) {  //该处为了让当前方法不结束，请勿修改
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (translateBigFileState == QiniuUploadState.UPLOAD_FAILED) {
            mBigFileStopRequestException.printStackTrace();
        }
    }

    private void translateSimpleFile(HttpPost httpPost, TokenPojo.DataPojo.ClientToken clientToken) throws StopRequestException {
        if(mSimpleCloudUploader==null){
            mSimpleCloudUploader = new SimpleCloudUploader(this);
        }
        mSimpleCloudUploader.upload(httpPost, clientToken, mExtraFilesTemp);
    }

    @Override
    public void transferred(long num) {
        long temp = mNewBfcUploads.getCurrent_bytes() + num;
        mNewBfcUploads.setCurrent_bytes(temp);
        reportProgress(mNewBfcUploads, mDataSource);
    }

    @Override
    public void uploadSuccess() {
        updateFileUrl();
        translateBigFileState = QiniuUploadState.FREE;
        mBigFileStopRequestException = null;
    }

    @Override
    public void uploadFailure(StopRequestException exception) {
        mBigFileStopRequestException = exception;
        translateBigFileState = QiniuUploadState.UPLOAD_FAILED;

    }

    private void updateFileUrl() {
        mNewBfcUploads.setStatus(Impl.STATUS_SUCCESS);
        mNewBfcUploads.setExtraFiles(ExtrasConverter.encode(mExtraFilesTemp));
        LogUtils.i("extraFiless:"+ExtrasConverter.encode(mExtraFilesTemp));
        mDataSource.updateNewBfcUploads(mNewBfcUploads);
    }

}
