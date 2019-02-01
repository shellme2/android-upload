package com.eebbk.bfc.uploadsdk.module.clouduploader;

import android.text.TextUtils;

import com.eebbk.bfc.uploadsdk.ErrorCode;
import com.eebbk.bfc.uploadsdk.common.CloudWorkUtils;
import com.eebbk.bfc.uploadsdk.common.StopRequestException;
import com.eebbk.bfc.uploadsdk.common.Utils;
import com.eebbk.bfc.uploadsdk.module.httpuploader.CustomMultipartEntity;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者： chenxiang
 * 日期： 2017/7/27.
 * 公司： 步步高教育电子有限公司
 * 描述： 普通云上传
 */
class SimpleCloudUploader {
    private final CloudUploadListener mCloudUploadListener;
    private final HttpClient mHttpClient;
    private final HttpContext mHttpContext;
    private HashMap<String, String> mExtraFilesTemp;
    private int i=0;

    public SimpleCloudUploader(CloudUploadListener cloudUploadListener) {
        mCloudUploadListener = cloudUploadListener;
        mHttpClient = new DefaultHttpClient();
        mHttpContext = new BasicHttpContext();
    }

    public String upload(HttpPost httpPost,
                         TokenPojo.DataPojo.ClientToken clientToken,
                         HashMap<String, String> extraFilesTemp)
            throws StopRequestException {
        mExtraFilesTemp = extraFilesTemp;
        String serverResponse = null;
        CustomMultipartEntity multipartContent = new CustomMultipartEntity(new CustomMultipartEntity.ProgressListener() {
            @Override
            public void transferred(long num) {
                mCloudUploadListener.transferred(num);
            }
        });

        addFileBodyToEntity(clientToken, multipartContent);
        addStringBodyToEntity(clientToken, multipartContent);
        addHeaderToEntity(httpPost, clientToken);
        httpPost.setEntity(multipartContent);
        HttpResponse response;
        try {
            response = mHttpClient.execute(httpPost, mHttpContext);
        } catch (IOException e) {
            throw new StopRequestException(ErrorCode.STATUS_HTTP_IO_EXCEPTION, "http response IO exception!!" + e);
        }

        int status = response.getStatusLine().getStatusCode();
        String errorMsg;
        try {
            errorMsg = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (ParseException e) {
            throw new StopRequestException(ErrorCode.STATUS_HTTP_RESPONSE_PARSE_EXCEPTION, "parse http response exception!!" + e);
        } catch (IOException e) {
            throw new StopRequestException(ErrorCode.STATUS_HTTP_IO_EXCEPTION, "http response IO exception!! " + e);
        }
        if (status != HttpStatus.SC_OK) {
            throw new StopRequestException(ErrorCode.STATUS_HTTP_EXCEPTION, "state : " + status + " errorMsg :" + errorMsg);
        }

        final String urlHead = "url=";
        try {
            String url;
            if (errorMsg.startsWith("{\"response\"")) {
                JSONObject jSONObject = new JSONObject(errorMsg);
                String retString2 = jSONObject.getString("response");
                jSONObject = new JSONObject(retString2);
                url = jSONObject.getString("url");
            } else if (errorMsg.startsWith("{")) {
                url = errorMsg.substring(8, errorMsg.length() - 2);
            } else {
                serverResponse = Utils.urlsafeDecodeString(errorMsg);
                url = serverResponse;
                if (serverResponse.startsWith(urlHead)) {
                    url = serverResponse.substring(urlHead.length(), serverResponse.length());
                } else {
                    throw new StopRequestException(ErrorCode.STATUS_SERVER_RETURN_URL_EXCEPTION,
                            "The server return the URL is illegal !! url : " + url);
                }
            }

            String fileName = CloudWorkUtils.getFileNameByClientToken(clientToken);
            mExtraFilesTemp.put(fileName, url);
            LogUtils.d("CloundWork:url = " + url);
            i++;
            if(i==mExtraFilesTemp.size()){
                mCloudUploadListener.uploadSuccess();
            }
        } catch (Exception e) {
            throw new StopRequestException(ErrorCode.STATUS_HTTP_IO_EXCEPTION, "这里解析返回数据异常:" + e);
        }
        return serverResponse;
    }

    private void addHeaderToEntity(HttpPost httpPost, TokenPojo.DataPojo.ClientToken clientToken) {
        if (clientToken.getHeadMap() == null) {
            return;
        }
        for (Map.Entry entry : clientToken.getHeadMap().entrySet()) {
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            httpPost.addHeader(key, val);
        }
    }

    private void addStringBodyToEntity(TokenPojo.DataPojo.ClientToken clientToken, CustomMultipartEntity multipartContent) {
        if (clientToken.getStringBody() == null) {
            return;
        }
        for (Map.Entry entry : clientToken.getStringBody().entrySet()) {
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();

            try {
                multipartContent.addPart(key, new StringBody(val, Charset.forName("utf-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void addFileBodyToEntity(TokenPojo.DataPojo.ClientToken clientToken, CustomMultipartEntity multipartContent)
            throws StopRequestException {
        if (clientToken.getFileBody() == null) {
            return;
        }
        int count = 0;
        for (Map.Entry entry : clientToken.getFileBody().entrySet()) {
            String key = (String) entry.getKey();
            String fileName = (String) entry.getValue();

            String path = mExtraFilesTemp.get(fileName);
            if (TextUtils.isEmpty(path) || !new File(path).exists()) {
                LogUtils.d("CloundWork:filePath :" + path);
                throw new StopRequestException(ErrorCode.STATUS_HTTP_EXCEPTION, "The filePath is not exist!!");
            }
            LogUtils.i("CloundWork:filePath :" + path);
            multipartContent.addPart(key, new FileBody(new File(path)));
            count++;
        }
        if (count > 1) {
            LogUtils.e("errror", "这里是多文件操作!!!!");
            throw new StopRequestException(ErrorCode.STATUS_UNSUPPORT_MULTI_FILE_EXCEPTION,
                    "unsupport multiFile exception , count=" + count);
        }
    }
}
