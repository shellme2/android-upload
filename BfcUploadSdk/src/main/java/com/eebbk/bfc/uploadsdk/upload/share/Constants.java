/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eebbk.bfc.uploadsdk.upload.share;

import android.os.Build;
import android.text.TextUtils;


public class Constants {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int SOCKET_TIMEOUT = 60000;
    public static boolean isSupportCrossProcess = true;
    public static final String BFC_UPLOADLOAD_HOST_APP_ID = "BFC_UPLOAD_HOST_APP_ID";
    public static final String FAILED_CONNECTIONS = "numfailed";
    public static final int MIN_PROGRESS_STEP = 2048;
    public static final long MIN_PROGRESS_TIME = 750;
    @SuppressWarnings("unused")
    private static final boolean LOCAL_LOGV = false;
    @SuppressWarnings("unused")
    private static final boolean LOCAL_LOGVV = false;

    private Constants() {
    }

    static {
        final StringBuilder builder = new StringBuilder();
        final boolean validRelease = !TextUtils.isEmpty(Build.VERSION.RELEASE);
        final boolean validId = !TextUtils.isEmpty(Build.ID);
        final boolean includeModel = "REL".equals(Build.VERSION.CODENAME)
                && !TextUtils.isEmpty(Build.MODEL);
        builder.append("AndroidUploadManager");
        if (validRelease) {
            builder.append("/").append(Build.VERSION.RELEASE);
        }
        builder.append(" (Linux; U; Android");
        if (validRelease) {
            builder.append(" ").append(Build.VERSION.RELEASE);
        }
        if (includeModel || validId) {
            builder.append(";");
            if (includeModel) {
                builder.append(" ").append(Build.MODEL);
            }
            if (validId) {
                builder.append(" Build/").append(Build.ID);
            }
        }
        builder.append(")");
    }
}
