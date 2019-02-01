package com.eebbk.bfc.uploadsdk.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.eebbk.bfc.uploadsdk.upload.share.Constants;
import com.org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Utils {
    private static final int B_95 = 95;
    private static final int B_47 = 47;
    private static final int B_45 = 45;
    private static final int B_43 = 43;

    private Utils() {
    }

    private static byte[] urlsafeBase64Decode(String encoded) {
        byte[] rawbs = encoded.getBytes();
        for (int i = 0; i < rawbs.length; i++) {
            if (rawbs[i] == B_95) {
                rawbs[i] = B_47;
            } else if (rawbs[i] == B_45) {
                rawbs[i] = B_43;
            }
        }
        return Base64.decodeBase64(rawbs);
    }

    public static String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();
        switch (end) {
            case "m4a":
            case "mp3":
            case "mid":
            case "xmf":
            case "ogg":
            case "wav":
                type = "audio";
                break;
            case "3gp":
            case "mp4":
                type = "video";
                break;
            case "jpg":
            case "gif":
            case "png":
            case "jpeg":
            case "bmp":
                type = "image";
                break;
            default:
                type = "*";
                break;
        }
        type += "/*";
        return type;
    }


    public static String getHostAppId(Context appContext) throws IllegalArgumentException {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo == null) {
                throw new IllegalArgumentException(" get application info = null, has no meta data! ");
            }
            return applicationInfo.metaData.getString(Constants.BFC_UPLOADLOAD_HOST_APP_ID);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(" get application info error! ", e);
        } catch (NullPointerException e) {
            throw new NullPointerException("maybe the context is null");
        }
    }

    public static String toString(HashMap<String, String> map) throws Exception {
        @SuppressWarnings("rawtypes")
        Iterator iter = map.entrySet().iterator();
        StringBuilder str = new StringBuilder();
        while (iter.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();

            str.append(BASE64Coder.encode(key.getBytes()));
            if (iter.hasNext()) {
                str.append(",");
            }
        }
        return str.toString();
    }

    public static String getAppVersionName(Context context, String packageName) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getAppVersionCode(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.versionCode;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String urlsafeDecodeString(String encoded) {
        try {
            return new String(urlsafeBase64Decode(encoded), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
