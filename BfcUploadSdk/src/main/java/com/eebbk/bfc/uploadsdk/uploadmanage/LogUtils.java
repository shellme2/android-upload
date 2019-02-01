package com.eebbk.bfc.uploadsdk.uploadmanage;

import com.eebbk.bfc.bfclog.BfcLog;

/**
 * 日志记录工具类 可以通过修改是否启用调试模式、日志级别、是否记录日志到文件, 灵活的记录日志。
 * 记录日志到文件需要配置权限：android.permission.WRITE_EXTERNAL_STORAGE
 *
 * @author llp
 */
public class LogUtils {

    private static final String TAG = "bfc-upload";//SDKVersion.getLibraryName();

    private LogUtils(){}

    private static boolean sDebug = false;
    private static BfcLog sBfcLog;
    private static final boolean sShowThreadInfo = false;

    /**
     * 创建BfcLog日志记录器
     *
     * @param isDebug  是否debug
     * @param savePath 日志保存地址，设置为null不保存，如果保存到sd卡，必须声明读写权限
     *
     * @return 日志记录器
     */
    public static BfcLog buildLog(boolean isDebug, String savePath){

        return new BfcLog.Builder()
                .tag(TAG)
                .showLog(true)
                .logLevel(isDebug ? BfcLog.VERBOSE : BfcLog.INFO)
                //.showThreadInfo(debug)
                .showThreadInfo(sShowThreadInfo)
                .methodCount(isDebug ? 1 : 0)
                .methodOffset(1)
//                .saveLog(debug && !TextUtils.isEmpty(savePath), savePath)
                .build();
    }

    /**
     * 设置日志记录器
     *
     * @param log
     * @param isDebug
     */
    public static void setLog(BfcLog log, boolean isDebug){
        sDebug = isDebug;
        sBfcLog = log;
    }

    private static synchronized BfcLog getBfcLog(){
        if(sBfcLog == null){
            sBfcLog = new BfcLog.Builder()
                    .tag(TAG)
                    .showLog(true)
                    .logLevel(BfcLog.VERBOSE)
                    .showThreadInfo(sShowThreadInfo)
                    .methodCount(0)
                    .build();
        }

        return sBfcLog;
    }

    public static boolean isDebug(){
        return sDebug;
    }

    /**
     * 记录V级别日志 在记录V级别日志时调用, 如果日志配置为不记录日志或日志级别高于V, 不记录日志
     *
     * @param msg 日志信息, 支持动态传参可以是一个或多个
     */
    public static void v(String... msg){
        if(getBfcLog() != null){
            getBfcLog().v(combineLogMsg(msg));
        }
    }

    /**
     * 记录D级别日志 在记录D级别日志时调用, 如果日志配置为不记录日志或日志级别高于D, 不记录日志
     *
     * @param msg 日志信息, 支持动态传参可以是一个或多个
     */
    public static void d(String... msg){
        if(getBfcLog() != null){
            getBfcLog().d(combineLogMsg(msg));
        }
    }

    /**
     * 记录I级别日志 在记录I级别日志时调用, 如果日志配置为不记录日志或日志级别高于I, 不记录日志
     *
     * @param msg 日志信息, 支持动态传参可以是一个或多个
     */
    public static void i(String... msg){
        if(getBfcLog() != null){
            getBfcLog().i(combineLogMsg(msg));
        }
    }

    /**
     * 记录W级别日志 在记录W级别日志时调用, 如果日志配置为不记录日志或日志级别高于W, 不记录日志
     *
     * @param msg 日志信息, 支持动态传参可以是一个或多个
     */
    public static void w(String... msg){
        if(getBfcLog() != null){
            getBfcLog().w(combineLogMsg(msg));
        }
    }

    /**
     * 记录w级别日志 在记录w级别日志时调用, 如果日志配置为不记录日志或日志级别高于w, 不记录日志
     *
     * @param tr  异常对象
     * @param msg 日志信息, 支持动态传参可以是一个或多个
     */
    public static void w(Throwable tr, String... msg){
        if(getBfcLog() != null){
            getBfcLog().w(tr, combineLogMsg(msg));
        }
    }

    /**
     * 记录E级别日志 在记录E级别日志时调用, 如果日志配置为不记录日志或日志级别高于E, 不记录日志
     *
     * @param msg 日志信息, 支持动态传参可以是一个或多个
     */
    public static void e(String... msg){
        if(getBfcLog() != null){
            getBfcLog().e(combineLogMsg(msg));
        }
    }

    /**
     * 记录E级别日志 在记录E级别日志时调用, 如果日志配置为不记录日志或日志级别高于E, 不记录日志
     *
     * @param tr  异常对象
     * @param msg 日志信息, 支持动态传参可以是一个或多个
     */
    public static void e(Throwable tr, String... msg){
        if(getBfcLog() != null){
            getBfcLog().e(tr, combineLogMsg(msg));
        }
    }

    /**
     * 组装动态传参的字符串 将动态参数的字符串拼接成一个字符串
     *
     * @param msg 动态参数
     *
     * @return 拼接后的字符串
     */
    private static String combineLogMsg(String... msg){
        if(null == msg)
            return null;

        StringBuilder sb = new StringBuilder();
        for(String s : msg){
            sb.append(s);
            sb.append(" ");
        }

        return sb.toString();
    }

}
