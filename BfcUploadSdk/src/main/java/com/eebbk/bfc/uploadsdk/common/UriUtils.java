package com.eebbk.bfc.uploadsdk.common;

import android.content.Context;
import android.net.Uri;

public class UriUtils {
    public static Uri getContentUri(Context context){
        return Uri.parse("content://" + Utils.getHostAppId(context) + ".bbkupload/my_uploads" );
    }
}
