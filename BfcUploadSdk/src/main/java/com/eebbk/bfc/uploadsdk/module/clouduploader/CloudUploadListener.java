package com.eebbk.bfc.uploadsdk.module.clouduploader;


import com.eebbk.bfc.uploadsdk.common.StopRequestException;

/**
 * 作者： jiazy
 * 日期： 2017/7/27.
 * 公司： 步步高教育电子有限公司
 * 描述： 云上传接口回调
 */
public interface CloudUploadListener {
    void transferred(long num);
    void uploadSuccess();
    void uploadFailure(StopRequestException exception);

}
