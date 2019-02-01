package com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager;

import android.app.DownloadManager.Request;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.eebbk.bfc.uploadsdk.upload.ExtrasConverter;
import com.eebbk.bfc.uploadsdk.upload.net.NetworkType;
import com.eebbk.bfc.uploadsdk.upload.share.Constants;
import com.eebbk.bfc.uploadsdk.upload.share.Impl;
import com.eebbk.bfc.uploadsdk.upload.share.UploadConstants;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

public abstract class UploadTask implements ITask{
    /**
     * 任务类型 1 HTTP 2 FTP 3 CLOUD
     */
    private final int mTaskType;
    /**
     * id
     */
    private long mId;
    /**
     * 文件名
     */
    private String mFileName;
    /**
     * 上传的优先级
     */
    private int mPriority = UploadConstants.PRIORITY_NORMAL;
    /**
     * 文件路径
     */
    private String mFilePath;
    /**
     * 文件大小
     */
    private long mFileSize;

    private int mVisibility = Request.VISIBILITY_HIDDEN;

    /**
     * 状态
     */
    private int mState;
    /**
     * 上传速度
     */
    private int mSpeed;
    /**
     * 上传剩余时间
     */
    private String mNeedtime;
    /**
     * 已上传文件大小
     */
    private int mUploadedSize;

    /**
     * 扩展字段的数据
     */
    private HashMap<String, String> mExtrasMap;
    /**
     * 扩展文件数据
     */
    private HashMap<String, String> mFileMap;
    /**
     * http输出
     */
    private String mOutput;
    /**
     * 包名
     */
    private String mPackageName;

    /**
     * 下载可用的网络类型：{@link NetworkType#NETWORK_WIFI}, {@link NetworkType#NETWORK_MOBILE}, {@link NetworkType#NETWORK_BLUETOOTH},
     */
    private int mNetworkTypes = NetworkType.DEFAULT_NETWORK;

    public static final String PREFIX_FILE = "F:";

    public static final String PREFIX_STRING = "S:";

    private static final String NULL_NAME = "The name is null !";

    private static final int TIME_UNIT = 60;
    private static final int DAY_UNIT = 24;

    UploadTask(int pTaskType){
        super();
        mTaskType = pTaskType;
        mExtrasMap = new HashMap<>();
        mFileMap = new HashMap<>();
    }

