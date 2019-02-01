package com.eebbk.bfc.uploadsdk.module.clouduploader.chatclouduploader;

/** 
 * 语聊token信息
 * @date 2017年9月21日 下午7:27:41
 * @company 步步高教育电子有限公司
 */
class ChatUpTokenDto
{
    /**
     * 初始版本只提供七牛云存储
     * */
    private ChatCloudStorageDto qiNiuCloud;

    public ChatCloudStorageDto getQiNiuCloud()
    {
        return this.qiNiuCloud;
    }


}
