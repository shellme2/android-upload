package com.eebbk.bfc.uploadsdk.module.clouduploader.chatclouduploader;

import android.content.Context;
import android.support.annotation.NonNull;

import com.eebbk.bfc.json.JsonTool;
import com.eebbk.bfc.json.reflect.TypeToken;
import com.eebbk.bfc.uploadsdk.ErrorCode;
import com.eebbk.bfc.uploadsdk.Injection;
import com.eebbk.bfc.uploadsdk.common.SharedpreferenceUtil;
import com.eebbk.bfc.uploadsdk.common.StopRequestException;
import com.eebbk.bfc.uploadsdk.common.Utils;
import com.eebbk.bfc.uploadsdk.module.UploadJob;
import com.eebbk.bfc.uploadsdk.module.clouduploader.CloudUploadListener;
import com.eebbk.bfc.uploadsdk.module.clouduploader.ResumeCloudUploader;
import com.eebbk.bfc.uploadsdk.upload.ExtrasConverter;
import com.eebbk.bfc.uploadsdk.upload.share.Constants;
import com.eebbk.bfc.uploadsdk.upload.share.Impl;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.eebbk.bfc.uploadsdk.common.GsonUtil.fromJson;

/**
 * Author: chenxiang
 * Date:   2018/10/12
 * Description:
 */

