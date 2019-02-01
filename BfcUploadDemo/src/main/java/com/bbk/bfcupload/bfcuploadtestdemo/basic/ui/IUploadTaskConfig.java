package com.bbk.bfcupload.bfcuploadtestdemo.basic.ui;

import java.io.File;
import java.util.List;


public interface IUploadTaskConfig {

    List<File> getFileList();

    String getTaskName();

    int getNetworkTypes();

}
