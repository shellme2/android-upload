package com.eebbk.bfc.uploadsdk.repo.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "newbfcuploads")
public class NewBfcUploads {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private Integer id;

    @ColumnInfo(name = "job_id")
    private int jobId;

    @ColumnInfo(name = "notificationpackage")
    private String packageName;

    @ColumnInfo(name = "file_size")
    private Long fileSize;

    @ColumnInfo(name = "file_name")
    private String fileName;

    @ColumnInfo(name = "file_path")
    private String filePath;

    @ColumnInfo(name = "priority")
    private Integer priority;

    @ColumnInfo(name = "extras")
    private String extras;

    @ColumnInfo(name = "extrafiles")
    private String extraFiles;

    @ColumnInfo(name = "task_type")
    private Integer taskType;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "notificationclass")
    private String notifiClass;

    @ColumnInfo(name = "notificationextras")
    private String notifiExtras;

    @ColumnInfo(name = "visibility")
    private Integer notifiVisibility;

    @NonNull
    @ColumnInfo(name = "allowed_network_types")
    private Integer allowNetWorkType;


    @ColumnInfo(name = "allow_roaming")
    private boolean allowRoaming;


    @ColumnInfo(name = "allow_metered")
    private boolean allowMetered;


    @ColumnInfo(name = "overWrite")
    private boolean overWrite;

    @ColumnInfo(name = "lastmod")
    private Integer lastmod;

    @ColumnInfo(name = "current_speed")
    private Integer current_speed;

    @ColumnInfo(name = "errorMsg")
    private String errorMsg;

    @ColumnInfo(name = "deleted")
    private boolean deleted;

    @NonNull
    @ColumnInfo(name = "bypass_recommended_size_limit")
    private Integer bypass_recommended_size_limit;

    @ColumnInfo(name = "current_bytes")
    private Long current_bytes;

    @ColumnInfo(name = "numfailed")
    private Integer numfailed;

    @ColumnInfo(name = "output")
    private String output;

    @ColumnInfo(name = "control")
    private Integer control;

    @ColumnInfo(name = "status")
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Long getFileSize() {
        if (fileSize==null){
            fileSize = 0L;
        }
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getPriority() {
        return priority == null ? 0 : priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getExtraFiles() {
        return extraFiles;
    }

    public void setExtraFiles(String extraFiles) {
        this.extraFiles = extraFiles;
    }

    public Integer getTaskType() {
        return taskType == null ? 0 : taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNotifiClass() {
        return notifiClass;
    }

    public void setNotifiClass(String notifiClass) {
        this.notifiClass = notifiClass;
    }

    public String getNotifiExtras() {
        return notifiExtras;
    }

    public void setNotifiExtras(String notifiExtras) {
        this.notifiExtras = notifiExtras;
    }

    public Integer getNotifiVisibility() {
        return notifiVisibility == null ? 0 : notifiVisibility;
    }

    public void setNotifiVisibility(Integer notifiVisibility) {
        this.notifiVisibility = notifiVisibility;
    }

    @NonNull
    public Integer getAllowNetWorkType() {
        return allowNetWorkType == null ? 0 : allowNetWorkType;
    }

    public void setAllowNetWorkType(@NonNull Integer allowNetWorkType) {
        this.allowNetWorkType = allowNetWorkType;
    }


    public boolean getAllowRoaming() {
        return allowRoaming;
    }

    public void setAllowRoaming(boolean allowRoaming) {
        this.allowRoaming = allowRoaming;
    }


    public boolean getAllowMetered() {
        return allowMetered;
    }

    public void setAllowMetered(boolean allowMetered) {
        this.allowMetered = allowMetered;
    }


    public boolean getOverWrite() {
        return overWrite;
    }

    public void setOverWrite(boolean overWrite) {
        this.overWrite = overWrite;
    }

    public Integer getLastmod() {
        return lastmod == null ? 0 : lastmod;
    }

    public void setLastmod(Integer lastmod) {
        this.lastmod = lastmod;
    }

    public Integer getCurrent_speed() {
        return current_speed == null ? 0 : current_speed;
    }

    public void setCurrent_speed(Integer current_speed) {
        this.current_speed = current_speed;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @NonNull
    public Integer getBypass_recommended_size_limit() {
        return bypass_recommended_size_limit == null ? 0 : bypass_recommended_size_limit;
    }

    public void setBypass_recommended_size_limit(@NonNull Integer bypass_recommended_size_limit) {
        this.bypass_recommended_size_limit = bypass_recommended_size_limit;
    }

    public Long getCurrent_bytes() {
        return current_bytes == null ? 0 : current_bytes;
    }

    public void setCurrent_bytes(Long current_bytes) {
        this.current_bytes = current_bytes;
    }

    public Integer getNumfailed() {
        return numfailed == null ? 0 : numfailed;
    }

    public void setNumfailed(Integer numfailed) {
        this.numfailed = numfailed;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Integer getControl() {
        return control == null ? 0 : control;
    }

    public void setControl(Integer control) {
        this.control = control;
    }

    public Integer getStatus() {
        return status == null ? 0 : status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
