package com.eebbk.bfc.uploadsdk.common;

import com.eebbk.bfc.sequence.SequenceTools;

import java.lang.reflect.Type;

/**
 * Author: chenxiang
 * Date:   2016/11/20
 * Description:
 */
public final class GsonUtil {

    private GsonUtil() {
    }
    public static <T> T fromJson(String json, Type type) {
        return SequenceTools.deserialize(json, type);
    }
}
