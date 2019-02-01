package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import java.util.HashMap;


@SuppressWarnings("ALL")
public interface ITask{
    String BUCKET_TEST = "com.eebbk.bfc.app.uploaddemo";
    String EXTRA_KEY_BUCKET_ID = "bucketId";

    /**
     * 添加扩展字段数据
     */
    void putExtra(String name, String value);

    void putExtra(String name, int value);

    void putExtra(String name, boolean value);

    void putExtra(String name, float value);

    void putExtra(String name, double value);

    void putExtra(String name, char value);

    void putExtra(String name, byte value);

    void putExtra(String name, byte[] value);

    void putExtra(String name, short value);

    /**
     * 获取扩展字段数据
     */
    String getStringExtra(String name);

    int getIntExtra(String name, int defaultValue);

    boolean getBooleanExtra(String name, boolean defaultValue);

    float getFloatExtra(String name, float defaultValue);

    double getDoubleExtra(String name, double defaultValue);

    char getCharExtra(String name, char defaultValue);

    byte getByteExtra(String name, byte defaultValue);

    byte[] getByteArrayExtra(String name);

    short getShortExtra(String name, short defaultValue);

    /**
     * 　设置扩展数据MAP
     */
    HashMap<String, String> getExtrasMap();

    void setExtrasMap(HashMap<String, String> map);


    /**
     * 设置文件MAP
     */
    HashMap<String, String> getFilesMap();

    void setFilesMap(HashMap<String, String> map);

    /**
     * 添加文件
     */

    void addFile(String fileName, String filePath);

    /**
     * 获取文件
     */
    String getFileUri(String key);

    void addFileBody(String key, String filePath);

    void addStringBody(String key, String value);

    /**
     * 获取ID
     */
    long getId();

    /**
     * 设置ID
     */
    void setId(long pId);

    /**
     * 获取文件名
     */
    String getFileName();

    /**
     * 设置文件名
     */
    void setFileName(String pFileName);

    /**
     * 获取优先级
     */
    int getPriority();

    /**
     * 设置优先级
     */
    void setPriority(int pPriority);

    /**
     * 获取文件路径
     */
    String getFilePath();

    /**
     * 设置文件路径
     */
    void setFilePath(String pFilePath);

    /**
     * 获取URL
     */
    String getUrl();

    /**
     * 设置URL
     */
    void setUrl(String pUrl);

    /**
     * 获取文件大小
     */
    long getFileSize();

    /**
     * 设置文件大小
     */
    void setFileSize(long pFileSize);

    /**
     * 获取状态
     */
    int getState();

    /**
     * 设置状态
     */
    void setState(int pState);

    /**
     * 获取速度
     */
    int getSpeed();

    /**
     * 设置速度
     */
    void setSpeed(int pSpeed);

    /**
     * 获取所需时间
     */
    String getNeedtime();

    /**
     * 设置所需时间
     */
    void setNeedtime(String pNeedtime);

    /**
     * 获取已上传的大小
     */
    int getUploadedSize();

    /**
     * 设置已上传大小
     */
    @Deprecated
    void setUploadedSize(int pUploadedSize);

    /**
     * 获取远程文件路径
     */
    String getRemoteFilePath();

    /**
     * 设置远程文件路径
     */
    void setRemoteFilePath(String pRemoteFilePath);

    /**
     * 获取远程文件名
     */
    String getRemoteFileName();

    /**
     * 设置远程文件名
     */
    void setRemoteFileName(String pRemoteFileName);

    /**
     * 获取用户名
     */
    String getUserName();

    /**
     * 设置用户名
     */
    void setUserName(String pUserName);

    /**
     * 获取密码
     */
    String getPassword();

    /**
     * 设置密码
     */
    void setPassword(String pPassword);

    /**
     * 获取服务器地址
     */
    String getServerAddress();

    /**
     * 设置服务器地址
     */
    void setServerAddress(String pServerAddress);

    /**
     * 获取服务器端口
     */
    int getServerPort();

    /**
     * 设置服务器端口
     */
    void setServerPort(int pServerPort);

    /**
     * 获取任务类型
     */
    int getTaskType();

    /**
     * 获取通知栏可见状态
     */
    int getNotificationVisibility();

    /**
     * 设置通知栏可见状态
     */
    void setNotificationVisibility(int visibility);

    /**
     * http输出文本
     */
    String getOutput();

    /**
     * 设置http输出文本
     */
    void setOutput(String output);

    /**
     * 设置是否覆盖
     */
    void setOverWrite(boolean overWrite);
    /**
     * 设置是否支持跨进程
     */
    void setSupportProcess(boolean process);

    boolean getSupportProcess();

    /**
     * 是否覆盖
     */
    boolean getOverWrite();

    /**
     * 包名
     */
    String getPackageName();

    /**
     * 下载可用的网络类型：{@link NetworkType#NETWORK_WIFI}, {@link NetworkType#NETWORK_MOBILE}, {@link NetworkType#NETWORK_BLUETOOTH},
     * @param networkType 网络类型
     */
    void setNetworkTypes(int networkType);

    /**
     * 获取网络类型
     */
    int getNetworkTypes();


    void setBucketId(String bucketId);

    String getBucketId();
}