public class ChatUploaderJob extends UploadJob implements CloudUploadListener {
    public static final String TAG = "job_chatuploader_tag";
    private static final String TOKEN_URL = "http://jiajiaoji.eebbk.net/fileServer/app/chatUpToken/getToken";
    private TokenBean mTokenBean = null;
    private ChatUpTokenDto mChatUpTokenDto = null;
    private ChatCloudStorageDto mChatCloudStorageDto = null;
    private Context mContext;
    private ResumeCloudUploader mResumeCloudUploader;
    private final String mTokenUrl = TOKEN_URL;
    private String mFileName = "";
    private String mKey = "";
    private String mToken;
    private String mPutUrl;
    private static final int SOCKET_TIMEOUT = 15000;
    private final HashMap<String, String> mExtraFilesTemp = new HashMap<>();
    private int mCount=0;


    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        mTaskId = params.getExtras().getLong("taskId", -1);
        LogUtils.i("ChatUploaderJob  onRunJob ");
        return chatCloudUpload();
    }

    private Result chatCloudUpload() {
        mCount++;
        mContext = getContext().getApplicationContext();
        mDataSource = Injection.provideUploadSdkDataSource(getContext());
        mNewBfcUploads = mDataSource.getUploadObjectById(mTaskId.intValue());
        mToken = SharedpreferenceUtil.getToken(mContext);
        mPutUrl = SharedpreferenceUtil.getUrl(mContext);
        if (SharedpreferenceUtil.getReset(mContext)) {
            LogUtils.d(TAG, "onPreExecute...");
            HttpPost httpRequest = null;
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, Constants.CONNECTION_TIMEOUT);
            httpClient.getParams().setParameter(
                    CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT);
            try {
                httpRequest = new HttpPost(mTokenUrl);
                LogUtils.v(TAG, "TokenUrl :" + mTokenUrl);
                LogUtils.v(TAG, "mInfo.mOverWrite :" + mNewBfcUploads.getOverWrite());
                List<NameValuePair> params = new ArrayList<>();
                ChatUpDto chatUpDto = new ChatUpDto();
                chatUpDto.setDeviceModel(android.os.Build.MODEL);
                chatUpDto.setDeviceOsVersion(android.os.Build.VERSION.INCREMENTAL);
                chatUpDto.setMachineId(android.os.Build.SERIAL);
                chatUpDto.setPackageName(mContext.getPackageName());
                //chatUpDto.setPackageName("com.eebbk.parentalcontrol");
                chatUpDto.setPushAlias("OK123344");
                chatUpDto.setVersionCode(Utils.getAppVersionName(mContext,mNewBfcUploads.getPackageName()));
                //chatUpDto.setVersionCode("2.3.0.0");
                LogUtils.v(TAG, "PackageName :" + mContext.getPackageName());
                String json = new JsonTool().toJson(chatUpDto);
                String encrypt = null;
                try {
                    encrypt = new DesUtils().encrypt(json);
                    params.add(new BasicNameValuePair("data", encrypt));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogUtils.v(TAG, "params :" + params);

                try {
                    httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                } catch (UnsupportedEncodingException e) {
                    return upLoadFailure(ErrorCode.STATUS_HTTP_EXCEPTION, "Unsupported Encoding Exception!!");
                }
                HttpResponse httpResponse = null;
                try {
                    httpResponse = httpClient.execute(httpRequest);
                } catch (ClientProtocolException e) {
                    return upLoadFailure(ErrorCode.STATUS_HTTP_EXCEPTION, "Client Protocol Exception!!");
                } catch (IOException e) {
                    e.printStackTrace();
                    return upLoadFailure(ErrorCode.STATUS_HTTP_IO_EXCEPTION, "IOException!!");
                }

                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String strResult;
                    try {
                        strResult = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                        LogUtils.d("CloudWork strResult:" + strResult);
                        mTokenBean = fromJson(strResult, new TypeToken<TokenBean>() {
                        }.getType());
                        LogUtils.d("mTokenPojo" + mTokenBean.getStateCode() + "state:" + mTokenBean.getStateInfo());
                        String decrypt = new DesUtils().decrypt(mTokenBean.getData());
                        mChatUpTokenDto = fromJson(decrypt, ChatUpTokenDto.class);
                        mChatCloudStorageDto = mChatUpTokenDto.getQiNiuCloud();
                        mToken = mChatCloudStorageDto.getToken();
                        mPutUrl = mChatCloudStorageDto.getPutUrl();
                        SharedpreferenceUtil.saveToken(mToken, mContext);
                        SharedpreferenceUtil.saveUrl(mPutUrl, mContext);
                        SharedpreferenceUtil.saveReset(false, mContext);
                    } catch (ParseException e) {
                        return upLoadFailure(ErrorCode.STATUS_HTTP_RESPONSE_PARSE_EXCEPTION, "parse http response exception!!");
                    } catch (IOException e) {
                        return upLoadFailure(ErrorCode.STATUS_HTTP_IO_EXCEPTION, "http response IO exception!!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    return upLoadFailure(ErrorCode.STATUS_HTTP_TOKEN_EXCEPTION, "http token is null exception!!");
                }
            } finally {
                if (httpRequest != null) {
                    httpRequest.abort();
                }
            }
        }
        Set<Map.Entry<String, String>> sets = null;
        try {
            sets = ExtrasConverter.decode(mNewBfcUploads.getExtraFiles()).entrySet();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, String> entry : sets) {
            mExtraFilesTemp.put(entry.getKey(), entry.getValue());
            mFileName = entry.getValue();
            mKey = entry.getKey();
        }

        File sourceFile = new File(mFileName);
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), sourceFile);
        Map<String, String> upParam = CloudClientUtil.createUpParamMap(mToken,
                android.os.Build.SERIAL, android.os.Build.MODEL, new File(mFileName).getName());
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        multipartBodyBuilder.addFormDataPart("file", sourceFile.getName(), body);
        for (Map.Entry<String, String> en : upParam.entrySet()) {
            if (en.getValue() != null) {
                multipartBodyBuilder.addFormDataPart(en.getKey(), en.getValue());
            }
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mPutUrl)
                .post(multipartBodyBuilder.build())
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String urlStr = response.body().string();
            if(mCount>5){
                mCount=0;
                return upLoadFailure(ErrorCode.STATUS_HTTP_TOKEN_EXCEPTION, "bad token!!");
            }
            if (urlStr.contains("error")) {
                SharedpreferenceUtil.saveToken("", mContext);
                SharedpreferenceUtil.saveUrl("", mContext);
                SharedpreferenceUtil.saveReset(true, mContext);
                return chatCloudUpload();
            }
            TokenBean responseBean = fromJson(urlStr, TokenBean.class);
            mExtraFilesTemp.put(mKey, responseBean.getData());
            uploadSuccess();
        } catch (IOException e) {
            e.printStackTrace();
            return upLoadFailure(ErrorCode.STATUS_HTTP_IO_EXCEPTION, "http response IO exception!!");
        }
        if (mResumeCloudUploader != null) {
            mResumeCloudUploader.cancelUpload();
            mResumeCloudUploader = null;
        }
        return Result.SUCCESS;
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
    }

    @Override
    public void uploadFailure(StopRequestException exception) {
    }

    private void updateFileUrl() {
        mNewBfcUploads.setStatus(Impl.STATUS_SUCCESS);
        mNewBfcUploads.setExtraFiles(ExtrasConverter.encode(mExtraFilesTemp));
        mDataSource.updateNewBfcUploads(mNewBfcUploads);
    }

    @SuppressWarnings("SameReturnValue")
    private Result upLoadFailure(int status, String str) {
        LogUtils.e(" ChatUploaderJob ErrorCode:" + status + str);
        mNewBfcUploads.setStatus(status);
        mNewBfcUploads.setErrorMsg(str);
        mDataSource.updateNewBfcUploads(mNewBfcUploads);
        return Result.RESCHEDULE;
    }
}
