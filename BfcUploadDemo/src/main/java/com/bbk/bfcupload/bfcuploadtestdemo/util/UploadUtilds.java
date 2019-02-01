package com.bbk.bfcupload.bfcuploadtestdemo.util;

import android.text.format.DateUtils;
import java.text.DecimalFormat;


public class UploadUtilds {
    private static final int KB_UNIT = 1024;

    /**
     * 把毫秒时间，变成可读时间
     */
    public static CharSequence formatDuration(long millis) {
        final int hours = (int) (millis / DateUtils.HOUR_IN_MILLIS);
        final int minutes = (int) ((millis % DateUtils.HOUR_IN_MILLIS) / DateUtils.MINUTE_IN_MILLIS);
        final int seconds = (int) ((millis % DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS);

        //四舍五入
        if (hours >= 1) {
            return hours + "小时" + minutes + "分钟";
        } else if (minutes >= 1) {
            return minutes + "分钟" + seconds + "秒";
        } else if (seconds >= 1) {
            return seconds + "秒";
        } else {
            return "";
        }
    }

    /**
     * 把字节流量，变成可读流量
     */
    public static CharSequence formatBytes(long bytes) {

        final float K = bytes / (KB_UNIT * 1.0f);
        final float M = bytes / (KB_UNIT * 1.0f) / (KB_UNIT * 1.0f);
        final float G = bytes / (KB_UNIT * 1.0f) / (KB_UNIT * 1.0f) / (KB_UNIT * 1.0f);

        if (G >= 1) {
            return new DecimalFormat("#.00").format(G) + "G";
        } else if (M >= 1) {
            return new DecimalFormat("#.00").format(M) + "M";
        } else if (K >= 1) {
            return new DecimalFormat("#.00").format(K) + "KB";
        } else {
            return "";
        }
    }

    public static String getSpeedString(long speed) {
        if (speed < 0) {
            return null;
        }
        String speedString;
        if (speed < KB_UNIT) {
            speedString = speed + "B/s";
        } else if (KB_UNIT <= speed && speed <= KB_UNIT * KB_UNIT) {
            speedString = (formatNumber(speed / (float) KB_UNIT)) + "KB/s";
        } else {
            speedString = (formatNumber(speed / (float) KB_UNIT / (KB_UNIT * 1.0f))) + "MB/s";
        }
        return speedString;
    }

    private static String formatNumber(float number) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(number);
    }
}
