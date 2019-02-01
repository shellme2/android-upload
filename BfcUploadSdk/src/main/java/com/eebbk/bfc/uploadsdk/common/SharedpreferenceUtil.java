package com.eebbk.bfc.uploadsdk.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Author: chenxiang
 * Date:   2017/9/27
 * Description:
 */

public class SharedpreferenceUtil {

    private static final String CHAT_SP_TOKEN = "chat_token";
    private static final String CHAT_SP_URL = "chat_url";
    private static final String CHAT_RESET = "chat_reset";

    public static void saveToken(String token, Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("bfcUpload", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CHAT_SP_TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("bfcUpload", Context.MODE_PRIVATE);
        return sp.getString(CHAT_SP_TOKEN, "");
    }

    public static void saveUrl(String url, Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("bfcUpload", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CHAT_SP_URL, url);
        editor.apply();
    }

    public static String getUrl(Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("bfcUpload", Context.MODE_PRIVATE);
        return sp.getString(CHAT_SP_URL, "");
    }

    public static void saveReset(boolean isReset, Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("bfcUpload", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(CHAT_RESET, isReset);
        editor.apply();
    }

    public static boolean getReset(Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("bfcUpload", Context.MODE_PRIVATE);
        return sp.getBoolean(CHAT_RESET, true);
    }

}