    UploadTask(Cursor cursor){
        super();
        mId = cursor.getLong(cursor.getColumnIndex(Impl._ID));
        mTaskType = cursor.getInt(cursor.getColumnIndex(Impl.COLUMN_TASK_TYPE));
        mFileName = cursor.getString(cursor
                .getColumnIndex(Impl.COLUMN_FILE_NAME));
        mOutput = cursor.getString(cursor
                .getColumnIndex(Impl.COLUMN_OUTPUT));
        mPriority = cursor.getInt(cursor.getColumnIndex(Impl.COLUMN_PRIORITY));
        mFilePath = cursor.getString(cursor
                .getColumnIndex(Impl.COLUMN_FILE_PATH));
        mVisibility = cursor.getInt(cursor.getColumnIndex(Impl.COLUMN_VISIBILITY));
        mFileSize = cursor
                .getLong(cursor.getColumnIndex(Impl.COLUMN_FILE_SIZE));
        mState = cursor.getInt(cursor.getColumnIndex(Impl.COLUMN_STATUS));
        mSpeed = cursor
                .getInt(cursor.getColumnIndex(Impl.COLUMN_CURRENT_SPEED));
        mUploadedSize = cursor.getInt(cursor
                .getColumnIndex(Impl.COLUMN_CURRENT_BYTES));
        mPackageName = cursor.getString(cursor
                .getColumnIndex(Impl.COLUMN_NOTIFICATION_PACKAGE));
        String needTimeString;
        if(mSpeed != 0){
            long needtime = (mFileSize - mUploadedSize)/mSpeed;
            if(needtime > 0){
                needTimeString = timeFormat(needtime);
            }else{
                needTimeString = "耐心等待";
            }
        }else{
            needTimeString = "耐心等待";
        }
        mNeedtime = needTimeString;
        HashMap<String, String> extras = null;
        try{
            extras = ExtrasConverter.decode(cursor.getString(cursor
                    .getColumnIndex(Impl.COLUMN_EXTRAS)));
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        mExtrasMap = extras;
        HashMap<String, String> extraFiles = null;
        try{
            extraFiles = ExtrasConverter.decode(cursor.getString(cursor
                    .getColumnIndex(Impl.COLUMN_EXTRA_FILES)));
            LogUtils.i("mFileMap:"+cursor.getString(cursor
                    .getColumnIndex(Impl.COLUMN_EXTRA_FILES)));
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        mFileMap = extraFiles;

    }

    /**
     * 转换显示剩余时间
     *
     * @param totalSeconds
     *
     * @return
     */
    private static String timeFormat(long totalSeconds){
        if(totalSeconds < 0){
            return null;
        }
        StringBuilder formatBuilder = new StringBuilder();
        @SuppressWarnings("resource")
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

        long seconds = totalSeconds%TIME_UNIT;
        long minutes = (totalSeconds/TIME_UNIT)%TIME_UNIT;
        long hours = totalSeconds/(TIME_UNIT*TIME_UNIT);
        formatBuilder.setLength(0);

        if(hours > 0 && hours < (DAY_UNIT - 1)){
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        }else if(hours >= (DAY_UNIT - 1)){
            return formatter.format("%d:%02d:%02d", DAY_UNIT - 1, minutes, seconds).toString();
        }else{
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    public String toString(){
        return "NewBfcUploads [mTaskType=" + mTaskType + ", mId=" + mId + ", mFileName=" + mFileName + ", mPriority=" + mPriority + ", mFilePath=" + mFilePath
                + ", mFileSize=" + mFileSize + ", mVisibility=" + mVisibility + ", mState=" + mState + ", mSpeed=" + mSpeed + ", mNeedtime=" + mNeedtime
                + ", mUploadedSize=" + mUploadedSize + ", mExtrasMap=" + mExtrasMap + ", mFileMap=" + mFileMap + ", mOutput=" + mOutput + "]";
    }

    @Override
    public int getNotificationVisibility(){
        return mVisibility;
    }

    @Override
    public void setNotificationVisibility(int visibility){
        if(visibility < Request.VISIBILITY_VISIBLE || visibility > Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION){
            throw new IllegalArgumentException("The value is not in the scope of legal!!");
        }
        mVisibility = visibility;
    }

    @Override
    public int getTaskType(){
        return mTaskType;
    }

    @Override
    public long getId(){
        return mId;
    }

    @Override
    public void setId(long pId){
        mId = pId;
    }

    @Override
    public String getFileName(){
        return mFileName;
    }

    @Override
    public void setFileName(@NonNull String pFileName){

        mFileName = pFileName;
    }

    @Override
    public int getPriority(){
        return mPriority;
    }

    @Override
    public void setPriority(int pPriority){
        mPriority = pPriority;
    }

    @Override
    public String getFilePath(){
        return mFilePath;
    }

    @Override
    public void setFilePath(String pFilePath){
        mFilePath = pFilePath;
    }

    @Override
    public long getFileSize(){
        return mFileSize;
    }

    @Override
    public void setFileSize(long pFileSize){
        mFileSize = pFileSize;
    }

    @Override
    public int getState(){
        return mState;
    }

    @Override
    public void setState(int pState){
        mState = pState;
    }

    @Override
    public int getSpeed(){
        return mSpeed;
    }

    @Override
    public void setSpeed(int pSpeed){
        mSpeed = pSpeed;
    }

    @Override
    public String getNeedtime(){
        return mNeedtime;
    }

    @Override
    public void setNeedtime(String pNeedtime){
        mNeedtime = pNeedtime;
    }

    @Override
    public int getUploadedSize(){
        return mUploadedSize;
    }

    @Override
    public void setUploadedSize(int pUploadedSize){
        mUploadedSize = pUploadedSize;
    }

    @Override
    public String getUrl(){
        return null;
    }

    @Override
    public void setUrl(String pUrl){

    }

    @Override
    public String getRemoteFilePath(){
        return null;
    }

    @Override
    public void setRemoteFilePath(String pRemoteFilePath){

    }

    @Override
    public String getRemoteFileName(){
        return null;
    }

    @Override
    public void setRemoteFileName(String pRemoteFileName){

    }

    @Override
    public String getUserName(){
        return null;
    }

    @Override
    public void setUserName(String pUserName){

    }

    @Override
    public String getPassword(){
        return null;
    }

    @Override
    public void setPassword(String pPassword){

    }

    @Override
    public boolean getSupportProcess() {
        return Constants.isSupportCrossProcess;

    }

    @Override
    public void setSupportProcess(boolean process) {

    }
    @Override
    public String getServerAddress(){
        return null;
    }

    @Override
    public void setServerAddress(String pServerAddress){

    }

    @Override
    public int getServerPort(){
        return 0;
    }

    @Override
    public void setServerPort(int pServerPort){

    }

    @Override
    public void putExtra(String name, String value){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        mExtrasMap.put(name, value);
    }

    @Override
    public void putExtra(String name, int value){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        mExtrasMap.put(name, String.valueOf(value));
    }

    @Override
    public void putExtra(String name, boolean value){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        mExtrasMap.put(name, String.valueOf(value));
    }

    @Override
    public void putExtra(String name, float value){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        mExtrasMap.put(name, String.valueOf(value));
    }

    @Override
    public void putExtra(String name, double value){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        mExtrasMap.put(name, String.valueOf(value));
    }

    @Override
    public void putExtra(String name, char value){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        mExtrasMap.put(name, String.valueOf(value));
    }

    @Override
    public void putExtra(String name, byte value){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        mExtrasMap.put(name, String.valueOf(value));
    }

    @Override
    public void putExtra(String name, byte[] value){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        mExtrasMap.put(name, Arrays.toString(value));
    }

    @Override
    public void putExtra(String name, short value){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        mExtrasMap.put(name, String.valueOf(value));
    }

    @Override
    public String getStringExtra(String name){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        return mExtrasMap.get(name);
    }

    @Override
    public int getIntExtra(String name, int defaultValue){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        if(mExtrasMap.containsKey(name)){
            return Integer.parseInt(mExtrasMap.get(name));
        }else{
            return defaultValue;
        }
    }

    @Override
    public boolean getBooleanExtra(String name, boolean defaultValue){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        if(mExtrasMap.containsKey(name)){
            return Boolean.parseBoolean(mExtrasMap.get(name));
        }else{
            return defaultValue;
        }
    }

    @Override
    public float getFloatExtra(String name, float defaultValue){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        if(mExtrasMap.containsKey(name)){
            return Float.parseFloat(mExtrasMap.get(name));
        }else{
            return defaultValue;
        }
    }

    @Override
    public double getDoubleExtra(String name, double defaultValue){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        if(mExtrasMap.containsKey(name)){
            return Double.parseDouble(mExtrasMap.get(name));
        }else{
            return defaultValue;
        }
    }

    @Override
    public char getCharExtra(String name, char defaultValue){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        if(mExtrasMap.containsKey(name)){
            return (char) Integer.parseInt(mExtrasMap.get(name));
        }else{
            return defaultValue;
        }
    }

    @Override
    public byte getByteExtra(String name, byte defaultValue){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        if(mExtrasMap.containsKey(name)){
            return Byte.parseByte(mExtrasMap.get(name));
        }else{
            return defaultValue;
        }
    }

    @Override
    public byte[] getByteArrayExtra(String name){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        return mExtrasMap.get(name) == null ? null : mExtrasMap.get(name).getBytes();

    }

    @Override
    public short getShortExtra(String name, short defaultValue){
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException(NULL_NAME);
        }
        if(mExtrasMap.containsKey(name)){
            return Short.parseShort(mExtrasMap.get(name));
        }else{
            return defaultValue;
        }
    }

    @Override
    public void addFile(String fileName, String filePath){
        if(TextUtils.isEmpty(fileName)){
            throw new IllegalArgumentException("The fileName is null !");
        }
        if(TextUtils.isEmpty(filePath)){
            throw new IllegalArgumentException("The filePath is null !");
        }
        mFileMap.put(fileName, filePath);
    }

    @Override
    public String getFileUri(String fileName){
        if(TextUtils.isEmpty(fileName)){
            throw new IllegalArgumentException("The fileName is null !");
        }
        if(mFileMap.containsKey(fileName)){
            return mFileMap.get(fileName);
        }else{
            return null;
        }
    }

    @Override
    public HashMap<String, String> getExtrasMap(){
        return mExtrasMap;
    }

    @Override
    public void setExtrasMap(HashMap<String, String> map){
        mExtrasMap = map;
    }

    @Override
    public HashMap<String, String> getFilesMap(){
        return mFileMap;
    }

    @Override
    public void setFilesMap(HashMap<String, String> map){
        mFileMap = map;
    }

    @Override
    public String getOutput(){
        return mOutput;
    }

    @Override
    public void setOutput(String output){
        this.mOutput = output;
    }

    @Override
    public void addFileBody(String key, String filePath){
        if(TextUtils.isEmpty(key)){
            throw new IllegalArgumentException("The key is null !");
        }
        if(TextUtils.isEmpty(filePath)){
            throw new IllegalArgumentException("The filePath is null !");
        }
        mFileMap.put(PREFIX_FILE + key, filePath);
    }

    @Override
    public void addStringBody(String key, String value){
        if(TextUtils.isEmpty(key)){
            throw new IllegalArgumentException("The key is null !");
        }
        if(TextUtils.isEmpty(value)){
            throw new IllegalArgumentException("The value is null !");
        }
        mFileMap.put(PREFIX_STRING + key, value);
    }

    @Override
    public void setOverWrite(boolean overWrite){
    }

    @Override
    public boolean getOverWrite(){
        return false;
    }

    @Override
    public String getPackageName(){
        return mPackageName;
    }

    @Override
    public void setNetworkTypes(int networkType) {
        mNetworkTypes = networkType;
    }

    @Override
    public int getNetworkTypes() {
        return mNetworkTypes;
    }

    @Override
    public void setBucketId(String bucketId) {
        if (mExtrasMap != null){
            mExtrasMap.put(EXTRA_KEY_BUCKET_ID, bucketId);
        }else {
            throw new NullPointerException("UploadTask's mExtrasMap is null, can't set BucketId");
        }
    }

    @Override
    public String getBucketId() {
        return mExtrasMap != null?mExtrasMap.get(EXTRA_KEY_BUCKET_ID):null;
    }
}
