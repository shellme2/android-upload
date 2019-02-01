package com.bbk.bfcupload.bfcuploadtestdemo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements UncaughtExceptionHandler {
    private Context mContext;
    private UncaughtExceptionHandler mDefaultHandler;
    private static final CrashHandler INSTANCE = new CrashHandler();
    private final Map<String, String> info = new HashMap<>();

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (mDefaultHandler != null) {
            handleException(ex);
            mDefaultHandler.uncaughtException(thread, ex);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void handleException(final Throwable ex) {
        if (ex == null) return;
        collectDeviceInfo(mContext);
        final String exString = getCrashInfoString(ex);
        saveCrashInfo2File(exString, mContext);
    }

    private void collectDeviceInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                info.put("versionName", versionName);
                info.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            LogUtils.e("Error: " + e);
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                info.put(field.getName(), field.get("").toString());
            } catch (IllegalArgumentException e) {
                LogUtils.e("Error: " + e);
            } catch (IllegalAccessException e) {
                LogUtils.e("Error: " + e);
            }
        }
    }

    private String getCrashInfoString(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\r\n");
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();
        String result = writer.toString();
        sb.append(result);
        return sb.toString();
    }

    @SuppressLint("SimpleDateFormat")
    private static void saveCrashInfo2File(String exString, Context context) {
        long timetamp = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        String fileName = "-crash-" + time + "-" + timetamp + ".log";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".crash" + File.separator + context.getPackageName());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File outfile = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(outfile);
                fos.write(exString.getBytes());
                fos.close();
                outfile.getAbsolutePath();
            } catch (FileNotFoundException e) {
                LogUtils.e("Error: " + e);
            } catch (IOException e) {
                LogUtils.e("Error: " + e);
            }
        }
    }
}
