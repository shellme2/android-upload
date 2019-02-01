package com.eebbk.bfc.uploadsdk.module.clouduploader.chatclouduploader;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** 
 * 语聊文件上传公共类
 */
class CloudClientUtil
{
    /**项目名为:亲子语聊*/
    private final static String PROJECT_NAME = "childchat";
    private final static String PATH_SPLIT = "/";
    
    /**
     * 创建上传文件时，需要和file一起提交的参数
     * @param token            从服务端拿到的token   建议每次进入语聊时，找服务端拿一个新的token
     *                         每个上传token服务端设置的过期时间为24小时
     * @param bindMachineId    绑定的机器序列号
     * @param deviceModel      机型
     *                         平板为步步高的机型 比如OK100
     *                         手机客户端为 Android或者 IOS
     * @param fileName         要上传的语音文件名
     * @return
     */
    public static Map<String, String> createUpParamMap(
            String token,
            String bindMachineId, 
            String deviceModel, 
            String fileName)
    {
        Map<String, String> upParamBodyMap = new HashMap<>();
        upParamBodyMap.put("token", token);
        upParamBodyMap.put("key", createTokenKey(bindMachineId, deviceModel, fileName));
        upParamBodyMap.put("desc", fileName);
        return upParamBodyMap;
    }
    
    /***
     * 生成上传时需要放到body中key字段的值
     * 该值决定了下载地址的路径格式,按规范设置可以方便服务端进行存储数据的清理和读取
     * @param bindMachineId 绑定的机器序列号
     * @param deviceModel   机型  
     *                      平板为步步高的机型 比如OK100
     *                      手机客户端为 Android或者 IOS
     * @param fileName      要上传的语音文件名，建议用UUID或者md5去生成
     * @return 
     * 格式 eg.
     * /PROJECT_NAME/bindMachineId/年/月/日/时分秒/[OK100|IOS|Android]/随机数.文件后缀
     */
    private static String createTokenKey(String bindMachineId, String deviceModel, String fileName)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HHmmss", Locale.US);
        String timePath = sdf.format(new Date());
        return PROJECT_NAME +
            PATH_SPLIT + bindMachineId +
            PATH_SPLIT + timePath +
            PATH_SPLIT + (TextUtils.isEmpty(deviceModel) ? deviceModel : deviceModel.replace(" ", "").replace("+", "")) +
            PATH_SPLIT + fileName;
    }
}
