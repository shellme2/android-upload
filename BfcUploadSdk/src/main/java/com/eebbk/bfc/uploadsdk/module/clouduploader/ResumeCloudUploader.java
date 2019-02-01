package com.eebbk.bfc.uploadsdk.module.clouduploader;

import android.content.Context;
import android.util.Log;


import com.eebbk.bfc.uploadsdk.common.CloudWorkUtils;
import com.eebbk.bfc.uploadsdk.common.StopRequestException;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;
import com.qiniu.android.utils.UrlSafeBase64;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static com.eebbk.bfc.uploadsdk.ErrorCode.STATUS_HTTP_IO_EXCEPTION;
import static com.eebbk.bfc.uploadsdk.ErrorCode.STATUS_QI_NIU_EXCEPTION;


/**
 * 作者： chenxiang
 * 日期： 2017/7/26.
 * 公司： 步步高教育电子有限公司
 * 描述： 断点续传类
 */
public class ResumeCloudUploader {
    private final CloudUploadListener mCloudUploadListener;
    private final Context mContext;
    private UploadManager mUploadManager;
    private HashMap<String , String> mExtraFilesTemp;
    private TokenPojo.DataPojo.ClientToken mClientToken;
    private boolean cancelUpload;
    private long mLastTransferredBytes;

    public ResumeCloudUploader(Context context, CloudUploadListener cloudUploadListener) throws StopRequestException {
        mContext = context.getApplicationContext();
        mCloudUploadListener = cloudUploadListener;
        initUploadManager();
    }

    public void upload(TokenPojo.DataPojo.ClientToken clientToken, HashMap<String, String> extraFilesTemp, long uploadedSize) {
        mExtraFilesTemp = extraFilesTemp;
        mClientToken = clientToken;
        mLastTransferredBytes = uploadedSize;

        String filePath = CloudWorkUtils.getFilePathByClientToken(clientToken, mExtraFilesTemp);
        String key = clientToken.getStringBody().get("key");
        File uploadFile = new File(filePath);
        final long fileLength = uploadFile.length();
        String uploadToken = CloudWorkUtils.getTokenByClientToken(clientToken);

        mUploadManager.put(uploadFile, key, uploadToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo respInfo, JSONObject jsonData) {
                        dealComplete(respInfo);
                    }
                }, getUploadOptions(fileLength));
    }

    public void cancelUpload() {
        cancelUpload = true;
    }

    private void dealComplete(ResponseInfo respInfo) {
        if(respInfo.isOK()) {
            String fileName = CloudWorkUtils.getFileNameByClientToken(mClientToken);
            String url = CloudWorkUtils.getUrlByToken(mClientToken);
            mExtraFilesTemp.put(fileName, url);

            mCloudUploadListener.uploadSuccess();
        } else {
            String errorMsg = respInfo.error + ",statusCode=" + respInfo.statusCode;
            mCloudUploadListener.uploadFailure(new StopRequestException(STATUS_QI_NIU_EXCEPTION, errorMsg));
        }
    }

    private UploadOptions getUploadOptions(final long fileLength) {
        return new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                long totalTransferredBytes = (long) (fileLength * percent);
                long currentBytes = totalTransferredBytes - mLastTransferredBytes;
                mCloudUploadListener.transferred(currentBytes);

                mLastTransferredBytes = totalTransferredBytes;
            }
        }, new UpCancellationSignal() {

            @Override
            public boolean isCancelled() {
                return cancelUpload;
            }
        });
    }

    private void initUploadManager() throws StopRequestException {
        FileRecorder fileRecorder;
        try {
            String directory = mContext.getFilesDir() + "/QiniuAndroid";
            fileRecorder = new FileRecorder(directory);
        } catch (IOException e) {
            throw new StopRequestException(STATUS_HTTP_IO_EXCEPTION, "");
        }

        mUploadManager = new UploadManager(fileRecorder, new KeyGenerator() {
                    @Override
                    public String gen(String key, File file) {
                        String recorderName = System.currentTimeMillis() + ".progress";
                        try {
                            recorderName = UrlSafeBase64.encodeToString(CloudWorkUtils.sha1(file
                                    .getAbsolutePath() + ":" + file.lastModified())) + ".progress";
                        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                            Log.e("QiniuLab", e.getMessage());
                        }
                        return recorderName;
                    }
                });
    }

}
