package com.bbk.bfcupload.bfcuploadtestdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbk.bfcupload.bfcuploadtestdemo.R;
import com.bbk.bfcupload.bfcuploadtestdemo.util.FileUtils;
import com.eebbk.bfc.uploadsdk.upload.net.NetworkType;
import com.eebbk.bfc.uploadsdk.uploadmanage.LogUtils;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.HttpTask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.IController;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.ITask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.MultiCloudTask;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.UploadController;
import com.eebbk.bfc.uploadsdk.uploadmanage.uploadmanager.UploadListener;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: chenxiang
 * Date:   2018/9/28
 * Description:
 */

public class UploadTypeActivity extends Activity{
    private static final String url = "http://da.eebbk.net:80/v3/receiveFile";

    @BindView(R.id.uploadtype_button1)
    Button mButton1;
    @BindView(R.id.uploadtype_button2)
    Button mButton2;
    @BindView(R.id.httpuploadsuccess)
    TextView mSuccessTv;
    @BindView(R.id.httpuploadcount)
    TextView mCountTv;
    @BindView(R.id.httpuploadfail)
    TextView mFailTv;
    @BindView(R.id.httpuploadfailinfo)
    TextView mFailInfo;
    @BindView(R.id.title_tv)
    TextView mTitle;
    @BindView(R.id.back_iv)
    ImageView mBack;

    private ITask task = null;
    private String path="";
    private volatile int mFailNumber = 0;
    private UploadListener mUploadListener;
    private volatile int mSuccessNumber = 0;
    private IController mUploadController = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadtype);
        init();
    }

    private void init(){
        ButterKnife.bind(this);
        mTitle.setText("成功率测试");
        mUploadController = new UploadController(UploadTypeActivity.this);
        mUploadListener = new UploadListener() {
            @Override
            public void onLoading(int id, long total, long current, long remainingMillis, long speed, int precent) {
            }

            @Override
            public void onSuccess(int id, String taskName, long total, List<String> urlList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSuccessNumber += 1;
                        mSuccessTv.setText("上传成功个数：" + mSuccessNumber);
                    }
                });
            }

            @Override
            public void onFailure(int id, final String taskName, final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFailNumber += 1;
                        mFailTv.setText("上传失败个数：" + mFailNumber);
                        mFailInfo.setText("失败信息:" + msg);
                        writeTxtToFile("文件名："+taskName+"    错误码："+msg,path+"/errlog/","log.txt");
                    }
                });
            }
        };
    }

    @OnClick({R.id.uploadtype_button1, R.id.uploadtype_button2,R.id.back_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uploadtype_button1:
                readDir(0);
                break;
            case R.id.uploadtype_button2:
                readDir(1);
                break;
            case R.id.back_iv:
                finish();
                break;
            default:
                break;
        }
    }

    private void readDir(int type) {
        mSuccessNumber=0;
        mFailNumber=0;
        path = Environment.getExternalStorageDirectory() + "/uploadtest";
        File file = new File(path+"/uploaddata");
        File[] fs = file.listFiles();
        int count = fs.length;
        if (type == 0) {
            mCountTv.setText("Http上传总个数：" + count);
            for (File f : fs) {
                httpUpload(f);
            }
        } else {
            mCountTv.setText("云上传总个数：" + count);

            for (File f : fs) {
                CloudUpload(f);
            }
        }
    }

    private void CloudUpload(File file) {
        task = new MultiCloudTask();
        String taskName = file.getName();
        if (!file.exists()) {
            FileUtils.copyFromAssets2(this, file);
        }
        String pFilePath = file.getAbsolutePath();
        String pFileName = file.getName();
        for (int i = 0; i < 10; i++) {
            task.addFile(i + pFileName, pFilePath);
            LogUtils.d("添加：" + i);
        }
        task.setOverWrite(true);
        task.putExtra("e1", "默认测试extra");
        task.setFileName(taskName + "1");
        mUploadController.addTask(mUploadListener, task);
    }


    private void httpUpload(File file) {
        FileUtils.copyFromAssets2(this, file);
        task = new HttpTask(url);
        task.setUrl(url);
        String pFilePath = file.getAbsolutePath();
        task.addFileBody("testaa", pFilePath);
        task.putExtra("HTTP", "dss");
        task.setNetworkTypes(NetworkType.NETWORK_WIFI | NetworkType.NETWORK_MOBILE);
        mUploadController.addTask(mUploadListener, task);
    }


    private void writeTxtToFile(String strcontent, String filePath, String fileName) {
        deleteFile(path,fileName);
        makeFilePath(filePath, fileName);
        String strFilePath = filePath + fileName;
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    private void makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void makeRootDirectory(String filePath) {
        File file;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    private void deleteFile(String filePath, String fileName) {
        File f = new File(filePath + fileName);
        if (f.exists()) {
            f.delete();
        }
    }
}
