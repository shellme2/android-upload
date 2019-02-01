package com.bbk.bfcupload.bfcuploadtestdemo.util;

import android.app.Activity;
import android.content.Intent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyUtils {
    /**
     * 功能描述:简单地Activity的跳转(不携带任何数据)
     *
     * @param activity 发起跳转的Activity实例
     *                 <p>
     *                 目标Activity实例
     * @Time 2016年4月25日
     * @Author lizy18
     */
    public static void skipAnotherActivity(Activity activity,
                                           Class<? extends Activity> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }

    /**
     * 功能描述：带数据的Activity之间的跳转
     *
     * @param activity
     * @param cls
     * @param hashMap
     * @Time 2016年4月25日
     * @Author lizy18
     */
    public static void skipAnotherActivity(Activity activity,
                                           Class<? extends Activity> cls,
                                           HashMap<String, ? extends Object> hashMap) {
        Intent intent = new Intent(activity, cls);
        for (Object o : hashMap.entrySet()) {
            @SuppressWarnings("unchecked")
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) o;
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                intent.putExtra(key, (String) value);
            }
            if (value instanceof Boolean) {
                intent.putExtra(key, (boolean) value);
            }
            if (value instanceof Integer) {
                intent.putExtra(key, (int) value);
            }
            if (value instanceof Float) {
                intent.putExtra(key, (float) value);
            }
            if (value instanceof Double) {
                intent.putExtra(key, (double) value);
            }
        }
        activity.startActivity(intent);
    }
}
