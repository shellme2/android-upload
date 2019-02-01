package com.eebbk.bfc.uploadsdk.common;

import android.support.annotation.NonNull;
import android.text.TextUtils;


import com.eebbk.bfc.uploadsdk.module.clouduploader.TokenPojo;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;
import com.qiniu.android.common.Constants;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.utils.UrlSafeBase64;
import org.json.JSONObject;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：
 * 日期： 2017/7/26.
 * 公司： 步步高教育电子有限公司
 * 描述：云上传工具类
 */
public class CloudWorkUtils {

    /**
     * 通过token获取文件最终可下载的地址
     * @param token 业务服务器返回的token
     * @return 七牛云最终返回給上传端（在指定 returnUrl 时是携带在跳转路径参数中）的数据
     * 以下为七牛token字段的说明
     * http://developer.qiniu.com/article/developer/security/upload-token.html
     * http://developer.qiniu.com/article/developer/security/put-policy.html
     */
    private static String getUrlByToken(@NonNull String token) {
        try {
            String[] strings = token.split(":");
            String policy = new String(UrlSafeBase64.decode(strings[2]), Constants.UTF_8);
            JSONObject obj = new JSONObject(policy);
            String returnBody = obj.getString("returnBody");
            JSONObject returnBodyObj = new JSONObject(returnBody);

            return returnBodyObj.getString("url");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getUrlByToken(@NonNull TokenPojo.DataPojo.ClientToken clientToken) {
        String uploadToken = CloudWorkUtils.getTokenByClientToken(clientToken);
        return getUrlByToken(uploadToken);
    }

    public static String getTokenByClientToken(TokenPojo.DataPojo.ClientToken clientToken) {
        if (clientToken.getStringBody() == null) {
            return "";
        }

        return clientToken.getStringBody().get("token");
    }

    public static String getFilePathByClientToken(TokenPojo.DataPojo.ClientToken clientToken, HashMap<String , String> extraFilesTemp) {
        if (clientToken.getFileBody() == null) {
            return "";
        }

        for (Map.Entry entry : clientToken.getFileBody().entrySet()) {
            LogUtils.i("CloudWork ttt");
            String fileName = (String) entry.getValue();
            if(!TextUtils.isEmpty(fileName)) {
                String filePath = extraFilesTemp.get(fileName);
                return TextUtils.isEmpty(filePath) ? "" : filePath;
            }
        }

        return "";
    }

    public static String getFileNameByClientToken(TokenPojo.DataPojo.ClientToken clientToken) {
        if (clientToken.getFileBody() == null) {
            return "";
        }

        for (Map.Entry entry : clientToken.getFileBody().entrySet()) {
            String fileName = (String) entry.getValue();
            return TextUtils.isEmpty(fileName) ? "" : fileName;
        }

        return "";
    }

    public static byte[] sha1(String val) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        byte[] data = val.getBytes("utf-8");
        MessageDigest mDigest = MessageDigest.getInstance("sha1");
        return mDigest.digest(data);
    }

    /**
     * 判断是否为大文件（超过4M为大文件）
     * @param filePath 文件路径
     */
    public static boolean isBigFile(String filePath) {
        return getFileSize(filePath) >= Configuration.BLOCK_SIZE;
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    private static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    /**
     * 获取文件大小, 返回数据是byte
     * 文件不存在时, 返回的大小是 -1
     *
     * @param filePath 文件路径
     * @return 文件大小 byte; 如果文件不存在, 返回 -1;
     */
    private static long getFileSize(String filePath) {
        return getFileSize(getFileByPath(filePath));
    }

    /**
     * 获取文件大小, 返回数据是byte
     * 文件不存在时, 返回的大小是 -1
     *
     * @param file 文件
     * @return 文件大小, byte; 如果文件不存在, 返回 -1
     */
    private static long getFileSize(File file) {
        if (!isFileExists(file)) return -1L;
        return file.length();
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     */
    private static File getFileByPath(String filePath) {
        return TextUtils.isEmpty(filePath) ? null : new File(filePath);
    }
}
