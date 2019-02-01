package com.eebbk.bfc.uploadsdk.module.clouduploader.chatclouduploader;

/**
 * 语聊上传信息
 * @project fileServer
 * @author
 * @date 2017年9月21日 下午8:44:40
 * @company 步步高教育电子有限公司
 */
class ChatUpDto
{
    private String pushAlias;
    private String machineId;
    private String deviceModel;
    private String deviceOsVersion;
    private String packageName;
    private String versionCode;

    public void setPushAlias(String pushAlias)
    {
        this.pushAlias = pushAlias;
    }
    public void setMachineId(String machineId)
    {
        this.machineId = machineId;
    }
    public void setDeviceModel(String deviceModel)
    {
        this.deviceModel = deviceModel;
    }
    public void setDeviceOsVersion(String deviceOsVersion)
    {
        this.deviceOsVersion = deviceOsVersion;
    }
    public String getPackageName()
    {
        return this.packageName;
    }
    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }
    public void setVersionCode(String versionCode)
    {
        this.versionCode = versionCode;
    }
}
