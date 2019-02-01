package com.eebbk.bfc.uploadsdk.module.clouduploader;

/**
 * 作者： jiazy
 * 日期： 2017/8/7.
 * 公司： 步步高教育电子有限公司
 * 描述： 七牛断点上传的状态码
 */
interface QiniuUploadState {
    int FREE = 0;
    int UPLOADING = 1;
    int UPLOAD_FAILED = 2;
}
